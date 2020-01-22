package org.ruanwei.demo.springframework;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.jdbc.UserJdbcDao;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.UserHibernateDao;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.UserJpaDao;
import org.ruanwei.demo.springframework.dataAccess.oxm.Settings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.jibx.JibxMarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.vibur.dbcp.ViburDBCPDataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 对于事务配置，没有与基于XML的配置元数据相匹配的基于Java的配置元数据,因此此处import了xml配置.
 * @author ruanwei
 *
 */
@Import(AbstractJdbcConfiguration.class)
@Profile("development")
@PropertySource("classpath:jdbc.properties")
//@EnableJpaRepositories("org.ruanwei.demo.springframework.dataAccess.springdata.jpa")
@EnableJdbcRepositories("org.ruanwei.demo.springframework.dataAccess.springdata.jdbc")
@EnableTransactionManagement(proxyTargetClass = true)
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

	// ==========C.1.JDBC==========
	@Bean
	public UserJdbcDao userJdbcDao() {
		UserJdbcDao userJdbcDao = new UserJdbcDao();
		userJdbcDao.setDataSource(springDataSource());
		return userJdbcDao;
	}

	// local transaction manager for plain JDBC
	@Primary
	@Bean("transactionManager")
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(springDataSource());
		return transactionManager;
	}

	// ==========C.2.ORM:JPA==========
	// @Bean
	public UserJpaDao userJpaDao() {
		UserJpaDao userJpaDao = new UserJpaDao();
		userJpaDao.setEntityManagerFactory(entityManagerFactory().getObject());
		return userJpaDao;
	}

	// local transaction manager for JPA
	// @Bean("jpaTransactionManager")
	public PlatformTransactionManager jpaTransactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		// jpaTransactionManager.setJpaDialect(jpaDialect);
		// jpaTransactionManager.setDataSource(dataSource1());
		return jpaTransactionManager;
	}

	@Qualifier("entityManagerFactory")
	// @Bean("entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(springDataSource());
		entityManagerFactory.setPackagesToScan("org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity");
		entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter()); // EclipseLinkJpaVendorAdapter
		// entityManagerFactory.setJpaDialect(jpaDialect);

		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL57Dialect");
		jpaProperties.put("hibernate.show_sql", true);
		jpaProperties.put("hibernate.format_sql", true);
		jpaProperties.put("hibernate.hbm2ddl.auto", "update");
		entityManagerFactory.setJpaProperties(jpaProperties);

		// entityManagerFactory.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());

		return entityManagerFactory;
	}

	// ==========C.3.ORM:Hibernate==========
	// @Bean
	public UserHibernateDao userHibernateDao() {
		UserHibernateDao userHibernateDao = new UserHibernateDao();
		userHibernateDao.setSessionFactory(sessionFactory().getObject());
		return userHibernateDao;
	}

	// local transaction manager for Hibernate
	// @Bean("hibernateTransactionManager")
	public PlatformTransactionManager hibernateTransactionManager() {
		HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
		hibernateTransactionManager.setSessionFactory(sessionFactory().getObject());
		hibernateTransactionManager.setDataSource(springDataSource());
		return hibernateTransactionManager;
	}

	// @Bean("sessionFactory")
	@Qualifier("sessionFactory")
	@Bean("sessionFactory")
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(springDataSource());
		sessionFactory.setPackagesToScan("org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity");

		Properties hibernateProperties = new Properties();
		hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL57Dialect");
		hibernateProperties.put("hibernate.show_sql", true);
		hibernateProperties.put("hibernate.format_sql", true);
		hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
		sessionFactory.setHibernateProperties(hibernateProperties);

		return sessionFactory;
	}

	// ==========C.4.ORM:MyBatis==========

	// global transaction manager for JTA
	// @Bean("jtaTransactionManager")
	public PlatformTransactionManager jtaTransactionManager() {
		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
		return jtaTransactionManager;
	}

	// ==========C.5.DataSource==========
	// DataSource:HSQL/H2/DERBY
	@Qualifier("embeddedDataSource")
	@Lazy
	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder().generateUniqueName(true).setType(EmbeddedDatabaseType.HSQL)
				.setScriptEncoding("UTF-8").ignoreFailedDrops(true).addScript("classpath:db/db-schema-hsql.sql")
				.addScripts("classpath:db/db-test-data.sql").build();
	}

	// DataSource:plain JDBC
	// should only be used for testing purposes since no pooling.
	@Primary
	@Qualifier("dataSource1")
	@Bean
	public DataSource springDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	@Qualifier("hikariDataSource")
	@Lazy
	@Bean(destroyMethod = "close")
	public DataSource hikariDataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setJdbcUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		return dataSource;
	}

	@Qualifier("viburDBCPDataSource")
	@Lazy
	@Bean(destroyMethod = "close")
	public DataSource viburDBCPDataSource() {
		ViburDBCPDataSource dataSource = new ViburDBCPDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setJdbcUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	// polled-DataSource:dbcp2, see PoolingDataSource
	@Qualifier("dbcp2DataSource")
	@Lazy
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

	// polled-DataSource:c3p0
	@Qualifier("c3p0DataSource")
	@Lazy
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

	@Qualifier("jndiDataSource")
	@Lazy
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

	// C.4.OXM
	// Castor project is not active.
	/*@Lazy
	@Qualifier("castorMarshaller")
	@Bean
	public CastorMarshaller castorMarshaller() {
		CastorMarshaller castorMarshaller = new CastorMarshaller();
		castorMarshaller.setMappingLocation(new ClassPathResource("mapping.xml"));
		return castorMarshaller;
	}*/

	@Lazy
	@Qualifier("jaxb2Marshaller")
	@Bean
	public Jaxb2Marshaller jaxb2Marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.ruanwei.demo.springframework.dataAccess.oxm");
		marshaller.setClassesToBeBound(Settings.class);
		marshaller.setSchema(new ClassPathResource("schema.xsd"));
		return marshaller;
	}

	@Lazy
	@Qualifier("jibxMarshaller")
	@Bean
	public JibxMarshaller jibxMarshaller() {
		JibxMarshaller jibxMarshaller = new JibxMarshaller();
		jibxMarshaller.setTargetClass(Settings.class);
		return jibxMarshaller;
	}

	@Lazy
	@Qualifier("xStreamMarshaller")
	@Bean
	public XStreamMarshaller xStreamMarshaller() {
		XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
		xStreamMarshaller.setSupportedClasses(Settings.class);
		Map<String, Class<?>> aliases = new HashMap<String, Class<?>>();
		aliases.put("Settings", Settings.class);
		xStreamMarshaller.setAliases(aliases);
		return xStreamMarshaller;
	}

	// ==========D.Spring Data:Redis==========

}
