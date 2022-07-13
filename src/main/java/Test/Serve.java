package Test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @Author: Lishenglong
 * @Date: 2022/5/23 16:03
 */
public class Serve {
    private int port;
    public Serve(int port){
        this.port=port;
    }
    public void run() throws Exception{

        EventLoopGroup boosEventLoopGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerEvebtLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosEventLoopGroup,workerEvebtLoopGroup)
                    .channel(ServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("decoder",new StringDecoder());
                            pipeline.addLast("encoder",new StringEncoder());
                        }
                    });
            System.out.println("服务器准备好");
            ChannelFuture sync = b.bind(port).sync();
            sync.channel().closeFuture().sync();
        } finally {
            boosEventLoopGroup.shutdownGracefully();
            workerEvebtLoopGroup.shutdownGracefully();
        }


    }

    public static void main(String[] args) {
        try {
            new Serve(6668).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
