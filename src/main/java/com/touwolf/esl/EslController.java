
package com.touwolf.esl;

import java.io.IOException;

public interface EslController
{
    void execute(String uuid, String appName, String appArg) throws IOException;
    
    void hangup(String uuid, HangupCause cause) throws IOException;
    
    void event(String format, String events) throws IOException;

    void originate(String originateString, String app) throws IOException;

    void uuidPark(String uuid) throws IOException;

    void uuidMediaReneg(String uuid, String codec) throws IOException;

    void uuidAnswer(String uuid) throws IOException;

    void uuidBridge(String uuid, String otherUuid) throws IOException;
}
