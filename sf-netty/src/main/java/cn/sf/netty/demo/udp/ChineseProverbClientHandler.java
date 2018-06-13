package cn.sf.netty.demo.udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class ChineseProverbClientHandler extends SimpleChannelInboundHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object o)
            throws Exception {
        //接收到服务端的消息之后将其转成字符串，然后判断是否以“谚语查询结果：”开头，
        //如果没有发生丢包等问题，数据是完整的，就打印查询结果，然后释放资源。
        DatagramPacket msg = (DatagramPacket)o;
        String response = msg.content().toString(CharsetUtil.UTF_8);
        if (response.startsWith("谚语查询结果: ")) {
            System.out.println(response);
            //隔一秒查询一次
            Thread.sleep(1000);
            ctx.writeAndFlush(
                    new DatagramPacket(
                            Unpooled.copiedBuffer("谚语字典查询?",CharsetUtil.UTF_8),
                            msg.sender()
                    )
            );
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}