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
import org.ruanwei.demo.springframework.core.ioc.Family;
import org.ruanwei.demo.springframework.core.ioc.FamilyFactory;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeopleFormatterRegistrar;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeoplePropertyEditor;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeoplePropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.config.FieldRetrievingFactoryBean;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.beans.factory.config.PropertyPathFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.annotation.Order;
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
 *  要引用外部化配置，以下两种方式： 
 * <li>通过EnvironmentAware注入Environment，然后获取属性 
 * <li>利用@Bean方法参数的隐式支持@Value和@Autowired(可以替换为@Value("#{myBean}"))
 * 对于方法注入，没有与基于XML的配置元数据相匹配的基于Java的配置元数据
 * 
 * @author ruanwei
 *
 */
@EnableAspectJAutoProxy
// @Profile("development")
@PropertySource("classpath:propertySource-${spring.profiles.active:development_def}.properties")
@PropertySource("classpath:family.properties")
@ComponentScan(basePackages = { "org.ruanwei.demo.springframework" })
// @ImportResource({"classpath:spring/applicationContext.xml"})
@Configuration
public class AppConfig {
	private static Log log = LogFactory.getLog(AppConfig.class);

	@Value("${family.familyCount:2}")
	private int familyCount;

	@Value("#{systemProperties['java.version']?:'1.8'}")
	private String javaVersion;

	@Autowired
	private Environment env;

	// ==========A.The IoC Container==========
	// A.1.Bean Definition and Dependency Injection
	// A.1.1.Bean instantiation with a constructor
	// The 'family' bean is defined in Family component,other Family beans are
	// defined in XML/Java as below.
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
		Family family = FamilyFactory.createInstance1(familyName, familyCount, father);
		family.setMother(mother);
		family.setSon(son);
		family.setDaughter(daughter);
		family.setGuest(guest);
		return family;
	}

	// A.1.3.Bean instantiation using an instance factory method
	@Lazy
	@DependsOn("house")
	@Bean("family3")
	public Family family3(@Value("${family.3.familyName:Ruan3_def}") String familyName, People father, People mother,
			@Value("${son.all}") People son, @Value("${daughter.all}") People daughter, People guest,
			FamilyFactory familyFactory) {
		Family family = familyFactory.createInstance2(familyName, familyCount, father);
		family.setMother(mother);
		family.setSon(son);
		family.setDaughter(daughter);
		family.setGuest(guest);
		return family;
	}

	// The 'father' bean is defined in People component,other People beans are
	// defined in XML/Java as below
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
//		Set<Object> converters = new HashSet<Object>();
//		converters.add(new StringToPeopleConverter());
//		conversionService.setConverters(converters);

		// 方式二：单个指定Formatter/AnnotationFormatterFactory String->T
//		Set<Object> formatters = new HashSet<Object>();
//		formatters.add(new PeopleFormatter());
//		formatters.add(new PeopleFormatAnnotationFormatterFactory());
//		conversionService.setFormatters(formatters);

		// 方式三：分组指定converters和formatters
		Set<FormatterRegistrar> formatterRegistrars = new HashSet<FormatterRegistrar>();
		formatterRegistrars.add(new PeopleFormatterRegistrar());
		formatterRegistrars.add(makeJodaTimeFormatterRegistrar());
		conversionService.setFormatterRegistrars(formatterRegistrars);

		log.info("conversionService==========" + conversionService);
		return conversionService;
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

	private JodaTimeFormatterRegistrar makeJodaTimeFormatterRegistrar() {
		JodaTimeFormatterRegistrar jodaTimeFormatterRegistrar = new JodaTimeFormatterRegistrar();
		DateTimeFormatterFactoryBean dateTimeFormatterFactoryBean = new DateTimeFormatterFactoryBean();
		dateTimeFormatterFactoryBean.setPattern("yyyy-MM-dd");
		jodaTimeFormatterRegistrar.setDateFormatter(dateTimeFormatterFactoryBean.getObject());
		return jodaTimeFormatterRegistrar;
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

	// A.2.3.Validation JSR-303/JSR-349/JSR-380
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

	// A.4.Lifecycle:Initialization/Destruction/Startup/Shutdown callbacks
	// A.4.1.Bean lifecycle callbacks, see @PostConstruct/@PreDestroy
	// A.4.2.Context lifecycle callbacks, see @Smartlifecycle

	// A.5.Environment：Profile and PropertySource
	// A.5.1.PropertySource：供Environment访问。无对应XML配置，参考@PropertySource
	// A.5.2.Profile：-Dspring.profiles.active="development"
	// -Dspring.profiles.default="production"
	// 参考下面的<beans profile="xx">和@Profile

	// A.6.Extension Points
	// A.6.1.Customizing beans using a BeanPostProcessor include:
	// BeanValidationPostProcessor/MethodValidationPostProcessor/AutowiredAnnotationBeanPostProcessor/
	// CommonAnnotationBeanPostProcessor/RequiredAnnotationBeanPostProcessor etc.

	// A.6.2.Customizing configuration metadata with a BeanFactoryPostProcessor
	// include:
	// PropertyOverrideConfigurer/
	// PropertyPlaceholderConfigurer/PropertySourcesPlaceholderConfigurer/PreferencesPlaceholderConfigurer/
	// CustomEditorConfigurer/CustomScopeConfigurer/CustomAutowireConfigurer etc.

	// PropertySourcesPlaceholderConfigurer通过将@PropertySource加入到属性，以替换@Value中的占位符，因此此处可以不用指定
	@Order(0)
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		propertySourcesPlaceholderConfigurer.setFileEncoding("UTF-8");
		propertySourcesPlaceholderConfigurer.setLocations(new ClassPathResource("family.properties"),
				new ClassPathResource("jdbc.properties"));
		log.info("propertySourcesPlaceholderConfigurer==========" + propertySourcesPlaceholderConfigurer);
		return propertySourcesPlaceholderConfigurer;
	}

	// A.6.3.Customizing instantiation logic with a FactoryBean include:
	// ListFactoryBean/SetFactoryBean/PropertiesFactoryBean/MapFactoryBean
	// FieldRetrievingFactoryBean/PropertyPathFactoryBean/MethodInvokingFactoryBean
	// LocalValidatorFactoryBean/FormattingConversionServiceFactoryBean/Jackson2ObjectMapperFactoryBean
	// FreeMarkerConfigurationFactoryBean/ContentNegotiationManagerFactoryBean/ProxyFactoryBean
	// etc.

	// A.7 Switches on the load-time weaving

	// ==========B.AOP and Instrumentation==========
}
