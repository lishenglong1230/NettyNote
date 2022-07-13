package netty.zxx;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.text.SimpleDateFormat;
import java.util.Scanner;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
         new Thread(()->{

             Scanner sc=new Scanner(System.in);
             System.out.println("请输地址\n");
             String address=sc.nextLine();
             System.out.println("请输入类型\n");
             String type=sc.nextLine();
             MessagePOJO.Msg message = MessagePOJO.Msg.newBuilder().setAddress(address).setType(type).build();

             ctx.writeAndFlush(message);

         }).start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (msg instanceof MessagePOJO.Msg){
            MessagePOJO.Msg message = (MessagePOJO.Msg) msg;
            System.out.println(message.getAddress() + "发来车祸报警,车祸类型为" + message.getType() + "，时间为：" + sdf.format(new java.util.Date()));

        }


    }
}
