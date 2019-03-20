package org.ruanwei.demo.springframework.integration.jms.annotation;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

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
@Component("jmsQueueClient")
public class JmsQueueClient {
	private static Log log = LogFactory.getLog(JmsQueueClient.class);

	@Autowired
	private JmsTemplate jmsTemplate;

	// @Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;

	@Autowired
	@Qualifier("activeMQQueue1")
	private Destination destination;
	
	@Autowired
	@Qualifier("activeMQQueue2")
	private Destination replyDestination;

	public void sendTextMessage(String hello) {
		log.info("sendTextMessage(String hello)");
		this.jmsTemplate.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message message = session.createTextMessage(hello);
				message.setJMSReplyTo(replyDestination);
				message.setJMSCorrelationID("123456");
				//message.setIntProperty("myIntProperty", 1234);
				return message;
			}
		});
	}

	public void sendMessage(Object message) {
		log.info("sendMessage(Object Message)");
		jmsTemplate.convertAndSend(destination, message,
				new MessagePostProcessor() {
					public Message postProcessMessage(Message message)
							throws JMSException {
						message.setJMSReplyTo(replyDestination);
						message.setJMSCorrelationID("123456");
						message.setIntProperty("myIntProperty", 1234);
						return message;
					}
				});
	}

}
