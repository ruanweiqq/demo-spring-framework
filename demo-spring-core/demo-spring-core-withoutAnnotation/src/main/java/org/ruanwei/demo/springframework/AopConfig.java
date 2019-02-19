package org.ruanwei.demo.springframework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 对于AOP配置，没有与基于XML的配置元数据相匹配的基于Java的配置元数据,因此此处import了xml配置.
 * 
 * @author ruanwei
 *
 */
@ImportResource({ "classpath:spring/aop.xml" })
@Configuration
public class AopConfig {
	private static Log log = LogFactory.getLog(AopConfig.class);

	// ==========B.AOP and Instrumentation==========
}
