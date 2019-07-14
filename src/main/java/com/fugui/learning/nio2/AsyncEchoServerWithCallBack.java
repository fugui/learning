package com.fugui.learning.nio2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;

public class AsyncEchoServerWithCallBack {
    private AsynchronousServerSocketChannel server;
    private AsynchronousChannelGroup group;

    public AsyncEchoServerWithCallBack() throws IOException {
        System.out.println("Open Server Channel");
        group = AsynchronousChannelGroup.withFixedThreadPool(10, Executors.defaultThreadFactory());
        server = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress("127.0.0.1", 9090));
        //当有新连接建立时会调用 CompletionHandler接口实现对象中的 completed()方法
        server.accept(null, new CompletionHandler<>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                System.out.println("Accept completed in " + Thread.currentThread().getName() + "@" + Thread.currentThread().getId());
                if (server.isOpen()) {
                    server.accept(null, this);
                }
                AsynchronousSocketChannel worker = result;
                if ((worker != null) && (worker.isOpen())) {

                    ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
                    worker.read(byteBuffer, null, new CompletionHandler<>() {

                        @Override
                        public void completed(Integer result, Object attachment) {
                            if (result > 0) {
                                System.out.println("received the client: " + new String(byteBuffer.array()));
                            }
                        }

                        @Override
                        public void failed(Throwable exc, Object attachment) {

                        }
                    });
                }

            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                //TODO
            }
        });
    }

    public static void main(String[] args) throws IOException {
        new AsyncEchoServerWithCallBack();
    }
}

