package org.ruanwei.demo.springframework.core.ioc.databinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.FormatterRegistry;

public class PeopleFormatterRegistrar2 implements FormatterRegistrar {
	private static Log log = LogFactory.getLog(PeopleFormatterRegistrar2.class);

	public PeopleFormatterRegistrar2() {
		log.info("PeopleFormatterRegistrar2()");
	}

	@Override
	public void registerFormatters(FormatterRegistry registry) {
		log.debug("registerFormatters(FormatterRegistry registry)" + registry);
		registry.addConverter(new StringToPeopleConverter2());
		registry.addConverterFactory(new StringToPeopleConverterFactory2());
		registry.addFormatter(new PeopleFormatter2("/"));
		registry.addFormatterForFieldType(People.class, new PeopleFormatter2("/"));
		registry.addFormatterForFieldAnnotation(new PeopleFormatAnnotationFormatterFactory2());
	}

}
