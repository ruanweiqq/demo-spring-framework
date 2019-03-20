package org.ruanwei.demo.springframework.integration.redis.xml;

import java.io.Serializable;
import java.util.Map;

import org.ruanwei.demo.springframework.integration.jms.Email;

public interface MessageDelegate {

	void handleMessage(Map message);

	void handleMessage(byte[] message);

	void handleMessage(Serializable message);

	void handleMessage(String message);

	void handleMessage(Serializable message, String channel);

	Email handleMessage(Email message);
}
