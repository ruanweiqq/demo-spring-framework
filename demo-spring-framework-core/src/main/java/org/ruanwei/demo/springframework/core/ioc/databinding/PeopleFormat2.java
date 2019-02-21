package org.ruanwei.demo.springframework.core.ioc.databinding;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface PeopleFormat2 {
	Separator separator() default Separator.SLASH;

	enum Separator {
		SLASH, COMMA, SEMICOLON, COLON
	}
}
