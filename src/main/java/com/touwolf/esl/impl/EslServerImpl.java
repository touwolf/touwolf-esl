
package com.touwolf.esl.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.touwolf.esl.EslDialPlan;
import com.touwolf.esl.EslServer;

/**
 *
 */

public class EslServerImpl implements EslServer
{
    private static final Logger LOG = Logger.getLogger(EslServerImpl.class.getName());

    private EventLoopGroup group;

    @Override
    public void start(int port)
    {
        try
        {
            EslDialPlan dialPlan = Ctxs.app().find(EslDialPlan.class);
            group = new NioEventLoopGroup();
            try
            {
                ServerBootstrap b = new ServerBootstrap();
                b.group(group)
                        .channel(NioServerSocketChannel.class)
                        .localAddress(port)
                        .childHandler(new ChannelInitializer<SocketChannel>()
                        {
                            @Override
                            public void initChannel(SocketChannel ch)
                            {
                                ch.pipeline().addLast("decoder", new EslMessageDecoder());
                                ch.pipeline().addLast("handler", new EslMessageHandler(dialPlan.createHandler()));
                            }
                        });
                ChannelFuture f = b.bind(port).sync();
                f.channel().closeFuture().sync();
            }
            finally
            {
                group.shutdownGracefully().sync();
            }
        }
        catch (InterruptedException e)
        {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void stop()
    {
        try
        {
            group.shutdownGracefully().sync();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(EslServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
