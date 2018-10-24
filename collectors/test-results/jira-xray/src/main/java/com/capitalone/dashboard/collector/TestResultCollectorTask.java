package com.capitalone.dashboard.collector;

import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.capitalone.dashboard.client.JiraXRayRestClient;
import com.capitalone.dashboard.client.testexecution.TestExecutionRestClientImpl;
import com.capitalone.dashboard.model.TestResultCollector;
import com.capitalone.dashboard.repository.*;
import com.capitalone.dashboard.util.CoreFeatureSettings;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.util.TestResultSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import java.net.URI;

/**
 * Collects {@link TestResultCollector} data from feature content source system.
 */

public class TestResultCollectorTask extends CollectorTask<TestResultCollector> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestResultCollectorTask.class);
	
	private final CoreFeatureSettings coreFeatureSettings;
	private final TestResultRepository testResultRepository;
	private final FeatureRepository featureRepository;
	private final TestResultCollectorRepository testResultCollectorRepository;
	private final TestResultSettings testResultSettings;
	private final JiraXRayRestClient jiraXRayRestClient;
	private final DisposableHttpClient httpClient;

	/**
	 * Default constructor for the collector task. This will construct this
	 * collector task with all repository, scheduling, and settings
	 * configurations custom to this collector.
	 * 
	 * @param taskScheduler
	 *            A task scheduler artifact
	 * @param testResultSettings
	 *            The settings being used for feature collection from the source
	 *            system
	 */
	@Autowired
	public TestResultCollectorTask(CoreFeatureSettings coreFeatureSettings, DisposableHttpClient httpClient, TaskScheduler taskScheduler, TestResultRepository testResultRepository,
								   TestResultCollectorRepository testResultCollectorRepository, TestResultSettings testResultSettings,
								   JiraXRayRestClient jiraXRayRestClient, FeatureRepository featureRepository) {
		super(taskScheduler, FeatureCollectorConstants.JIRA);
		this.testResultRepository = testResultRepository;
		this.testResultCollectorRepository = testResultCollectorRepository;
		this.coreFeatureSettings = coreFeatureSettings;
		this.testResultSettings = testResultSettings;
		this.jiraXRayRestClient = jiraXRayRestClient;
		this.featureRepository = featureRepository;
		this.httpClient = httpClient;
	}

	/**
	 * Accessor method for the collector prototype object
	 */
	@Override
	public TestResultCollector getCollector() {
		return TestResultCollector.prototype();
	}

	/**
	 * Accessor method for the collector repository
	 */
	@Override
	public BaseCollectorRepository<TestResultCollector> getCollectorRepository() {
		return testResultCollectorRepository;
	}

	/**
	 * Accessor method for the current chronology setting, for the scheduler
	 */
	@Override
	public String getCron() {
		return testResultSettings.getCron();
	}

	/**
	 * The collection action. This is the task which will run on a schedule to
	 * gather data from the feature content source system and update the
	 * repository with retrieved data.
	 */
	@Override
	public void collect(TestResultCollector collector) {
		logBanner(testResultSettings.getJiraBaseUrl());
		int count = 0;

		try {
			long testExecutionDataStart = System.currentTimeMillis();
			TestExecutionRestClientImpl testExecutionData = new TestExecutionRestClientImpl(new URI(""), httpClient, this.testResultCollectorRepository, this.testResultRepository, this.featureRepository);
			count = testExecutionData.updateTestExecutionInformation();
			
			log("Test Execution Data", testExecutionDataStart, count);
			log("Finished", testExecutionDataStart);
		} catch (Exception e) {
			// catch exception here so we don't blow up the collector completely
			LOGGER.error("Failed to collect Jira XRay information", e);
		}
	}
}
