package com.capitalone.dashboard.client.testexecution;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousSearchRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.util.concurrent.Promise;
import com.atlassian.util.concurrent.TimedOutException;
import com.capitalone.dashboard.client.JiraXRayRestClient;
import com.capitalone.dashboard.model.Feature;
import com.capitalone.dashboard.repository.FeatureRepository;
import com.capitalone.dashboard.repository.TestResultCollectorRepository;
import com.capitalone.dashboard.repository.TestResultRepository;
import com.capitalone.dashboard.util.ClientUtil;
import com.capitalone.dashboard.util.FeatureCollectorConstants;
import com.capitalone.dashboard.util.TestResultSettings;
import com.google.common.base.Function;
import com.capitalone.dashboard.client.api.domain.TestExecution;
import com.capitalone.dashboard.client.core.PluginConstants;
import com.capitalone.dashboard.client.core.json.TestArrayJsonParser;
import com.capitalone.dashboard.client.core.json.gen.TestExecUpdateJsonGenerator;
import org.bson.types.ObjectId;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestExecutionRestClientImpl extends AbstractAsynchronousRestClient implements TestExecutionRestClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestExecutionRestClientImpl.class);
    private static final ClientUtil TOOLS = ClientUtil.getInstance();
    private URI baseUri;
    private final  TestArrayJsonParser testsParser=new TestArrayJsonParser();
    private final static TestExecUpdateJsonGenerator execUpdateGenerator=new TestExecUpdateJsonGenerator();
    private final TestResultSettings testResultSettings = new TestResultSettings();


    private SearchRestClient searchRestClient=null;

    private final DateFormat SETTINGS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

//    private final TestResultSettings testResultSettings;
//    private final TestResultCollectorRepository testResultCollectorRepository;
//    private final TestResultRepository testResultRepository;
//    private final JiraXRayRestClient jiraXRayRestClient;

    public TestExecutionRestClientImpl(URI serverUri, DisposableHttpClient httpClient){
        super(httpClient);
        baseUri = UriBuilder.fromUri(serverUri).path("/rest/raven/{restVersion}/api/").build(PluginConstants.XRAY_REST_VERSION);
        searchRestClient=new AsynchronousSearchRestClient(UriBuilder.fromUri(serverUri).path("rest/api/latest/").build(new Object[0]),httpClient);

//        this.testResultSettings = testResultSettings;
//        this.testResultCollectorRepository = testResultCollectorRepository;
//        this.testResultRepository = testResultRepository;
//        this.jiraXRayRestClient = jiraXRayRestClient;
    }

    /**
     * Explicitly updates queries for the source system, and initiates the
     * update to MongoDB from those calls.
     */
//    public int updateTestExecutionInformation() {
//        int count = 0;
//
//        //long startDate = featureCollectorRepository.findByName(FeatureCollectorConstants.JIRA).getLastExecuted();
//
//        String startDateStr = testResultSettings.getDeltaStartDate();
////        String maxChangeDate = getMaxChangeDate();
//        if (maxChangeDate != null) {
//            startDateStr = maxChangeDate;
//        }
//
//        startDateStr = getChangeDateMinutePrior(startDateStr);
//        long startTime;
//        try {
//            startTime = SETTINGS_DATE_FORMAT.parse(startDateStr).getTime();
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//
//        int pageSize = jiraClient.getPageSize();
//
//        updateStatuses();
//
//        boolean hasMore = true;
//        for (int i = 0; hasMore; i += pageSize) {
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("Obtaining story information starting at index " + i + "...");
//            }
//            long queryStart = System.currentTimeMillis();
//            List<Issue> issues = jiraClient.getIssues(startTime, i);
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("Story information query took " + (System.currentTimeMillis() - queryStart) + " ms");
//            }
//
//            if (issues != null && !issues.isEmpty()) {
//                updateMongoInfo(issues);
//                count += issues.size();
//            }
//
//            LOGGER.info("Loop i " + i + " pageSize " + issues.size());
//
//            // will result in an extra call if number of results == pageSize
//            // but I would rather do that then complicate the jira client implementation
//            if (issues == null || issues.size() < pageSize) {
//                hasMore = false;
//                break;
//            }
//        }
//
//        return count;
//    }

    /**
     * Updates the MongoDB with a JSONArray received from the source system
     * back-end with story-based data.
     *
     * @param currentPagedJiraRs
     *            A list response of Jira issues from the source system
     */
    @SuppressWarnings({ "PMD.AvoidDeeplyNestedIfStmts", "PMD.NPathComplexity" })
    private void updateMongoInfo(List<Issue> currentPagedJiraRs) {
        final TestResultSettings testResultSettings = new TestResultSettings();
        final TestResultCollectorRepository testResultCollectorRepository;

        LOGGER.info("\n IN updateMongoInfo Method");

        try {
            LOGGER.info("\n IN TRY BLOCK");

            TestExecution testExecution = new TestExecution(new URI(""), "EME-1946", 1977L);
            LOGGER.info("\n TEST EXECUTION: " + testExecution);

            Iterable<TestExecution.Test> tests = this.getTests(testExecution).claim();

            LOGGER.info("\n TESTS: " + tests.toString());

            tests.forEach(test -> {
                LOGGER.info("\n TEST ID: " + test.getId() + " TEST SELF" + test.getSelf() + " TEST KEY" + test.getKey());
            });

//            if (this.getTests(testExecution).isDone()) {
//                Iterable<TestExecution.Test> tests = this.getTests(testExecution).claim();
//
//                while (tests.iterator().hasNext()) {
//                    LOGGER.info("\n TESTS: " + tests.toString());
//
//                    tests.forEach(test -> {
//                        LOGGER.info("\n TEST ID: " + test.getId() + " TEST SELF" + test.getSelf() + " TEST KEY" + test.getKey());
//                    });
//            }


//            }
//            LOGGER.info("\n TESTS: " + tests.toString());
//
//            for(TestExecution.Test t:tests)
//            {
//                System.out.print("\n TEST KEY: " + t.getKey());
//                System.out.print("\n TEST ID: " + t.getId());
//                System.out.print("\n TEST Version: " + t.getVersion());
////                assertNotNull(t);
//            }

//            tests.forEach(test -> {
//                LOGGER.info("\n TEST ID: " + test.getId() + " TEST SELF" + test.getSelf() + " TEST KEY" + test.getKey());
//            });

        } catch (URISyntaxException u) {
            LOGGER.error("URI Syntax Invalid");
        }

//        catch (InterruptedException i) {
//
//        }
//        catch (ExecutionException ex) {
//
//        }
//        catch (TimeoutException t) {
//
//        }

//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("Size of paged Jira response: " + (currentPagedJiraRs == null? 0 : currentPagedJiraRs.size()));
//        }
//
//        if (currentPagedJiraRs != null) {
//            List<Feature> testResultsToSave = new ArrayList<>();
//
//            Map<String, String> issueEpics = new HashMap<>();
////            ObjectId jiraFeatureId = testResultCollectorRepository.findByName(FeatureCollectorConstants.JIRA_XRAY).getId();
//            Set<String> issueTypeNames = new HashSet<>();
//            for (String issueTypeName : testResultSettings.getJiraIssueTypeNames()) {
//                issueTypeNames.add(issueTypeName.toLowerCase(Locale.getDefault()));
//            }
//
//
//            for (Issue issue : currentPagedJiraRs) {
////                TestExecution.Test issueId = new TestExecution.Test(issue.getId());
//                try {
//                    TestExecution testExecution = new TestExecution(new URI(""), issue.getKey(), issue.getId());
//                    Promise<Iterable<TestExecution.Test>> tests = this.getTests(testExecution);
//                    Iterable<TestExecution.Test> itr = tests.get(10, TimeUnit.SECONDS);
//
//                    itr.forEach(test -> {
//                        LOGGER.info("\n TEST ID: " + test.getId() + " TEST SELF" + test.getSelf() + " TEST KEY" + test.getKey());
//                    });
//
//                } catch (URISyntaxException u) {
//                    LOGGER.error("URI Syntax Invalid");
//                } catch (InterruptedException i) {
//
//                } catch (ExecutionException ex) {
//
//                } catch (TimeoutException t) {
//
//                }


//                Map<String, IssueField> fields = buildFieldMap(issue.getFields());
//                IssueType issueType = issue.getIssueType();
//                User assignee = issue.getAssignee();
//                IssueField epic = fields.get(featureSettings.getJiraEpicIdFieldName());
//                IssueField sprint = fields.get(featureSettings.getJiraSprintDataFieldName());
//
//                if (issueTypeNames.contains(TOOLS.sanitizeResponse(issueType.getName()).toLowerCase(Locale.getDefault()))) {
//                    if (LOGGER.isDebugEnabled()) {
//                        LOGGER.debug(String.format("[%-12s] %s",
//                                TOOLS.sanitizeResponse(issue.getKey()),
//                                TOOLS.sanitizeResponse(issue.getSummary())));
//                    }
//
//                    // collectorId
//                    feature.setCollectorId(jiraFeatureId);
//
//                    // ID
//                    feature.setsId(TOOLS.sanitizeResponse(issue.getId()));
//
//                    // Type
//                    feature.setsTypeId(TOOLS.sanitizeResponse(issueType.getId()));
//                    feature.setsTypeName(TOOLS.sanitizeResponse(issueType.getName()));
//
//                    processFeatureData(feature, issue, fields);
//
//                    // delay processing epic data for performance
//                    if (epic != null && epic.getValue() != null && !TOOLS.sanitizeResponse(epic.getValue()).isEmpty()) {
//                        issueEpics.put(feature.getsId(), TOOLS.sanitizeResponse(epic.getValue()));
//                    }
//
//
//                    processSprintData(feature, sprint);
//
//                    processAssigneeData(feature, assignee);
//
//                    featuresToSave.add(feature);
//                }
//            }
//
//            // Load epic data into cache
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("Processing epic data");
//            }
//
//            long epicStartTime = System.currentTimeMillis();
//            Collection<String> epicsToLoad = issueEpics.values();
//            loadEpicData(epicsToLoad);
//
//            for (Feature feature : featuresToSave) {
//                String epicKey = issueEpics.get(feature.getsId());
//
//                processEpicData(feature, epicKey);
//            }
//
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("Processing epic data took " + (System.currentTimeMillis() - epicStartTime) + " ms");
//            }
//
//            // Saving back to MongoDB
//            featureRepo.save(featuresToSave);
//            }
//        }
    }

    public Promise<Iterable<TestExecution.Test>> getTests(TestExecution testExecution) {
        UriBuilder uriBuilder= UriBuilder.fromUri(baseUri);
        uriBuilder.path("testexec").path("{isssue-key}").path("test");
        return this.getAndParse(uriBuilder.build(testExecution.getKey()),this.testsParser);
    }


    /**
     * Adds/Removes the test associated with this test execution
     * @param testExec
     * @return
     */
    public Promise<Void> setTests(TestExecution testExec) {
        UriBuilder uriBuilder= UriBuilder.fromUri(baseUri);
        LOGGER.info("\n IN setTests Method");
        this.updateMongoInfo(new ArrayList<>());

        uriBuilder.path("testexec").path("{isssue-key}").path("test");
        return this.postAndParse(uriBuilder.build(testExec.getKey()), testExec, execUpdateGenerator, new JsonObjectParser<Void>() {
            public Void parse(JSONObject jsonObject) throws JSONException {
                return null;
            }
        });
    }

    /**
     * Removes a test from the test execution identified by his test key
     * @param testExecKey
     * @param testKey
     * @return
     */
    public Promise<Void> removeTest(TestExecution testExecKey, TestExecution.Test testKey) {
        UriBuilder uriBuilder= UriBuilder.fromUri(baseUri);
        uriBuilder.path("testexec").path("{isssue-key}").path("test").path("{ŧest-key");
        return this.delete(uriBuilder.build(testExecKey.getKey(),testKey.getKey()));
    }


    /**
     * Method who queries the JQL testTestExecution() from the X-RAY plugin. Returning the test-executions related to this test identified by his key.
     * @param test Test containin the test key to search for.
     * @return A list of test execution promises which runs this test.
     */
    // TODO: MOVE THIS METHOD TO A BOUNDARY
    public Promise<Iterable<TestExecution>> get(TestExecution.Test test){
        if(test.getKey()==null){
            throw new IllegalArgumentException("KEY NOT SET");
        }
        Promise<SearchResult> searchResultPromise= searchRestClient.searchJql("issue in testTestExecutions(\""+test.getKey()+"\") ");
        return searchResultPromise.map(new Function<SearchResult, Iterable<TestExecution>>() {
            public Iterable<TestExecution> apply(@Nullable SearchResult searchResult) {
                ArrayList<TestExecution> testExceList=new ArrayList<TestExecution>();
                for(Issue testExcecIssue : searchResult.getIssues()){
                    testExceList.add(new TestExecution(testExcecIssue.getSelf(),testExcecIssue.getKey(),testExcecIssue.getId()));
                }
                return testExceList;
            }
        });
    }
}
