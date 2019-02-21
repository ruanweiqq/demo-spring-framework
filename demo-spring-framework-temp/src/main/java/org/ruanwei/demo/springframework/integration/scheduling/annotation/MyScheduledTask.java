package org.ruanwei.demo.springframework.integration.scheduling.annotation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

@Component("myScheduledTask")
public class MyScheduledTask {
	private static Log logger = LogFactory.getLog(MyScheduledTask.class);

	// measured from the completion time of each preceding invocation
	// 前一项任务的结束时间与下一项任务的开始时间之间的延迟
	@Scheduled(fixedDelay = 10000,initialDelay=1000)
	public void doSomething1() {
		// something that should execute periodically.
		logger.info("+++++++++++++++++++++++++++++doSomething1()");
		doSomething4("1");
	}

	// measured between the successive start times of each invocation.
	// 前一项任务的开始时间与下一项任务的开始时间的频率
	@Scheduled(fixedRate = 10000, initialDelay = 1000)
	public void doSomething2() {
		// something that should execute periodically
		logger.info("+++++++++++++++++++++++++++++doSomething2()");
		doSomething4("2");
	}

	@Scheduled(cron = "0/10 * * * * ?")
	public void doSomething3() {
		logger.info("+++++++++++++++++++++++++++++doSomething3()");
		doSomething4("4");
	}

	@Async("myExecutor")
	public void doSomething4(String s) {
		// this will be executed asynchronously
		logger.info("+++++++++++++++++++++++++++++doSomething4()" + s);
	}

	@Async("myExecutor")
	public Future<String> returnSomething(int i) {
		// this will be executed asynchronously
		logger.info("+++++++++++++++++++++++++++++returnSomething()");
		return new Future<String>() {
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDone() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String get() throws InterruptedException, ExecutionException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@Async("myExecutor")
	public ListenableFuture<String> returnSomething2(int i) {
		logger.info("+++++++++++++++++++++++++++++returnSomething2()");
		return new ListenableFuture<String>() {

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isCancelled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isDone() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String get() throws InterruptedException, ExecutionException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException,
					TimeoutException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void addCallback(
					ListenableFutureCallback<? super String> callback) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addCallback(
					SuccessCallback<? super String> successCallback,
					FailureCallback failureCallback) {
				// TODO Auto-generated method stub

			}
		};
	}

	@Async("myExecutor")
	public CompletableFuture<String> returnSomething3(int i) {
		logger.info("+++++++++++++++++++++++++++++returnSomething3()");
		return new CompletableFuture<String>() {
		};
	}
}
