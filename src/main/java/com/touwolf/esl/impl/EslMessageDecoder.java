/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.touwolf.esl.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.net.URLDecoder;
import java.util.List;

/**
 *
 */
public class EslMessageDecoder extends ByteToMessageDecoder
{
    private EslMessageImpl eslMsg = new EslMessageImpl();

    private StringBuilder sb = new StringBuilder();

    private String header = null;

    private String value = null;

    private boolean isHeader = true;

    private boolean isBody = false;
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        while (in.isReadable())
        {
            char ch = (char) in.readByte();
            if(isBody && ch == '\n')
            {
                eslMsg.setBody(decode(sb.toString()));
                out.add(eslMsg);
                isHeader = true;
                isBody = false;
                sb = new StringBuilder();
                eslMsg = new EslMessageImpl();
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
                    if(!eslMsg.getHeaders().isEmpty())
                    {
                        out.add(eslMsg);
                    }
                    eslMsg = new EslMessageImpl();
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
        }
    }

    private String decode(String toString)
    {
        return toString.replace("%23", "#");
    }
    
}
