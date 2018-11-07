package com.capitalone.dashboard.core.async;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.util.concurrent.Promise;
import com.capitalone.dashboard.api.*;
import com.capitalone.dashboard.api.domain.TestExecution;
import java.net.URI;

/**
 * Created by lucho on 11/08/16.
 */
public class AsyncXrayJiraRestClient extends AsynchronousJiraRestClient implements XrayJiraRestClient {
    private TestRestClient testClient=null;
    private TestExecutionRestClient testExecutionClient=null;
    private TestRunRestClient testRunClient=null;
    private TestSetRestClient testSetClient=null;

    public AsyncXrayJiraRestClient(URI serverUri, DisposableHttpClient httpClient) {
        super(serverUri, httpClient);
        this.testRunClient=new AsyncTestRunRestClient(serverUri,httpClient);
        this.testExecutionClient= new AsyncTestExecRestClient(serverUri, httpClient) {
            @Override
            public Promise<Void> setTests(TestExecution testExec) {
                return null;
            }
        };
    }

    public TestRestClient getTestClient() {
        return testClient;
    }

    public TestExecutionRestClient getTestExecutionClient() {
        return testExecutionClient;
    }

    public TestRunRestClient getTestRunClient() {
        return testRunClient;
    }

    public TestSetRestClient getTestSetClient() {
        return testSetClient;
    }
}
