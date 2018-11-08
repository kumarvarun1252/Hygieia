package com.capitalone.dashboard.api;

import com.atlassian.jira.rest.client.api.JiraRestClient;

/**
 * Created by lucho on 11/08/16.
 */
public interface JiraXRayRestClient extends JiraRestClient {

    TestRestClient getTestClient();
    TestExecutionRestClient getTestExecutionClient();
    TestRunRestClient getTestRunClient();
    TestSetRestClient getTestSetClient();

}
