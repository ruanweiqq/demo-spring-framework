package org.ruanwei.demo.springframework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

//@Import(JdbcConfiguration.class)
//@EnableJdbcRepositories
@PropertySource("classpath:jdbc.properties")
@Configuration
public class SpringDataConfig {// implements
								// TransactionManagementConfigurer
								// {
	private static Log log = LogFactory.getLog(SpringDataConfig.class);

}
