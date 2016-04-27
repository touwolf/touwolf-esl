
package com.touwolf.esl.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.touwolf.esl.*;

public class EslMessageHandler extends ChannelInboundHandlerAdapter
{
    private static final Logger LOG = Logger.getLogger(EslMessageHandler.class.getName());

    private final EslDialPlanHandler dialPlanHandler;

    private final List<ExecutedCommand> commands;

    public EslMessageHandler(EslDialPlanHandler dialPlanHandler)
    {
        System.out.println("Creating Handler");
        this.dialPlanHandler = dialPlanHandler;
        this.commands = new LinkedList<>();
    }

    public List<ExecutedCommand> getCommands()
    {
        return commands;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        ByteBuf connectMsg = ctx.alloc().buffer();
        connectMsg.writeBytes("connect\n\n".getBytes());
        ctx.channel().writeAndFlush(connectMsg);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        if(msg instanceof EslMessageImpl)
        {
            EslMessageImpl eslMsg = (EslMessageImpl)msg;
            EslController ctrl = new EslControllerImpl(this, ctx);
            Class<? extends EslDialPlanHandler> cls = dialPlanHandler.getClass();

            Method[] methods = cls.getMethods();
            boolean isEvent = eslMsg.getHeaders().get("Event-Name") != null;
            boolean isCommandReplay = "command/reply".equalsIgnoreCase(eslMsg.getHeaders().get("Content-Type"))
                                        || "api/response".equalsIgnoreCase(eslMsg.getHeaders().get("Content-Type"));
            boolean isDisconect = "text/disconnect-notice".equalsIgnoreCase(eslMsg.getHeaders().get("Content-Type"));
            LOG.log(Level.INFO, "Msg received: " + isEvent + " " + isCommandReplay + " " + eslMsg.getHeaders().get("Content-Type"));

            ExecutedCommand lastCommand = null;
            if(isCommandReplay)
            {
                lastCommand = findLastCommand();
                if(lastCommand != null)
                {
                    LOG.log(Level.INFO, "Last Command: " + lastCommand.getCommand());
                }
            }
            for (Method method : methods)
            {
                if(isEvent)
                {
                    OnMessage onMsg = method.getAnnotation(OnMessage.class);
                    if(onMsg != null)
                    {
                        String eventName = onMsg.eventName();
                        Boolean invoke = eventName != null &&
                                eventName.equalsIgnoreCase(eslMsg.getHeaders().get("Event-Name")) &&
                                hasHeaders(eslMsg, onMsg);

                        if (invoke)
                        {
                            method.invoke(dialPlanHandler, ctrl, eslMsg);
                        }
                    }
                }
                else if(isCommandReplay && lastCommand != null)
                {
                    OnReplay onReplay = method.getAnnotation(OnReplay.class);
                    if(onReplay != null)
                    {
                        String commandName = onReplay.commandName();
                        Boolean invoke = commandName != null &&
                                lastCommand.getName().equals(onReplay.commandName());

                        if(invoke)
                        {
                            eslMsg.getHeaders().put("executed_command_name", lastCommand.getName());
                            eslMsg.getHeaders().put("executed_command_command", lastCommand.getCommand());
                            method.invoke(dialPlanHandler, ctrl, eslMsg);
                        }
                    }
                }
                else if(isDisconect)
                {
                    OnDisconect onDisconect = method.getAnnotation(OnDisconect.class);
                    if(onDisconect != null)
                    {
                        method.invoke(dialPlanHandler, ctrl, eslMsg);
                    }                    
                }
            }
        }
    }

    private boolean hasHeaders(EslMessageImpl eslMsg, OnMessage onMsg)
    {
        WithHeader[] headers = onMsg.headers();
        if(null != headers && headers.length > 0)
        {
            for (WithHeader header : headers)
            {
                String value = eslMsg.getHeaders().get(header.name());
                if(null == value || !value.equals(header.value()))
                {
                    return false;
                }
            }
        }

        return true;
    }

    private ExecutedCommand findLastCommand()
    {
        if(commands.size() > 0)
        {
            ExecutedCommand result = commands.get(0);
            commands.remove(0);
            return result;
        }
        return null;
    }
}
