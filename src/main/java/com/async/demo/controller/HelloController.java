package com.async.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.async.demo.model.AsyncRequestContext;
import com.async.demo.model.Result;
import com.async.demo.model.ToDo;
import com.async.demo.service.AsyncDemoService;

@RestController
@EnableAsync
public class HelloController {

	private static final Logger LOG = Logger.getLogger(HelloController.class.getName());

	@Autowired
	private AsyncDemoService service;

	@GetMapping("asyncwithcompeltablefutureandcommonpool")
	private Result getAsyncWithCompeltableFutureAndCommonPool() throws InterruptedException {
		AsyncRequestContext asyncRequestContext = new AsyncRequestContext();
		// multiple asynchronous lookups
		long start = System.currentTimeMillis();

		CompletableFuture<ToDo> result1 = service.findToDo();
		CompletableFuture<List<Map<String, Object>>> result2 = service.getUserComments();
		CompletableFuture.allOf(result1, result2).thenApply(r -> {
			Result result = new Result();
			List<Map<String, Object>> secondResult = new ArrayList<>();
			ToDo firstResult = null;
			try {
				secondResult = result2.get();
				firstResult = result1.get();
				long end = System.currentTimeMillis();
				LOG.info(
						"asyncwithcompeltablefutureandcommonpool future with common pool took " + (end - start) + "ms");
				result = service.marshallResponse(firstResult, secondResult);
				asyncRequestContext.setResult(result);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			return null;
		}).join();

		return asyncRequestContext.getResult();
	}

	@GetMapping("asyncwithcompeltablefutureandcustompool")
	private Result getAsyncWithCompeltableFutureAndCustomPool() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		AsyncRequestContext asyncRequestContext = new AsyncRequestContext();
		// multiple asynchronous lookups
		long start = System.currentTimeMillis();
		CompletableFuture<ToDo> result1 = service.findToDoWithoutAsyncAnnotation(executor);
		CompletableFuture<List<Map<String, Object>>> result2 = service.getUserCommentsWithoutAsyncAnnotation(executor);
		CompletableFuture.allOf(result1, result2).thenAccept(r -> {
			Result result = new Result();
			try {
				List<Map<String, Object>> secondResult = result2.get();
				ToDo firstResult = result1.get();
				long end = System.currentTimeMillis();
				LOG.info(
						"asyncwithcompeltablefutureandcustompool future with custom pool took " + (end - start) + "ms");
				result = service.marshallResponse(firstResult, secondResult);
				asyncRequestContext.setResult(result);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}).join();

		return asyncRequestContext.getResult();
	}

	@GetMapping("asyncwithcompeltablefutureandcommonpoolwithoutasyncannotation")
	private Result getAsyncWithCompeltableFutureAndCommonPoolWithoutAsyncAnnotation() throws Exception {
		AsyncRequestContext asyncRequestContext = new AsyncRequestContext();
		// multiple asynchronous lookups
		long start = System.currentTimeMillis();
		CompletableFuture<ToDo> result1 = service.findToDoWithoutAsyncAnnotation();
		CompletableFuture<List<Map<String, Object>>> result2 = service.getUserCommentsWithoutAsyncAnnotation();
		CompletableFuture.allOf(result1, result2).thenAccept(r -> {
			Result result = new Result();
			try {
				List<Map<String, Object>> secondResult = result2.get();
				ToDo firstResult = result1.get();
				long end = System.currentTimeMillis();
				LOG.info("asyncwithcompeltablefutureandcommonpoolwithoutasyncannotation future with custom pool took "
						+ (end - start) + "ms");
				result = service.marshallResponse(firstResult, secondResult);
				asyncRequestContext.setResult(result);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}).join();
		return asyncRequestContext.getResult();
	}

}
