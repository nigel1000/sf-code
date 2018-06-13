package cn.sf.netty.demo.unpack.delimiter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelHandlerAdapter {

    int counter = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        //直接将接收的消息打印出来，由于DelimiterBasedFrameDecoder自动对请求消息进行了解码
        //后续的ChannelHandler接收到的msg对象就是个完整的消息包；
        //第二个ChannelHandler是StringDecoder，它将ByteBuf解码成字符串对象
        //第三个EchoServerHandler接收到的msg消息就是解码后的字符串对象。
        String body = (String) msg;
        System.out.println("This is " + ++counter + " times receive client : ["+ body + "]");
        body += "$_";
        //由于我们设置DelimiterBasedFrameDecoder过滤掉了分隔符，
        //所以，返回给客户端时需要在请求消息尾部拼接分隔符“$_”，
        //最后创建ByteBuf，将原始消息重新返回给客户端。
        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
        ctx.writeAndFlush(echo);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();// 发生异常，关闭链路
    }
}