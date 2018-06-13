package cn.sf.netty.demo.udp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThreadLocalRandom;

public class ChineseProverbServerHandler extends SimpleChannelInboundHandler {
    // 谚语列表
    private static final String[] DICTIONARY = {
            "只要功夫深，铁棒磨成针。",
            "旧时王谢堂前燕，飞入寻常百姓家。",
            "洛阳亲友如相问，一片冰心在玉壶。",
            "一寸光阴一寸金，寸金难买寸光阴。",
            "老骥伏枥，志在千里。烈士暮年，壮心不已!"};

    private String nextQuote() {
        //由于ChineseProverbServerHandler存在多线程并发操作的可能，
        //所以使用了Netty的线程安全随机类ThreadLocalRandom。
        // 如果使用的是JDK7，可以直接使用JDK7的java.util.concurrent.ThreadLocalRandom。
        int quoteId = ThreadLocalRandom.current().nextInt(DICTIONARY.length);
        return DICTIONARY[quoteId];
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object msg)throws Exception {
        //Netty对UDP进行了封装，因此，接收到的是Netty封装后的io.netty. channel.socket.DatagramPacket对象。
        DatagramPacket packet = (DatagramPacket) msg;
        //将packet内容转换为字符串（利用ByteBuf的toString(Charset)方法），
        String req = packet.content().toString(CharsetUtil.UTF_8);
        System.out.println(req);
        // 然后对请求消息进行合法性判断：如果是“谚语字典查询?”，则构造应答消息返回。
        // DatagramPacket有两个参数：第一个是需要发送的内容，为ByteBuf；
        // 另一个是目的地址，包括IP和端口，可以直接从发送的报文DatagramPacket中获取。
        if ("谚语字典查询?".equals(req)) {
            System.out.println(packet.sender());
            ctx.writeAndFlush(
                    new DatagramPacket(
                            Unpooled.copiedBuffer("谚语查询结果: " + nextQuote(), CharsetUtil.UTF_8),
                            packet.sender()
                    )
            );
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}