package org.ruanwei.demo.springframework.integration.email.xml;

import java.io.File;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class EmailClient {
	private static Log log = LogFactory.getLog(EmailClient.class);

	private MailSender mailSender;
	private JavaMailSender javaMailSender;

	public void sendEmail() {
		log.info("sendEmail()");

		SimpleMailMessage message1 = new SimpleMailMessage();
		message1.setTo("ruanweiqq@163.com");
		message1.setFrom("ruanweiqq@163.com");
		message1.setSubject("New Mail1");
		message1.setText("Hello World1 !");

		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				mimeMessage.setRecipient(Message.RecipientType.TO,
						new InternetAddress("ruanweiqq@163.com"));
				mimeMessage.setFrom(new InternetAddress("ruanweiqq@163.com"));
				mimeMessage.setSubject("New Mail2");
				mimeMessage.setText("Hello World2 !");
			}
		};

		MimeMessage message3 = javaMailSender.createMimeMessage();
		// use the true flag to indicate you need a multipart message
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message3, true);
			helper.setTo("ruanweiqq@163.com");
			helper.setFrom("ruanweiqq@163.com");
			helper.setSubject("New Mail3");
			helper.setText("<html><body><img src='cid:identifier1234'></body></html>", true);
			ClassPathResource res = new ClassPathResource("email.jpeg");
			helper.addAttachment("CoolImage.jpg", res);
			helper.addInline("identifier1234", res);
		} catch (MessagingException ex) {
			log.error(ex);
		}

		try {
			this.mailSender.send(message1);
			this.javaMailSender.send(preparator);
			this.javaMailSender.send(message3);
		} catch (MailException ex) {
			log.error(ex);
		}
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

}
