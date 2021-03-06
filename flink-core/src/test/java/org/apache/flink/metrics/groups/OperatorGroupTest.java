/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.metrics.groups;

import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.MetricRegistry;
import org.apache.flink.util.AbstractID;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class OperatorGroupTest {
	@Test
	public void testGenerateScopeDefault() {
		MetricRegistry registry = new MetricRegistry(new Configuration());
		
		OperatorMetricGroup operator = new TaskManagerMetricGroup(registry, "host", "id")
			.addTaskForJob(new JobID(), "job", new AbstractID(), new AbstractID(), 0, "task")
			.addOperator("operator");

		List<String> scope = operator.generateScope();
		assertEquals(6, scope.size());
		assertEquals("host", scope.get(0));
		assertEquals("taskmanager", scope.get(1));
		assertEquals("id", scope.get(2));
		assertEquals("job", scope.get(3));
		assertEquals("operator", scope.get(4));
		assertEquals("0", scope.get(5));
	}

	@Test
	public void testGenerateScopeWildcard() {
		MetricRegistry registry = new MetricRegistry(new Configuration());
		OperatorMetricGroup operator = new TaskManagerMetricGroup(registry, "host", "id")
			.addTaskForJob(new JobID(), "job", new AbstractID(), new AbstractID(), 0, "task")
			.addOperator("operator");

		Scope.ScopeFormat format = new Scope.ScopeFormat();
		format.setOperatorFormat(Scope.concat(Scope.SCOPE_WILDCARD, "op", OperatorMetricGroup.SCOPE_OPERATOR_NAME));

		List<String> scope = operator.generateScope(format);
		assertEquals(7, scope.size());
		assertEquals("host", scope.get(0));
		assertEquals("taskmanager", scope.get(1));
		assertEquals("id", scope.get(2));
		assertEquals("job", scope.get(3));
		assertEquals("task", scope.get(4));
		assertEquals("op", scope.get(5));
		assertEquals("operator", scope.get(6));
	}

	@Test
	public void testGenerateScopeCustom() {
		MetricRegistry registry = new MetricRegistry(new Configuration());
		OperatorMetricGroup operator = new TaskManagerMetricGroup(registry, "host", "id")
			.addTaskForJob(new JobID(), "job", new AbstractID(), new AbstractID(), 0, "task")
			.addOperator("operator");

		Scope.ScopeFormat format = new Scope.ScopeFormat();
		format.setOperatorFormat(Scope.concat("jobs", JobMetricGroup.SCOPE_JOB_NAME, "op", OperatorMetricGroup.SCOPE_OPERATOR_NAME));

		List<String> scope = operator.generateScope(format);
		assertEquals(4, scope.size());
		assertEquals("jobs", scope.get(0));
		assertEquals("job", scope.get(1));
		assertEquals("op", scope.get(2));
		assertEquals("operator", scope.get(3));
	}
}
