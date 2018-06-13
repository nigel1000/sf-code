package cn.sf.netty.demo.protocol;

import cn.sf.netty.demo.protocol.coding.NettyMessageDecoder;
import cn.sf.netty.demo.protocol.coding.NettyMessageEncoder;
import cn.sf.netty.demo.protocol.handlers.heart.ClientHeartBeatHandler;
import cn.sf.netty.demo.protocol.handlers.login.ClientLoginHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    EventLoopGroup group = new NioEventLoopGroup();

    public void connect(int port, String host) throws Exception {
        // 配置客户端NIO线程组
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer() {
                        @Override
                        public void initChannel(Channel ch)throws Exception {
                            //NettyMessageDecoder用于Netty消息解码，
                            //为了防止由于单条消息过大导致的内存溢出或者畸形码流导致解码错位引起内存分配失败，
                            //我们对单条消息最大长度进行了上限限制。
                            //第一个参数为信息最大长度，超过这个长度回报异常，
                            //第二参数为长度属性的起始（偏移）位，我们的协议中长度是0到第3个字节，所以这里写0，
                            //第三个参数为“长度属性”的长度，我们是4个字节，所以写4，
                            //第四个参数为长度调节值，在总长被定义为包含包头长度时，修正信息长度，
                            //第五个参数为跳过的字节数，根据需要我们跳过前4个字节，以便接收端直接接受到不含“长度属性”的内容。
                            ch.pipeline().addLast("NettyMessageDecoder",
                                    new NettyMessageDecoder(
                                    1024 * 1024,
                                    0,
                                    4,
                                    -4,
                                    4));
                            //Netty消息编码器，用于协议消息的自动编码。
                            ch.pipeline().addLast("NettyMessageEncoder",new NettyMessageEncoder());
                            //读超时Handler
                            ch.pipeline().addLast("ReadTimeoutHandler",new ReadTimeoutHandler(50));
                            //握手请求Handler
                            ch.pipeline().addLast("ClientLoginHandler",new ClientLoginHandler());
                            //心跳消息Handler
                            ch.pipeline().addLast("ClientHeartBeatHandler",new ClientHeartBeatHandler());
                        }
                    });
            // 发起异步连接操作
            ChannelFuture future = b.connect(
                    new InetSocketAddress(host, port),
                    //绑定本地端口,主要用于服务器重复登录保护
                    new InetSocketAddress(NettyConstant.LOCALIP,NettyConstant.LOCAL_PORT)
            ).sync();

            future.channel().closeFuture().sync();
        } finally {
            // 所有资源释放完成之后，清空资源，再次发起重连操作
            executor.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    try {
                        connect(NettyConstant.PORT, NettyConstant.REMOTEIP);// 发起重连操作
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyClient().connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
    }
}