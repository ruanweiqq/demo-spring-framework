package org.ruanwei.demo.springframework.core.ioc.databinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;

public class PeopleFormatterRegistrar implements FormatterRegistrar {
	private static Log log = LogFactory.getLog(PeopleFormatterRegistrar.class);

	public PeopleFormatterRegistrar() {
		log.info("PeopleFormatterRegistrar()");
	}

	@Override
	public void registerFormatters(FormatterRegistry registry) {
		log.debug("registerFormatters(FormatterRegistry registry)" + registry);
		registry.addConverter(new StringToPeopleConverter());
		registry.addConverterFactory(new StringToPeopleConverterFactory());
		
		registry.addFormatter(new PeopleFormatter("/"));
		registry.addFormatterForFieldType(People.class, new PeopleFormatter("/"));
		registry.addFormatterForFieldAnnotation(new PeopleFormatAnnotationFormatterFactory());
	}

}
