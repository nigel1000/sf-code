package cn.sf.netty.demo.unpack.delimiter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {

    public void bind(int port) throws Exception {
// 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChildChannelHandler());
            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer {
        @Override
        protected void initChannel(Channel arg0) throws Exception {
            //首先创建分隔符缓冲对象ByteBuf，本例程中使用“$_”作为分隔符。
            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
            //创建DelimiterBasedFrameDecoder对象，将其加入到ChannelPipeline中。
            //DelimiterBasedFrameDecoder有多个构造方法，这里我们传递两个参数，
            //第一个1024表示单条消息的最大长度，当达到该长度后仍然没有查找到分隔符，
            //就抛出TooLongFrame Exception异常，防止由于异常码流缺失分隔符导致的内存溢出，
            //这是Netty解码器的可靠性保护；第二个参数就是分隔符缓冲对象。
            arg0.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
            arg0.pipeline().addLast(new StringDecoder());
            arg0.pipeline().addLast(new EchoServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        new EchoServer().bind(port);
    }
}
