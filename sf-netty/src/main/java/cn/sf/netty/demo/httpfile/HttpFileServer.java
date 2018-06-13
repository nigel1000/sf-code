package cn.sf.netty.demo.httpfile;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {

    public void run(final int port, final String url) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            //首先向ChannelPipeline中添加HTTP请求消息解码器，
                            ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
                            //添加了HttpObjectAggregator解码器，
                            //它的作用是将多个消息转换为单一的FullHttpRequest或者FullHttpResponse，
                            //原因是HTTP解码器在每个HTTP消息中会生成多个消息对象。
                            //（1）HttpRequest / HttpResponse；
                            //（2）HttpContent；
                            //（3）LastHttpContent。
                            ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
                            //新增HTTP响应编码器，对HTTP响应消息进行编码；
                            ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
                            //新增Chunked handler，它的主要作用是支持异步发送大的码流（例如大的文件传输），
                            //但不占用过多的内存，防止发生Java内存溢出错误。
                            ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
                            //添加HttpFileServerHandler，用于文件服务器的业务逻辑处理。
                            ch.pipeline().addLast("fileServerHandler",new HttpFileServerHandler(url));
                        }
                    });
            ChannelFuture future = b.bind("127.0.0.1", port).sync();
            System.out.println("HTTP文件目录服务器启动，网址是 : " + "http://127.0.0.1:"
                    + port + url);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8081;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        String url = "/Users";
        if (args.length > 1)
            url = args[1];
        //它有两个参数：第一个是端口，第二个是HTTP服务端的URL路径。
        new HttpFileServer().run(port, url);
    }
}