package org.ruanwei.demo.springframework.integration.jms.xml;

import java.io.Serializable;
import java.util.Map;

import javax.jms.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.integration.jms.Email;

/**
 * send back a response Message if a handler method returns a non-void value.
 * 
 * @author ruanwei
 *
 */
public class MyMessageDelegate implements MessageDelegate {
	private static Log logger = LogFactory.getLog(MyMessageDelegate.class);

	// MapMessage转换为Map
	@Override
	public void handleMessage(Map message) {
		logger.info("handleMessage(Map message)" + message);
	}

	// BytesMessage转换为byte[]
	@Override
	public void handleMessage(byte[] message) {
		logger.info("handleMessage(byte[] message)" + message);
	}

	// ObjectMessage转换为对应的Serializable
	@Override
	public void handleMessage(Serializable message) {
		logger.info("handleMessage(Serializable message)" + message);
	}

	// TextMessage转换为String
	@Override
	public String handleMessage(String message) {
		logger.info("handleMessage(String message)" + message);
		logger.info("Received Message From qqq1:" + message);
		String replyMessage = "Message From Listener1";
		logger.info("Send Message to qqq2:" + replyMessage);
		return replyMessage;
	}

	// ObjectMessage转换为对应的Email
	@Override
	public Email handleMessage(Email message) {
		logger.info("handleMessage(Email message)" + message);
		logger.info("Received Message From qqq1:" + message);
		logger.info("Send Message to qqq2:" + message);
		return message;
	}
	
	//do not use any MessageConvertor including SimpleMessageConverter(spring-jms.jar)
	@Override
	public void handleMessage(Message message) {
		logger.info("handleMessage(Message message)" + message);
	}

}
