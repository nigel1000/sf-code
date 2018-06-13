package cn.sf.netty.demo.httpfile;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpFileServerHandler extends SimpleChannelInboundHandler {

    private final String url;

    public HttpFileServerHandler(String url) {
        this.url = url;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object o) throws Exception {
        FullHttpRequest request = (FullHttpRequest) o;
        //首先对HTTP请求消息的解码结果进行判断，如果解码失败，直接构造HTTP 400错误返回。
        if (!request.getDecoderResult().isSuccess()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }
        //对请求行中的方法进行判断，如果不是从浏览器或者表单设置为GET发起的请求（例如POST），则构造HTTP 405错误返回。
        if (request.getMethod() != GET) {
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }
        //对请求URL进行包装，然后对sanitizeUri方法展开分析。
        final String uri = request.getUri();
        final String path = sanitizeUri(uri);
        //如果构造的URI不合法，则返回HTTP 403错误。
        if (path == null) {
            sendError(ctx, FORBIDDEN);
            return;
        }
        //使用新组装的URI路径构造File对象。
        File file = new File(path);
        //如果文件不存在或者是系统隐藏文件，则构造HTTP 404异常返回。
        if (file.isHidden() || !file.exists()) {
            sendError(ctx, NOT_FOUND);
            return;
        }
        //如果文件是目录，则发送目录的链接给客户端浏览器。
        if (file.isDirectory()) {
            if (uri.endsWith("/")) {
                sendListing(ctx, file);
            } else {
                sendRedirect(ctx, uri + '/');
            }
            return;
        }
        //如果用户在浏览器上点击超链接直接打开或者下载文件
        //对超链接的文件进行合法性判断，如果不是合法文件，则返回HTTP 403错误。
        if (!file.isFile()) {
            sendError(ctx, FORBIDDEN);
            return;
        }
        //使用随机文件读写类以只读的方式打开文件，如果文件打开失败，则返回HTTP 404错误。
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");// 以只读的方式打开文件
        } catch (FileNotFoundException fnfe) {
            sendError(ctx, NOT_FOUND);
            return;
        }
        //获取文件的长度，构造成功的HTTP应答消息
        long fileLength = randomAccessFile.length();
        //在消息头中设置content length和content type
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        setContentLength(response, fileLength);
        setContentTypeHeader(response, file);
        //判断是否是Keep-Alive，如果是，则在应答消息头中设置Connection为Keep-Alive。
        if (isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        //发送响应消息。
        ctx.write(response);
        ChannelFuture sendFileFuture;
        //通过Netty的ChunkedFile对象直接将文件写入到发送缓冲区中。
        sendFileFuture = ctx.write(
                new ChunkedFile(randomAccessFile, 0, fileLength, 8192),
                ctx.newProgressivePromise());
        //最后为sendFileFuture增加GenericFutureListener，如果发送完成，打印“Transfer complete.”。
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future,
                                            long progress, long total) {
                if (total < 0) { // total unknown
                    System.err.println("Transfer progress: " + progress);
                } else {
                    System.err.println("Transfer progress:" + progress + "/" + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future)
                    throws Exception {
                System.out.println("Transfer complete.");
            }
        });
        //如果使用chunked编码，最后需要发送一个编码结束的空消息体，
        //将LastHttpContent的EMPTY_LAST_CONTENT发送到缓冲区中，标识所有的消息体已经发送完成，
        //同时调用flush方法将之前在发送缓冲区的消息刷新到SocketChannel中发送给对方。
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        //如果是非Keep-Alive的，最后一包消息发送完成之后，服务端要主动关闭连接。
        if (!isKeepAlive(request)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    private String sanitizeUri(String uri) {
        //首先使用JDK的java.net.URLDecoder对URL进行解码，使用UTF-8字符集，
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error();
            }
        }
        //解码成功之后对URI进行合法性判断，如果URI与允许访问的URI一致或者是其子目录（文件），则校验通过，否则返回空。
        if (!uri.startsWith(url)) {
            return null;
        }
        if (!uri.startsWith("/")) {
            return null;
        }
        //将硬编码的文件路径分隔符替换为本地操作系统的文件路径分隔符。
        uri = uri.replace('/', File.separatorChar);
        //对新的URI做二次合法性校验，如果校验失败则直接返回空。
        if (uri.contains(File.separator + '.')
                || uri.contains('.' + File.separator) || uri.startsWith(".")
                || uri.endsWith(".") || INSECURE_URI.matcher(uri).matches()) {
            return null;
        }
        //最后对文件进行拼接，使用当前运行程序所在的工程目录 + URI构造绝对路径返回。
//        return System.getProperty("user.dir") + File.separator + uri;
        return uri;
    }

    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

    private static void sendListing(ChannelHandlerContext ctx, File dir) {
        //首先创建成功的HTTP响应消息
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        //随后设置消息头的类型为“text/html; charset=UTF-8”。
        response.headers().set(CONTENT_TYPE, "text/html;charset=UTF-8");
        //用于构造响应消息体，由于需要将响应结果显示在浏览器上，所以采用了HTML的格式。
        StringBuilder buf = new StringBuilder();
        String dirPath = dir.getPath();
        buf.append("<!DOCTYPE HTML>\r\n");
        buf.append("<HTML><HEAD><TITLE>");
        buf.append(dirPath);
        buf.append(" 目录：");
        buf.append("</TITLE></HEAD><BODY>");
        buf.append("<H3>");
        buf.append(dirPath);
        buf.append(" 目录：");
        buf.append("</H3>\r\n");
        buf.append("<UL>");
        //打印了一个 ..的链接。
        buf.append("<LI>链接：<a href=\"../\">..</a></LI>\r\n");
        //用于展示根目录下的所有文件和文件夹，同时使用超链接来标识。
        for (File f : dir.listFiles()) {
            if (f.isHidden() || !f.canRead()) {
                continue;
            }
            String name = f.getName();
            if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                continue;
            }
            buf.append("<LI>链接：<a href=\"");
            buf.append(name);
            buf.append("\">");
            buf.append(name);
            buf.append("</a></LI>\r\n");
        }
        buf.append("</UL></BODY></HTML>\r\n");
        //分配对应消息的缓冲对象
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        //将缓冲区中的响应消息存放到HTTP应答消息中，然后释放缓冲区
        response.content().writeBytes(buffer);
        buffer.release();
        //最后调用writeAndFlush将响应消息发送到缓冲区并刷新到SocketChannel中。
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendError(ChannelHandlerContext ctx,
                                  HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset= UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }
}