
package com.touwolf.esl;

public interface EslServer
{
    void start(int port, final EslDialPlan dialplan);
    
    void stop();
}
