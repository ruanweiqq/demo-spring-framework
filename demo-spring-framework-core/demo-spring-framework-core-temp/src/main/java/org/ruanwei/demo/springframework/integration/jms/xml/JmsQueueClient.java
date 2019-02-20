package org.ruanwei.demo.springframework.integration.jms.xml;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;

/**
 * The JmsTemplate delegates the resolution of a destination name to a JMS
 * destination object to an implementation of the interface
 * DestinationResolver.DynamicDestinationResolver is the default implementation
 * used by JmsTemplate and accommodates resolving dynamic destinations.
 * DynamicDestinationResolver is the default implementation used by JmsTemplate
 * and accommodates resolving dynamic destinations.
 * 
 * MessagePostProcessor SessionCallback and ProducerCallback
 * 
 * @author ruanwei
 *
 */
public class JmsQueueClient {
	private static Log log = LogFactory.getLog(JmsQueueClient.class);

	private JmsTemplate jmsTemplate;
	private JmsMessagingTemplate jmsMessagingTemplate;
	private Destination destination;
	private Destination replyDestination;

	public void sendTextMessage(String hello) {
		log.info("sendTextMessage(String hello)");
		this.jmsTemplate.send(destination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				Message message = session.createTextMessage(hello);
				message.setJMSReplyTo(replyDestination);
				message.setJMSCorrelationID("123456");
				message.setIntProperty("myIntProperty", 1234);
				return message;
			}
		});
	}

	/**
	 * SimpleMessageConverter(spring-jms.jar) is a simple message converter
	 * which is able to handle TextMessages, BytesMessages, MapMessages, and
	 * ObjectMessages. Used as default conversion strategy by
	 * org.springframework.jms.core.JmsTemplate, for convertAndSend and
	 * receiveAndConvert operations.
	 */
	public void sendMessage(Object message) {
		log.info("sendMessage(Object message)");
		jmsTemplate.convertAndSend(destination, message,
				new MessagePostProcessor() {
					@Override
					public Message postProcessMessage(Message message)
							throws JMSException {
						message.setJMSReplyTo(replyDestination);
						message.setJMSCorrelationID("123456");
						message.setIntProperty("myIntProperty", 1234);
						return message;
					}
				});
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setJmsMessagingTemplate(
			JmsMessagingTemplate jmsMessagingTemplate) {
		this.jmsMessagingTemplate = jmsMessagingTemplate;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public void setReplyDestination(Destination replyDestination) {
		this.replyDestination = replyDestination;
	}

}
