package org.ruanwei.demo.springframework.dataAccess.springdata.redis;

import java.io.Serializable;
import java.util.Map;

public interface MessageDelegate {

	void handleMessage(Map message);

	void handleMessage(byte[] message);

	void handleMessage(Serializable message);

	void handleMessage(String message);

	void handleMessage(Serializable message, String channel);

	Email handleMessage(Email message);
}
