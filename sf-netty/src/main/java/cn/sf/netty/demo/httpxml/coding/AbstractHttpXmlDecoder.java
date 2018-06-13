package cn.sf.netty.demo.httpxml.coding;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

import java.io.StringReader;
import java.nio.charset.Charset;

public abstract class AbstractHttpXmlDecoder extends MessageToMessageDecoder {
    private IBindingFactory factory;
    private StringReader reader;
    private Class clazz;
    private boolean isPrint;
    private final static String CHARSET_NAME = "UTF-8";
    private final static Charset UTF_8 = Charset.forName(CHARSET_NAME);

    protected AbstractHttpXmlDecoder(Class clazz) {
        this(clazz, false);
    }
    protected AbstractHttpXmlDecoder(Class clazz, boolean isPrint) {
        this.clazz = clazz;
        this.isPrint = isPrint;
    }
    protected Object decode0(ChannelHandlerContext ctx, ByteBuf body)
            throws Exception {
        //从HTTP的消息体中获取请求码流，然后通过JiBx类库将XML转换成POJO对象。
        factory = BindingDirectory.getFactory(clazz);
        String content = body.toString(UTF_8);
        //根据码流开关决定是否打印消息体码流。
        //增加码流开关往往是为了方便问题定位，在实际项目中，需要打印到日志中。
        if (isPrint) {
            System.out.println("The body is : " + content);
        }
        reader = new StringReader(content);
        IUnmarshallingContext uctx = factory.createUnmarshallingContext();
        Object result = uctx.unmarshalDocument(reader);
        reader.close();
        reader = null;
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        // 释放资源
        //如果解码发生异常，要判断StringReader是否已经关闭，
        //如果没有关闭，则关闭输入流并通知JVM对其进行垃圾回收。
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }
}