package org.ruanwei.demo.springframework;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.dataAccess.oxm.Settings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
// import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.jibx.JibxMarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.vibur.dbcp.ViburDBCPDataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;

@EnableTransactionManagement(proxyTargetClass = true)
@PropertySource("classpath:jdbc.properties")
@Configuration
public class DataAccessConfig2 {// implements TransactionManagementConfigurer {
	private static Log log = LogFactory.getLog(DataAccessConfig2.class);

	@Value("${jdbc.driverClassName}")
	private String driverClassName;
	@Value("${jdbc.url}")
	private String url;
	@Value("${jdbc.username}")
	private String username;
	@Value("${jdbc.password}")
	private String password;

	// ==========C.Spring JDBC Data Access==========
	// C.1.JDBC DataSource
	@Qualifier("embeddedDataSource")
	@Lazy
	@Bean
	public DataSource embeddedDataSource() {
		return new EmbeddedDatabaseBuilder().generateUniqueName(true).setType(EmbeddedDatabaseType.HSQL)
				.setScriptEncoding("UTF-8").ignoreFailedDrops(true).addScript("classpath:db/db-schema-hsql.sql")
				.addScripts("classpath:db/db-test-data.sql").build();
	}

	// DataSource:plain JDBC
	// should only be used for testing purposes since no pooling.
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

	// Last Update: 2018-07-16 2.5.0
	// see PoolingDataSource
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

	// Last Update: 2015-12-09 0.9.5.2
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

	// C.2.ORM
	// C.2.1.Hibernate Native API
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

	// C.2.2.Java Persistence API
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

	// JndiObjectFactoryBean

	// C.3.Transaction
	// local transaction manager for plain JDBC
	@Primary
	@Bean("transactionManager")
	public DataSourceTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(springDataSource());
		return transactionManager;
	}

	// local transaction manager for Hibernate
	@Bean("hibernateTransactionManager")
	public HibernateTransactionManager hibernateTransactionManager() {
		HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
		hibernateTransactionManager.setSessionFactory(sessionFactory().getObject());
		// hibernateTransactionManager.setDataSource(dataSource1());
		return hibernateTransactionManager;
	}

	// local transaction manager for JPA
	@Bean("jpaTransactionManager")
	public JpaTransactionManager jpaTransactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		// jpaTransactionManager.setJpaDialect(jpaDialect);
		// jpaTransactionManager.setDataSource(dataSource1());
		return jpaTransactionManager;
	}

	// global transaction manager for JTA
	// @Bean("jtaTransactionManager")
	public JtaTransactionManager jtaTransactionManager() {
		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
		// jtaTransactionManager.setTransactionManager(transactionManager);
		return jtaTransactionManager;
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

}
