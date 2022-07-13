package netty.zxx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class MyMessageDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final byte[] array;
        int length = in.readableBytes();
        array = new byte[length];
        in.getBytes(in.readerIndex(), array, 0, length);
        Message message = new Message();
        out.add(message);


    }
}
