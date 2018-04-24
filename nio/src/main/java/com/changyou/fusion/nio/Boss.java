package com.changyou.fusion.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

public class Boss implements Runnable {

    private ServerSocketChannel serverSocket;

    private Selector selector;

    private Worker worker;

    private Boss(ServerSocketChannel serverSocket, Selector selector, Worker worker) {
        this.serverSocket = serverSocket;
        this.selector = selector;
        this.worker = worker;
    }

    @Override
    public void run() {
        try {
            // 启动Worker线程
            new Thread(worker).start();

            // 监听端口
            serverSocket.configureBlocking(false);
            serverSocket.bind(new InetSocketAddress(8001));
            serverSocket.register(selector, SelectionKey.OP_ACCEPT, this.serverSocket);

            while (true) {
                // 一直阻塞(select)到有客户端连接为止
                selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey selectionKey = it.next();
                    it.remove();
                    // 处理连接事件
                    if (selectionKey.isAcceptable()) {
                        SocketChannel socket = ((ServerSocketChannel) selectionKey.attachment()).accept();
                        System.out.println("connect:" + socket.getRemoteAddress());

                        // 将socket交给其他线程处理IO(Worker)
                        socket.configureBlocking(false);
                        worker.register(socket);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 入口函数
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // 开启一个线程(Boss)，负责接收客户端连接
        new Thread(new Boss(ServerSocketChannel.open(), Selector.open(), new Worker(Selector.open()))).start();

        // 暂停main方法，防止进程退出
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
