package cn.sf.netty.demo.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class ChineseProverbClient {

    public void run(int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            //创建UDP Channel和设置支持广播属性等与服务端完全一致。
            // 由于不需要和服务端建立链路，UDP Channel创建完成之后，客户端就要主动发送广播消息；
            // TCP客户端是在客户端和服务端链路建立成功之后由客户端的业务handler发送消息，这就是两者最大的区别。
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChineseProverbClientHandler());
            Channel ch = b.bind(0).sync().channel();
            // 向网段内的所有机器广播UDP消息
            // 用于构造DatagramPacket发送广播消息，
            // 注意，广播消息的IP设置为“255.255.255.255”。
            // 消息广播之后，客户端等待15s用于接收服务端的应答消息，然后退出并释放资源。
            ch.writeAndFlush(
                    new DatagramPacket(
                            Unpooled.copiedBuffer("谚语字典查询?",CharsetUtil.UTF_8),
                            new InetSocketAddress("255.255.255.255", port)
                    )
            ).sync();
            if (!ch.closeFuture().await(5000)) {
                System.out.println("查询结束!");
            }
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
        new ChineseProverbClient().run(port);
    }
}