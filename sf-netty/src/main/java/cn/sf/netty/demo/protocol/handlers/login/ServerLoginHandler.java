package cn.sf.netty.demo.protocol.handlers.login;

import cn.sf.netty.demo.protocol.model.Header;
import cn.sf.netty.demo.protocol.model.MessageType;
import cn.sf.netty.demo.protocol.model.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerLoginHandler extends ChannelHandlerAdapter {

    private Map<String,Boolean> nodeCheck = new ConcurrentHashMap<>();
    private String[] whiteList = {"127.0.0.1"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj)throws Exception {
        NettyMessage msg = (NettyMessage) obj;

        // 如果是握手请求消息，处理，其他消息透传
        if (msg.getHeader() != null
                && msg.getHeader().getType() == MessageType.LOGIN_REQ.value()) {

            System.out.println(new Date()+" get login request: " + msg + " body [" + msg.getBody() + "]");

            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp;
            // 重复登录，拒绝
            // 重复登录保护
            if (nodeCheck.containsKey(nodeIndex)) {
                loginResp = buildResponse((byte) -1);
            } else {
                //IP认证白名单列表
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                for (String wip : whiteList) {
                    if (wip.equals(ip)) {
                        isOK = true;
                        break;
                    }
                }
                //通过buildResponse构造握手应答消息返回给客户端
                loginResp = isOK ? buildResponse((byte) 0) : buildResponse((byte) -1);
                if (isOK) {
                    nodeCheck.put(nodeIndex, true);
                }
            }
            ctx.writeAndFlush(loginResp);
            System.out.println("send login response: " + loginResp + " body [" + loginResp.getBody() + "]");
        } else {
            ctx.fireChannelRead(obj);
        }
    }

    private NettyMessage buildResponse(byte result) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(result);
        return message;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception {
        //当发生异常关闭链路的时候，需要将客户端的信息从登录注册表中去注册，以保证后续客户端可以重连成功。
        nodeCheck.remove(ctx.channel().remoteAddress().toString());//删除缓存
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}