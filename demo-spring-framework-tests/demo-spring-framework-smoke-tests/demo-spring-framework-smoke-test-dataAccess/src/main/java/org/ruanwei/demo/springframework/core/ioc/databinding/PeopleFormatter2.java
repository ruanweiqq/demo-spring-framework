package org.ruanwei.demo.springframework.core.ioc.databinding;

import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.springframework.format.Formatter;

/**
 * 
 * @author Administrator
 *
 */
public final class PeopleFormatter2 implements Formatter<People> {
	private static Log log = LogFactory.getLog(PeopleFormatter2.class);

	private String delimiter;
	
	public PeopleFormatter2() {
		log.info("PeopleFormatter2()");
		this.delimiter = "/";
	}

	public PeopleFormatter2(String delimiter) {
		log.info("PeopleFormatter2(String delimiter)" + delimiter);
		this.delimiter = delimiter;
	}

	@Override
	public String print(People people, Locale locale) {
		log.info("print(People2 people, Locale locale) " + people);
		if (people == null) {
			return "";
		}
		return people.toString();
	}

	@Override
	public People parse(String text, Locale locale) throws ParseException {
		log.info("parse(String text, Locale locale) " + text);
		if (text == null || text.isEmpty()) {
			return null;
		}
		String[] kv = text.split(delimiter);
		return new People(kv[0], Integer.parseInt(kv[1]));
	}

}
