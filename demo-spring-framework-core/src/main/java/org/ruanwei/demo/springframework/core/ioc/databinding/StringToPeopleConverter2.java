package org.ruanwei.demo.springframework.core.ioc.databinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.springframework.core.convert.converter.Converter;

/**
 * Take care to ensure that your Converter implementation is thread-safe.
 * 
 */
public final class StringToPeopleConverter2 implements Converter<String, People> {
	private static Log log = LogFactory.getLog(StringToPeopleConverter2.class);

	public StringToPeopleConverter2() {
		log.info("StringToPeopleConverter2()");
	}

	@Override
	public People convert(String source) {
		log.info("convert(String source)" + source);
		if (source == null || source.isEmpty()) {
			return null;
		}
		String[] kv = source.split("/");
		return new People(kv[0], Integer.parseInt(kv[1]));
	}

}
