package org.ruanwei.demo.springframework.core.ioc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("production")
@Component("house")
public class House_2 extends AbsHouse {
	private static Log log = LogFactory.getLog(House_2.class);

	@Value("${house.name:houseName_def}")
	private String houseName;
	
	@Value("${house.host.production:production_def}")
	private String hostName;

	@Override
	public String toString() {
		return "House_2 [hostName=" + hostName + "]" + super.toString();
	}

}
