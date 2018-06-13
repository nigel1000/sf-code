package cn.sf.netty.demo.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class ChineseProverbServer {
    public void run(int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            //由于使用UDP通信，在创建Channel的时候需要通过NioDatagramChannel来创建
            b.group(group).channel(NioDatagramChannel.class)
                    //随后设置Socket参数支持广播，
                    .option(ChannelOption.SO_BROADCAST, true)
                    //最后设置业务处理handler。
                    //相比于TCP通信，UDP不存在客户端和服务端的实际连接，
                    //因此不需要为连接（ChannelPipeline）设置handler，
                    //对于服务端，只需要设置启动辅助类的handler即可。
                    .handler(new ChineseProverbServerHandler());
            b.bind(port).sync().channel().closeFuture().await();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        new ChineseProverbServer().run(port);
    }
}