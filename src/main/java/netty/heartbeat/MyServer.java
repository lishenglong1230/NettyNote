package netty.heartbeat;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;
public class MyServer {
    public static void main(String[] args) throws Exception {
        //创建两个线程组
        //BOSS负责接受连接
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //Worker负责读写
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //默认8个NioEventLoop
        try {
            //netty启动的时候需要做的事情
            ServerBootstrap serverBootstrap = new ServerBootstrap();//实例化出服务器启动器BOOT
            serverBootstrap.group(bossGroup, workerGroup);//将BOSS WOker注册到其中
            serverBootstrap.channel(NioServerSocketChannel.class); //服务器是ServerSocket
            serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();//把管道实例化出来
                    //加入一个netty 提供 IdleStateHandler
                        /*
                        说明
                        1. IdleStateHandler 是netty 提供的处理空闲状态的处理器
                        2. long readerIdleTime : 表示多长时间没有读, 就会发送一个心跳检测包检测是否连接
                        3. long writerIdleTime : 表示多长时间没有写, 就会发送一个心跳检测包检测是否连接
                        4. long allIdleTime : 表示多长时间没有读写, 就会发送一个心跳检测包检测是否连接
                        5. 文档说明
                        triggers an {@link IdleStateEvent} when a {@link Channel} has not performed
     * read, write, or both operation for a while.
     *                  6. 当 IdleStateEvent 触发后 , 就会传递给管道 的下一个handler去处理
     *                  通过调用(触发)下一个handler 的 userEventTiggered , 在该方法中去处理 IdleStateEvent(读空闲，写空闲，读写空闲)
                         */
                    //把方法加入到管道里面
                    pipeline.addLast(new IdleStateHandler(10, 5, 7, TimeUnit.SECONDS));
                    //加入一个对空闲检测进一步处理的handler(自定义)
                    pipeline.addLast(new MyServerHandler());
                }
            });
            //启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(17000).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            //在最后不要忘记优雅的关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}