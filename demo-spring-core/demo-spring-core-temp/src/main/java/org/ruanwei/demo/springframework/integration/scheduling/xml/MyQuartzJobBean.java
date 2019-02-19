package org.ruanwei.demo.springframework.integration.scheduling.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class MyQuartzJobBean extends QuartzJobBean {
	private static Log logger = LogFactory.getLog(MyQuartzJobBean.class);

	private int timeout;

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		logger.info("executeInternal(JobExecutionContext context)" + timeout);
		JobDetail jobDetail = context.getJobDetail();
		Class<?> jobClass = jobDetail.getJobClass();
		Trigger trigger = context.getTrigger();
		Scheduler scheduler = context.getScheduler();
	}

	protected void doIt() {
		logger.info("doIt()" + timeout);
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
