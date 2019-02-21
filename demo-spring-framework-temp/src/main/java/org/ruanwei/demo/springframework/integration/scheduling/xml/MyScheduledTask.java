package org.ruanwei.demo.springframework.integration.scheduling.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MyScheduledTask {
	private static Log logger = LogFactory.getLog(MyScheduledTask.class);

	// measured from the completion time of each preceding invocation
	// 前一项任务的结束时间与下一项任务的开始时间之间的延迟
	public void doSomething1() {
		// something that should execute periodically.
		logger.info("+++++++++++++++++++++++++++++doSomething1()");
		doSomething4("1");
	}

	// measured between the successive start times of each invocation.
	// 前一项任务的开始时间与下一项任务的开始时间的频率
	public void doSomething2() {
		// something that should execute periodically
		logger.info("+++++++++++++++++++++++++++++doSomething2()");
		doSomething4("2");
	}

	public void doSomething3() {
		logger.info("+++++++++++++++++++++++++++++doSomething3()");
		doSomething4("4");
	}

	public void doSomething4(String s) {
		// this will be executed asynchronously
		logger.info("+++++++++++++++++++++++++++++doSomething4()" + s);
	}
}
