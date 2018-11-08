package com.capitalone.dashboard.api;

import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.api.domain.TestExecution;

/**
 * Created by lucho on 11/08/16.
 */
public interface TestExecutionRestClient {
    Promise<Iterable<TestExecution.Test>> getTests(TestExecution key);
    Promise<Void> setTests(TestExecution testExec);
    Promise<Void> removeTest(TestExecution testExecKey, TestExecution.Test testKey);

}
