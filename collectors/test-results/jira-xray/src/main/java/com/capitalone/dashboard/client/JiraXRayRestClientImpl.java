package com.capitalone.dashboard.client;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.capitalone.dashboard.client.api.domain.TestExecution;
import com.capitalone.dashboard.client.test.TestRestClient;
import com.capitalone.dashboard.client.testexecution.TestExecutionRestClient;
import com.capitalone.dashboard.client.testexecution.TestExecutionRestClientImpl;
import com.capitalone.dashboard.client.testrun.TestRunRestClient;
import com.capitalone.dashboard.client.testrun.TestRunRestClientImpl;
import com.capitalone.dashboard.client.testset.TestSetRestClient;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.TestResultCollectorRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.util.TestResultSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class JiraXRayRestClientImpl implements JiraXRayRestClient {
    private TestRestClient testClient=null;
    private TestExecutionRestClient testExecutionClient=null;
    private TestRunRestClient testRunClient=null;
    private TestSetRestClient testSetClient=null;
    TestResultSettings testResultSettings;
    JiraRestClient restClientSupplier;

    @Autowired
    public JiraXRayRestClientImpl(TestResultSettings testResultSettings, JiraXRayRestClientSupplier jiraXRayRestClientSupplier) {
// this.testRunClient=new TestRunRestClientImpl(serverUri,httpClient);
//        this.testExecutionClient=new TestExecutionRestClientImpl(httpClient,testResultCollectorRepository, testResultRepository, featureRepository);
//        this.restClientSupplier = restClientSupplier.get();
        this.testResultSettings = testResultSettings;
        this.restClientSupplier = jiraXRayRestClientSupplier.get();

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

    @Override
    public int getPageSize() {
        int pageSize = 0;
        if (restClientSupplier != null) {
            pageSize = testResultSettings.getPageSize();
        }
        return pageSize;
    }
}