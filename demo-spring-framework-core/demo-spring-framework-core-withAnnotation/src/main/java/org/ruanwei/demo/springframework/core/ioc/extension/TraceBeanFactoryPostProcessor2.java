package org.ruanwei.demo.springframework.core.ioc.extension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class TraceBeanFactoryPostProcessor2 implements BeanFactoryPostProcessor {
	private static Log log = LogFactory.getLog(TraceBeanFactoryPostProcessor2.class);

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		log.debug("postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)" + beanFactory);
		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
		for (String beanName : beanDefinitionNames) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
			log.debug(beanName + "=====" + beanDefinition);
			MutablePropertyValues mpv = beanDefinition.getPropertyValues();
			PropertyValue[] pvArray = mpv.getPropertyValues();
			for (PropertyValue pv : pvArray) {
				log.debug("\t" + pv.getName() + "===" + pv.getValue());
			}
		}
	}

}
