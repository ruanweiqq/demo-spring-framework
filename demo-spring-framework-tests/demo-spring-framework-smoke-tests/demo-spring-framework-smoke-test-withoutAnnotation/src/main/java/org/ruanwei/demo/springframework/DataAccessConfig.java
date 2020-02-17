package org.ruanwei.demo.springframework;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hibernate.SessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.ruanwei.demo.springframework.dataAccess.DataAccessService;
import org.ruanwei.demo.springframework.dataAccess.DataAccessServiceImpl;
import org.ruanwei.demo.springframework.dataAccess.jdbc.JdbcDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.JdbcExampleDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.UserJdbcDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.UserJdbcExampleDao;
import org.ruanwei.demo.springframework.dataAccess.jdbc.UserSaveEvent;
import org.ruanwei.demo.springframework.dataAccess.jdbc.entity.UserJdbcEntity;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.HibernateDao;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.UserHibernateDao;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.JpaDao;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.UserJpaDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.config.JtaTransactionManagerFactoryBean;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;
import org.vibur.dbcp.ViburDBCPDataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 对于事务配置，没有与基于XML的配置元数据相匹配的基于Java的配置元数据(无注解),因此开启了@ImportResource.参考TransactionProxyFactoryBean
 * 
 * @author ruanwei
 *
 */
@Profile("development")
//@EnableJpaRepositories("org.ruanwei.demo.springframework.dataAccess.springdata.jpa")
//@EnableJdbcRepositories("org.ruanwei.demo.springframework.dataAccess.springdata.jdbc")
@PropertySource("classpath:jdbc.properties")
@MapperScan(basePackages = "org.ruanwei.demo.springframework.dataAccess.orm.mybatis", sqlSessionFactoryRef = "sqlSessionFactory", factoryBean = MapperFactoryBean.class)
@ImportResource({ "classpath:spring/dataAccess.xml" })
@Configuration
public class DataAccessConfig implements EnvironmentAware, InitializingBean {// implements
																				// TransactionManagementConfigurer
																				// {
	private static Log log = LogFactory.getLog(DataAccessConfig.class);

	private String driverClassName;
	private String url;
	private String username;
	private String password;

	private Environment env;

	@Override
	public void setEnvironment(Environment environment) {
		this.env = environment;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("afterPropertiesSet()======" + env);

		driverClassName = env.getProperty("jdbc.driverClassName");
		url = env.getProperty("jdbc.url");
		username = env.getProperty("jdbc.username");
		password = env.getProperty("jdbc.password");
	}

	@Order(0)
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		propertySourcesPlaceholderConfigurer.setFileEncoding("UTF-8");
		// propertySourcesPlaceholderConfigurer.setLocations(new
		// ClassPathResource("jdbc.properties"));
		log.info("propertySourcesPlaceholderConfigurer==========" + propertySourcesPlaceholderConfigurer);
		return propertySourcesPlaceholderConfigurer;
	}

	// B.Data Access:DAO/Transaction/JDBC/ORM/OXM/Spring Data
	// B.0.Transaction
	//@Bean("transactionProxyFactoryBean")
	public TransactionProxyFactoryBean transactionProxyFactoryBean() {
		TransactionProxyFactoryBean transactionProxyFactoryBean = new TransactionProxyFactoryBean();
		transactionProxyFactoryBean.setTransactionManager(dataSourceTransactionManager());
		Properties transactionAttributes = new Properties();
		transactionAttributes.put("saveWithKey", "PROPAGATION_REQUIRES_NEW");
		transactionAttributes.put("doSomethingWithJdbcTransaction", "PROPAGATION_REQUIRED");
		transactionAttributes.put("find*", "PROPAGATION_REQUIRED,readOnly");
		transactionProxyFactoryBean.setTransactionAttributes(transactionAttributes);
		transactionProxyFactoryBean.setTarget(userJdbcDao());
		return transactionProxyFactoryBean;
	} 
	
	@Bean("dataAccessService")
	public DataAccessService dataAccessService() {
		DataAccessServiceImpl dataAccessService = new DataAccessServiceImpl();
		dataAccessService.setUserJdbcDao(userJdbcDao());
		dataAccessService.setUserJdbcExampleDao(userJdbcExampleDao());
		dataAccessService.setUserJpaDao(userJpaDao());
		dataAccessService.setUserHibernateDao(userHibernateDao());
		// dataAccessService.setUserMyBatisMapper(userMyBatisMapper());
		// dataAccessService.setUserJdbcRepository(userJdbcRepository());
		// dataAccessService.setUserJpaRepository(userJpaRepository());
		return dataAccessService;
	}

	// B.1.JDBC
	@Bean("userJdbcDao")
	public JdbcDao userJdbcDao() {
		UserJdbcDao userJdbcDao = new UserJdbcDao();
		userJdbcDao.setDataSource(springDataSource());
		return userJdbcDao;
	}

	@Bean("userJdbcExampleDao")
	public JdbcExampleDao userJdbcExampleDao() {
		UserJdbcExampleDao userJdbcExampleDao = new UserJdbcExampleDao();
		userJdbcExampleDao.setDataSource(springDataSource());
		return userJdbcExampleDao;
	}

	// local transaction manager for JDBC DataSource
	@Bean("dataSourceTransactionManager")
	public PlatformTransactionManager dataSourceTransactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(springDataSource());
		return transactionManager;
	}

	// B.2.ORM
	// B.2.1.MyBatis
	// SqlSessionFactory和TransactionManager使用的DataSource要一致
	@Bean("sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(springDataSource());

		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
		configuration.setMapUnderscoreToCamelCase(true);
		factoryBean.setConfiguration(configuration);
		// factoryBean.setConfigLocation(new
		// ClassPathResource("mybatis/mybatis-config.xml"));
		// 简单SQL使用注解，复杂SQL使用XML文件。这里不支持通配符，也不支持classpath:前缀。xml配置支持。
		factoryBean.setMapperLocations(new ClassPathResource("mybatis/user-mapper.xml"));
		return factoryBean.getObject();
	}

	// B.2.2.JPA:Hibernate/EclipseLink/OpenJPA==========
	@Bean("userJpaDao")
	public JpaDao userJpaDao() {
		UserJpaDao userJpaDao = new UserJpaDao();
		// 这里使用了@Primary强制注入Hibernate的SessionFactory而不是JPA的EntityManagerFactory，否则注入失败
		// 由于是使用注解按照类型进行注入的，因此下面这行配置无效
		//userJpaDao.setEntityManagerFactory(sessionFactory().getObject());
		return userJpaDao;
	}

	// see HibernateTransactionManager
	@Bean("jpaTransactionManager")
	public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
		jpaTransactionManager.setDataSource(hikariDataSource());
		jpaTransactionManager.setJpaDialect(new HibernateJpaDialect()); // EclipseLinkJpaDialect
		return jpaTransactionManager;
	}

	// see LocalSessionFactoryBean
	@Bean("entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		// see also LocalEntityManagerFactoryBean
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(hikariDataSource());
		entityManagerFactory.setPackagesToScan("org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity");
		entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter()); // EclipseLinkJpaVendorAdapter
		entityManagerFactory.setJpaDialect(new HibernateJpaDialect()); // EclipseLinkJpaDialect
		entityManagerFactory.setBootstrapExecutor(new SimpleAsyncTaskExecutor());
		entityManagerFactory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());// ReflectiveLoadTimeWeaver

		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL57Dialect");
		jpaProperties.put("hibernate.show_sql", true);
		jpaProperties.put("hibernate.format_sql", true);
		jpaProperties.put("hibernate.hbm2ddl.auto", "update");
		entityManagerFactory.setJpaProperties(jpaProperties);

		return entityManagerFactory;
	}

	// B.2.3.Hibernate Native==========
	@Bean("userHibernateDao")
	public HibernateDao userHibernateDao() {
		UserHibernateDao userHibernateDao = new UserHibernateDao();
		userHibernateDao.setSessionFactory(sessionFactory().getObject());
		return userHibernateDao;
	}

	// LocalSessionFactoryBean and HibernateTransactionManager are alternative to
	// LocalContainerEntityManagerFactoryBean and JpaTransactionManager for common
	// JPA purposes.
	// local transaction manager for Hibernate
	@Bean("hibernateTransactionManager")
	public PlatformTransactionManager hibernateTransactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
		hibernateTransactionManager.setSessionFactory(sessionFactory);
		hibernateTransactionManager.setDataSource(hikariDataSource());
		return hibernateTransactionManager;
	}

	// implements JPA EntityManagerFactory
	@Primary
	@Qualifier("sessionFactory")
	@Bean("sessionFactory")
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(hikariDataSource());
		sessionFactory.setPackagesToScan("org.ruanwei.demo.springframework.dataAccess.orm.*.entity");
		sessionFactory.setBootstrapExecutor(new SimpleAsyncTaskExecutor());

		Properties hibernateProperties = new Properties();
		hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL57Dialect");
		hibernateProperties.put("hibernate.show_sql", true);
		hibernateProperties.put("hibernate.format_sql", true);
		hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
		sessionFactory.setHibernateProperties(hibernateProperties);

		return sessionFactory;
	}

	// global transaction manager for JTA
	// JtaTransactionManagerFactoryBean
	// @Bean("jtaTransactionManager")
	public PlatformTransactionManager jtaTransactionManager() {
		JtaTransactionManagerFactoryBean jtaTransactionManager = new JtaTransactionManagerFactoryBean();
		return jtaTransactionManager.getObject();
	}

	// B.3.DataSource
	// B.3.1.Embedded DataSource:HSQL/H2/DERBY
	@Lazy
	@Bean
	public DataSource embeddedDataSource() {
		return new EmbeddedDatabaseBuilder().generateUniqueName(true).setType(EmbeddedDatabaseType.HSQL)
				.setScriptEncoding("UTF-8").ignoreFailedDrops(true).addScript("classpath:db/db-schema-hsql.sql")
				.addScripts("classpath:db/db-test-data.sql").build();
	}

	// B.3.2.Plain JDBC DataSource(no pooling for test only)
	@Primary
	@Qualifier("springDataSource")
	@Bean
	public DataSource springDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	// B.3.3.Hikari DataSource
	@Lazy
	@Qualifier("hikariDataSource")
	@Bean(destroyMethod = "close")
	public DataSource hikariDataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setJdbcUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		// dataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		return dataSource;
	}

	// B.3.4.Vibur DataSource
	@Lazy
	@Qualifier("viburDBCPDataSource")
	@Bean(destroyMethod = "close")
	public DataSource viburDBCPDataSource() {
		ViburDBCPDataSource dataSource = new ViburDBCPDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setJdbcUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	// B.3.5.Tomcat JDBC DataSource(a replacement or an alternative to dbcp2)
	@Lazy
	@Qualifier("tomcatDataSource")
	@Bean(destroyMethod = "close")
	public DataSource tomcatDataSource() {
		org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	// B.3.6.DBCP2 DataSource(Last update:2018-07-16 2.5.0, see PoolingDataSource)
	@Lazy
	@Qualifier("dbcp2DataSource")
	@Bean(destroyMethod = "close")
	public DataSource dbcp2DataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);

		dataSource.setInitialSize(10);
		dataSource.setMaxTotal(100);
		dataSource.setMinIdle(3);
		dataSource.setMaxIdle(10);
		return dataSource;
	}

	// B.3.7.C3P0 DataSource(Last update:2015-12-09 0.9.5.2)
	@Lazy
	@Qualifier("c3p0DataSource")
	@Bean(destroyMethod = "close")
	public DataSource c3p0DataSource() throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass(driverClassName);
		dataSource.setJdbcUrl(url);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		dataSource.setInitialPoolSize(10);
		dataSource.setMinPoolSize(10);
		dataSource.setMaxPoolSize(100);
		return dataSource;
	}

	// B.3.8.JNDI DataSource
	@Lazy
	@Qualifier("jndiDataSource")
	// @Bean(destroyMethod = "close")
	public JndiObjectFactoryBean jndiDataSource() {
		JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
		jndiObjectFactoryBean.setJndiName("java:comp/env/jdbc/myds");
		return jndiObjectFactoryBean;
	}

	// 应该移动到service层
	@TransactionalEventListener
	public void handleTransactionalEvent(UserSaveEvent event) {
		log.info("handleTransactionalEvent" + event);
		UserJdbcEntity user = (UserJdbcEntity) event.getSource();
		// User u = findById(user.getId());
		// log.info("user has been saved=======================" + u);
	}

	// B.4.OXM
	// Castor project is not active.
	/*@Lazy
	@Qualifier("castorMarshaller")
	@Bean
	public CastorMarshaller castorMarshaller() {
		CastorMarshaller castorMarshaller = new CastorMarshaller();
		castorMarshaller.setMappingLocation(new ClassPathResource("mapping.xml"));
		return castorMarshaller;
	}*/

	/*@Lazy
	@Qualifier("jaxb2Marshaller")
	@Bean
	public Jaxb2Marshaller jaxb2Marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.ruanwei.demo.springframework.dataAccess.oxm");
		marshaller.setClassesToBeBound(Settings.class);
		marshaller.setSchema(new ClassPathResource("schema.xsd"));
		return marshaller;
	}*/

	// JiBX project is not active.
	/*@Lazy
	@Qualifier("jibxMarshaller")
	@Bean
	public JibxMarshaller jibxMarshaller() {
		JibxMarshaller jibxMarshaller = new JibxMarshaller();
		jibxMarshaller.setTargetClass(Settings.class);
		return jibxMarshaller;
	}*/

	/*@Lazy
	@Qualifier("xStreamMarshaller")
	@Bean
	public XStreamMarshaller xStreamMarshaller() {
		XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
		xStreamMarshaller.setSupportedClasses(Settings.class);
		Map<String, Class<?>> aliases = new HashMap<String, Class<?>>();
		aliases.put("Settings", Settings.class);
		xStreamMarshaller.setAliases(aliases);
		return xStreamMarshaller;
	}*/

	// B.Data Access:Spring Data

}
