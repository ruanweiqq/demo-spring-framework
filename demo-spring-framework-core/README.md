# demo-springframework

### demo-springframework项目共包含两个模块：
- demo-springframework-withoutAnnotation模块为纯基于XML/Java的配置元数据的项目，未开启基于注解的配置元数据。
- demo-springframework-withAnnotation模块为基于XML/Java的配置元数据的项目，并开启基于注解的配置元数据(<context:annotation-config/>)。

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
tx:annotation-driven transaction-manager="txManager"/> | @EnableTransactionManagement | 开启基于@Transactional注解的事务声明配置
基于XML的事务声明配置 | 基于Java的配置元数据 | 基于@Transactional注解的事务声明配置
< tx:advice id="txAdvice" transaction-manager="txManager"><br/>< tx:attributes><br/>< tx:method name="get*"/><br/>< /tx:attributes><br/>< /tx:advice>  | none | @Transactional
< aop:config><br/>< aop:pointcut id="myPointcut" expression="execution(* transfer(..))"/><br/>< aop:advisor advice-ref="txAdvice" pointcut-ref="myPointcut"/><br/>< /aop:config>  | none |
分隔线 | 分隔线 | 分隔线

### 基于XML的和基于Java的配置元数据主要对比：
- &lt;beans> vs @Configuration.
- &lt;beans profile="dev"/> vs @Profile("dev").
- &lt;beans default-lazy-init="true"/> vs nothing.
- &lt;beans default-autowire="byName"/> vs nothing.
- &lt;beans default-autowire-candidates="*Service"/> vs nothing.
- &lt;import resource="classpath:dataAccess.xml"> vs @Import(DadaAccessConfig.class).
- &lt;bean class="DadaAccessConfig.class"> vs @ImportResource("classpath:dataAccess.xml").
- ctx.getEnvironment().getPropertySources().addFirst(new ResourcePropertySource("ps.properties")) vs @PropertySource("classpath:ps.properties").
- &lt;bean id="myBean" class="org.ruanwei.MyBean"> vs @Bean("myBean").
- nothing vs @Description("this is a bean").
- &lt;bean lazy-init-"true"> vs @Lazy.
- &lt;bean depends-on-"anotherBean"> vs @DependsOn("anotherBean").
- &lt;bean scope="singleton"> vs @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) vs JSR-330:@Singleton/@Scope.
- &lt;bean init-method="init"> vs InitializingBean vs @Bean(initMethod="init").
- &lt;bean destroy-method="destroy"> vs DisposableBean vs @Bean(destroyMethod="destroy").
- &lt;bean p:order="1">(PriorityOrdered/Ordered) vs @Order(1) vs JSR-250:@Priority(1).
- &lt;bean autowire="byType"> vs @Bean(autowire=Autowire.BY_TYPE).
- &lt;bean primary="true"> vs @Primary.
- &lt;bean autowire-candidate="false"> vs nothing.
- &lt;bean>&lt;qualifier value="primaryBean"/>&lt;/bean> vs @Qualifier("primaryBean") vs JSR-330:@Named("primaryBean")/@Qualifier.
- &lt;bean>&lt;lookup-method name="createCommand" bean="myCommand"/>&lt;/bean> vs nothing.
- &lt;bean>&lt;replaced-method name="computeValue" replacer="replacementComputeValue"/>&lt;/bean> vs nothing.
- &lt;context:load-time-weaver/> vs @EnableLoadTimeWeaving.
- &lt;context:spring-configured/> vs @EnableSpringConfigured.
- &lt;aop:scoped-proxy proxy-target-class="true"/> vs @Scope(proxyMode=ScopedProxyMode.TARGET_CLASS).
<p>注意：基于XML的配置元数据需要使用&lt;context:annotation-config/>开启@Configuration注解支持.

### 开启基于注解的配置元数据：
- &lt;context:annotation-config/> vs @Bean xxxBeanPostProcessor.
- &lt;context:component-scan base-package="org.ruanwei" scoped-proxy="class"/> vs @ComponentScan(basePackages="org.ruanwei",scopedProxy=ScopedProxyMode.CLASS).

#### 基于注解和基于非注解的配置元数据主要对比：
- @Component("myBean")/JSR-250:@ManagedBean("myBean") vs @Bean("myBean").
- @Required vs nothing.
- @Autowired(required="true")/JSR-250:@Resource("myBean")/JSR-330:@Inject vs nothing.
- @Value("${placeholder}")/@Value("#{SpEL}") vs nothing.
- JSR-250:@PostConstruct vs @Bean(init-method="init").
- JSR-250:@PreDestroy vs @Bean(destroy-method="destroy").
- @EventListener vs org.springframework.context.event.EventListener.
- @Lookup vs &lt;lookup-method name="createCommand" bean="myCommand"/>.
- @PersistenceContext vs .

### 开启基于@AspectJ风格的AOP配置：
- &lt;aop:aspectj-autoproxy/> vs @EnableAspectJAutoProxy.

#### 基于@AspectJ风格的和基于schema风格的AOP配置主要对比：
- @AspectJ vs &lt;aop:config>&lt;aop:aspect ref="myAspect">&lt;/aop:config>
- @Pointcut("execution(* transfer(..))") vs &lt;aop:aspect><aop:pointcut id="myPointcut" expression="execution(* transfer(..))"/>&lt;/aop:aspect>
- @Before("org.ruanwei.SystemArchitecture.myPointcut()") vs &lt;aop:aspect>&lt;aop:before pointcut-ref="myPointcut" method="myAdviceMethod"/>
- @DeclareParents(value="org.ruanwei.*A*",defaultImpl=BImpl.class) vs &lt;aop:aspect>&lt;aop:declare-parents types-matching="org.ruanwei.*A*" implement-interface="org.ruanwei.B" default-impl="org.ruanwei.BImpl"/>
<p>注意：对于AOP配置，没有与基于XML的配置元数据相匹配的基于Java的配置元数据.

### 开启基于@Transactional注解的事务声明配置：
- &lt;tx:annotation-driven transaction-manager="txManager"/> vs @EnableTransactionManagement

#### 基于@Transactional注解的和基于XML的事务声明配置主要对比：
- @Transactional vs &lt;tx:advice id="txAdvice" transaction-manager="txManager">&lt;tx:attributes>&lt;tx:method name="get*"/>&lt;/tx:attributes>&lt;/tx:advice>
- &lt;aop:config>&lt;aop:pointcut id="myPointcut" expression="execution(* transfer(..))"/>&lt;aop:advisor advice-ref="txAdvice" pointcut-ref="myPointcut"/>&lt;/aop:config>
<p>注意：对于事务配置，没有与基于XML的配置元数据相匹配的基于Java的配置元数据.

### TODO:
1. 补充@Valid支持分组验证
2. 补充@Valid支持bean validation2.0
3. 确认@Valid/@Validated注解是否是自动校验还是需要BeanValidationPostProcessor；
4. 确认@Format注解是否独立发挥了作用而不依赖Converter；
5. Spring Data Jdbc的分页和排序失败；
6. Spring JPA和Hibernate及其事务梳理；
7. 补充在Spring中使用AspectJ；
8. 解析自定义配置xml标签
9. demo-springframework-temp为临时项目，准备删除
