package org.ruanwei.demo.springframework;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.HibernateValidator;
import org.ruanwei.demo.springframework.core.aop.AOP;
import org.ruanwei.demo.springframework.core.aop.GoodImpl;
import org.ruanwei.demo.springframework.core.aop.MyAspect;
import org.ruanwei.demo.springframework.core.ioc.Family;
import org.ruanwei.demo.springframework.core.ioc.FamilyFactory;
import org.ruanwei.demo.springframework.core.ioc.House;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeopleFormatAnnotationFormatterFactory;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeopleFormatter;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeopleFormatterRegistrar;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeoplePropertyEditor;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeoplePropertyEditorRegistrar;
import org.ruanwei.demo.springframework.core.ioc.databinding.StringToPeopleConverter;
import org.ruanwei.demo.springframework.core.ioc.extension.MyFamilyFactoryBean;
import org.ruanwei.demo.springframework.core.ioc.extension.TraceBeanFactoryPostProcessor;
import org.ruanwei.demo.springframework.core.ioc.extension.TraceBeanPostProcessor;
import org.ruanwei.demo.springframework.core.ioc.lifecycle.MyDisposableBean;
import org.ruanwei.demo.springframework.core.ioc.lifecycle.MyInitializingBean;
import org.ruanwei.demo.springframework.core.ioc.lifecycle.MyLifecycle;
import org.ruanwei.demo.springframework.core.ioc.lifecycle.MyLifecycleProcessor;
import org.ruanwei.demo.springframework.core.ioc.lifecycle.MySmartLifecycle;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.config.FieldRetrievingFactoryBean;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.beans.factory.config.PropertyPathFactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.datetime.joda.DateTimeFormatterFactoryBean;
import org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.beanvalidation.BeanValidationPostProcessor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * The 'family', 'father' and 'house' bean are defined as @Component,other beans are defined in XML/Java.
 * 
 * 如果没有开启注解，以下三种方式均无法注入依赖到AppConfig： 
 * <li>@Value(${placeholder}).
 * <li>@Value(#{SpEL). 
 * <li>@Autowired/@Qualifier.
 * 测试类中式默认支持注解的.
 * 
 * 如果没有开启注解配置，对于方法注入、方法替换和AOP，没有与基于XML的配置元数据等价的基于Java的配置元数据。
 * 可以通过@ImportResource来加载相应的XML配置元数据来解决这个问题
 * 
 * 注意：@Bean方法参数隐式支持@Value和@Autowired
 * 
 * 要引用外部化配置，可以使用以下两种方式： 
 * <li>通过@PropertySource/@TestPropertySource指定PropertySource,注入Environment，然后通过其获取属性
 * <li>通过PropertyPlaceholderConfigurer+@PropertySource/setLocations指定占位符，然后通过@Value获取属性
 * 
 * @author ruanwei
 *
 */
@Profile("development")
@ImportResource({ "classpath:spring/applicationContext.xml" })
@PropertySource("classpath:propertySource-${spring.profiles.active:development_def}.properties")
@Configuration
public class AppConfig implements EnvironmentAware, InitializingBean {
	private static Log log = LogFactory.getLog(AppConfig.class);

	private Environment env;

	private int familyCount;
	private String javaVersion;

	@Override
	public void setEnvironment(Environment environment) {
		this.env = environment;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("afterPropertiesSet()======" + env);
		familyCount = Integer.parseInt(env.getProperty("family.familyCount", "2"));
		javaVersion = env.getProperty("java.version", "1.8");
	}

	// ==========A.Core:IoC==========
	// A.1.Bean Definition and Dependency Injection
	// A.1.1.Bean instantiation with a constructor
	@Lazy
	@DependsOn("house")
	@Bean(name = "family", initMethod = "init", destroyMethod = "destroy2")
	public Family family(@Value("${family.familyName:Ruan_def}") String familyName, People father, People mother,
			@Value("${son.all}") People son, @Value("${daughter.all}") People daughter, People guest) {
		// 1.Constructor-based dependency injection
		Family family = new Family(familyName, familyCount, father);
		// 2.Setter-based dependency injection
		family.setMother(mother);
		family.setSon(son);
		family.setDaughter(daughter);
		// Proxied scoped beans as dependencies
		family.setGuest(guest);
		return family;
	}

	@Lazy
	@DependsOn("house")
	@Bean(name = "family1", initMethod = "init", destroyMethod = "destroy2")
	public Family family1(@Value("${family.1.familyName:Ruan_def}") String familyName, People father, People mother,
			@Value("${son.all}") People son, @Value("${daughter.all}") People daughter, People guest) {
		// 1.Constructor-based dependency injection
		Family family = new Family(familyName, familyCount, father);
		// 2.Setter-based dependency injection
		family.setMother(mother);
		family.setSon(son);
		family.setDaughter(daughter);
		// Proxied scoped beans as dependencies
		family.setGuest(guest);
		return family;
	}

	// A.1.2.Bean instantiation with a static factory method
	@Lazy
	@DependsOn("house")
	@Bean("family2")
	public Family family2(@Value("${family.2.familyName:Ruan2_def}") String familyName, People father, People mother,
			@Value("${son.all}") People son, @Value("${daughter.all}") People daughter, People guest) {
		// 1.Constructor-based dependency injection
		Family family = FamilyFactory.createInstance1(familyName, familyCount, father);
		// 2.Setter-based dependency injection
		family.setMother(mother);
		family.setSon(son);
		family.setDaughter(daughter);
		// Proxied scoped beans as dependencies
		family.setGuest(guest);
		return family;
	}

	// A.1.3.Bean instantiation using an instance factory method
	@Lazy
	@DependsOn("house")
	@Bean("family3")
	public Family family3(@Value("${family.3.familyName:Ruan3_def}") String familyName, People father, People mother,
			@Value("${son.all}") People son, @Value("${daughter.all}") People daughter, People guest) {
		// 1.Constructor-based dependency injection
		Family family = familyFactory().createInstance2(familyName, familyCount, father);
		// 2.Setter-based dependency injection
		family.setMother(mother);
		family.setSon(son);
		family.setDaughter(daughter);
		// Proxied scoped beans as dependencies
		family.setGuest(guest);
		return family;
	}

	@Lazy
	@Bean("familyFactory")
	public FamilyFactory familyFactory() {
		return new FamilyFactory();
	}

	@Lazy
	@Bean("father")
	public People father(@Value("${father.name:ruanwei_def}") String name, @Value("${father.age:88}") int age) {
		return new People(name, age);
	}

	@Lazy
	@Bean("mother")
	public People mother(@Value("${mother.name:lixiaoling_def}") String name, @Value("${mother.age:88}") int age) {
		return new People(name, age);
	}

	@Lazy
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Bean("guest")
	public People guest(@Value("${guest.name:ruan_def}") String name,
			@Value("#{(new java.util.Random()).nextInt(100) ?: 8}") int age) {
		return new People(name, age);
	}

	@Bean("absHouse")
	public House absHouse(@Value("${house.name.abstract:AbsHouse_def}") String houseName,
			@Value("1,2") Integer[] someArray, @Value("3,4") List<Integer> someList, @Value("5,6") Set<Integer> someSet,
			@Value("a=7,b=8") Properties someProperties, @Value("#{{c:9,d:10}}") Map<String, Integer> someMap,
			@Value("#{someField1}") double someField1, @Value("#{someField2}") String someField2,
			@Value("#{someField3}") String someField3) {
		House abshouse = new House();

		abshouse.setHouseName(houseName);

		abshouse.setSomeArray(someArray);
		abshouse.setSomeList(someList);
		abshouse.setSomeSet(someSet);
		abshouse.setSomeProperties(someProperties);
		// abshouse.setSomeMap(someMap);

		abshouse.setSomeList2(someList2());
		abshouse.setSomeSet2(someSet2());
		abshouse.setSomeProperties2(someProperties2());
		abshouse.setSomeMap2(someMap2());

		// 这里注入的是FactoryBean，无法使用someField1()直接引用
		abshouse.setSomeField1(someField1);
		abshouse.setSomeField2(someField2);
		abshouse.setSomeField3(someField3);

		return abshouse;
	}

	@Lazy
	@Description("result in a List bean,see ListFactoryBean")
	@Bean("someList2")
	public List<Integer> someList2() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(3);
		list.add(4);
		return list;
	}

	@Lazy
	@Description("result in a Set bean,see SetFactoryBean")
	@Bean("someSet2")
	public Set<Integer> someSet2() {
		Set<Integer> set = new HashSet<Integer>();
		set.add(5);
		set.add(6);
		return set;
	}

	@Lazy
	@Description("result in a Properties bean,see PropertiesFactoryBean")
	@Bean("someProperties2")
	public Properties someProperties2() {
		Properties props = new Properties();
		props.put("a", 7);
		props.put("b", 8);
		return props;
	}

	@Lazy
	@Description("result in a Map bean,see MapFactoryBean")
	@Bean("someMap2")
	public Map<String, Integer> someMap2() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("c", 9);
		map.put("d", 10);
		return map;
	}

	@Bean("someField1")
	public FieldRetrievingFactoryBean someField1() {
		FieldRetrievingFactoryBean fieldRetrievingFactoryBean = new FieldRetrievingFactoryBean();
		fieldRetrievingFactoryBean.setStaticField("java.lang.Math.PI");
		return fieldRetrievingFactoryBean;
	}

	@Bean("someField2")
	public PropertyPathFactoryBean someField2() {
		PropertyPathFactoryBean propertyPathFactoryBean = new PropertyPathFactoryBean();
		propertyPathFactoryBean.setTargetBeanName("father");
		propertyPathFactoryBean.setPropertyPath("name");
		return propertyPathFactoryBean;
	}

	@Bean("someField3")
	public MethodInvokingFactoryBean someField3() {
		MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
		try {
			methodInvokingFactoryBean.setTargetObject(sysProps().getObject());
		} catch (Exception e) {
			log.error("", e);
		}
		methodInvokingFactoryBean.setTargetMethod("getProperty");
		methodInvokingFactoryBean.setArguments("java.version");
		return methodInvokingFactoryBean;
	}

	@Bean("sysProps")
	public MethodInvokingFactoryBean sysProps() {
		MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
		methodInvokingFactoryBean.setTargetClass(System.class);
		methodInvokingFactoryBean.setTargetMethod("getProperties");
		return methodInvokingFactoryBean;
	}

	// A.2.Data Binding
	// A.2.1.ConversionService-based Type Conversion and Formatting
	@Bean("conversionService")
	public FormattingConversionServiceFactoryBean conversionService() {
		FormattingConversionServiceFactoryBean conversionService = new FormattingConversionServiceFactoryBean();
		conversionService.setRegisterDefaultFormatters(true);

		// 方式一：单个指定Converter/ConverterFactory/GenericConverter S->T
		Set<Object> converters = new HashSet<Object>();
		converters.add(new StringToPeopleConverter());
		conversionService.setConverters(converters);

		// 方式二：单个指定Formatter/AnnotationFormatterFactory String->T
		Set<Object> formatters = new HashSet<Object>();
		formatters.add(new PeopleFormatter());
		formatters.add(new PeopleFormatAnnotationFormatterFactory());
		conversionService.setFormatters(formatters);

		// 方式三：分组指定converters和formatters
		Set<FormatterRegistrar> formatterRegistrars = new HashSet<FormatterRegistrar>();
		formatterRegistrars.add(new PeopleFormatterRegistrar());
		formatterRegistrars.add(makeJodaTimeFormatterRegistrar());
		conversionService.setFormatterRegistrars(formatterRegistrars);

		log.info("conversionService==========" + conversionService);
		return conversionService;
	}

	private JodaTimeFormatterRegistrar makeJodaTimeFormatterRegistrar() {
		JodaTimeFormatterRegistrar jodaTimeFormatterRegistrar = new JodaTimeFormatterRegistrar();
		DateTimeFormatterFactoryBean dateTimeFormatterFactoryBean = new DateTimeFormatterFactoryBean();
		dateTimeFormatterFactoryBean.setPattern("yyyy-MM-dd");
		jodaTimeFormatterRegistrar.setDateFormatter(dateTimeFormatterFactoryBean.getObject());
		return jodaTimeFormatterRegistrar;
	}

	// A.2.2.PropertyEditor-based Conversion
	// @Bean
	public static CustomEditorConfigurer customEditorConfigurer() {
		CustomEditorConfigurer customEditorConfigurer = new CustomEditorConfigurer();

		// 方式四：单个指定PropertyEditor
		Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new HashMap<Class<?>, Class<? extends PropertyEditor>>();
		customEditors.put(People.class, PeoplePropertyEditor.class);
		customEditorConfigurer.setCustomEditors(customEditors);

		// 方式五：分组指定PropertyEditor
		PropertyEditorRegistrar[] propertyEditorRegistrars = new PeoplePropertyEditorRegistrar[] {
				new PeoplePropertyEditorRegistrar() };
		customEditorConfigurer.setPropertyEditorRegistrars(propertyEditorRegistrars);

		log.info("customEditorConfigurer==========" + customEditorConfigurer);
		return customEditorConfigurer;
	}

	// A.2.3.Validation JSR-303/JSR-349/JSR-380
	// JSR-303:Bean Validation 1.0, see ValidationUtils in share-commons.jar
	@Bean
	public BeanValidationPostProcessor beanValidationPostProcessor() {
		BeanValidationPostProcessor beanValidationPostProcessor = new BeanValidationPostProcessor();
		beanValidationPostProcessor.setValidator(validator());
		log.info("beanValidationPostProcessor==========" + beanValidationPostProcessor);
		return beanValidationPostProcessor;
	}

	// JSR-349:Bean Validation 1.1, see @Validated
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
		methodValidationPostProcessor.setValidator(validator());
		methodValidationPostProcessor.setOrder(0);
		log.info("methodValidationPostProcessor==========" + methodValidationPostProcessor);
		return methodValidationPostProcessor;
	}

	@Bean("validator")
	public LocalValidatorFactoryBean validator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setProviderClass(HibernateValidator.class);
		validator.setValidationMessageSource(messageSource());
		log.info("validator==========" + validator);
		return validator;
	}

	// building message codes from validation error codes,used by DataBinder
	@Bean
	public MessageCodesResolver messageCodesResolver() {
		return new DefaultMessageCodesResolver();
	}

	// A.3.Internationalization:MessageSource/ResourceBundleMessageSource
	@Bean("messageSource")
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("exception");
		messageSource.setCacheSeconds(10);
		messageSource.setDefaultEncoding("utf-8");
		log.info("messageSource==========" + messageSource);
		return messageSource;
	}

	// A.4.Lifecycle: Bean and Context callbacks
	// A.4.1.Bean lifecycle, see
	// InitializingBean/DisposableBean/@PostConstruct/@PreDestroy
	@Bean
	public MyInitializingBean myInitializingBean() {
		return new MyInitializingBean();
	}

	@Bean
	public MyDisposableBean myDisposableBean() {
		return new MyDisposableBean();
	}

	// A.4.2.Context lifecycle, see SmartLifecycle/@Order/PriorityOrdered/Ordered
	@Bean
	public MyLifecycle myLifecycle() {
		return new MyLifecycle();
	}

	@Bean
	public MySmartLifecycle mySmartLifecycle() {
		return new MySmartLifecycle();
	}

	@Bean
	public MyLifecycleProcessor myLifecycleProcessor() {
		return new MyLifecycleProcessor();
	}

	// A.5.Environment：Profile and PropertySource
	// A.5.1.PropertySource：供Environment访问。无对应XML配置，参考@PropertySource
	// A.5.2.Profile：参考下面的<beans profile="xx">和@Profile
	// -Dspring.profiles.active="development" -Dspring.profiles.default="production"
	@Profile("development")
	@Bean("house")
	public House house1(@Value("${house.name.override:RuanHouse_def}") String houseName,
			@Value("${house.host.name.development:developmentHost_def}") String hostName,
			@Value("#{absHouse}") House abshouse) {
		abshouse.setHouseName(houseName);
		abshouse.setHostName(hostName);
		return abshouse;
	}

	@Profile("production")
	@Bean("house")
	public House house2(@Value("${house.name.override:RuanHouse_def}") String houseName,
			@Value("${house.host.name.production:productionHost_def}") String hostName,
			@Value("#{absHouse}") House abshouse) {
		abshouse.setHouseName(houseName);
		abshouse.setHostName(hostName);
		return abshouse;
	}

	// A.6.Extension Points
	// PriorityOrdered/Ordered/@Order
	// A.6.1.Customizing beans using a BeanPostProcessor include:
	// BeanValidationPostProcessor/MethodValidationPostProcessor/AutowiredAnnotationBeanPostProcessor/
	// CommonAnnotationBeanPostProcessor/RequiredAnnotationBeanPostProcessor etc.
	@Bean
	public TraceBeanPostProcessor traceBeanPostProcessor() {
		TraceBeanPostProcessor traceBeanPostProcessor = new TraceBeanPostProcessor();
		traceBeanPostProcessor.setOrder(0);
		return traceBeanPostProcessor;
	}

	// A.6.2.Customizing configuration metadata with a BeanFactoryPostProcessor
	// include:
	// PropertyOverrideConfigurer/
	// PropertyPlaceholderConfigurer/PropertySourcesPlaceholderConfigurer/PreferencesPlaceholderConfigurer/
	// CustomEditorConfigurer/CustomScopeConfigurer/CustomAutowireConfigurer etc.
	// 注意，由于生命周期的原因，返回BeanFactoryPostProcessor的@Bean方法一定要声明为static，否则无法处理@Autowired、@Value、@PostConstruct等注解.
	// Static @Bean methods will not be enhanced for scoping and AOP semantics.
	// A WARN-level log message will be issued for any non-static @Bean methods
	// having a return type assignable to BeanFactoryPostProcessor.
	@Bean
	public static TraceBeanFactoryPostProcessor traceBeanFactoryPostProcessor() {
		TraceBeanFactoryPostProcessor traceBeanFactoryPostProcessor = new TraceBeanFactoryPostProcessor();
		traceBeanFactoryPostProcessor.setOrder(0);
		return traceBeanFactoryPostProcessor;
	}

	// PropertySourcesPlaceholderConfigurer通过将@PropertySource加入到占位符，以替换@Value和XML中的占位符
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		propertySourcesPlaceholderConfigurer.setLocations(new ClassPathResource("family.properties"),
				new ClassPathResource("jdbc.properties"));
		// propertySourcesPlaceholderConfigurer.setProperties(properties);
		propertySourcesPlaceholderConfigurer.setFileEncoding("UTF-8");
		propertySourcesPlaceholderConfigurer.setOrder(0);
		log.info("propertySourcesPlaceholderConfigurer==========" + propertySourcesPlaceholderConfigurer);
		return propertySourcesPlaceholderConfigurer;
	}

	// A.6.3.Customizing instantiation logic with a FactoryBean
	@Bean("familyx")
	public MyFamilyFactoryBean myFamilyFactoryBean(@Value("${family.x.familyName:RuanX_def}") String familyName,
			@Value("${family.familyCount:2}") int familyCount, @Qualifier("father") People father) {
		MyFamilyFactoryBean myFamilyFactoryBean = new MyFamilyFactoryBean(familyName, familyCount, father);
		return myFamilyFactoryBean;
	}

	// A.Core:AOP and Instrumentation
	@Bean("myAspect")
	public MyAspect myAspect() {
		return new MyAspect();
	}

	@Bean("aop")
	public AOP aop() {
		return new AOP();
	}

	@Bean("good")
	public GoodImpl good() {
		return new GoodImpl();
	}
}
