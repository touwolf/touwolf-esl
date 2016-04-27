
package com.touwolf.esl.impl;

import java.io.IOException;
import java.net.Socket;
import com.touwolf.esl.EslClient;
import com.touwolf.esl.EslMessage;

class EslClientImpl extends AbstractEslController implements EslClient
{
    private final Socket socket;

    protected EslClientImpl(String host, int port, String password) throws IOException
    {
        socket = new Socket(host, port);
        EslMessage msg = read();
        if(msg.get("Content-Type") != null && msg.get("Content-Type").equals("auth/request"))
        {
            write("auth " + password + "\n\n");
        }
        else
        {
            throw new IOException("Cannot connect to the server.");
        }
        msg = read();
        if(msg.get("Reply-Text") != null && msg.get("Reply-Text").startsWith("+OK"))
        {
            return;
        }
        else
        {
            throw new IOException("Invalid password.");
        }
    }

    private EslMessageImpl eslMsg = new EslMessageImpl();

    private StringBuilder sb = new StringBuilder();

    private String header = null;

    private String value = null;

    private boolean isHeader = true;

    private boolean isBody = false;
    
    @Override
    public EslMessage read() throws IOException
    {
        int i = socket.getInputStream().read();
        while (i > -1)
        {
            char ch = (char)i;
            System.out.print(ch);
            if(isBody && ch == '\n')
            {
                eslMsg.setBody(decode(sb.toString()));
                isHeader = true;
                isBody = false;
                sb = new StringBuilder();
                EslMessage toReturn = eslMsg;
                eslMsg = new EslMessageImpl();
                return toReturn;
            }
            else if(isHeader && ch == '\n' && header == null && !isBody)
            {
                if("api/response".equalsIgnoreCase(eslMsg.get("Content-Type")))
                {
                    isHeader = false;
                    isBody = true;
                }
                else
                {
                    EslMessage toReturn = eslMsg;
                    eslMsg = new EslMessageImpl();
                    return toReturn;
                }
                sb = new StringBuilder();
                header = null;
                value = null;
            }
            else if(isHeader && ch == ':' && !isBody)
            {
                header = sb.toString();
                sb = new StringBuilder();
                isHeader = false;
            }
            else if(ch == '\n' && !isBody)
            {
                value = decode(sb.toString());
                isHeader = true;
                sb = new StringBuilder();
                if(header != null && value != null)
                {
                    eslMsg.getHeaders().put(header.trim(), value.trim());
                }
                header = null;
                value = null;
            }
            else
            {
                sb.append(ch);
            }
            i = socket.getInputStream().read();
        }
        return null;
    }

    private String decode(String toString)
    {
        return toString.replace("%23", "#");
    }

    @Override
    public void write(String string) throws IOException
    {
        socket.getOutputStream().write(string.getBytes());
        socket.getOutputStream().flush();
    }

    @Override
    protected void addCommand(String name, String commands)
    {
    }
    
}
