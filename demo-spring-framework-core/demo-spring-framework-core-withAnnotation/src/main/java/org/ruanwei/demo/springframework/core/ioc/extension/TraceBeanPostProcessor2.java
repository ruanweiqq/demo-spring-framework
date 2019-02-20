package org.ruanwei.demo.springframework.core.ioc.extension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ruanwei.demo.springframework.core.ioc.AbsHouse;
import org.ruanwei.demo.springframework.core.ioc.Family;
import org.ruanwei.demo.springframework.core.ioc.People;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class TraceBeanPostProcessor2 implements BeanPostProcessor {
	private static Log log = LogFactory.getLog(TraceBeanPostProcessor2.class);

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		log.debug("postProcessBeforeInitialization(Object bean, String beanName) " + beanName + "=" + bean);
		if (bean instanceof People) {
			People people = (People) bean;
			log.info("postProcessBeforeInitialization====================" + people);
		} else if (bean instanceof Family) {
			Family family = (Family) bean;
			log.info("postProcessBeforeInitialization====================" + family);
		} else if (bean instanceof AbsHouse) {
			AbsHouse house = (AbsHouse) bean;
			log.info("postProcessBeforeInitialization====================" + house);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		log.debug("postProcessAfterInitialization(Object bean, String beanName)" + beanName + "=" + bean);
		if (bean instanceof People) {
			People people = (People) bean;
			log.info("postProcessAfterInitialization====================" + people);
		} else if (bean instanceof Family) {
			Family family = (Family) bean;
			log.info("postProcessAfterInitialization====================" + family);
		} else if (bean instanceof AbsHouse) {
			AbsHouse house = (AbsHouse) bean;
			log.info("postProcessAfterInitialization====================" + house);
		}
		return bean;
	}

}
