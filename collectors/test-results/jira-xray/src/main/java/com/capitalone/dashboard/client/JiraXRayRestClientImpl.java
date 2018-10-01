package com.capitalone.dashboard.client;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import java.net.URI;

/**
 * Created by lucho on 11/08/16.
 */
public class JiraXRayRestClientImpl extends AsynchronousJiraRestClient implements JiraXRayRestClient {
//    private TestRestClient testClient=null;
//    private TestExecutionRestClient testExecutionClient=null;
//    private TestRunRestClient testRunClient=null;
//    private TestSetRestClient testSetClient=null;

    public JiraXRayRestClientImpl(URI serverUri, DisposableHttpClient httpClient) {
        super(serverUri, httpClient);
//        this.testRunClient=new TestRunRestClientImpl(serverUri,httpClient);
//        this.testExecutionClient=new TestExecutionRestClientImpl(serverUri,httpClient);
    }

//    public TestRestClient getTestClient() {
//        return testClient;
//    }
//
//    public TestExecutionRestClient getTestExecutionClient() {
//        return testExecutionClient;
//    }
//
//    public TestRunRestClient getTestRunClient() {
//        return testRunClient;
//    }
//
//    public TestSetRestClient getTestSetClient() {
//        return testSetClient;
//    }
}
