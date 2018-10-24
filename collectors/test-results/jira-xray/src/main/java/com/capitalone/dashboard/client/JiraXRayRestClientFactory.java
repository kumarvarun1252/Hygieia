package com.capitalone.dashboard.client;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.TestResultCollectorRepository;
import com.capitalone.dashboard.repository.TestResultRepository;

import java.net.URI;

/**
 * Jira XRay Rest Client Factory methods
 */
public class JiraXRayRestClientFactory extends AsynchronousJiraRestClientFactory {


    public AsynchronousJiraRestClient create(URI serverUri, AuthenticationHandler authenticationHandler, TestResultCollectorRepository testResultCollectorRepository, TestResultRepository testResultRepository, FeatureRepository featureRepository) {
        DisposableHttpClient httpClient = (new AsynchronousHttpClientFactory()).createClient(serverUri, authenticationHandler);

        return new JiraXRayRestClientImpl(serverUri, httpClient, testResultCollectorRepository, testResultRepository, featureRepository);
    }

    public AsynchronousJiraRestClient createWithBasicHttpAuthentication(URI serverUri, String username, String password, TestResultCollectorRepository testResultCollectorRepository, TestResultRepository testResultRepository, FeatureRepository featureRepository) {
        return this.create(serverUri, (AuthenticationHandler)(new BasicHttpAuthenticationHandler(username, password)), testResultCollectorRepository, testResultRepository, featureRepository);
    }

    public AsynchronousJiraRestClient create(URI serverUri, HttpClient httpClient, TestResultCollectorRepository testResultCollectorRepository, TestResultRepository testResultRepository, FeatureRepository featureRepository) {
        DisposableHttpClient disposableHttpClient = (new AsynchronousHttpClientFactory()).createClient(httpClient);
        return new JiraXRayRestClientImpl(serverUri, disposableHttpClient, testResultCollectorRepository, testResultRepository, featureRepository);
    }


}