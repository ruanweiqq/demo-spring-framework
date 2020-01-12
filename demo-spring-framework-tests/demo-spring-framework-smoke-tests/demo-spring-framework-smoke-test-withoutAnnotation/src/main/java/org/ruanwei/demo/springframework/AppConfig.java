package org.ruanwei.demo.springframework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * 
 * @author ruanwei
 *
 */
@Profile("development")
@PropertySource("classpath:propertySource-${spring.profiles.active:development_def}.properties")
@PropertySource("classpath:family.properties")
// @ImportResource({"classpath:spring/applicationContext.xml"})
@Import({ IocConfig.class, AopConfig.class/*, DataAccessConfig.class*/ })
@Configuration
public class AppConfig {
	private static Log log = LogFactory.getLog(AppConfig.class);

}
