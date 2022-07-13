package netty.zxx;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.util.Scanner;


public class WarnClient {

    //属性
    private final String host;
    private final int port;

    public WarnClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            //得到pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            //加入相关handler
                            //向pipeline加入编码器
                            pipeline.addLast("encoder",new ProtobufEncoder());
                            //向pipeline加入解码器
                            pipeline.addLast("decoder", new ProtobufDecoder(MessagePOJO.Msg.getDefaultInstance()));
                            //加入自定义的handler
                            pipeline.addLast(new WarnClientHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            //得到channel
            Channel channel = channelFuture.channel();
            System.out.println("-------" + channel.localAddress() + "--------");
            //客户端需要输入信息，创建一个扫描器
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("请输入当前地点： ");
                String address = scanner.nextLine();
                System.out.print("请输入当前车祸类型： ");
                String type = scanner.nextLine();
                MessagePOJO.Msg message = MessagePOJO.Msg.newBuilder().setAddress(address).setType(type).build();
                //通过channel 发送到服务器端
                channel.write(message);
            }

        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new WarnClient("127.0.0.1", 17000).run();
    }
}
