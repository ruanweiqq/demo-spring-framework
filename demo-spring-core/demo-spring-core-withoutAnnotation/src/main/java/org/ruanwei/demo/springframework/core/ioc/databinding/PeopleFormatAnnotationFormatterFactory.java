package org.ruanwei.demo.springframework.core.ioc.databinding;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.ruanwei.demo.springframework.core.ioc.databinding.PeopleFormat.Separator;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

/**
 * 
 * @author Administrator
 *
 */
public final class PeopleFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<PeopleFormat> {
	private static Log log = LogFactory.getLog(PeopleFormatAnnotationFormatterFactory.class);

	public PeopleFormatAnnotationFormatterFactory() {
		log.info("PeopleFormatAnnotationFormatterFactory()");
	}

	@Override
	public Set<Class<?>> getFieldTypes() {
		log.info("getFieldTypes()");
		return new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { People.class }));
	}

	@Override
	public Printer<People> getPrinter(PeopleFormat annotation, Class<?> fieldType) {
		log.info("getPrinter(MyPeopleFormat annotation, Class<?> fieldType)");
		return configureFormatterFrom(annotation, fieldType);
	}

	@Override
	public Parser<People> getParser(PeopleFormat annotation, Class<?> fieldType) {
		log.info("getParser(MyPeopleFormat annotation, Class<?> fieldType)");
		return configureFormatterFrom(annotation, fieldType);
	}

	private Formatter<People> configureFormatterFrom(PeopleFormat annotation, Class<?> fieldType) {
		Separator separator = annotation.separator();
		if (separator == Separator.SLASH) {
			return new PeopleFormatter("/");
		} else if (separator == Separator.COMMA) {
			return new PeopleFormatter(",");
		} else {
			return new PeopleFormatter("/");
		}
	}

}
