
package com.touwolf.esl.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.logging.Logger;
import com.touwolf.esl.EslController;
import com.touwolf.esl.HangupCause;

/**
 *
 */
public class EslControllerImpl implements EslController
{
    private static final Logger LOG = Logger.getLogger(EslControllerImpl.class.getName());

    private final ChannelHandlerContext ctx;
    
    private final EslMessageHandler handler;
    
    public EslControllerImpl(EslMessageHandler handler, ChannelHandlerContext ctx)
    {
        this.ctx = ctx;
        this.handler = handler;
    }

    @Override
    public void execute(String uuid, String appName, String appArg)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("sendmsg ");
        sb.append(uuid);
        sb.append('\n');
        sb.append("call-command: execute");
        sb.append('\n');
        sb.append("execute-app-name: ");
        sb.append(appName);
        sb.append('\n');
        if(appArg != null && !appArg.isEmpty())
        {
            sb.append("execute-app-arg: ");
            sb.append(appArg);
            sb.append('\n');
        }
        sb.append('\n');

        String write = sb.toString();
        addCommand(appName, write);
        write(write);
    }

    @Override
    public void hangup(String uuid, HangupCause cause)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("sendmsg ");
        sb.append(uuid);
        sb.append('\n');
        sb.append("call-command: hangup");
        sb.append('\n');
        sb.append("execute-app-name: ");
        sb.append(cause.name());
        sb.append('\n');
        sb.append('\n');

        String write = sb.toString();
        addCommand("hangup", write);
        write(write);
    }

    @Override
    public void event(String format, String events)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("event ");
        sb.append(format);
        sb.append(" ");
        sb.append(events);
        sb.append("\n\n");

        String write = sb.toString();
        addCommand("event", write);
        write(write);
    }

    @Override
    public void originate(String originateString, String app)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("api originate ");
        sb.append(originateString);
        sb.append(" ");
        sb.append(app);
        sb.append("\n\n");

        String write = sb.toString();
        addCommand("originate", write);
        write(write);
    }

    @Override
    public void uuidPark(String uuid)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("api uuid_park ");
        sb.append(uuid);
        sb.append("\n\n");

        String write = sb.toString();
        addCommand("uuid_park", write);
        write(write);
    }

    @Override
    public void uuidMediaReneg(String uuid, String codec)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("api uuid_media_reneg ");
        sb.append(uuid);
        sb.append(" ");
        sb.append(codec);
        sb.append("\n\n");

        String write = sb.toString();
        addCommand("uuid_media_reneg", write);
        write(write);
    }

    @Override
    public void uuidAnswer(String uuid)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("api uuid_answer ");
        sb.append(uuid);
        sb.append("\n\n");

        String write = sb.toString();
        addCommand("uuid_answer", write);
        write(write);
    }

    @Override
    public void uuidBridge(String uuid, String otherUuid)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("api uuid_bridge ");
        sb.append(uuid);
        sb.append(" ");
        sb.append(otherUuid);
        sb.append("\n\n");

        String write = sb.toString();
        addCommand("uuid_bridge", write);
        write(write);
    }

    private void addCommand(String name, String commands)
    {
        handler.getCommands().add(new ExecutedCommand(name, commands));
    }

    private void write(String string)
    {
        ByteBuf connectMsg = ctx.alloc().buffer();
        connectMsg.writeBytes(string.getBytes());
        ctx.channel().writeAndFlush(connectMsg);
    }
}
