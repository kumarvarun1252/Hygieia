package com.capitalone.dashboard.client;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.capitalone.dashboard.repository.TestResultCollectorRepository;

import java.net.URI;

public class JiraXrayRestClientFactory extends AsynchronousJiraRestClientFactory {


    public AsynchronousJiraRestClient create(URI serverUri, AuthenticationHandler authenticationHandler, TestResultCollectorRepository testResultCollectorRepository) {
        DisposableHttpClient httpClient = (new AsynchronousHttpClientFactory()).createClient(serverUri, authenticationHandler);

        return new JiraXRayRestClientImpl(serverUri, httpClient, testResultCollectorRepository);
    }

    public AsynchronousJiraRestClient createWithBasicHttpAuthentication(URI serverUri, String username, String password, TestResultCollectorRepository testResultCollectorRepository) {
        return this.create(serverUri, (AuthenticationHandler)(new BasicHttpAuthenticationHandler(username, password)), testResultCollectorRepository);
    }

    public AsynchronousJiraRestClient create(URI serverUri, HttpClient httpClient, TestResultCollectorRepository testResultCollectorRepository) {
        DisposableHttpClient disposableHttpClient = (new AsynchronousHttpClientFactory()).createClient(httpClient);
        return new JiraXRayRestClientImpl(serverUri, disposableHttpClient, testResultCollectorRepository);
    }


}
