package org.ruanwei.demo.springframework.core.ioc.databinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

public class PeoplePropertyEditorRegistrar2 implements PropertyEditorRegistrar {
	private static Log log = LogFactory.getLog(PeoplePropertyEditorRegistrar2.class);

	public PeoplePropertyEditorRegistrar2() {
		log.info("PeoplePropertyEditorRegistrar2()");
	}

	@Override
	public void registerCustomEditors(PropertyEditorRegistry registry) {
		log.info("registerCustomEditors(PropertyEditorRegistry registry)" + registry);

		registry.registerCustomEditor(People.class, new PeoplePropertyEditor2());
	}

}
