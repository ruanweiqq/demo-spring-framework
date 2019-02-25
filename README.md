# demo-spring-framework

### demo-spring-framework项目包含如下模块：
- demo-spring-framework-dependencies模块：依赖声明。
- demo-spring-framework-parent模块：所有项目的父模块，包含构建参数。
- demo-springframework-starters模块：父模块为demo-springframework-parent。
- demo-spring-framework-commons模块：父模块为demo-springframework-parent。
- demo-springframework-core模块：父模块为demo-springframework-starter-parent。
- demo-springframework-dataAccess模块：父模块为demo-springframework-starter-parent。
- demo-springframework-web模块：父模块为demo-springframework-starter-parent。
- demo-springframework-integration模块：父模块为demo-springframework-starter-parent。
- demo-spring-framework-withoutAnnotation模块：父模块为demo-springframework-starter-parent。未开启基于注解的配置元数据。
- demo-springframework-temp模块：父模块为demo-springframework-starter-parent。临时项目，准备删除。

每个项目均同时支持基于XML的和Java的配置元数据。除第一个项目外，均开启了基于注解的配置元数据，即：

- < context:annotation-config/> 或 @Bean xxxBeanPostProcessor
- < aop:aspectj-autoproxy/> 或 @EnableAspectJAutoProxy
- < tx:annotation-driven transaction-manager="txManager"/> 或 @EnableTransactionManagement

### 三种配置元数据对比：
基于XML的配置元数据  | 基于Java的配置元数据 | 基于注解的配置元数据
------------- | ------------- | -------------
< beans>  | @Configuration | Content Cell
< beans profile="dev"/>  | @Profile("dev") | 
< beans default-lazy-init="true"/>  | none | 
< beans default-autowire="byName"/>  | none | 
< beans default-autowire-candidates="*Service"/>  | none | 
< import resource="classpath:dataAccess.xml">  | @Import(DadaAccessConfig.class) | 
< bean class="DadaAccessConfig.class">  | @ImportResource("classpath:dataAccess.xml") |
ctx.getEnvironment().getPropertySources().addFirst(new ResourcePropertySource("ps.properties"))  | @PropertySource("classpath:ps.properties") |
< bean id="myBean" class="org.ruanwei.MyBean"> | @Bean("myBean") | @Component("myBean")<br/>JSR-250:@ManagedBean("myBean")
none  | @Description("this is a bean") |
< bean lazy-init="true"> | @Lazy |
< bean depends-on="anotherBean"> | @DependsOn("anotherBean") |
< bean scope="singleton"> | @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)<br/>JSR-330:@Singleton/@Scope |
< bean init-method="init"><br/>InitializingBean | @Bean(initMethod="init") | JSR-250:@PostConstruct
< bean destroy-method="destroy"><br/>DisposableBean  | @Bean(destroyMethod="destroy") | JSR-250:@PreDestroy
< bean p:order="1"><br/>PriorityOrdered/Ordered | @Order(1)/JSR-250:@Priority(1) |
< bean autowire="byType"> | @Bean(autowire=Autowire.BY_TYPE) |
< bean primary="true"> | @Primary |
< bean autowire-candidate="false"> | none |
< bean><br/>< qualifier value="primaryBean"/><br/>< /bean> | @Qualifier("primaryBean")<br/>JSR-330:@Named("primaryBean")<br/>@Qualifier |
< bean><br/>< lookup-method name="createCommand" bean="myCommand"/><br/>< /bean> | none | @Lookup
< bean><br/>< replaced-method name="computeValue" replacer="replacementComputeValue"/><br/>< /bean>  | none |
< context:load-time-weaver/> | @EnableLoadTimeWeaving |
< context:spring-configured/> | @EnableSpringConfigured |
< aop:scoped-proxy proxy-target-class="true"/> | @Scope(proxyMode=ScopedProxyMode.TARGET_CLASS) |
none  | none | @Required
none  | none | @Autowired(required="true")<br/>JSR-250:@Resource("myBean")<br/>JSR-330:@Inject
none  | none | @Value("${placeholder}")/@Value("#{SpEL}")
org.springframework.context.event.EventListener  | none | @EventListener
none  | none | @PersistenceContext
分隔线 | 分隔线 | 分隔线
< context:annotation-config/> | @Bean xxxBeanPostProcessor | 开启基于注解的配置元数据
< context:component-scan base-package="org.ruanwei" scoped-proxy="class"/> | @ComponentScan(basePackages="org.ruanwei",scopedProxy=ScopedProxyMode.CLASS) |
分隔线 | 分隔线 | 分隔线
< aop:aspectj-autoproxy/>  | @EnableAspectJAutoProxy | 开启基于@AspectJ风格的AOP配置
基于schema风格的AOP配置  | 基于Java的配置元数据 | 基于@AspectJ风格的AOP配置
< aop:config><br/>< aop:aspect ref="myAspect"><br/>< /aop:config> | none | @AspectJ
< aop:aspect><br/>< aop:pointcut id="myPointcut" expression="execution(* transfer(..))"/><br/>< /aop:aspect> | none | @Pointcut("execution(* transfer(..))")
< aop:aspect><br/>< aop:before pointcut-ref="myPointcut" method="myAdviceMethod"/><br/>< /aop:aspect> | none | @Before("org.ruanwei.SystemArchitecture.myPointcut()")
< aop:aspect><br/>< aop:declare-parents types-matching="org.ruanwei.*A*" implement-interface="org.ruanwei.B" default-impl="org.ruanwei.BImpl"/><br/>< /aop:aspect> | none | @DeclareParents(value="org.ruanwei.*A*",defaultImpl=BImpl.class)
分隔线 | 分隔线 | 分隔线
< tx:annotation-driven transaction-manager="txManager"/> | @EnableTransactionManagement | 开启基于@Transactional注解的事务声明配置
基于XML的事务声明配置 | 基于Java的配置元数据 | 基于@Transactional注解的事务声明配置
< tx:advice id="txAdvice" transaction-manager="txManager"><br/>< tx:attributes><br/>< tx:method name="get*"/><br/>< /tx:attributes><br/>< /tx:advice>  | none | @Transactional
< aop:config><br/>< aop:pointcut id="myPointcut" expression="execution(* transfer(..))"/><br/>< aop:advisor advice-ref="txAdvice" pointcut-ref="myPointcut"/><br/>< /aop:config>  | none |
分隔线 | 分隔线 | 分隔线

<p>注意：基于XML的配置元数据需要使用&lt;context:annotation-config/>开启@Configuration注解支持.
<p>注意：对于AOP配置，没有与基于XML相对应的基于Java的配置元数据.
<p>注意：对于事务配置，没有与基于XML相对应的基于Java的配置元数据.

# 项目结构
<pre>
spring-boot
	-spring-boot-build:pom
	spring-boot-project
		-spring-boot-project:pom  spring-boot-build:pom
		spring-boot-dependencies
			-spring-boot-dependencies:pom  spring-boot-build:pom
		spring-boot-parent
			-spring-boot-parent:pom  spring-boot-dependencies:pom
		spring-boot-starters
		    -spring-boot-starters:pom  spring-boot-parent:pom
		    spring-boot-starter-parent
		        -spring-boot-starter-parent:pom  spring-boot-dependencies:pom
		    spring-boot-starter
		    	 -spring-boot-starter:jar  spring-boot-starters:pom
		    spring-boot-starter-web
			 -spring-boot-starter-web:jar  spring-boot-starters:pom
	            spring-boot-starter-webflux
	                 -spring-boot-starter-webflux:jar  spring-boot-starters:pom
		spring-boot
		    -spring-boot:jar  spring-boot-parent
		spring-boot-actuator
		    -spring-boot-actuator:jar  spring-boot-parent
		spring-boot-autoconfigure
		spring-boot-devtools		
		spring-boot-cli
	spring-boot-tests
		pom.xml    spring-boot-tests:pom  spring-boot-parent
		spring-boot-integration-tests
			pom.xml    spring-boot-deployment-tests:pom  spring-boot-tests
			spring-boot-deployment-test-glassfish
				pom.xml spring-boot-deployment-test-tomcat:war  spring-boot-deployment-tests
			spring-boot-deployment-test-tomcat
		spring-boot-deployment-tests
	spring-boot-samples
		pom.xml    spring-boot-samples:pom  spring-boot-starter-parent
------------------------------------------
demo-spring-framework
	-demo-spring-framework-build:pom
	demo-spring-framework-dependencies
	    -demo-spring-framework-dependencies:pom  demo-spring-framework-build:pom
	demo-spring-framework-parent
	    -demo-spring-framework-parent:pom  demo-spring-framework-dependencies:pom
	demo-spring-framework-starters
	    -demo-spring-framework-starters:pom  demo-spring-framework-parent:pom
	    demo-spring-framework-starter-parent
	        -demo-spring-framework-starter-parent:pom  demo-spring-framework-dependencies:pom
	    demo-spring-framework-starter
		-demo-spring-framework-starter:pom  demo-spring-framework-starters:pom
	    demo-spring-framework-starter-core
		-demo-spring-framework-starter-core:jar demo-spring-framework-starters:pom
	    demo-spring-framework-starter-dataAccess
		-demo-spring-framework-starter-dataAccess:jar demo-spring-framework-starters:pom
	    demo-spring-framework-starter-web
	        -demo-spring-framework-starter-web:jar  demo-spring-framework-starters:pom
	    demo-spring-framework-starter-integration
		-demo-spring-framework-starter-integration:jar demo-spring-framework-starters:pom
	demo-spring-framework-commons
	    -demo-spring-framework-commons  demo-spring-framework-parent:pom
	demo-spring-framework-core
	    -demo-spring-framework-core:pom  demo-spring-framework-starter-parent:pom
	demo-spring-framework-web
	    -demo-spring-framework-web:pom  demo-spring-framework-starter-parent:pom
	demo-spring-framework-dataAccess
	    -demo-spring-framework-dataAccess:pom  demo-spring-framework-starter-parent:pom
	demo-spring-framework-integration
	    -demo-spring-framework-integration:pom  demo-spring-framework-starter-parent:pom
	demo-spring-framework-withoutAnnotation
	    -demo-spring-framework-withoutAnnotation:pom  demo-spring-framework-starter-parent:pom
------------------------------------------
</pre>

### 运行方式：
cd demo-spring-framework  
cd demo-spring-framework-parent  
mvn clean install  
cd ../demo-spring-framework-commons  
mvn clean install  
cd ../demo-springframework-starters  
mvn clean install  
cd ../demo-springframework-core  
mvn clean test


### TODO:
1. 补充@Valid支持分组验证
2. 补充@Valid支持bean validation2.0
3. 确认@Valid/@Validated注解是否是自动校验还是需要BeanValidationPostProcessor；
4. 确认@Format注解是否独立发挥了作用而不依赖Converter；
5. Spring Data Jdbc的分页和排序失败；
6. Spring JPA和Hibernate及其事务梳理；
7. 补充在Spring中使用AspectJ；
8. 解析自定义配置xml标签
9. demo-springframework-temp为临时项目，准备删除yayaaaaaa
