//package com.capitalone.dashboard.client.testexecution;
//
//import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
//import com.capitalone.dashboard.client.JiraXRayRestClient;
//import com.capitalone.dashboard.client.JiraXRayRestClientImpl;
//import com.capitalone.dashboard.client.JiraXRayRestClientSupplier;
////import com.capitalone.dashboard.client.JiraXRayRestClientFactory;
//import com.capitalone.dashboard.client.api.domain.TestExecution;
//import com.capitalone.dashboard.repository.FeatureRepository;
//import com.capitalone.dashboard.repository.TestResultCollectorRepository;
//import com.capitalone.dashboard.repository.TestResultRepository;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.net.URI;
//
//import static org.junit.Assert.assertNotNull;
//
//public class TestExecutionRestClientImplTest {
//
//    private final String TEST_EXEC_KEY="EME-4944";
//    private final String TEST_KEY="EME-1946";
//    private final long TEST_ID=1977;
//
//    private final JiraXRayRestClientSupplier restClientSupplier=new JiraXRayRestClientSupplier();
//    private JiraXRayRestClientImpl restClient;
//    private TestExecution testExecution;
//    DisposableHttpClient httpClient;
//    TestResultCollectorRepository testResultCollectorRepository;
//    TestResultRepository testResultRepository;
//    FeatureRepository featureRepository;
//    JiraXRayRestClient jiraXRayRestClient;
//
//    @Before
//    public void setUp() throws Exception {
//        restClient= (JiraXRayRestClientImpl) restClientSupplier.get();
//        testExecution=new TestExecution(new URI(""),TEST_EXEC_KEY,1977l);
//
//    }
//
//    @After
//    public void tearDown() throws Exception {
//
//    }
//
//    @Test
//    public void testGetTests() throws Exception {
//        restClient=new TestExecutionRestClientImpl(httpClient,testResultCollectorRepository, testResultRepository, featureRepository), jiraXRayRestClient;
//        Iterable<TestExecution.Test>tests= restClient.getTestExecutionClient().getTests(testExecution).claim();
//
//        restClient.getTestExecutionClient().setTests(testExecution);
//
//        for(TestExecution.Test t:tests)
//        {
//            System.out.print("\n TEST KEY: " + t);
//            System.out.print("\n TEST Version: " + t.getVersion());
//            assertNotNull(t);
//        }
//    }