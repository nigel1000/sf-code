package cn.sf.netty.demo.base.nio;

import java.io.IOException;

public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        //MultiplexerTimeServer的多路复用类，它是个一个独立的线程，
        //负责轮询多路复用器Selector，可以处理多个客户端的并发接入。
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread (timeServer, "NIO-MultiplexerTimeServer-001").start();
    }
}