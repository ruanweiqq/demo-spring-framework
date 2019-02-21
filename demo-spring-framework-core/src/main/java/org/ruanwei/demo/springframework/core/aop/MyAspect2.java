package org.ruanwei.demo.springframework.core.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * 
 * execution - for matching method execution join points, this is the primary
 * pointcut designator you will use when working with Spring AOP.<br/>
 * within - limits matching to join points within certain types (simply the
 * execution of a method declared within a matching type when using Spring
 * AOP).<br/>
 * this - limits matching to join points (the execution of methods when using
 * Spring AOP) where the bean reference (Spring AOP proxy) is an instance of the
 * given type.<br/>
 * target - limits matching to join points (the execution of methods when using
 * Spring AOP) where the target object (application object being proxied) is an
 * instance of the given type.<br/>
 * args - limits matching to join points (the execution of methods when using
 * Spring AOP) where the arguments are instances of the given types.<br/>
 * 
 * @target - limits matching to join points (the execution of methods when using
 *         Spring AOP) where the class of the executing object has an annotation
 *         of the given type.<br/>
 * @args - limits matching to join points (the execution of methods when using
 *       Spring AOP) where the runtime type of the actual arguments passed have
 *       annotations of the given type(s).<br/>
 * @within - limits matching to join points within types that have the given
 *         annotation (the execution of methods declared in types with the given
 *         annotation when using Spring AOP).<br/>
 * @annotation - limits matching to join points where the subject of the join
 *             point (method being executed in Spring AOP) has the given
 *             annotation.<br/>
 *             bean(idOrNameOfBean) - This allows you to limit the matching of
 *             join points to a particular named Spring bean, or to a set of
 *             named Spring beans (when using wildcards). <br/>
 * 
 *             execute before the transactional advice (hence the lower order
 *             number.
 * 
 * @author Administrator
 *
 */
@Order(2)
@Aspect
@Component
public class MyAspect2 {
	private static Log log = LogFactory.getLog(MyAspect2.class);

	@Pointcut("within(org.ruanwei.demo.springframework.core.ioc.*)")
	public void myPointcut1() {
	}

	@Pointcut("execution(* *..Family.say*(..))")
	public void myPointcut2() {
	}

	@Pointcut("myPointcut1() && myPointcut2()")
	public void myPointcut() {
	}

	@Before("myPointcut() && this(t) && args(message)")
	public void before(JoinPoint jp, Object t, String message) {
		log.info("before() message=" + message + " JoinPoint=" + jp.toLongString() + " this=" + t);
	}

	@After("myPointcut() && this(t) && args(message)")
	public void after(JoinPoint jp, Object t, String message) {
		log.info("after() message=" + message + " JoinPoint=" + jp.toLongString() + " this=" + t);
	}

	@AfterReturning(pointcut = "myPointcut() && this(t) && args(message)", returning = "ret")
	public void afterReturning(JoinPoint jp, Object t, String message, Object ret) {
		log.info("afterReturning() message=" + message + " JoinPoint=" + jp + " this=" + t + " ret=" + ret);
	}

	@AfterThrowing(pointcut = "myPointcut() && this(t) && args(message)", throwing = "e")
	public void afterThrowing(JoinPoint jp, Object t, String message, Throwable e) {
		log.info("afterThrowing() message=" + message + " JoinPoint=" + jp + " this=" + t + " e=" + e);
	}

	@Around("myPointcut()  && args(message)")
	public Object around(ProceedingJoinPoint call, String message) throws Throwable {
		log.info("around() message=" + message + " call=" + call);

		StopWatch clock = new StopWatch("message=" + message);
		try {
			clock.start(call.toShortString());
			log.info(clock.prettyPrint());
			Object[] args = call.getArgs();
			for (Object arg : args) {
				log.info("around() arg=" + arg);
			}
			Object ret = call.proceed();
			log.info("around() ret=" + ret);
			return ret;
		} finally {
			clock.stop();
			log.info(clock.prettyPrint());
		}
	}

	@DeclareParents(value = "org.ruanwei.demo.springframework.core.aop.GoodImpl2", defaultImpl = HappyImpl2.class)
	public static Happy2 mixin;

	/**
	 * A join point is in the web layer if the method is defined in a type in
	 * the com.xyz.someapp.web package or any sub-package under that.
	 */
	@Pointcut("within(com.xyz.someapp.web..*)")
	public void inWebLayer() {
	}

	/**
	 * A join point is in the service layer if the method is defined in a type
	 * in the com.xyz.someapp.service package or any sub-package under that.
	 */
	@Pointcut("within(com.xyz.someapp.service..*)")
	public void inServiceLayer() {
	}

	/**
	 * A join point is in the data access layer if the method is defined in a
	 * type in the com.xyz.someapp.dao package or any sub-package under that.
	 */
	@Pointcut("within(com.xyz.someapp.dao..*)")
	public void inDataAccessLayer() {
	}

	/**
	 * A business service is the execution of any method defined on a service
	 * interface. This definition assumes that interfaces are placed in the
	 * "service" package, and that implementation types are in sub-packages.
	 *
	 * If you group service interfaces by functional area (for example, in
	 * packages com.xyz.someapp.abc.service and com.xyz.someapp.def.service)
	 * then the pointcut expression "execution(*
	 * com.xyz.someapp..service.*.*(..))" could be used instead.
	 *
	 * Alternatively, you can write the expression using the 'bean' PCD, like so
	 * "bean(*Service)". (This assumes that you have named your Spring service
	 * beans in a consistent fashion.)
	 */
	@Pointcut("execution(* com.xyz.someapp..service.*.*(..))")
	public void businessService() {
	}

	/**
	 * A data access operation is the execution of any method defined on a dao
	 * interface. This definition assumes that interfaces are placed in the
	 * "dao" package, and that implementation types are in sub-packages.
	 */
	@Pointcut("execution(* com.xyz.someapp.dao.*.*(..))")
	public void dataAccessOperation() {
	}

}
