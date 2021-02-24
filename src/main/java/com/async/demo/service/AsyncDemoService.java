package com.async.demo.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.async.demo.model.Result;
import com.async.demo.model.ToDo;

@Service
public class AsyncDemoService {

	private static final Logger logger = LoggerFactory.getLogger(AsyncDemoService.class);

	private final RestTemplate restTemplate;

	public AsyncDemoService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Async
	public CompletableFuture<ToDo> findToDo() throws InterruptedException {
		logger.info("findToDo thread " + Thread.currentThread().getName());
		String url = "https://jsonplaceholder.typicode.com/todos/1";
		ToDo result = restTemplate.getForObject(url, ToDo.class);
		// delay of 1s for demo
		Thread.sleep(1000L);
		return CompletableFuture.completedFuture(result);
	}

	@Async
	public CompletableFuture<List<Map<String, Object>>> getUserComments() throws InterruptedException {
		logger.info("getUserComments thread " + Thread.currentThread().getName());
		String url = "https://jsonplaceholder.typicode.com/comments?postId=1";
		List<Map<String, Object>> result = restTemplate.getForObject(url, List.class);
		// delay of 1s for demo
		Thread.sleep(1000L);
		return CompletableFuture.completedFuture(result);
	}

	public CompletableFuture<ToDo> findToDoWithoutAsyncAnnotation(ExecutorService executor)
			throws InterruptedException {
		CompletableFuture<ToDo> future = CompletableFuture.supplyAsync(new Supplier<ToDo>() {
			@Override
			public ToDo get() {
				logger.info("findToDo thread " + Thread.currentThread().getName());
				String url = "https://jsonplaceholder.typicode.com/todos/1";
				ToDo result = restTemplate.getForObject(url, ToDo.class);
				return result;
			}
		}, executor);
		return future;
	}

	public CompletableFuture<List<Map<String, Object>>> getUserCommentsWithoutAsyncAnnotation(ExecutorService executor)
			throws InterruptedException {
		CompletableFuture<List<Map<String, Object>>> future = CompletableFuture
				.supplyAsync(new Supplier<List<Map<String, Object>>>() {
					@Override
					public List<Map<String, Object>> get() {
						logger.info("getUserComments thread " + Thread.currentThread().getName());
						String url = "https://jsonplaceholder.typicode.com/comments?postId=1";
						List<Map<String, Object>> result = restTemplate.getForObject(url, List.class);
						return result;
					}
				}, executor);
		return future;
	}

	public CompletableFuture<ToDo> findToDoWithoutAsyncAnnotation() throws InterruptedException {
		CompletableFuture<ToDo> future = CompletableFuture.supplyAsync(new Supplier<ToDo>() {
			@Override
			public ToDo get() {
				logger.info("findToDo thread " + Thread.currentThread().getName());
				String url = "https://jsonplaceholder.typicode.com/todos/1";
				ToDo result = restTemplate.getForObject(url, ToDo.class);
				return result;
			}
		});
		return future;
	}

	public CompletableFuture<List<Map<String, Object>>> getUserCommentsWithoutAsyncAnnotation()
			throws InterruptedException {
		CompletableFuture<List<Map<String, Object>>> future = CompletableFuture
				.supplyAsync(new Supplier<List<Map<String, Object>>>() {
					@Override
					public List<Map<String, Object>> get() {
						logger.info("getUserComments thread " + Thread.currentThread().getName());
						String url = "https://jsonplaceholder.typicode.com/comments?postId=1";
						List<Map<String, Object>> result = restTemplate.getForObject(url, List.class);
						return result;
					}
				});
		return future;
	}

	public Result marshallResponse(ToDo firstResult, List<Map<String, Object>> secondResult) {
		Result result = new Result();
		result.setComments(secondResult);
		result.setToDo(firstResult);
		return result;
	}

}
