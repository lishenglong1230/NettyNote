package nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupServer {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private final static int PORT = 6667;

    public GroupServer() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listen() {
        System.out.println("监听线程");
        try {
            while (true) {
                int l = selector.select();
                if (l > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            System.out.println(socketChannel.getRemoteAddress()+ " ：上线");
                        }
                        if (key.isReadable()) {
                            //读
                            readData(key);
                        }
                        iterator.remove();
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    public void readData(SelectionKey key) {
        SocketChannel socketChannel=null;
        try {
            socketChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int read = socketChannel.read(buffer);
            //转发
            if (read>0) {
                String str=new String(buffer.array());
                zhuanfa(str, socketChannel);
            }
        } catch (IOException e) {
            try {
                System.out.println(socketChannel.getRemoteAddress()+"：离线了");
                key.cancel();
                socketChannel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    public void zhuanfa(String msg, SocketChannel self) throws IOException {
        System.out.println("服务器转发消息中：");
        for (SelectionKey key : selector.keys()){
            //当强制类型转换的时候要先判断比较安全。
            SelectableChannel channel = key.channel();
            if(channel instanceof SocketChannel && channel!=self){
                SocketChannel socketChannel = (SocketChannel) channel;
                ByteBuffer buffer=ByteBuffer.wrap(msg.getBytes());
                socketChannel.write(buffer);
            }

        }
    }

    public static void main(String[] args) {
        GroupServer groupServer=new GroupServer();
        groupServer.listen();
    }
}
