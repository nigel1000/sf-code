package cn.sf.netty.demo.base.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {


    public void connect(int port, String host) throws Exception {
        // 配置客户端NIO线程组
        //首先创建客户端处理I/O读写的NioEventLoop Group线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //继续创建客户端辅助启动类Bootstrap，随后需要对其进行配置。
            //与服务端不同的是，它的Channel需要设置为NioSocketChannel
            //然后为其添加handler，此处为了简单直接创建匿名内部类，实现initChannel方法，
            //其作用是当创建NioSocketChannel成功之后，
            //在初始化它的时候将它的ChannelHandler设置到ChannelPipeline中，用于处理网络I/O事件。
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new TimeClientHandler());
                        }
                    });

            // 发起异步连接操作
            //客户端启动辅助类设置完成之后，调用connect方法发起异步连接，
            //然后调用同步方法等待连接成功。
            ChannelFuture f = b.connect(host, port).sync();

            // 等待客户端链路关闭
            //当客户端连接关闭之后，客户端主函数退出.
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放NIO线程组
            //在退出之前，释放NIO线程组的资源。
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new TimeClient().connect(8080, "127.0.0.1");
    }
}
