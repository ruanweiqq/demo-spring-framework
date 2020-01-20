package org.ruanwei.demo.springframework.core.ioc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Profile("development")
@Component("house")
public class HouseDev extends AbsHouse {
	private static Log log = LogFactory.getLog(HouseDev.class);

	@Value("${house.name.override:RuanHouse_def}")
	private String houseName;

	@Value("${house.host.name.development:developmentHost_def}")
	private String hostName;

	@Override
	public String toString() {
		return "HouseDev [hostName=" + hostName + "]" + super.toString();
	}

}
