package org.ruanwei.demo.springframework.integration.jms.xml;

import java.io.Serializable;
import java.util.Map;

import javax.jms.Message;

import org.ruanwei.demo.springframework.integration.jms.Email;

public interface MessageDelegate {
	
	void handleMessage(Map message);

	void handleMessage(byte[] message);

	void handleMessage(Serializable message);
	
	String handleMessage(String message);
	
	Email handleMessage(Email message);

	void handleMessage(Message message);
}
