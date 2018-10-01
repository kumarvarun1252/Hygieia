package com.capitalone.dashboard.client.core.json;

import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.capitalone.dashboard.util.RendereableItem;
import com.capitalone.dashboard.util.RendereableItemImpl;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by lucho on 22/08/16.
 */
public class RendereableItemJsonParser implements JsonObjectParser<RendereableItem> {
    private static final String KEY_RAW="raw";
    private static final String KEY_RENDERED="rendered";

    public RendereableItem parse(JSONObject jsonObject) throws JSONException {
        return new RendereableItemImpl(jsonObject.optString(KEY_RAW),jsonObject.optString(KEY_RENDERED));
    }
}
