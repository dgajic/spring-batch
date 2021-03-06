/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.batch.core.repository.dao;

import java.util.Map;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.support.SerializationUtils;
import org.springframework.batch.support.transaction.TransactionAwareProxyFactory;

/**
 * In-memory implementation of {@link ExecutionContextDao} backed by maps.
 * 
 * @author Robert Kasanicky
 * @author Dave Syer
 */
public class MapExecutionContextDao implements ExecutionContextDao {

	private Map<Long, ExecutionContext> contextsByStepExecutionId = TransactionAwareProxyFactory
			.createAppendOnlyTransactionalMap();

	private Map<Long, ExecutionContext> contextsByJobExecutionId = TransactionAwareProxyFactory
			.createAppendOnlyTransactionalMap();

	public void clear() {
		contextsByJobExecutionId.clear();
		contextsByStepExecutionId.clear();
	}

	private static ExecutionContext copy(ExecutionContext original) {
		return (ExecutionContext) SerializationUtils.deserialize(SerializationUtils.serialize(original));
	}

	public ExecutionContext getExecutionContext(StepExecution stepExecution) {
		return copy(contextsByStepExecutionId.get(stepExecution.getId()));
	}

	public void updateExecutionContext(StepExecution stepExecution) {
		ExecutionContext executionContext = stepExecution.getExecutionContext();
		if (executionContext != null) {
			contextsByStepExecutionId.put(stepExecution.getId(), copy(executionContext));
		}
	}

	public ExecutionContext getExecutionContext(JobExecution jobExecution) {
		return copy(contextsByJobExecutionId.get(jobExecution.getId()));
	}

	public void updateExecutionContext(JobExecution jobExecution) {
		ExecutionContext executionContext = jobExecution.getExecutionContext();
		if (executionContext != null) {
			contextsByJobExecutionId.put(jobExecution.getId(), copy(executionContext));
		}
	}

	public void saveExecutionContext(JobExecution jobExecution) {
		updateExecutionContext(jobExecution);
	}

	public void saveExecutionContext(StepExecution stepExecution) {
		updateExecutionContext(stepExecution);
	}

}
