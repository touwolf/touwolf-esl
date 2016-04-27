
package com.touwolf.esl;

import java.io.IOException;

public interface EslService
{
    EslClient connect(String host, int port, String password) throws IOException;
}
