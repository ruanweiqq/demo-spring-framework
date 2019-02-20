package org.ruanwei.demo.springframework.core.ioc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FamilyFactory {
	private static Log log = LogFactory.getLog(FamilyFactory.class);

	// 2 Bean instantiation with a static factory method
	public static Family createInstance1(String familyName, int familyCount, People father) {
		log.info("createInstance1(String familyName, int familyCount, People father)" + familyName + familyCount
				+ father);
		return new Family(familyName, familyCount, father);
	}

	// 3 Bean instantiation using an instance factory method
	public Family createInstance2(String familyName, int familyCount, People father) {
		log.info("createInstance2(String familyName, int familyCount, People father)" + familyName + familyCount
				+ father);

		return createInstance1(familyName, familyCount, father);
	}

}
