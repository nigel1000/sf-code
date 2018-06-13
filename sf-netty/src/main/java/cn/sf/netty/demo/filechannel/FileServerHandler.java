package cn.sf.netty.demo.filechannel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.RandomAccessFile;

public class FileServerHandler extends SimpleChannelInboundHandler {

    private static final String CR = System.getProperty("line.separator");

    public void messageReceived(ChannelHandlerContext ctx, Object o) throws Exception {
        String msg = (String) o;
        File file = new File(msg);
        if (file.exists()) {
            //首先对文件的合法性进行校验，如果不存在，构造异常消息返回。
            if (!file.isFile()) {
                ctx.writeAndFlush("Not a file : " + file + CR);
                return;
            }
            ctx.write(file + " " + file.length() + CR);
            //如果文件存在，使用RandomAccessFile以只读的方式打开文件，
            RandomAccessFile randomAccessFile = new RandomAccessFile(msg, "r");
            //通过Netty提供的DefaultFileRegion进行文件传输，
            //它有如下三个参数。
            //FileChannel：文件通道，用于对文件进行读写操作；FileChannel不能直接被打开，需要InputStream,OutputStream,RandomAccessFile来获取一个实例
            //Position：文件操作的指针位置，读取或者写入的起始点；
            //Count：操作的总字节数。
            FileRegion region = new DefaultFileRegion(
                    randomAccessFile.getChannel(), 0, randomAccessFile.length());
            //直接调用ChannelHandlerContext的write方法实现文件的发送。Netty底层对文件写入进行了封装，上层应用不需要关心发送的细节。
            ctx.write(region);
            // 最后写入回车换行符告知CMD控制台：文件传输结束。
            ctx.writeAndFlush(CR);
            randomAccessFile.close();
        } else {
            ctx.writeAndFlush("File not found: " + file + CR);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}