package org.ruanwei.demo.springframework.integration.jms.annotation;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.integration.jms.Email;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;

@Component("myMessageConverter")
public class MyMessageConverter implements MessageConverter {
	private static Log logger = LogFactory.getLog(MyMessageConverter.class);

	private SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();

	@Override
	public Message toMessage(Object object, Session session)
			throws JMSException, MessageConversionException {
		logger.info("toMessage(Object object, Session session)" + object);
		if (object instanceof Email) {
			return session.createObjectMessage((Email) object);
		} else {
			return simpleMessageConverter.toMessage(object, session);
		}
	}

	@Override
	public Object fromMessage(Message message) throws JMSException,
			MessageConversionException {
		logger.info("fromMessage(Message message)" + message);
		if (message instanceof ObjectMessage) {
			Serializable obj = ((ObjectMessage) message).getObject();
			if (obj instanceof Email) {
				return (Email) obj;
			}
		}
		return simpleMessageConverter.fromMessage(message);
	}

}
