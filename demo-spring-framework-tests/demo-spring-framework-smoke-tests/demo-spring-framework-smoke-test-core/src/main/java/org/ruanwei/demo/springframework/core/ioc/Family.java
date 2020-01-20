package org.ruanwei.demo.springframework.core.ioc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
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
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Data
@Validated
@Lazy
@DependsOn("house")
@Component("family")
public class Family implements BeanNameAware, BeanClassLoaderAware, LoadTimeWeaverAware {
	private static Log log = LogFactory.getLog(Family.class);

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private Environment environment;

	// 需要*Aware接口
	private String beanName;
	private ClassLoader classLoader;
	private LoadTimeWeaver loadTimeWeaver;

	private String familyName;
	private int familyCount;
	private People father;

	@Valid
	// @Qualifier("mother")
	@Autowired
	private People mother;

	@Value("${son.all}")
	@PeopleFormat(separator = Separator.SLASH)
	private People son;

	@Value("${daughter.all}")
	@PeopleFormat(separator = Separator.SLASH)
	private People daughter;

	@Valid
	@Autowired
	private People guest;

	// a.Bean instantiation with a constructor
	// 1.Constructor-based dependency injection
	@Autowired
	public Family(@Value("${family.familyName:ruan_def}") String familyName,
			@Value("${family.familyCount:2}") int familyCount, @Valid People father) {
		this.familyName = familyName;
		this.familyCount = familyCount;
		this.father = father;
		log.info("Family(String familyName, int familyCount, People father)" + this);
	}

	// 2.Setter-based dependency injection
	@Override
	public void setBeanName(String name) {
		log.info("setBeanName(String name)" + name);
		this.beanName = name;
	}

	@Override
	public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
		log.info("setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver)" + loadTimeWeaver);
		this.loadTimeWeaver = loadTimeWeaver;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		log.info("setBeanClassLoader(ClassLoader classLoader)" + classLoader);
		this.classLoader = classLoader;
	}

	// 3.Method injection: Lookup method injection
	@Lookup("guest")
	public People createBueaty() {
		log.info("createBueaty");
		return null;
	}

	// 3.Method injection: Arbitrary method replacement
	public int calc(int a, int b) {
		return a + b;
	}

	// JSR-349:Method Validation with @Validated
	@NotNull
	public String sayHello(@Size(min = 2, max = 8) String message) {
		log.info("sayHello(String message)" + message);
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
		log.info("activeProfile==========" + Arrays.toString(environment.getActiveProfiles()) + " defaultProfile========"
				+ Arrays.toString(environment.getDefaultProfiles()));
		AbsHouse house = context.getBean("house", AbsHouse.class);
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
		log.info("activeProfile2==========" + Arrays.toString(environment.getActiveProfiles()) + " defaultProfile2========"
				+ Arrays.toString(environment.getDefaultProfiles()));

		house = context.getBean("house", AbsHouse.class);
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

	public void magic() {
		log.info("magic()");

		Good good = (Good) context.getBean("good");
		Happy mixin = (Happy) context.getBean("good");

		log.info(good.good("whatever") + mixin.happy("whatever"));
	}

	// JSR-250.Initialization callback.等价于<bean init-method="init"/>.
	@PostConstruct
	public void init() {
		log.info("====================init()");
		Recorder.record("destroy2()", this.getClass());
	}

	// JSR-250.Destruction callback.等价于<bean destroy-method="destroy"/>.
	@PreDestroy
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