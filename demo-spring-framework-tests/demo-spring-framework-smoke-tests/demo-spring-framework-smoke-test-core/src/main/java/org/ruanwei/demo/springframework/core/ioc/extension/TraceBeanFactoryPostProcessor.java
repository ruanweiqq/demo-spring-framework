package org.ruanwei.demo.springframework.core.ioc.extension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.util.Recorder;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(0)
@Component
public class TraceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static Log log = LogFactory.getLog(TraceBeanFactoryPostProcessor.class);

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		//log.debug("postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)" + beanFactory);
		Recorder.record("postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)", this.getClass());
		
		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
		for (String beanName : beanDefinitionNames) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
			//log.debug(beanName + "=====" + beanDefinition);
			MutablePropertyValues mpv = beanDefinition.getPropertyValues();
			PropertyValue[] pvArray = mpv.getPropertyValues();
			for (PropertyValue pv : pvArray) {
				//log.debug("\t" + pv.getName() + "===" + pv.getValue());
			}
		}
	}

}
