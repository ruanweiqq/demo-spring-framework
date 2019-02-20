package org.ruanwei.demo.springframework.web.user.web.form.conversion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface UserFormFormat {

	Separator separator() default Separator.SLASH;

	enum Separator {
		SLASH, COMMA, SEMICOLON, COLON
	}
}
