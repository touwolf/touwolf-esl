
package com.touwolf.esl;

import java.io.IOException;

public interface EslClient extends EslController
{
    EslMessage read() throws IOException;
    
    void write(String string) throws IOException;
}
