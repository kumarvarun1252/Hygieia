package com.capitalone.dashboard.client.async;

import com.capitalone.dashboard.api.domain.TestExecution;
import com.capitalone.dashboard.core.client.JiraXRayRestClientImpl;
import com.capitalone.dashboard.core.client.JiraXRayRestClientFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertNotNull;

/**
 * Created by lucho on 26/08/16.
 */
public class TestExecRestClientTestImpl {

    private final String uriLocation="https://jira.kdc.capitalone.com";
    private final String username="CWC338";
    private final String password= "Developer=123";
    private final String TEST_EXEC_KEY="EA-21658";
    private final String TEST_KEY="PBT-2";
    private final long TEST_ID=1977;

    private final JiraXRayRestClientFactory factory=new JiraXRayRestClientFactory();
    private JiraXRayRestClientImpl restClient;
    private TestExecution testExecution;

    @Before
    public void setUp() throws Exception {
        restClient= (JiraXRayRestClientImpl) factory.createWithBasicHttpAuthentication(new URI(uriLocation),username,password);
        testExecution=new TestExecution(new URI(""),TEST_EXEC_KEY,1977l);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetTests() throws Exception {
        Iterable<TestExecution.Test>tests= restClient.getTestExecutionClient().getTests(testExecution).claim();
        for(TestExecution.Test t:tests)
        {
            assertNotNull(t);
            System.out.println(t);
        }
    }

    @Test
    public void testSetTests() throws Exception {

    }

    @Test
    public void testRemoveTest() throws Exception {

    }

    @Test
    public void testGet() throws Exception {

    }
}