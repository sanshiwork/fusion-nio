package com.changyou.fusion.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Worker implements Runnable {

    private Selector selector;

    private Queue<SocketChannel> sockets;

    Worker(Selector selector) {
        this.selector = selector;
        sockets = new LinkedBlockingQueue<>();
    }

    void register(SocketChannel socketChannel) {
        sockets.add(socketChannel);
    }

    @Override
    public void run() {

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int length;

        while (true) {
            // 一直阻塞(select)到有客户端数据为止
            try {
                selector.select(1000);
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey selectionKey = it.next();
                    it.remove();
                    // 处理读事件
                    if (selectionKey.isReadable()) {
                        SocketChannel socket = ((SocketChannel) selectionKey.attachment());
                        length = socket.read(buffer);

                        // 断开处理
                        if (length < 0) {
                            System.out.println("disconnect:" + socket.getRemoteAddress());
                            socket.close();
                            continue;
                        }

                        // 打印读取内容
                        do {
                            buffer.flip();
                            byte[] data = new byte[buffer.limit()];
                            buffer.get(data);
                            System.out.println(new String(data));

                            // 继续读
                            buffer.clear();
                            length = socket.read(buffer);
                        } while (length > 0);

                    }
                }

                // 待加入的Socket注册到selector里
                SocketChannel socketChannel;
                while ((socketChannel = sockets.poll()) != null) {
                    socketChannel.register(selector, SelectionKey.OP_READ, socketChannel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
