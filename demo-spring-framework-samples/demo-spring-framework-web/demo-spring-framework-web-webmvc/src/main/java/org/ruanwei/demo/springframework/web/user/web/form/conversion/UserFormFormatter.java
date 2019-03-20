package org.ruanwei.demo.springframework.web.user.web.form.conversion;

import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.web.user.web.form.UserForm;
import org.springframework.format.Formatter;

/**
 * 
 * @author Administrator
 *
 */
public final class UserFormFormatter implements Formatter<UserForm> {
	private static Log log = LogFactory.getLog(UserFormFormatter.class);
	
	private String delimiter;

	public UserFormFormatter() {
		log.info("UserFormFormatter()");
		this.delimiter = "/";
	}
	
	public UserFormFormatter(String delimiter) {
		log.info("UserFormFormatter(String delimiter)" + delimiter);
		this.delimiter = delimiter;
	}

	@Override
	public String print(UserForm people, Locale locale) {
		log.info("print(UserForm people, Locale locale) " + people);
		if (people == null) {
			return "UserForm";
		}
		return people.toString();
	}

	@Override
	public UserForm parse(String text, Locale locale) throws ParseException {
		log.info("parse(String text, Locale locale) " + text);
		if (text == null || text.isEmpty()) {
			return null;
		}
		String[] kv = text.split(delimiter);
		return new UserForm(kv[0], Integer.parseInt(kv[1]));
	}

}
