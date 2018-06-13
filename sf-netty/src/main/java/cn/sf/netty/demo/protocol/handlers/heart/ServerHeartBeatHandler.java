package cn.sf.netty.demo.protocol.handlers.heart;

import cn.sf.netty.demo.protocol.model.Header;
import cn.sf.netty.demo.protocol.model.MessageType;
import cn.sf.netty.demo.protocol.model.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

public class ServerHeartBeatHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 返回心跳应答消息
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
            System.out.println(new Date()+" receive client heart beat request : ---> "+ message);
            NettyMessage heartBeat = buildHeatBeat();
            ctx.writeAndFlush(heartBeat);
            System.out.println(new Date()+" send heart beat response to client : ---> "+ heartBeat);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildHeatBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.value());
        message.setHeader(header);
        return message;
    }
}