package org.ruanwei.demo.springframework.core.ioc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;

/**
 * You can apply @Autowired to constructors, fields and methods with arbitrary
 * names and/or multiple arguments. <br/>
 * Fine-tuning annotation-based autowiring with primary and qualifier.<br/>
 * For a fallback match, the bean name is considered a default qualifier value.
 * <br/>
 * 
 * <li>Spring’s @Autowired @Component("beanName") @Qualifier("fristCandidate") @Primary.
 * byType, then by Qualifiers, then by Name.
 * 
 * <li>Spring’s @Value("${placeholder}") @Value("#{beanName}")
 * by Name.
 * 
 * <li>JSR-250’s @Resource(name=”beanName”).
 * by Name, then by Type, then by Qualifiers.
 * 
 * <li>JSR-330’s @Inject @Named("beanName") @Named("fristCandidate")/@Qualifier("fristCandidate").
 * by Type,then by Named/Qualifier, then by Name.
 *
 * <li>@Required.
 * 
 * <li>注意@Autowired注入集合时，不是注入类型匹配的集合对象，而是注入所有匹配类型的元素到集合中
 * 
 * @author Administrator
 *
 */
public abstract class AbsHouse {
	private static Log log = LogFactory.getLog(AbsHouse.class);

	@Value("${abshouse.name:abshouseName_def}")
	private String houseName;

	@Value("1,2")
	private Integer[] someArray;

	@Value("3,4")
	private List<Integer> someList;

	@Value("5,6")
	private Set<Integer> someSet;

	@Value("a=1,b=2")
	private Properties someProperties;

	// @Value("c=3,d=4")
	private Map<String, Integer> someMap;

	@Value("#{someList2}")
	private List<Integer> someList2;

	@Value("#{someSet2}")
	private Set<Integer> someSet2;

	@Value("#{someProperties2}")
	private Properties someProperties2;

	@Value("#{someMap2}")
	private Map<String, Integer> someMap2;

	@Value("#{systemProperties['java.version']?:'1.8'}")
	private String javaVersion;

	// JSR-250
	@Resource(name = "someList2")
	private List<Integer> someList3;

	@Resource(name = "someSet2")
	private Set<Integer> someSet3;

	@Resource(name = "someProperties2")
	private Properties someProperties3;

	@Resource(name = "someMap2")
	private Map<String, Integer> someMap3;

	@Qualifier("someList3")
	@Autowired(required = false)
	private List<Integer> someList4;

	@Qualifier("someSet3")
	@Autowired(required = false)
	private Set<Integer> someSet4;

	@Qualifier("someProperties3")
	@Autowired(required = false)
	private Properties someProperties4;

	@Qualifier("someMap3")
	@Autowired(required = false)
	private Map<String, Integer> someMap4;

	// JSR-330
	@Named("someList3")
	// @Inject
	private List<Integer> someList5;

	@Named("someSet3")
	// @Inject
	private Set<Integer> someSet5;

	@Named("someProperties3")
	//@Inject
	private Properties someProperties5;

	@Named("someMap3")
	// @Inject
	private Map<String, Integer> someMap5;

	@Resource(name = "someField1")
	private double someField1;

	@Resource(name = "someField2")
	private String someField2;

	@Resource(name = "someField3")
	private String someField3;

	// JSR-250.Initialization callback.等价于<bean init-method="init"/>.
	@PostConstruct
	public void init() {
		log.info("====================init()");
	}

	// JSR-250.Destruction callback.等价于<bean destroy-method="destroy"/>.
	@PreDestroy
	public void destroy() {
		log.info("====================destroy()");
	}

	@Override
	public String toString() {
		return "AbsHouse [houseName=" + houseName + ", someArray=" + Arrays.toString(someArray) + ", someList="
				+ someList + ", someSet=" + someSet + ", someProperties=" + someProperties + ", someMap=" + someMap
				+ ", someList2=" + someList2 + ", someSet2=" + someSet2 + ", someProperties2=" + someProperties2
				+ ", someMap2=" + someMap2 + ", javaVersion=" + javaVersion + ", someList3=" + someList3 + ", someSet3="
				+ someSet3 + ", someProperties3=" + someProperties3 + ", someMap3=" + someMap3 + ", someList4="
				+ someList4 + ", someSet4=" + someSet4 + ", someProperties4=" + someProperties4 + ", someMap4="
				+ someMap4 + ", someList5=" + someList5 + ", someSet5=" + someSet5 + ", someProperties5="
				+ someProperties5 + ", someMap5=" + someMap5 + ", someField1=" + someField1 + ", someField2="
				+ someField2 + ", someField3=" + someField3 + "]";
	}
}
