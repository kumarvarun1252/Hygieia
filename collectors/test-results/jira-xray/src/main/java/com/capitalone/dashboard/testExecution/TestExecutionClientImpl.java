package com.capitalone.dashboard.testExecution;

import com.capitalone.dashboard.TestResultSettings;
import com.capitalone.dashboard.api.domain.TestExecution;
import com.capitalone.dashboard.api.domain.TestRun;
import com.capitalone.dashboard.api.domain.TestStep;
import com.capitalone.dashboard.core.client.JiraXRayRestClientImpl;
import com.capitalone.dashboard.core.client.JiraXRayRestClientSupplier;
import com.capitalone.dashboard.model.*;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.TestResultCollectorRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TestExecutionClientImpl implements TestExecutionClient {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestExecutionClientImpl.class);
    private final TestResultSettings testResultSettings;
    private final TestResultRepository testResultRepository;
    private final TestResultCollectorRepository testResultCollectorRepository;
    private final FeatureRepository featureRepository;
    private JiraXRayRestClientImpl restClient;
    private final JiraXRayRestClientSupplier restClientSupplier;

    public TestExecutionClientImpl(TestResultRepository testResultRepository, TestResultCollectorRepository testResultCollectorRepository, FeatureRepository featureRepository, TestResultSettings testResultSettings, JiraXRayRestClientSupplier restClientSupplier) {
        this.testResultRepository = testResultRepository;
        this.testResultCollectorRepository = testResultCollectorRepository;
        this.featureRepository = featureRepository;
        this.testResultSettings = testResultSettings;
        this.restClientSupplier = restClientSupplier;
    }


    public int updateTestResultInformation() {
        int count = 0;
        int pageSize = testResultSettings.getPageSize();

        boolean hasMore = true;
        for (int i = 0; hasMore; i += pageSize) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Obtaining story information starting at index " + i + "...");
            }
            long queryStart = System.currentTimeMillis();
            List<Feature> tests = featureRepository.getStoryByType("Test Execution");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Story information query took " + (System.currentTimeMillis() - queryStart) + " ms");
            }

            if (tests != null && !tests.isEmpty()) {
                updateMongoInfo(tests);
                count += tests.size();
            }

            LOGGER.info("Loop i " + i + " pageSize " + tests.size());

            if (tests == null || tests.size() > pageSize) {
                hasMore = false;
                break;
            }
        }

        return count;
    }

    /**
     * Updates the MongoDB with a JSONArray received from the source system
     * back-end with story-based data.
     *
     * @param currentPagedTestExecutions
     *            A list response of Jira issues from the source system
     */
    @SuppressWarnings({ "PMD.AvoidDeeplyNestedIfStmts", "PMD.NPathComplexity" })
    private void updateMongoInfo(List<Feature> currentPagedTestExecutions) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Size of paged Jira response: " + (currentPagedTestExecutions == null? 0 : currentPagedTestExecutions.size()));
        }

        if (currentPagedTestExecutions != null) {
            List<TestResult> testResultsToSave = new ArrayList<>();
//            ObjectId jiraXRayFeatureId = testResultCollectorRepository.findByName(FeatureCollectorConstants.JIRA_XRAY).getId();

            for (Feature testExec : currentPagedTestExecutions) {

                TestResult testResult = new TestResult();

//                testResult.setCollectorItemId(jiraXRayFeatureId);
                testResult.setDescription(testExec.getsName());
                testResult.setTargetAppName(testExec.getsProjectName());
                testResult.setType(TestSuiteType.Manual);
               try {
                    TestExecution testExecution = new TestExecution(new URI(testExec.getsUrl()), testExec.getsNumber(), Long.parseLong(testExec.getsId()));
                    testResult.setUrl(testExecution.getSelf().toString());

                    restClient= (JiraXRayRestClientImpl) restClientSupplier.get();
                    Iterable<TestExecution.Test> tests = restClient.getTestExecutionClient().getTests(testExecution).claim();

                    int totalCount = (int) tests.spliterator().getExactSizeIfKnown();
                    int failCount = this.getFailTestCount(testExec, tests);
                    int passCount = this.getPassTestCount(testExec, tests);
                    testResult.setTotalCount(totalCount);
                    testResult.setFailureCount(failCount);
                    testResult.setSuccessCount(passCount);

                    int skipCount = totalCount - (failCount + passCount);
                    testResult.setSkippedCount(skipCount);

                    if(failCount > 0) {
                        testResult.setResultStatus(TestCaseStatus.Failure.toString());
                    } else if (totalCount == passCount){
                        testResult.setResultStatus(TestCaseStatus.Success.toString());
                    } else {
                        testResult.setResultStatus(TestCaseStatus.Skipped.toString());
                    }

                    testResult.setTestCapabilities(this.getCapabilities(tests, testExec));
                } catch (URISyntaxException u) {
                    LOGGER.error("URI Syntax Invalid");
               }
                testResultsToSave.add(testResult);
            }

            // Saving back to MongoDB
            testResultRepository.save(testResultsToSave);
        }
    }
    private List<TestCapability> getCapabilities(Iterable<TestExecution.Test> tests, Feature testExec) {
        List<TestCapability> capabilities = new ArrayList<>();
        TestCapability capability = new TestCapability();
        capability.setDescription(testExec.getsName());

        int totalCount = (int) tests.spliterator().getExactSizeIfKnown();
        int failCount = this.getFailTestCount(testExec, tests);
        int passCount = this.getPassTestCount(testExec, tests);
        capability.setTotalTestSuiteCount(1);
        capability.setType(TestSuiteType.Manual);

        if(failCount > 0) {
            capability.setStatus(TestCaseStatus.Failure);
            capability.setFailedTestSuiteCount(1);
        } else if (totalCount == passCount){
            capability.setStatus(TestCaseStatus.Success);
            capability.setSuccessTestSuiteCount(1);
        } else {
            capability.setStatus(TestCaseStatus.Skipped);
            capability.setSkippedTestSuiteCount(1);
        }
        capability.setTestSuites(this.getTestSuites(tests, testExec));
        capabilities.add(capability);

        return capabilities;
    }

    private List<TestSuite> getTestSuites(Iterable<TestExecution.Test> tests, Feature testExec) {

        List<TestSuite> testSuites = new ArrayList<>();
        TestSuite testSuite = new TestSuite();

        testSuite.setDescription(testExec.getsName());
        testSuite.setType(TestSuiteType.Manual);
        int totalCount = (int) tests.spliterator().getExactSizeIfKnown();
        int failCount = this.getFailTestCount(testExec, tests);
        int passCount = this.getPassTestCount(testExec,tests);
        testSuite.setTotalTestCaseCount(totalCount);
        testSuite.setFailedTestCaseCount(failCount);
        testSuite.setSuccessTestCaseCount(passCount);

        int skipCount = totalCount - (failCount + passCount);
        testSuite.setSkippedTestCaseCount(skipCount);

        if(failCount > 0) {
            testSuite.setStatus(TestCaseStatus.Failure);
        } else if (totalCount == passCount){
            testSuite.setStatus(TestCaseStatus.Success);
        } else {
            testSuite.setStatus(TestCaseStatus.Skipped);
        }
        List<TestCase> testCases = new ArrayList<>();
        TestCase testCase = new TestCase();

        testSuite.setTestCases(this.getTestCases(tests,testExec));
        testSuites.add(testSuite);

        return testSuites;
    }

    private List<TestCase> getTestCases(Iterable<TestExecution.Test> tests, Feature testExec) {
        List<TestCase> testCases = new ArrayList<>();

        for (TestExecution.Test test : tests) {
            TestCase testCase = new TestCase();

            try {
               // TestRun testRun = new TestRun(new URI(""), test.getKey(), test.getId());
                TestRun testRun = restClient.getTestRunClient().getTestRun(testExec.getsNumber(), test.getKey()).claim();

                testCase.setId(testRun.getId().toString());
                testCase.setDescription(test.toString());
                int totalSteps = (int) testRun.getSteps().spliterator().getExactSizeIfKnown();
                int failSteps = this.getStepCount(testRun, "FAIL");
                int passSteps = this.getStepCount(testRun, "PASS");
                int skipSteps = totalSteps - (failSteps + passSteps);
                testCase.setTotalTestStepCount(totalSteps);
                testCase.setFailedTestStepCount(failSteps);
                testCase.setSuccessTestStepCount(passSteps);
                testCase.setSkippedTestStepCount(skipSteps);
                if(failSteps > 0) {
                    testCase.setStatus(TestCaseStatus.Failure);
                } else if (totalSteps == passSteps){
                    testCase.setStatus(TestCaseStatus.Success);
                } else {
                    testCase.setStatus(TestCaseStatus.Skipped);
                }

                testCase.setTestSteps(this.getTestSteps(testRun));

            } catch (Exception e) {

            }
            testCases.add(testCase);
        }

        return testCases;
    }

    private List<TestCaseStep> getTestSteps(TestRun testRun) {
        List<TestCaseStep> testSteps = new ArrayList<>();

        for (TestStep testStep : testRun.getSteps()) {
            TestCaseStep testCaseStep = new TestCaseStep();

            testCaseStep.setId(testStep.getId().toString());
            testCaseStep.setDescription(testStep.getStep().getRaw());
            if (testStep.getStatus().equals("PASS")) {
                testCaseStep.setStatus(TestCaseStatus.Success);
            } else if (testStep.getStatus().equals("FAIL")) {
                testCaseStep.setStatus(TestCaseStatus.Failure);
            } else {
                testCaseStep.setStatus(TestCaseStatus.Skipped);
            }
            testSteps.add(testCaseStep);
        }



        return testSteps;
    }

    private int getFailTestCount(Feature testExec ,Iterable<TestExecution.Test> tests) {
        int count = 0;

        for (TestExecution.Test test : tests) {
            try {
                TestRun testRun = restClient.getTestRunClient().getTestRun(testExec.getsNumber(), test.getKey()).claim();
                if (testRun.getStatus().toString().equals("FAIL")) {
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return count;
    }

    private int getPassTestCount(Feature testExec ,Iterable<TestExecution.Test> tests) {
        int count = 0;

        for (TestExecution.Test test : tests) {
            try {
                TestRun testRun = restClient.getTestRunClient().getTestRun(testExec.getsNumber(), test.getKey()).claim();
                if (testRun.getStatus().toString().equals("PASS")) {
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return count;
    }

    private int getStepCount(TestRun testRun, String statusType) {
        int count = 0;

        for (TestStep testStep : testRun.getSteps()) {
            if ("FAIL".equalsIgnoreCase(statusType) && testStep.getStatus().toString().equals("FAIL")) {
                count++;
            } else if ("PASS".equalsIgnoreCase(statusType) && testStep.getStatus().toString().equals("PASS")) {
                count++;
            }
        }

        return count;
    }

    /**
     * Retrieves the maximum change date for a given query.
     *
     * @return A list object of the maximum change date
     */
    public String getMaxChangeDate() {
        String data = null;

        try {
            List<Feature> response = featureRepository
                    .findTopByCollectorIdAndChangeDateGreaterThanOrderByChangeDateDesc(
                            testResultCollectorRepository.findByName(FeatureCollectorConstants.JIRA_XRAY).getId(),
                            testResultSettings.getDeltaStartDate());
            if ((response != null) && !response.isEmpty()) {
                data = response.get(0).getChangeDate();
            }
        } catch (Exception e) {
            LOGGER.error("There was a problem retrieving or parsing data from the local "
                    + "repository while retrieving a max change date\nReturning null", e);
        }

        return data;
    }
}
