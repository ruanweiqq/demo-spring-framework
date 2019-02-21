package org.ruanwei.demo.springframework.integration.jms.annotation;

import java.io.Serializable;
import java.util.Map;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component("myMessageListener")
public class MyMessageListener {
	private static Log logger = LogFactory.getLog(MyMessageListener.class);

	@Autowired
	@Qualifier("activeMQQueue3")
	private Destination replyDestination;

	@JmsListener(id = "messageListenerContainer1", destination = "qqq1", concurrency = "3")
	@SendTo("qqq2")
	public Object handleMessage1(Message message, Session session,
			@Headers Map headers, @Payload Email payload, Email email) {
		logger.info("handleMessage1(Message message, Session session)"
				+ message);
		try {
			logger.info("headers=" + headers);
			logger.info("payload=" + payload);
			logger.info("email=" + email);
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				logger.info("Received Message From qqq1:"
						+ textMessage.getText());
				String replyMessage = "Message From Listener1";
				logger.info("Send Message To qqq2:" + replyMessage);
				return replyMessage;
			} else if (message instanceof ObjectMessage) {
				ObjectMessage objMessage = (ObjectMessage) message;
				Serializable object = objMessage.getObject();
				logger.info("Received Message From qqq1:" + object);
				logger.info("Send Message To qqq2:" + object);
				return object;
			}
		} catch (JMSException e) {
			logger.error(e);
		}
		return null;
	}

	@JmsListener(id = "messageListenerContainer2", destination = "qqq2", concurrency = "3")
	public void handleMessage2(Message message, Session session,
			@Headers Map headers, @Payload Email payload, Email email) {
		logger.info("handleMessage2(Message message, Session session)"
				+ message);
		try {
			logger.info("headers=" + headers);
			logger.info("payload=" + payload);
			logger.info("email=" + email);
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

	@JmsListener(id = "messageListenerContainer3", destination = "qqq3", concurrency = "3")
	public void handleMessage3(Message message, Session session,
			@Headers Map headers, @Payload Email payload, Email email) {
		logger.info("handleMessage3(Message message, Session session)"
				+ message);
		try {
			logger.info("headers=" + headers);
			logger.info("payload=" + payload);
			logger.info("email=" + email);
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

	/**
	 * The annotated endpoint infrastructure creates a message listener
	 * container behind the scenes for each annotated method, using a
	 * JmsListenerContainerFactory. Such a container is not registered against
	 * the application context but can be easily located for management purposes
	 * using the JmsListenerEndpointRegistry bean.
	 * 
	 */
	public org.springframework.messaging.Message<String> onMessage(
			Message message, Session session,
			@Header("jms_correlationId") String jms_correlationId,
			@Headers Map headers, @Payload String payload) {
		logger.info("onMessage(Message message)" + message);
		try {
			logger.info("jms_correlationId=" + jms_correlationId);
			logger.info("headers=" + headers);
			logger.info("payload=" + payload);
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				logger.info("textMessage=" + textMessage.getText());

				org.springframework.messaging.Message<String> response = MessageBuilder
						.withPayload("hello")
						.setHeader("myHeader", "myHeaderValue").build();
				return response;
				// return JmsResponse.forQueue(response, "status");
			}
		} catch (JMSException e) {
			logger.error(e);
		}
		return null;
	}
}
