package cn.sf.netty.demo.base.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandle implements Runnable {
    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public TimeClientHandle(String host, int port) {
        //构造函数用于初始化NIO的多路复用器和SocketChannel对象。
        //需要注意的是，创建SocketChannel之后，需要将其设置为异步非阻塞模式。
        //我们可以设置SocketChannel的TCP参数，例如接收和发送的TCP缓冲区大小。
        this.host = host == null ? "127.0.0.1" : host;
        this.port = port;
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            //作为示例，连接是成功的，所以不需要做重连操作，因此将其放到循环之前。
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop) {
            try {
                //在循环体中轮询多路复用器Selector，当有就绪的Channel时，执行handleInput(key)方法
                selector.select(1000);
                Set selectedKeys = selector.selectedKeys();
                Iterator it = selectedKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = (SelectionKey) it.next();
                    it.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null)
                                key.channel().close();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        //线程退出循环后，我们需要对连接资源进行释放，以实现“优雅退出”.
        //由于多路复用器上可能注册成千上万的Channel或者pipe，如果一一对这些资源进行释放显然不合适。
        //因此，JDK底层会自动释放所有跟此多路复用器关联的资源。
        //多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
        if (selector != null)
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void handleInput(SelectionKey key) throws IOException {
        //我们首先对SelectionKey进行判断，看它处于什么状态。
        if (key.isValid()) {
            // 判断是否连接成功
            SocketChannel sc = (SocketChannel) key.channel();
            //如果是处于连接状态，说明服务端已经返回ACK应答消息。
            //这时我们需要对连接结果进行判断，调用SocketChannel的finishConnect()方法，
            //如果返回值为true，说明客户端连接成功；如果返回值为false或者直接抛出IOException，说明连接失败。
            //在本例程中，返回值为true，说明连接成功。
            if (key.isConnectable()) {
                if (sc.finishConnect()) {
                    //将SocketChannel注册到多路复用器上，注册SelectionKey.OP_READ操作位，
                    //监听网络读操作，然后发送请求消息给服务端。
                    sc.register(selector, SelectionKey.OP_READ);
                    doWrite(sc);
                } else {
                    System.exit(1);// 连接失败，进程退出
                }
            }
            //客户端是如何读取时间服务器应答消息的。
            if (key.isReadable()) {
                //如果客户端接收到了服务端的应答消息，则SocketChannel是可读的，
                //由于无法事先判断应答码流的大小，我们就预分配1M的接收缓冲区用于读取应答消息，
                //调用SocketChannel的read()方法进行异步读取操作。由于是异步操作，所以必须对读取的结果进行判断。
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {
                    //如果读取到了消息，则对消息进行解码，最后打印结果。执行完成后将stop置为true，线程退出循环。
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("Now is : " + body);
                    this.stop = true;
                } else if (readBytes < 0) {
                    // 对端链路关闭
                    key.cancel();
                    sc.close();
                } else
                    ; // 读到0字节，忽略
            }
        }

    }

    //首先对SocketChannel的connect()操作进行判断，如果连接成功，
    //则将SocketChannel注册到多路复用器Selector上，注册SelectionKey.OP_READ，
    //如果没有直接连接成功，则说明服务端没有返回TCP握手应答消息，
    //但这并不代表连接失败，我们需要将SocketChannel注册到多路复用器Selector上，
    //注册SelectionKey.OP_CONNECT，当服务端返回TCP syn-ack消息后，
    //Selector就能够轮询到这个SocketChannel处于连接就绪状态。
    private void doConnect() throws IOException {
        // 如果直接连接成功，则注册到多路复用器上，发送请求消息，读应答
        if (socketChannel.connect(new InetSocketAddress(host, port))) {
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        } else {
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    //构造请求消息体，然后对其编码，写入到发送缓冲区中，最后调用SocketChannel的write方法进行发送。
    //由于发送是异步的，所以会存在“半包写”问题。最后通过hasRemaining()方法对发送结果进行判断，
    //如果缓冲区中的消息全部发送完成，打印"Send order 2 server succeed."
    private void doWrite(SocketChannel sc) throws IOException {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        sc.write(writeBuffer);
        if (!writeBuffer.hasRemaining())
            System.out.println("Send order 2 server succeed.");
    }
}