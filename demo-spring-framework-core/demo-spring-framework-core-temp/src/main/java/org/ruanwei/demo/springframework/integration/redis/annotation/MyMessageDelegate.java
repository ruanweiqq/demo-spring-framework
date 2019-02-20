package org.ruanwei.demo.springframework.integration.redis.annotation;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.integration.jms.Email;
import org.springframework.stereotype.Component;

/**
 * 
 * @author ruanwei
 *
 */
@Component("myRedisMessageListener2")
public class MyMessageDelegate implements MessageDelegate {
	private static Log logger = LogFactory.getLog(MyMessageDelegate.class);

	@Override
	public void handleMessage(Map message) {
		logger.info("handleMessage(Map message)" + message);
	}

	@Override
	public void handleMessage(byte[] message) {
		logger.info("handleMessage(byte[] message)" + message);
	}

	@Override
	public void handleMessage(Serializable message) {
		logger.info("handleMessage(Serializable message)" + message);
	}

	@Override
	public void handleMessage(String message) {
		logger.info("handleMessage(String message)" + message);
		logger.info("Received Message From qqq1:" + message);
		String replyMessage = "Message From Listener1";
		logger.info("Send Message to qqq2:" + replyMessage);
	}

	@Override
	public void handleMessage(Serializable message, String channel) {
		logger.info("handleMessage(Message message, String channel)" + message
				+ channel);
	}

	@Override
	public Email handleMessage(Email message) {
		logger.info("handleMessage(Email message)" + message);
		logger.info("Received Message From qqq1:" + message);
		logger.info("Send Message to qqq2:" + message);
		return message;
	}

}
