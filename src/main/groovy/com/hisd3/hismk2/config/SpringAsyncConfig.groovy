package com.hisd3.hismk2.config

import groovy.util.logging.Slf4j
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Future

@Configuration
@EnableAsync()
@Slf4j
class SpringAsyncConfig implements AsyncConfigurer {


	@Bean
	protected WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
				configurer.setTaskExecutor( new ConcurrentTaskExecutor(getAsyncExecutor()))
			}
		}
	}



	@Bean(name = "taskExecutor")
	@Override
	Executor getAsyncExecutor() {
		log.debug("Creating Async Task Executor")
		def executor = new ThreadPoolTaskExecutor()
		executor.corePoolSize = 20
		executor.maxPoolSize = 50
		executor.queueCapacity = 10000
		executor.threadNamePrefix = "taskExecutor-"
		executor.setTaskDecorator { runnable -> new DelegatingSecurityContextRunnable(runnable) }
		// https://github.com/spring-projects/spring-security/issues/6856#issuecomment-518787966
		// com/hisd3/hismk2/config/MultiHttpSecurityConfig.groovy:42

		return new ExceptionHandlingAsyncTaskExecutor(executor)

		//https://livebook.manning.com/book/spring-security-in-action/chapter-4/v-2/120
		// https://docs.spring.io/spring-security/site/docs/5.0.x/reference/html/concurrency.html
	}

	@Override
	AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler()
	}
}

@Slf4j
class ExceptionHandlingAsyncTaskExecutor implements AsyncTaskExecutor, InitializingBean, DisposableBean {

	AsyncTaskExecutor executor

	ExceptionHandlingAsyncTaskExecutor(AsyncTaskExecutor asyncTaskExecutor) {
		this.executor = asyncTaskExecutor
	}

	@Override
	void execute(Runnable task, long startTimeout) {
		executor.execute(createWrappedRunnable(task), startTimeout)
	}

	@Override
	void execute(Runnable task) {
		executor.execute(createWrappedRunnable(task))
	}

	static Runnable createWrappedRunnable(Runnable task) {

		return new Runnable() {
			@Override
			void run() {

				try {
					task.run()
				} catch (Exception e) {
					handle(e)
				}
			}
		}

	}

	static def handle(Exception e) {
		log.error("Caught async exception", e)
	}

	@Override
	Future<?> submit(Runnable task) {
		return executor.submit(createWrappedRunnable(task))
	}

	@Override
	<T> Future<?> submit(Callable<T> task) {
		return executor.submit(createCallable(task))
	}

	static <T> Callable createCallable(Callable<T> tCallable) {

		return new Callable<T>() {

			@Override
			T call() throws Exception {
				try {
					return tCallable.call()
				} catch (Exception e) {
					handle(e)
					throw e
				}
			}
		}

	}

	@Override
	void destroy() throws Exception {
		if (executor instanceof DisposableBean) {
			executor.destroy()
		}
	}

	@Override
	void afterPropertiesSet() throws Exception {
		if (executor instanceof InitializingBean) {
			executor.afterPropertiesSet()
		}
	}

}
