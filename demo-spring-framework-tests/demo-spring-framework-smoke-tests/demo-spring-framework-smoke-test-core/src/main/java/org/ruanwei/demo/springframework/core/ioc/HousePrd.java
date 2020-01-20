package org.ruanwei.demo.springframework.core.ioc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("production")
@Component("house")
public class HousePrd extends AbsHouse {
	private static Log log = LogFactory.getLog(HousePrd.class);

	@Value("${house.name.override:RuanHouse_def}")
	private String houseName;
	
	@Value("${house.host.name.production:productionHost_def}")
	private String hostName;

	@Override
	public String toString() {
		return "HousePrd [hostName=" + hostName + "]" + super.toString();
	}

}
