package org.ruanwei.demo.springframework.core.ioc.databinding;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.People;

public class PeoplePropertyEditor extends PropertyEditorSupport {
	private static Log log = LogFactory.getLog(PeoplePropertyEditor.class);

	public PeoplePropertyEditor() {
		log.debug("PeoplePropertyEditor()");
	}

	@Override
	public void setAsText(String text) {
		log.info("setAsText(String text)" + text);
		String[] kv = text.split("/");
		setValue(new People(kv[0], Integer.parseInt(kv[1])));
	}

	@Override
	public String getAsText() {
		People poeple = (People) getValue();
		log.info("getAsText()" + poeple);
		return poeple.toString();
	}
}
