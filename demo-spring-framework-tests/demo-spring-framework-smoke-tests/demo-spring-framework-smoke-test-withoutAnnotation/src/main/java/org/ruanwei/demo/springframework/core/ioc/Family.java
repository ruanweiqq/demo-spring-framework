package org.ruanwei.demo.springframework.core.ioc;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.aop.Good;
import org.ruanwei.demo.springframework.core.aop.Happy;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeopleFormat;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeopleFormat.Separator;
import org.ruanwei.demo.springframework.core.ioc.event.MyApplicationEvent;
import org.ruanwei.demo.util.Recorder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.validation.annotation.Validated;

@Validated
public class Family implements ApplicationContextAware, BeanFactoryAware, MessageSourceAware, ResourceLoaderAware,
		ApplicationEventPublisherAware, EnvironmentAware, BeanClassLoaderAware, BeanNameAware, LoadTimeWeaverAware,
		InitializingBean, DisposableBean {
	private static Log log = LogFactory.getLog(Family.class);

	private BeanFactory beanFactory;
	private ApplicationContext context;
	private MessageSource messageSource;
	private ResourceLoader resourceLoader;
	private ApplicationEventPublisher publisher;
	private Environment environment;

	private String beanName;
	private ClassLoader classLoader;
	private LoadTimeWeaver loadTimeWeaver;

	private String familyName;
	private int familyCount;
	private People father;

	// JSR-303 Bean Validation
	@Valid
	private People mother;
	@Valid
	@PeopleFormat(separator = Separator.SLASH)
	private People son;
	@Valid
	@PeopleFormat(separator = Separator.SLASH)
	private People daughter;

	// TODO:加@Valid报错
	private People guest;

	// JSR-349:Method Validation with @Validated
	@NotNull
	public String sayHello(@Size(min = 2, max = 8) String message) {
		log.info("sayHello(String message)" + message);

		Good good = (Good) context.getBean("good");
		Happy mixin = (Happy) context.getBean("good");

		log.info(good.good("whatever") + mixin.happy("whatever"));

		return "Hello," + message;
	}

	// 1.Environment Profile
	/* 
	 * 初始通过-Dspring.profiles.active="development" -Dspring.profiles.default="development"或@Profile指定
	 * 通过代码变更为spring.profiles.active="development" spring.profiles.default="production"
	*/
	public void refreshProfile() {
		log.info("refreshProfile()");

		if (environment == null) {
			environment = context.getEnvironment();
		}
		log.info("activeProfile==========" + environment.getActiveProfiles() + " defaultProfile========"
				+ environment.getDefaultProfiles());
		House house = context.getBean("house", House.class);
		log.info("house==========" + house);

		if (environment instanceof ConfigurableEnvironment) {
			ConfigurableEnvironment configEnv = (StandardEnvironment) environment;
			configEnv.setActiveProfiles("development");
			configEnv.setDefaultProfiles("production");
			if (context instanceof ConfigurableApplicationContext) {
				ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) context;
				// TODO:报错，只允许调用一次
				// ctx.refresh();
			}
		}

		environment = context.getEnvironment();
		log.info("activeProfile2==========" + environment.getActiveProfiles() + " defaultProfile2========"
				+ environment.getDefaultProfiles());

		house = context.getBean("house", House.class);
		log.info("house2==========" + house);
	}

	// 2.Environment PropertySource
	/* 
	 * EnumerablePropertySource
	 *   CommandLinePropertySource
	 *     SimpleCommandLinePropertySource
	 *     JOptCommandLinePropertySource
	 *   MapPropertySource(systemProperties)
	 *     PropertiesPropertySource
	 *       ResourcePropertySource(@PeopertySource)
	 *     SystemEnvironmentPropertySource(systemEnvironment)
	 *   ServletConfigPropertySource
	 *   ServletContextPropertySource
	 *   
	 *   StandardEnvironment:
	 *     MapPropertySource(systemProperties)
	 *     SystemEnvironmentPropertySource(systemEnvironment)
	*/
	public void refreshPropertySource() {
		log.info("refreshPropertySource()");

		// StandardEnvironment:MapPropertySource(systemProperties)/SystemEnvironmentPropertySource(systemEnvironment)
		if (environment instanceof ConfigurableEnvironment) {
			ConfigurableEnvironment configEnv = (ConfigurableEnvironment) environment;
			MutablePropertySources propertySources = configEnv.getPropertySources();
			log.info("PropertySources1==========" + propertySources);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("d", 4);
			PropertySource<Map<String, Object>> mapPropertySource = new MapPropertySource("mapPropertySource", map);
			propertySources.addLast(mapPropertySource);

			propertySources = configEnv.getPropertySources();
			log.info("PropertySources2==========" + propertySources);
		}

		String a = environment.getProperty("a", "default_a"); // MapPropertySource(-Da=1)
		String b = environment.getProperty("b", "default_b"); // SystemEnvironmentPropertySource(export b=2)
		String c = environment.getProperty("c", "default_c");// ResourcePropertySource(@PeopertySource("propertySource.properties")//
																// c=3)
		String d = environment.getProperty("d", "default_d");// MapPropertySource(addLast d=4)
		log.info("property=========a=" + a + " b=" + b + " c=" + c + " d=" + d);
	}

	public void helloWorld() {
		log.info("helloWorld2()");

		// 1.MessageSource
		if (messageSource == null) {
			messageSource = (MessageSource) context;
		}
		String helloWorld1 = messageSource.getMessage("messageSource.helloWorld", new Object[] { "ruanwei" },
				"This is a message.", Locale.US);
		log.info("helloWorld1==========" + helloWorld1);

		String helloWorld2 = messageSource.getMessage("messageSource.helloWorld", new Object[] { "ruanwei" },
				"This is a message.", Locale.CHINA);
		log.info("helloWorld2==========" + helloWorld2);

		// 2.ResourceLoader
		if (resourceLoader == null) {
			resourceLoader = (ResourceLoader) context;
		}
		log.info("resourceLoader==========" + resourceLoader);
		Resource resource = resourceLoader.getResource("classpath:spring/applicationContext.xml");
		log.info("resource==========" + resource);

		// 3.ApplicationEventPublisher
		if (publisher == null) {
			publisher = (ApplicationEventPublisher) context;
		}
		// 3.1 发送PayloadApplicationEvent<String>(this,helloWorld1);
		publisher.publishEvent(helloWorld1);
		publisher.publishEvent(helloWorld2);
		publisher.publishEvent(resource.getFilename());

		// 3.2 发送MyApplicationEvent
		publisher.publishEvent(new MyApplicationEvent(this, resource.getFilename()));

		// 3.3 发送ApplicationContextEvent
		if (context instanceof AbstractApplicationContext) {
			AbstractApplicationContext absContext = (AbstractApplicationContext) context;
			absContext.start();
			absContext.stop();

			// 即ClassPathXmlApplicationContext和FileSystemXmlApplicationContext
			if (context instanceof AbstractRefreshableApplicationContext) {
				// absContext.refresh();
			}

			// absContext.close();
		}

		// 4.其他容器对象
		log.info("beanFactory=========" + beanFactory);
		log.info("beanName=========" + beanName);
		log.info("classLoader=========" + classLoader);
		log.info("loadTimeWeaver=========" + loadTimeWeaver);
	}

	// a.Bean instantiation with a constructor
	// 1.Constructor-based dependency injection(byName with javac -g)
	@ConstructorProperties({ "familyName", "familyCount", "father" })
	public Family(String familyName, int familyCount, @Valid People father) {
		this.familyName = familyName;
		this.familyCount = familyCount;
		this.father = father;
		log.info("Family(String familyName, int familyCount, People father)" + this);
	}

	public String getFamilyName() {
		return familyName;
	}

	// 2.Setter-based dependency injection
	public void setMother(People mother) {
		log.info("setMother(People mother)" + mother);
		this.mother = mother;
	}

	// TODO:具体怎么用
	public void setXXX(ObjectProvider<People> mother) {
		log.info("setMother(ObjectFactory<People> mother)" + mother);
		this.mother = mother.getIfUnique();
	}

	public void setSon(People son) {
		log.info("setSon(People son)" + son);
		this.son = son;
	}

	public void setDaughter(People daughter) {
		log.info("setDaughter(People daughter)" + daughter);
		this.daughter = daughter;
	}

	public void setGuest(People guest) {
		log.info("setGuest(People guest)" + guest);
		this.guest = guest;
	}

	// 3.Method injection: Lookup method injection
	public People createBueaty() {
		log.info("createBueaty");
		return null;
	}

	// 3.Method injection: Arbitrary method replacement
	public int calc(int a, int b) {
		return a + b;
	}

	// 4.Aware injection
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		log.info("setApplicationContext(ApplicationContext applicationContext)" + applicationContext);
		this.context = applicationContext;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		log.info("setBeanFactory(BeanFactory beanFactory)" + beanFactory);
		if (beanFactory == null) {
			beanFactory = (BeanFactory) context;
		}
		this.beanFactory = beanFactory;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		log.info("setMessageSource(MessageSource messageSource)" + messageSource);
		if (messageSource == null) {
			messageSource = (MessageSource) context;
		}
		this.messageSource = messageSource;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		log.info("setResourceLoader(ResourceLoader resourceLoader)" + resourceLoader);
		if (resourceLoader == null) {
			resourceLoader = (ResourceLoader) context;
		}
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		log.info("setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)"
				+ applicationEventPublisher);
		if (applicationEventPublisher == null) {
			applicationEventPublisher = (ApplicationEventPublisher) context;
		}
		this.publisher = applicationEventPublisher;
	}

	@Override
	public void setEnvironment(Environment env) {
		log.info("setEnvironment(Environment env)" + env);
		if (env == null) {
			env = context.getEnvironment();
		}
		this.environment = env;
	}

	@Override
	public void setBeanName(String name) {
		log.info("setBeanName(String name)" + name);
		this.beanName = name;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		log.info("setBeanClassLoader(ClassLoader classLoader)" + classLoader);
		this.classLoader = classLoader;
	}

	@Override
	public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
		log.info("setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver)" + loadTimeWeaver);
		this.loadTimeWeaver = loadTimeWeaver;
	}

	// Initialization callback
	public void init() {
		log.info("====================init()");
		Recorder.record("init()", this.getClass());

		if (context == null) {
			// context = new AnnotationConfigApplicationContext(AppConfig.class);
			context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext.xml");
		}
	}

	// Bean initialization callback
	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("====================afterPropertiesSet()");
		Recorder.record("afterPropertiesSet()", this.getClass());

		if (context == null) {
			// context = new AnnotationConfigApplicationContext(AppConfig.class);
			context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext.xml");
		}
	}

	// Destruction callback
	public void destroy2() {
		log.info("====================destroy()");
		Recorder.record("destroy2()", this.getClass());
	}

	@Override
	// Bean destruction callback
	public void destroy() {
		log.info("====================destroy()");
		Recorder.record("destroy()", this.getClass());
	}

	@Override
	public String toString() {
		return "Family [familyName=" + familyName + ", familyCount=" + familyCount + ", father=" + father + ", mother="
				+ mother + ", son=" + son + ", daughter=" + daughter + ", guest=" + guest + "]";
	}

}