package com.fugui.learning.nio2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AsyncEchoServerWithFuture {

    private AsynchronousServerSocketChannel server;

    private AsynchronousSocketChannel worker;
    private Future<AsynchronousSocketChannel> future;

    public void runServer() throws ExecutionException, InterruptedException, IOException, TimeoutException {
        //获取操作结果
        System.out.println("Open Server Channel");
        server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("127.0.0.1", 9090));
        future = server.accept();

        worker = future.get();
        if (worker != null && worker.isOpen()) {
            ByteBuffer buffer = ByteBuffer.allocate(100);
            //将通道中的数据写入缓冲区
            worker.read(buffer).get(10, TimeUnit.SECONDS);
            System.out.println("received from client: " + new String(buffer.array()));
        }
        server.close();
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException, TimeoutException {
        AsyncEchoServerWithFuture server = new AsyncEchoServerWithFuture();
        server.runServer();
    }
}

