package org.ruanwei.demo.springframework.integration.jms.xml;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MyMessageListener implements MessageListener {
	private static Log logger = LogFactory.getLog(MyMessageListener.class);

	@Override
	public void onMessage(Message message) {
		logger.info("onMessage(Message message)" + message);
		try {
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;

				logger.info("Received Message From qqq3:"
						+ textMessage.getText());

			} else if (message instanceof ObjectMessage) {
				ObjectMessage objMessage = (ObjectMessage) message;
				logger.info("Received Message From qqq3:"
						+ objMessage.getObject());
			}
		} catch (JMSException e) {
			logger.error(e);
		}
	}
}
