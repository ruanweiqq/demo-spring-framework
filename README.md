# demo-spring-framework

this is the master branch

### 项目结构

- demo-spring-framework(demo-spring-framework-build:pom)
  - demo-spring-framework-project ======> demo-spring-framework-build:pom
        - demo-spring-framework-project-dependencies ======> ../..(demo-spring-framework-build:pom)
        - demo-spring-framework-parent ======> ../demo-spring-framework-dependencies:pom
	- demo-spring-framework-commons(jar) ======> ../demo-spring-framework-parent:pom    
        - demo-spring-framework-starters ======> ../demo-spring-framework-parent:pom
	    - demo-spring-framework-starter-parent ======> ../../demo-spring-framework-dependencies:pom
	    - demo-spring-framework-starter(jar) ======> demo-spring-framework-starters:pom
	    - demo-spring-framework-starter-xxx(jar) ======> demo-spring-framework-starters:pom        
  - demo-spring-framework-tests ======> ../demo-spring-framework-project/demo-spring-framework-parent
        - demo-spring-framework-smoke-tests ======> ../../demo-spring-framework-project/demo-spring-framework-starters/demo-spring-framework-starter-parent
            - demo-spring-framework-smoke-test-xxx(jar) ======> demo-spring-framework-smoke-tests:pom
	- demo-spring-framework-smoke-tests-invoker
	- spring-boot-integration-tests
	- spring-boot-deployment-tests
<pre>
------------------------------------------
spring-boot
    -spring-boot-build:pom
    spring-boot-project
	-spring-boot-project:pom  spring-boot-build:pom
	spring-boot-dependencies
	    -spring-boot-dependencies:pom  ../..(spring-boot-build:pom)
	spring-boot-parent
	    -spring-boot-parent:pom  ../spring-boot-dependencies:pom	
	spring-boot
	    -spring-boot:jar  ../spring-boot-parent:pom
	spring-boot-xxx
	    -spring-boot-xxx:jar  ../spring-boot-parent:pom
	spring-boot-starters
	    -spring-boot-starters:pom  ../spring-boot-parent:pom
	    spring-boot-starter-parent
		-spring-boot-starter-parent:pom  ../../spring-boot-dependencies:pom
	    spring-boot-starter
		-spring-boot-starter:jar  spring-boot-starters:pom
	    spring-boot-starter-xxx
		-spring-boot-starter-xxx:jar  spring-boot-starters:pom
    spring-boot-tests
        -spring-boot-tests:pom  ../spring-boot-project/spring-boot-parent
        spring-boot-smoke-tests
	    -spring-boot-smoke-tests:pom  ../../spring-boot-project/spring-boot-starters/spring-boot-starter-parent:pom
	    spring-boot-smoke-test-xxx
	        -spring-boot-smoke-test-xxx:jar  spring-boot-smoke-tests:pom
        spring-boot-smoke-test-invoker
            -spring-boot-smoke-test-invoker:pom  ../../spring-boot-project/spring-boot-parent:pom
	spring-boot-integration-tests
	    -spring-boot-integration-tests:pom  spring-boot-tests:pom
	    spring-boot-xxx-tests
	        -spring-boot-xxx-tests   spring-boot-integration-tests:pom
	spring-boot-deployment-tests
	    -spring-boot-deployment-tests:pom  spring-boot-tests:pom
	    spring-boot-deployment-test-xxx
	        -spring-boot-deployment-test-xxx:war  spring-boot-deployment-tests:pom
------------------------------------------
</pre>

### 项目模块：
- demo-spring-framework-project模块：定义parent和starter，父模块为demo-spring-framework-build。
  - demo-spring-framework-dependencies模块：依赖声明(BOM)。
  - demo-spring-framework-parent模块：内部项目的父模块，父模块为demo-spring-framework-dependencies。
  - demo-spring-framework-starters模块：外部项目的父模块，父模块为demo-spring-framework-parent。
  - demo-spring-framework-commons模块：父模块为demo-spring-framework-parent。
- demo-spring-framework-tests模块：继承于demo-spring-framework-parent。
  - demo-spring-framework-smoke-tests模块：父模块为demo-spring-framework-starter-parent。
    - demo-spring-framework-smoke-test-core模块：父模块为demo-spring-framework-smoke-tests。
    - demo-spring-framework-smoke-test-dataAccess模块：父模块为demo-spring-framework-smoke-tests。
    - demo-spring-framework-smoke-test-web模块：父模块为demo-spring-framework-smoke-tests。
    - demo-spring-framework-smoke-test-integration模块：父模块为demo-spring-framework-smoke-tests。
    - demo-spring-framework-smoke-test-withoutAnnotation模块：父模块为demo-spring-framework-smoke-tests。未开启基于注解的配置元数据。
    - demo-spring-framework-smoke-test-temp模块：父模块为demo-spring-framework-smoke-tests。临时项目，准备删除。
  - demo-spring-framework-smoke-tests-invoker模块：父模块为demo-spring-framework-parent。

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
git status -s   // Show the working tree status
#### 跟踪文件(working tree <--> index)
git init   // Create an empty Git repository or reinitialize an existing one.  
git add <file>    // Add file contents to the index.  
git rm <file>     // Remove files from the working tree and index.  
git rm --cached <file>    // Remove files only from the index.  
git mv <file>     // Move or rename a file, a directory, or a symlink.  
git reset HEAD <file>   // to unstage. index --> working tree.    
git checkout -- <file>   // to discard changes and restore working tree files.repository -> working tree.   
git diff  // working tree vs index.   
git diff HEAD -- <file>  // working tree vs repository.HEAD指向当前分支. 
	
#### 提交变更(index <--> repository)
git commit -m "message"    // index --> repository.   
git commit -a    // working tree --> repository.   
git diff --staged/cached   // index vs repository.   
git reset --hard <commit_id>   // Reset current HEAD to the specified state(版本回退),HEAD指向当前分支的最新版本(commit)。HEAD表示当前版本(commit)，HEAD^表示上一个版本(commit)等。  
git log  
git log -p -2  
git log --stat  
git log --graph --pretty=oneline --decorate  
git log --pretty=format:"%h - %an, %ar : %s"  
git reflog  

#### 标签
git tag   // 查看标签  
git tag v1.0   // 以当前commit_id创建标签v1.0  
git tag v0.9 <commit_id>   // 以指定commit_id创建标签v1.0  
git tag -a v0.1 -m "version 0.1 released" <commit_id>  
git tag -d v0.1   // 删除本地标签v0.1  

#### 分支(默认分支master)  
git branch  // 查看分支
git branch --merged   
git branch --no-merged   
git branch dev   // 创建分支dev     
git checkout dev  // 切换到分支dev  
git checkout -b dev   // 创建并切换到分支dev
git checkout -b dev origin/dev   
git diff < source_branch> < target_branch>       
git merge dev --no-ff  // 合并dev到当前分支(master)，禁用Fast forward模式  
git branch -d dev // 删除分支dev  

git show v0.9   // tag 

#### 本地临时保存
git stash  
git stash pop  
git stash list  
git stash apply  
git stash drop  

#### 远程仓库(origin)
git remote -v  
git remote show origin
git remote rename origin origin2
git remote rm origin3
git remote add origin git@github.com:ruanweiqq/hello.git   // repository(master) <--> remote origin(master)   
git clone git@github.com:ruanweiqq/hello.git   // remote origin(master)  <--> repository(master)  
git fetch origin  // remote origin -->  repository, will not merge.  
git pull   // remote origin -->  working tree, will merge. 

git branch --set-upstream-to=origin/<branch> <branch>  // 当前分支与远程分支建立联系  
git rebase  // 统一基线方便push   
git push origin master   // repository --> remote origin.   
git push origin < tag>   // local tag --> remote origin tag   
git push origin --tags  // local all tags --> origin tag  
git push origin :refs/tags/<tagname>  // 删除远程tag  

#### 配置
git config --list
git config --global user.name "John Doe"  // 读取~/.gitconfig 或 ~/.config/git/config
git config --global user.email johndoe@example.com  // 读取~/.gitconfig 或 ~/.config/git/config
git config --system color.ui true    // 读取/etc/gitconfig   
git config format.pretty oneline   // 读取.git/config  
git help config


Table 1. git log --pretty=format 常用的选项
选项	说明
%H

提交对象（commit）的完整哈希字串

%h

提交对象的简短哈希字串

%T

树对象（tree）的完整哈希字串

%t

树对象的简短哈希字串

%P

父对象（parent）的完整哈希字串

%p

父对象的简短哈希字串

%an

作者（author）的名字

%ae

作者的电子邮件地址

%ad

作者修订日期（可以用 --date= 选项定制格式）

%ar

作者修订日期，按多久以前的方式显示

%cn

提交者（committer）的名字

%ce

提交者的电子邮件地址

%cd

提交日期

%cr

提交日期，按多久以前的方式显示

%s

提交说明


Table 2. git log 的常用选项
选项	说明
-p

按补丁格式显示每个更新之间的差异。

--stat

显示每次更新的文件修改统计信息。

--shortstat

只显示 --stat 中最后的行数修改添加移除统计。

--name-only

仅在提交信息后显示已修改的文件清单。

--name-status

显示新增、修改、删除的文件清单。

--abbrev-commit

仅显示 SHA-1 的前几个字符，而非所有的 40 个字符。

--relative-date

使用较短的相对时间显示（比如，“2 weeks ago”）。

--graph

显示 ASCII 图形表示的分支合并历史。

--pretty

使用其他格式显示历史提交信息。可用的选项包括 oneline，short，full，fuller 和 format（后跟指定格式）。
