package com.changyou.fusion.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;

public class Boss implements Runnable {

    private ServerSocket serverSocket;

    private Boss(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 开启一个线程(Worker)，负责处理客户端IO(accept阻塞)
                new Thread(new Worker(serverSocket.accept())).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 入口函数
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // 开启一个线程(Boss)，负责接收客户端连接
        new Thread(new Boss(new ServerSocket(8000))).start();

        // 暂停main方法，防止进程退出
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();

    }
}
