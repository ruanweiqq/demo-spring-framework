package org.ruanwei.demo.springframework.integration.jms.xml;

import java.io.Serializable;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.integration.jms.Email;
import org.springframework.jms.listener.SessionAwareMessageListener;

public class MySessionAwareMessageListener implements
		SessionAwareMessageListener<Message> {
	private static Log logger = LogFactory
			.getLog(MySessionAwareMessageListener.class);

	private Destination replyDestination;

	@Override
	public void onMessage(Message message, Session session) {
		logger.info("onMessage(Message message, Session session)" + message);
		try {
			Message replyMessage = null;
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				logger.info("Received Message From qqq2:"
						+ textMessage.getText());
				replyMessage = session
						.createTextMessage("Message From Listener2");
				logger.info("Send Message To qqq3:"
						+ ((TextMessage) replyMessage).getText());
			} else if (message instanceof ObjectMessage) {
				ObjectMessage objMessage = (ObjectMessage) message;
				Serializable object = objMessage.getObject();
				logger.info("Received Message From qqq2:" + object);
				replyMessage = session.createObjectMessage(object);
				logger.info("Send Message To qqq3:"
						+ ((ObjectMessage) replyMessage).getObject());
			}

			MessageProducer messageProducer = session
					.createProducer(replyDestination);
			messageProducer.send(replyMessage);
		} catch (JMSException e) {
			logger.error(e);
		}
	}

	public void setReplyDestination(Destination replyDestination) {
		this.replyDestination = replyDestination;
	}
}
