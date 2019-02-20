package org.ruanwei.demo.springframework.dataAccess.oxm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Component;

/**
 * @author ruanwei
 *
 */
@Component("marshallerClient")
public class MarshallerClient {
	private static Log log = LogFactory.getLog(MarshallerClient.class);

	// 具体用法参考Spring中关于OXM的部分JAXB/Castor/XMLBeans/JiBX/XStream
	@Qualifier("castorMarshaller")
	//@Autowired
	private Marshaller marshaller;

	@Qualifier("castorMarshaller")
	//@Autowired
	private Unmarshaller unmarshaller;

	public void saveSettings(Settings settings) {
		log.info("saveSettings(Settings settings)" + settings);

		try (FileOutputStream os = new FileOutputStream("settings.xml")) {
			this.marshaller.marshal(settings, new StreamResult(os));
		} catch (XmlMappingException | IOException e) {
			e.printStackTrace();
		}
	}

	public Settings loadSettings() {
		log.info("loadSettings()");
		Settings settings = null;
		try (FileInputStream is = new FileInputStream("settings.xml")) {
			settings = (Settings) this.unmarshaller.unmarshal(new StreamSource(is));

		} catch (XmlMappingException | IOException e) {
			e.printStackTrace();
		}
		return settings;
	}
}
