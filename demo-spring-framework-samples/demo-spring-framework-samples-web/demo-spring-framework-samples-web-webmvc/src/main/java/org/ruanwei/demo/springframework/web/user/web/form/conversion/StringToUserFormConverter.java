package org.ruanwei.demo.springframework.web.user.web.form.conversion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ruanwei.demo.springframework.web.user.web.form.UserForm;
import org.ruanwei.demo.util.Counter;
import org.springframework.core.convert.converter.Converter;

/**
 * Take care to ensure that your Converter implementation is thread-safe.
 * 
 * @author Administrator
 *
 */
final class StringToUserFormConverter implements Converter<String, UserForm> {
	private static final Logger logger = LogManager.getLogger();

	public StringToUserFormConverter() {
		logger.info("StringToUserFormConverter()==================" + Counter.getCount());
	}

	@Override
	public UserForm convert(String source) {
		logger.info("convert(String source)==================" + Counter.getCount()+" source=" + source);
		if (source.length() == 0) {
			return null;
		}
		String[] kv = source.split("/");
		return new UserForm(kv[0], Integer.parseInt(kv[1]));
	}

}
