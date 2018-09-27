package com.capitalone.dashboard.client.api;

import com.atlassian.jira.rest.client.api.JiraRestClient;

public interface XrayJiraRestClient extends JiraRestClient {

    public TestRestClient getTestClient();
    public TestExecutionRestClient getTestExecutionClient();
    public TestRunRestClient getTestRunClient();
    public TestSetRestClient getTestSetClient();

}
