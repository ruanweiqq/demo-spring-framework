package org.ruanwei.demo.springframework.core.ioc.databinding.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class PeopleSpringValidator2 implements Validator {
	private static Log log = LogFactory.getLog(PeopleSpringValidator2.class);

	@Override
	public boolean supports(Class<?> clazz) {
		log.info("supports(Class<?> clazz)" + clazz);
		return People.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		log.info("validate(Object target, Errors errors)" + target + errors);

		if (target == null) {
			errors.reject("org.ruanwei.demo.springframework.argument.null", new Object[] { "people" },
					"The argument must not be null.");
		}

		People people = (People) target;

		ValidationUtils.rejectIfEmpty(errors, "name", "org.ruanwei.demo.springframework.argument.empty",
				new Object[] { "name" }, "The argument must not be empty.");
		String name = people.getName();
		if (name.length() < 1 || name.length() > 10) {
			errors.rejectValue("name", "org.ruanwei.demo.springframework.argument.range",
					new Object[] { "name", name, 1, 10 }, "The argument must be between 1 and 10.");
		}

		int age = people.getAge();
		if (age < 0 || age > 100) {
			errors.rejectValue("age", "org.ruanwei.demo.springframework.argument.range",
					new Object[] { "age", age, 0, 100 }, "The argument must be between 0 and 100.");
		}

	}

}
