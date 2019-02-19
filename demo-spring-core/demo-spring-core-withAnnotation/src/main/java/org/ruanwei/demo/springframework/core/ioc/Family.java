package org.ruanwei.demo.springframework.core.ioc;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeopleFormat2;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeopleFormat2.Separator;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Lazy
@DependsOn("house")
@Component("family")
public class Family implements BeanNameAware, BeanClassLoaderAware,
		LoadTimeWeaverAware {
	private static Log log = LogFactory.getLog(Family.class);

	// 1.Constructor-based dependency injection
	private String familyName;
	private int familyCount;
	private People father;

	@Valid
	@Qualifier("somebody")
	@Autowired
	private People mother;

	@Value("${son.all}")
	@PeopleFormat2(separator = Separator.SLASH)
	private People son;

	@Value("${daughter.all}")
	@PeopleFormat2(separator = Separator.SLASH)
	private People daughter;

	@Valid
	@Qualifier("somebody")
	@Autowired
	private People guest;

	// 2.Setter-based dependency injection
	@Autowired
	private ApplicationContext context;

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private Environment env;

	// 需要*Aware接口
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
		if (guest == null) {
			guest = new People("ruanwei_def", 18);
		}
		// 等价于PayloadApplicationEvent<People2>(this,guest);
		publisher.publishEvent(guest);

		return message;
	}

	// a.Bean instantiation with a constructor
	// 1.Constructor-based dependency injection(byName with javac -g)
	@Autowired
	public Family(@Value("${family.1.familyName:ruan_def}") String familyName,
			@Value("${family.familyCount:2}") int familyCount,
			@Valid People father) {
		this.familyName = familyName;
		this.familyCount = familyCount;
		this.father = father;
		log.info("Family(String familyName, int familyCount, People father)"
				+ this);
	}

	// 2.Setter-based dependency injection
	@Override
	public void setBeanName(String name) {
		log.info("setBeanName(String name)" + name);
		this.beanName = name;
	}

	@Override
	public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
		log.info("setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver)"
				+ loadTimeWeaver);
		this.loadTimeWeaver = loadTimeWeaver;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		log.info("setBeanClassLoader(ClassLoader classLoader)" + classLoader);
		this.classLoader = classLoader;
	}

	// 3.Method injection: Lookup method injection
	@Lookup("father")
	protected People createGuest() {
		log.info("createGuest");
		return null;
	}

	// 3.Method injection: Arbitrary method replacement
	protected int calc(int a, int b) {
		return a + b;
	}

	// JSR-250.Initialization callback.等价于<bean init-method="init"/>.
	@PostConstruct
	public void init() {
		log.info("====================init()");
	}

	// JSR-250.Destruction callback.等价于<bean destroy-method="destroy"/>.
	@PreDestroy
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