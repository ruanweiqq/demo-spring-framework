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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.config.JtaTransactionManagerFactoryBean;
import org.springframework.transaction.event.TransactionalEventListener;
import org.vibur.dbcp.ViburDBCPDataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @author ruanwei
 *
 */
@Profile("development")
//@Import(AbstractJdbcConfiguration.class)
//@EnableJpaRepositories("org.ruanwei.demo.springframework.dataAccess.springdata.jpa")
//@EnableJdbcRepositories("org.ruanwei.demo.springframework.dataAccess.springdata.jdbc")
@EnableTransactionManagement
@PropertySource("classpath:jdbc.properties")
@MapperScan(basePackages = "org.ruanwei.demo.springframework.dataAccess.orm.mybatis", sqlSessionFactoryRef = "sqlSessionFactory", factoryBean = MapperFactoryBean.class)
@ComponentScan(basePackages = { "org.ruanwei.demo.springframework" })
@Configuration
public class AppConfig {// implements
						// TransactionManagementConfigurer
						// {
	private static Log log = LogFactory.getLog(AppConfig.class);

	@Value("${jdbc.driverClassName}")
	private String driverClassName;
	@Value("${jdbc.url}")
	private String url;
	@Value("${jdbc.username}")
	private String username;
	@Value("${jdbc.password}")
	private String password;

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
	// B.1.JDBC
	// local transaction manager for plain JDBC
	@Primary
	@Bean("transactionManager")
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(springDataSource());
		return transactionManager;
	}

	// B.2.ORM
	// B.2.1.JPA==========
	// local transaction manager for JPA
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
	@Qualifier("entityManagerFactory")
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

	// B.2.2.Hibernate==========
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

	// B.2.3.MyBatis
	// SqlSessionFactory和TransactionManager使用的DataSource要一致
	@Bean("sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(springDataSource());

		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
		configuration.setMapUnderscoreToCamelCase(true);
		factoryBean.setConfiguration(configuration);
		// factoryBean.setConfigLocation(new ClassPathResource("mybatis/mybatis-config.xml"));
		// 复杂SQL适合XML Mapper文件，这里不支持通配符，也不支持classpath前缀。xml配置支持。
		factoryBean.setMapperLocations(new ClassPathResource("mybatis/user-mapper.xml"));
		return factoryBean.getObject();
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

	// The valid phases are BEFORE_COMMIT, AFTER_COMMIT (default), AFTER_ROLLBACK
	// and AFTER_COMPLETION.
	@TransactionalEventListener
	public void handleTransactionalEvent(ApplicationEvent event) {
		log.info("handleTransactionalEvent======" + event);
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
