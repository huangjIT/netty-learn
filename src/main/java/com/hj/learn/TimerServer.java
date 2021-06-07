package com.hj.learn;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author HJ
 * @date 2021-06-04
 **/
public class TimerServer {


    private int port;

    public TimerServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        // boss
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        // worker
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // Netty Server端
            ServerBootstrap b = new ServerBootstrap(); // (2)
            // 设置group
            b.group(bossGroup, workerGroup)
                    // 设置当前为Server端的Channel类型是NioServerSocketChannel
                    .channel(NioServerSocketChannel.class) // (3)
                    // 设置所有accepted的Channel的pipeline处理链（需要经过那些ChannelHandle处理）
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeEncoder(), new TimeServerHandler());
                        }
                    });
            // 绑定端口，并初始化NioServerSocketChannel的pipeline，注册事件监听
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            // 关闭
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new TimerServer(port).run();
    }

}
