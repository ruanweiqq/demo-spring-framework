package org.ruanwei.demo.springframework.core.ioc.extension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.Family;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.springframework.beans.factory.FactoryBean;

public class MyFamilyFactoryBean implements FactoryBean<Family> {
	private static Log log = LogFactory.getLog(MyFamilyFactoryBean.class);

	private String familyName;
	private int familyCount;
	private People father;

	public MyFamilyFactoryBean(String familyName, int familyCount, People father) {
		this.familyName = familyName;
		this.familyCount = familyCount;
		this.father = father;
		log.info("MyFamilyFactoryBean(String familyName, int familyCount, People father)"
				+ this);
	}

	@Override
	public Family getObject() throws Exception {
		Family family = new Family(this.familyName, this.familyCount,
				this.father);
		log.info("getObject()" + family);
		return family;
	}

	@Override
	public Class<Family> getObjectType() {
		log.info("getObjectType()");
		return Family.class;
	}

	@Override
	public boolean isSingleton() {
		log.info("isSingleton()");
		return true;
	}

	@Override
	public String toString() {
		return "MyFamilyFactoryBean [familyName=" + familyName
				+ ", familyCount=" + familyCount + ", father=" + father + "]";
	}

}
