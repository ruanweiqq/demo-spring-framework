package org.ruanwei.demo.springframework.core.ioc.extension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.Family;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("familyx")
public class MyFamilyFactoryBean2 implements FactoryBean<Family> {
	private static Log log = LogFactory.getLog(MyFamilyFactoryBean2.class);

	private String familyName;
	private int familyCount;
	private People father;

	@Autowired
	public MyFamilyFactoryBean2(
			@Value("${family.x.familyName}") String familyName,
			@Value("${family.familyCount}") int familyCount, People father) {
		this.familyName = familyName;
		this.familyCount = familyCount;
		this.father = father;
		log.info("MyFamilyFactoryBean(String familyName, int familyCount, People2 father)"
				+ this);
	}

	@Override
	public Family getObject() throws Exception {
		log.info("getObject()");
		Family family = new Family(this.familyName, this.familyCount, null);
		return family;
	}

	@Override
	public Class<?> getObjectType() {
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
		return "MyFamilyFactoryBean2 [familyName=" + familyName
				+ ", familyCount=" + familyCount + ", father=" + father + "]";
	}

}
