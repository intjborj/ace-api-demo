package com.hisd3.hismk2.services

import graphql.ExecutionResult
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.ExecutionContext
import graphql.execution.ExecutionStrategyParameters
import graphql.execution.NonNullableFieldWasNullException
import org.springframework.transaction.annotation.Transactional

import java.util.concurrent.CompletableFuture

// Not used
//@Service
class AsyncTransactionalExecutionStrategy extends AsyncExecutionStrategy {
	
	@Override
	@Transactional
	CompletableFuture<ExecutionResult> execute(ExecutionContext executionContext, ExecutionStrategyParameters parameters) throws NonNullableFieldWasNullException {
		return super.execute(executionContext, parameters)
	}
}
