package com.changyou.fusion.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class App {

    /**
     * 入口函数
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(8002));
        serverSocket.accept(serverSocket, new AcceptHandler());

        // 暂停main方法，防止进程退出
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
