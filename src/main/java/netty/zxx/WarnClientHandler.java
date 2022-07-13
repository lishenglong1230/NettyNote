package netty.zxx;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.text.SimpleDateFormat;

public class WarnClientHandler extends SimpleChannelInboundHandler<MessagePOJO.Msg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePOJO.Msg msg) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(msg.getAddress() + "发来车祸报警,车祸类型为" + msg.getType() + "，时间为：" + sdf.format(new java.util.Date()));
    }
}

