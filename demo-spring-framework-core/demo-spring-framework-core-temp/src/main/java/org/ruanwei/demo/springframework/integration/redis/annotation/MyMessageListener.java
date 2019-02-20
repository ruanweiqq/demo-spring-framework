package org.ruanwei.demo.springframework.integration.redis.annotation;

import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component("myRedisMessageListener1")
public class MyMessageListener implements MessageListener {
	private static Log logger = LogFactory.getLog(MyMessageListener.class);

	@Override
	public void onMessage(Message message, byte[] pattern) {
		logger.info("onMessage(Message message, byte[] pattern)" + message);
		try {
			String body = new String(message.getBody(), "utf-8");
			String channel = new String(message.getChannel(), "utf-8");
			String p = new String(pattern, "utf-8");
			logger.info("channel=" + channel + " body=" + body + " pattern="
					+ p);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
	}
}
