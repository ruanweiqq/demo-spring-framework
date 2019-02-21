package org.ruanwei.demo.springframework.integration.jms;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Email implements Serializable {
	private String from;
	private String to;
	private String title;
	private String content;

	public Email(String from, String to, String title, String content) {
		super();
		this.from = from;
		this.to = to;
		this.title = title;
		this.content = content;
	}

	@Override
	public String toString() {
		return "Email [from=" + from + ", to=" + to + ", title=" + title
				+ ", content=" + content + "]";
	}
}
