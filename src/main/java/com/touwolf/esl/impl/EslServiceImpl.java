
package com.touwolf.esl.impl;

import java.io.IOException;
import com.touwolf.esl.EslClient;
import com.touwolf.esl.EslService;


public class EslServiceImpl implements EslService
{
    @Override
    public EslClient connect(String host, int port, String password) throws IOException
    {
        return new EslClientImpl(host, port, password);
    }
}
