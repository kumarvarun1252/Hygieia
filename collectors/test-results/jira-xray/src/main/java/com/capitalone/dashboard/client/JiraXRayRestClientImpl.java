package com.capitalone.dashboard.client;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.capitalone.dashboard.client.test.TestRestClient;
import com.capitalone.dashboard.client.testexecution.TestExecutionRestClient;
import com.capitalone.dashboard.client.testexecution.TestExecutionRestClientImpl;
import com.capitalone.dashboard.client.testrun.TestRunRestClient;
import com.capitalone.dashboard.client.testrun.TestRunRestClientImpl;
import com.capitalone.dashboard.client.testset.TestSetRestClient;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.TestResultCollectorRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class JiraXRayRestClientImpl extends AsynchronousJiraRestClient implements JiraXRayRestClient {
    private TestRestClient testClient=null;
    private TestExecutionRestClient testExecutionClient=null;
    private TestRunRestClient testRunClient=null;
    private TestSetRestClient testSetClient=null;


    public JiraXRayRestClientImpl(URI serverUri, DisposableHttpClient httpClient, TestResultCollectorRepository testResultCollectorRepository, TestResultRepository testResultRepository, FeatureRepository featureRepository) {
        super(serverUri, httpClient);
        this.testRunClient=new TestRunRestClientImpl(serverUri,httpClient);
        this.testExecutionClient=new TestExecutionRestClientImpl(serverUri,httpClient,testResultCollectorRepository, testResultRepository, featureRepository);
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