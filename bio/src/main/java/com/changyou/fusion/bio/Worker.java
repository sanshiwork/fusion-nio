package com.changyou.fusion.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

public class Worker implements Runnable {

    private Socket socket;

    Worker(Socket socket) {
        this.socket = socket;
        System.out.println("connect:" + socket.getRemoteSocketAddress());
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            byte[] data = new byte[1024];
            int length;
            while (true) {
                // 读取客户端的输入(read阻塞)
                length = in.read(data);

                // 断开处理
                if (length < 0) {
                    System.out.println("disconnect:" + socket.getRemoteSocketAddress());
                    socket.close();
                    break;
                }

                // 打印读取内容
                System.out.println(new String(Arrays.copyOfRange(data, 0, length)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
