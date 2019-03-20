package org.ruanwei.demo.springframework;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.jdbc.UserJdbcDao;
import org.ruanwei.demo.springframework.dataAccess.orm.hibernate.UserHibernateDao;
import org.ruanwei.demo.springframework.dataAccess.orm.jpa.UserJpaDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@PropertySource("classpath:jdbc.properties")
@EnableTransactionManagement
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
		driverClassName = env.getProperty("jdbc.driverClassName", "com.mysql.cj.jdbc.Driver");
		url = env.getProperty("jdbc.url",
				"jdbc:mysql://localhost:3306/demo?useUnicode=true&autoReconnect=true&characterEncoding=utf-8");
		username = env.getProperty("jdbc.username", "root");
		password = env.getProperty("jdbc.password", "qqqq1234");
	}

	// ==========A.Data Access:JDBC==========
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
	@Qualifier("jdbcDataSource")
	@Bean
	public DataSource dataSource1() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	// polled-DataSource:dbcp2, see PoolingDataSource
	@Qualifier("dbcp2DataSource")
	@Lazy
	@Bean(destroyMethod = "close")
	public DataSource dataSource2() {
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
	public DataSource dataSource3() throws Exception {
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

	// @Bean("sessionFactory")
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource1());
		return sessionFactory;
	}

	// @Bean("entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(dataSource1());
		entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		entityManagerFactory.setPackagesToScan("org.ruanwei.demo.springframework.dataAccess.orm.jpa.entity");
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.show_sql", true);
		jpaProperties.put("hibernate.format_sql", true);
		jpaProperties.put("hibernate.hbm2ddl.auto", "update");
		entityManagerFactory.setJpaProperties(jpaProperties);
		// entityManagerFactory.setLoadTimeWeaver(loadTimeWeaver);
		return entityManagerFactory;
	}

	// JndiObjectFactoryBean

	// ==========C.Data Access:DAO==========
	@Bean
	public UserJdbcDao userJdbcDao() {
		UserJdbcDao userJdbcDao = new UserJdbcDao();
		userJdbcDao.setDataSource(dataSource1());
		return userJdbcDao;
	}

	// @Bean
	public UserHibernateDao userHibernateDao() {
		UserHibernateDao userHibernateDao = new UserHibernateDao();
		userHibernateDao.setSessionFactory(sessionFactory().getObject());
		return userHibernateDao;
	}

	// @Bean
	public UserJpaDao userJpaDao() {
		UserJpaDao userJpaDao = new UserJpaDao();
		userJpaDao.setEntityManagerFactory(entityManagerFactory().getObject());
		return userJpaDao;
	}

	// ==========C.Data Access:TransactionManager==========
	// local transaction manager for plain JDBC
	@Primary
	@Bean("transactionManager")
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource1());
		return transactionManager;
	}

	// local transaction manager for Hibernate
	// @Bean("hibernateTransactionManager")
	public PlatformTransactionManager hibernateTransactionManager() {
		HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
		hibernateTransactionManager.setSessionFactory(sessionFactory().getObject());
		hibernateTransactionManager.setDataSource(dataSource1());
		return hibernateTransactionManager;
	}

	// local transaction manager for JPA
	// @Bean("jpaTransactionManager")
	public PlatformTransactionManager jpaTransactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		jpaTransactionManager.setDataSource(dataSource1());
		return jpaTransactionManager;
	}

	// global transaction manager for JTA
	// @Bean("jtaTransactionManager")
	public PlatformTransactionManager jtaTransactionManager() {
		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
		return jtaTransactionManager;
	}

}
