package org.ruanwei.demo.springframework.core.ioc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("development")
@Component("house")
public class House_1 extends AbsHouse {
	private static Log log = LogFactory.getLog(House_1.class);

	@Value("${house.name:houseName_def}")
	private String houseName;

	@Value("${house.host.development:development_def}")
	private String hostName;

	@Override
	public String toString() {
		return "House_1 [hostName=" + hostName + "]" + super.toString();
	}

}
