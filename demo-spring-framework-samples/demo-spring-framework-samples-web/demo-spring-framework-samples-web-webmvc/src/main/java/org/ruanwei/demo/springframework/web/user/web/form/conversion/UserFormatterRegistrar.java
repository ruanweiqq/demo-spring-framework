package org.ruanwei.demo.springframework.web.user.web.form.conversion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ruanwei.demo.util.Counter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;

public class UserFormatterRegistrar implements FormatterRegistrar {
	private static final Logger logger = LogManager.getLogger();

	@Override
	public void registerFormatters(FormatterRegistry registry) {
		logger.info("registerFormatters==================" + Counter.getCount()
				+ " registry=" + registry);
		registry.addConverter(new StringToUserFormConverter());
		registry.addFormatter(new UserFormFormatter());
		registry.addFormatterForFieldAnnotation(new UserFormFormatAnnotationFormatterFactory());
	}

}
