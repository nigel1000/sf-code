package cn.sf.netty.demo.protocol.handlers.login;

import cn.sf.netty.demo.protocol.model.Header;
import cn.sf.netty.demo.protocol.model.MessageType;
import cn.sf.netty.demo.protocol.model.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

public class ClientLoginHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //当客户端跟服务端TCP三次握手成功之后，由客户端构造握手请求消息发送给服务端
        NettyMessage msg = buildLoginReq();
        ctx.writeAndFlush(msg);
        System.out.println(new Date()+" send login request: " + msg + " body [" + msg.getBody() + "]");
    }

    // 握手请求发送之后，按照协议规范，服务端需要返回握手应答消息。
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception {
        NettyMessage message = (NettyMessage) msg;

        // 如果是握手应答消息，需要判断是否认证成功
        //对握手应答消息进行处理，首先判断消息是否是握手应答消息，
        if (message.getHeader() != null &&
                message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            byte loginResult = (Byte) message.getBody();
            if (loginResult != (byte) 0) {
                // 如果是握手应答消息，则对应答结果进行判断，如果非0，说明认证失败，关闭链路，重新发起连接。
                // 握手失败，关闭连接
                ctx.close();
            } else {
                System.out.println("Login is ok : " + message);
                ctx.fireChannelRead(msg);
            }
        } else {
            // 如果不是，直接透传给后面的ChannelHandler进行处理；
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildLoginReq() {
        // 由于采用IP白名单认证机制，因此，不需要携带消息体，消息体为空，消息类型为3：握手请求消息。
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}