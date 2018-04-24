package com.changyou.fusion.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel socket;

    ReadHandler(AsynchronousSocketChannel socket) {
        this.socket = socket;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {

        // 断开处理
        if (result < 0) {
            try {
                System.out.println("disconnect:" + socket.getRemoteAddress());
                socket.close();
                socket = null;
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        attachment.flip();
        byte[] data = new byte[attachment.limit()];
        attachment.get(data);
        System.out.println(new String(data));
        attachment.clear();

        // 处理下一个read
        socket.read(attachment, attachment, ReadHandler.this);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        exc.printStackTrace();
    }
}
