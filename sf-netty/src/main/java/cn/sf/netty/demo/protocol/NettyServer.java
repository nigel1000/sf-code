package cn.sf.netty.demo.protocol;

import cn.sf.netty.demo.protocol.coding.NettyMessageDecoder;
import cn.sf.netty.demo.protocol.coding.NettyMessageEncoder;
import cn.sf.netty.demo.protocol.handlers.heart.ServerHeartBeatHandler;
import cn.sf.netty.demo.protocol.handlers.login.ServerLoginHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.io.IOException;

public class NettyServer {

    public void bind() throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer() {
                    @Override
                    public void initChannel(Channel ch)throws IOException{
                        ch.pipeline().addLast("NettyMessageDecoder",
                                new NettyMessageDecoder(
                                        1024 * 1024,
                                        0,
                                        4,
                                        -4,
                                        4));
                        ch.pipeline().addLast("NettyMessageEncoder",new NettyMessageEncoder());
                        ch.pipeline().addLast("ReadTimeoutHandler",new ReadTimeoutHandler(50));
                        ch.pipeline().addLast("ServerLoginHandler",new ServerLoginHandler());
                        ch.pipeline().addLast("ServerHeartBeatHandler",new ServerHeartBeatHandler());
                    }
                });

        // 绑定端口，同步等待成功
        b.bind(NettyConstant.REMOTEIP, NettyConstant.PORT).sync();
        System.out.println("Netty server start ok : " + (NettyConstant.REMOTEIP + " : " + NettyConstant.PORT));
    }

    public static void main(String[] args) throws Exception {
        new NettyServer().bind();
    }
}