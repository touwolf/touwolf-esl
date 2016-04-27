
package com.touwolf.esl.impl;

import java.util.HashMap;
import java.util.Map;
import com.touwolf.esl.EslMessage;

public class EslMessageImpl implements EslMessage
{
    private final Map<String, String> headers;

    private String body;
    
    public EslMessageImpl()
    {
        this.headers = new HashMap<>();
    }

    protected Map<String, String> getHeaders()
    {
        return headers;
    }
        
    public String get(String header)
    {
        return headers.get(header);
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }
}
