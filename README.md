# demo-spring-framework

this is the master branch

### 项目结构
<pre>
demo-spring-framework
    -demo-spring-framework-build:pom
    demo-spring-framework-project
        -demo-spring-framework-project:pom  demo-spring-framework-build:pom
        demo-spring-framework-project-dependencies
	    -demo-spring-framework-project-dependencies:pom  ../..(demo-spring-framework-build:pom)
        demo-spring-framework-project-parent
	    -demo-spring-framework-project-parent:pom  ../demo-spring-framework-project-dependencies:pom
        demo-spring-framework-project-starters
	    -demo-spring-framework-project-starters:pom  ../demo-spring-framework-project-parent:pom
	    demo-spring-framework-project-starter-parent
	        -demo-spring-framework-project-starter-parent:pom  ../../demo-spring-framework-project-dependencies:pom
	    demo-spring-framework-project-starter
	        -demo-spring-framework-project-starter:jar  demo-spring-framework-project-starters:pom
	    demo-spring-framework-project-starter-core
	        -demo-spring-framework-project-starter-core:jar demo-spring-framework-project-starters:pom
	    demo-spring-framework-project-starter-dataAccess
	        -demo-spring-framework-project-starter-dataAccess:jar demo-spring-framework-project-starters:pom
	    demo-spring-framework-project-starter-web
	        -demo-spring-framework-project-starter-web:jar  demo-spring-framework-project-starters:pom
	    demo-spring-framework-project-starter-integration
	        -demo-spring-framework-project-starter-integration:jar demo-spring-framework-project-starters:pom
        demo-spring-framework-project-commons
	    -demo-spring-framework-project-commons:jar  ../demo-spring-framework-project-parent:pom
    demo-spring-framework-tests
        -demo-spring-framework-tests:pom  ../demo-spring-framework-project/demo-spring-framework-project-parent
    demo-spring-framework-samples
        -demo-spring-framework-samples:pom  ../demo-spring-framework-project/demo-spring-framework-project-starters/demo-spring-framework-project-starter-parent
        demo-spring-framework-samples-core
	    -demo-spring-framework-samples-core:jar  demo-spring-framework-samples:pom
        demo-spring-framework-samples-web
	    -demo-spring-framework-samples-web:war  demo-spring-framework-samples:pom
        demo-spring-framework-samples-dataAccess
	    -demo-spring-framework-samples-dataAccess:jar  demo-spring-framework-samples:pom
        demo-spring-framework-samples-integration
	    -demo-spring-framework-samples-integration:jar  demo-spring-framework-samples:pom
        demo-spring-framework-samples-withoutAnnotation
	    -demo-spring-framework-samples-withoutAnnotation:jar  demo-spring-framework-samples:pom
------------------------------------------
spring-boot
    -spring-boot-build:pom
    spring-boot-project
	-spring-boot-project:pom  spring-boot-build:pom
	spring-boot-dependencies
	    -spring-boot-dependencies:pom  ../..(spring-boot-build:pom)
	spring-boot-parent
	    -spring-boot-parent:pom  ../spring-boot-dependencies:pom
	spring-boot-starters
	    -spring-boot-starters:pom  ../spring-boot-parent:pom
	    spring-boot-starter-parent
		-spring-boot-starter-parent:pom  ../../spring-boot-dependencies:pom
	    spring-boot-starter
		-spring-boot-starter:jar  spring-boot-starters:pom
	    spring-boot-starter-web
		-spring-boot-starter-web:jar  spring-boot-starters:pom
	    spring-boot-starter-webflux
	        -spring-boot-starter-webflux:jar  spring-boot-starters:pom	
	spring-boot
	    -spring-boot:jar  ../spring-boot-parent:pom
	spring-boot-actuator
	    -spring-boot-actuator:jar  ../spring-boot-parent:pom
	spring-boot-autoconfigure
	spring-boot-devtools		
	spring-boot-cli
    spring-boot-tests
	-spring-boot-tests:pom  ../spring-boot-project/spring-boot-parent
	spring-boot-integration-tests
	    -spring-boot-integration-tests:pom  spring-boot-tests:pom
	    spring-boot-configuration-processor-tests
	        -spring-boot-configuration-processor-tests:pom  spring-boot-integration-tests:pom
	    spring-boot-devtools-tests
	        -spring-boot-devtools-tests   spring-boot-integration-tests:pom
	spring-boot-deployment-tests
	    -spring-boot-deployment-tests:pom  spring-boot-tests:pom
	    spring-boot-deployment-test-glassfish
	        -spring-boot-deployment-test-glassfish:war  spring-boot-deployment-tests:pom
	    spring-boot-deployment-test-tomcat
		-spring-boot-deployment-test-tomcat:war  spring-boot-deployment-tests:pom
    spring-boot-samples
	-spring-boot-samples:pom  ../spring-boot-project/spring-boot-starters/spring-boot-starter-parent:pom
	spring-boot-sample-simple
	    -spring-boot-sample-simple:jar  spring-boot-samples:pom
    spring-boot-samples-invoker
        -spring-boot-samples-invoker:pom  ../spring-boot-project/spring-boot-parent:pom
------------------------------------------
</pre>

### 项目模块：
- demo-spring-framework-project模块：定义parent和starter。
  - demo-spring-framework-project-dependencies模块：依赖声明(BOM)。
  - demo-spring-framework-project-parent模块：内部项目的父模块，包含构建参数。
  - demo-spring-framework-project-starters模块：父模块为demo-spring-framework-project-parent。
  - demo-spring-framework-project-commons模块：父模块为demo-spring-framework-project-parent。
- demo-spring-framework-tests模块：测试继承于demo-spring-framework-project-parent的模块。
- demo-spring-framework-samples模块：测试继承于demo-spring-framework-project-starter-parent的模块。
  - demo-spring-framework-samples-core模块：父模块为demo-spring-framework-project-starter-parent。
  - demo-spring-framework-samples-dataAccess模块：父模块为demo-spring-framework-project-starter-parent。
  - demo-spring-framework-samples-web模块：父模块为demo-spring-framework-project-starter-parent。
  - demo-spring-framework-samples-integration模块：父模块为demo-spring-framework-project-starter-parent。
  - demo-spring-framework-samples-withoutAnnotation模块：父模块为demo-spring-framework-project-starter-parent。未开启基于注解的配置元数据。
  - demo-spring-framework-samples-temp模块：父模块为demo-spring-framework-project-starter-parent。临时项目，准备删除。

### 运行方式：TODO
cd demo-spring-framework  
mvn clean install -P prepare  
mvn clean test

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

### Git:
#### 开始一个工作区(working area)  

git remote add origin git@github.com:ruanweiqq/hello.git   // local repository(master) <--> remote repository origin(master)   
git clone git@github.com:ruanweiqq/hello.git   // remote repository(master) origin <--> local repository(master)   

git init  // Create an empty Git repository or reinitialize an existing one

#### 操作当前变更 index <--> working area
git add <file>    // Add file contents to the index  
git rm <file>     // Remove files from the working tree and from the index  
git mv <file>     // Move or rename a file, a directory, or a symlink  
git reset HEAD <file>  // Reset current HEAD to the specified state  // index --> working area  HEAD指向当前分支(master)  

#### 提交当前变更 index <--> repository(branch)
git commit -m "message"    // Record changes to the repository  
git checkout -- <file>  // restore working tree files 

git diff     // index vs working area  
git reset --hard <commit_id>   // 版本回退。HEAD表示当前版本，HEAD^表示上一个版本等  
git diff HEAD -- <file>  // branch vs working area  HEAD指向当前分支(master)  
git diff --cached     // branch vs index   
 
git log --graph --pretty=oneline 
git reflog  
git status  
git show v0.9   // tag  

#### 本地分支(默认分支master)  
git branch  // 查看分支  
git branch dev   // 创建分支dev     
git checkout dev  // 切换到分支dev  
git checkout -b dev   // 创建并切换到分支dev
git checkout -b dev origin/dev   
git diff < source_branch> < target_branch>       
git merge dev --no-ff  // 合并dev到当前分支(master)，禁用Fast forward模式  
git branch -d dev // 删除分支dev  

#### 本地标签
git tag   // 查看标签  
git tag v1.0   // 以当前commit_id创建标签v1.0  
git tag v0.9 <commit_id>   // 以指定commit_id创建标签v1.0  
git tag -a v0.1 -m "version 0.1 released" <commit_id>  
git tag -d v0.1   // 删除本地标签v0.1  

#### 本地临时保存
git stash  
git stash pop  
git stash list  
git stash apply  
git stash drop  

#### 远程仓库(origin)
git remote -v  
git branch --set-upstream-to=origin/<branch> <branch>  // 当前分支与远程分支建立联系  
git pull   // origin branch -->  local branch, will merge  
git fetch origin  // origin branch -->  local branch, will not merge 
git rebase  // 统一基线方便push   
git push origin < branch>   // local branch --> origin branch   
git push origin < tag>   // local tag --> origin tag   
git push origin --tags  // local all tags --> origin tag  
git push origin :refs/tags/<tagname>  // 删除远程tag  

#### 配置
git config --global color.ui true  
git config format.pretty oneline   
