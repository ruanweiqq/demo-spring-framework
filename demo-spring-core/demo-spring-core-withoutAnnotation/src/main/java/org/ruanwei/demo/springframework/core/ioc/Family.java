package org.ruanwei.demo.springframework.core.ioc;

import java.beans.ConstructorProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeopleFormat;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeopleFormat.Separator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.validation.annotation.Validated;

@Validated
public class Family implements ApplicationContextAware, BeanFactoryAware,
		MessageSourceAware, ResourceLoaderAware,
		ApplicationEventPublisherAware, EnvironmentAware, BeanClassLoaderAware,
		BeanNameAware, LoadTimeWeaverAware {
	private static Log log = LogFactory.getLog(Family.class);

	// 1.Constructor-based dependency injection
	private String familyName;
	private int familyCount;
	private People father;

	// 2.Setter-based dependency injection
	// JSR-303 Bean Validation
	@Valid
	private People mother;
	@Valid
	@PeopleFormat(separator = Separator.SLASH)
	private People son;
	@Valid
	@PeopleFormat(separator = Separator.SLASH)
	private People daughter;

	// 3.Method injection: Lookup method injection
	// TODO:加@Valid报错
	private People guest;

	private ApplicationContext context;
	private BeanFactory beanFactory;
	private MessageSource messageSource;
	private ResourceLoader resourceLoader;
	private ApplicationEventPublisher publisher;

	private Environment env;

	private String beanName;
	private ClassLoader classLoader;
	private LoadTimeWeaver loadTimeWeaver;

	// JSR-349:Method Validation with @Validated
	@NotNull
	public String sayHello(@Size(min = 2, max = 8) String message) {
		log.info("sayHello(String message)" + message);

		// 3.Method injection: Arbitrary method replacement
		log.info("3 + 5 = " + calc(3, 5));

		// 3.Method injection: Lookup method injection
		People guest = createGuest();
		// 这里是为了兼容不适用@Lookup注解时的方法注入
		if (guest == null) {
			guest = new People("ruan_default", 0);
		}
		// 等价于PayloadApplicationEvent<People>(this,guest);
		publisher.publishEvent(guest);

		return message;
	}

	// a.Bean instantiation with a constructor
	// 1.Constructor-based dependency injection(byName with javac -g)
	@ConstructorProperties({ "familyName", "familyCount", "father" })
	public Family(String familyName, int familyCount, @Valid People father) {
		this.familyName = familyName;
		this.familyCount = familyCount;
		this.father = father;
		log.info("Family(String familyName, int familyCount, People father)"
				+ this);
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
	protected People createGuest() {
		log.info("createGuest");
		return null;
	}

	// 3.Method injection: Arbitrary method replacement
	protected int calc(int a, int b) {
		return a + b;
	}

	// 4.Aware injection
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		log.info("setApplicationContext(ApplicationContext applicationContext)"
				+ applicationContext);
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
		log.info("setMessageSource(MessageSource messageSource)"
				+ messageSource);
		if (messageSource == null) {
			messageSource = (MessageSource) context;
		}
		this.messageSource = messageSource;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		log.info("setResourceLoader(ResourceLoader resourceLoader)"
				+ resourceLoader);
		if (resourceLoader == null) {
			resourceLoader = (ResourceLoader) context;
		}
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
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
		this.env = env;
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
		log.info("setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver)"
				+ loadTimeWeaver);
		this.loadTimeWeaver = loadTimeWeaver;
	}

	// Initialization callback
	public void init() {
		log.info("====================init()");
	}

	// Destruction callback
	public void destroy() {
		log.info("====================destroy()");
	}

	@Override
	public String toString() {
		return "Family [familyName=" + familyName + ", familyCount="
				+ familyCount + ", father=" + father + ", mother=" + mother
				+ ", son=" + son + ", daughter=" + daughter + ", guest="
				+ guest + "]";
	}
}