package org.ruanwei.demo.springframework.web.user.web.form.conversion;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.web.user.web.form.UserForm;
import org.ruanwei.demo.springframework.web.user.web.form.conversion.UserFormFormat.Separator;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

/**
 * 
 * @author Administrator
 *
 */
public final class UserFormFormatAnnotationFormatterFactory implements
		AnnotationFormatterFactory<UserFormFormat> {
	private static Log log = LogFactory
			.getLog(UserFormFormatAnnotationFormatterFactory.class);

	public UserFormFormatAnnotationFormatterFactory() {
		log.info("UserFormFormatAnnotationFormatterFactory()");
	}

	@Override
	public Set<Class<?>> getFieldTypes() {
		log.info("getFieldTypes()");
		return new HashSet<Class<?>>(
				Arrays.asList(new Class<?>[] { UserForm.class }));
	}

	@Override
	public Printer<UserForm> getPrinter(UserFormFormat annotation,
			Class<?> fieldType) {
		log.info("getPrinter(UserFormFormat annotation, Class<?> fieldType)");
		return configureFormatterFrom(annotation, fieldType);
	}

	@Override
	public Parser<UserForm> getParser(UserFormFormat annotation,
			Class<?> fieldType) {
		log.info("getParser(UserFormFormat annotation, Class<?> fieldType)");
		return configureFormatterFrom(annotation, fieldType);
	}

	private Formatter<UserForm> configureFormatterFrom(
			UserFormFormat annotation, Class<?> fieldType) {
		Separator separator = annotation.separator();
		if (separator == Separator.SLASH) {
			return new UserFormFormatter("/");
		} else if (separator == Separator.COMMA) {
			return new UserFormFormatter(",");
		} else {
			return new UserFormFormatter("/");
		}
	}

}
