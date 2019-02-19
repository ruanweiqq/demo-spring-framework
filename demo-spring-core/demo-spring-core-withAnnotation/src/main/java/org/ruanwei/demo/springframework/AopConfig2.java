package org.ruanwei.demo.springframework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.aop.GoodImpl2;
import org.ruanwei.demo.springframework.core.aop.MyAspect2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 
 * @author ruanwei
 *
 */
// @Profile("development")
@EnableAspectJAutoProxy
@Configuration
public class AopConfig2 {
	private static Log log = LogFactory.getLog(AopConfig2.class);

	// ==========B.AOP and Instrumentation==========
	@Bean("myAspect")
	public MyAspect2 myAspect() {
		return new MyAspect2();
	}

	@Bean("good")
	public GoodImpl2 good() {
		GoodImpl2 good = new GoodImpl2();
		return good;
	}

}
