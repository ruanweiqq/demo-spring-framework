package org.ruanwei.demo.springframework.web;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import reactor.ipc.netty.http.server.HttpServer;

public class SpringWebApplication {
	private static final Logger logger = LogManager.getLogger();

	public static void main(String[] args) {
		logger.debug("main");

		SpringWebApplication webapp = new SpringWebApplication();
		// webapp.run1();
		// webapp.run2();
		webapp.run3();
	}

	private void run1() {
		WebHandler webHandler = new DispatcherHandler();

		HttpHandler httpHandler = WebHttpHandlerBuilder.webHandler(webHandler)
				.build();
		runWithNetty(httpHandler);

	}

	private void run2() {
		ApplicationContext context = new AnnotationConfigApplicationContext(
				WebAppConfig.class);
		
		HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(
				context).build();
		runWithNetty(httpHandler);
	}

	private void run3() {
		ApplicationContext context = new AnnotationConfigApplicationContext(
				WebAppConfig.class);
		RouterFunction<ServerResponse> timerRouter = context.getBean(RouterFunction.class);
		
		HttpHandler httpHandler = RouterFunctions.toHttpHandler(timerRouter);
		runWithNetty(httpHandler);
	}

	private static void runWithNetty(HttpHandler httpHandler) {
		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(
				httpHandler);
		HttpServer.create("localhost", 8081).newHandler(adapter)
				.block(Duration.ofMinutes(1));

	}

	private static void runWithUndertow(HttpHandler httpHandler) {
	}

	private static void runWithTomcat(HttpHandler httpHandler) {
	}
}
