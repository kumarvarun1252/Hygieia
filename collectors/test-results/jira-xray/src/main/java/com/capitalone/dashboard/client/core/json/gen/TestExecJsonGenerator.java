package com.capitalone.dashboard.client.core.json.gen;

import com.atlassian.jira.rest.client.internal.json.gen.JsonGenerator;
import com.capitalone.dashboard.client.api.domain.TestExecution;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by lucho on 25/08/16.
 */
public class TestExecJsonGenerator implements JsonGenerator<TestExecution> {
    public JSONObject generate(TestExecution testExecution) throws JSONException {
        return null;
    }
}
