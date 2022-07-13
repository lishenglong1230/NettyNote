package nio;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Optional;
import java.util.Scanner;

public class GroupClient {
    private SocketChannel socketChannel;
    private final String HOST = "127.0.0.1";
    private final int PORT = 6667;
    private Selector selector;
    private String username;

    public GroupClient() {
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendInfo(String msg) {
        msg = username + "说" + msg;
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readInfo() {
        try {
            int i = selector.select();
            if (i > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        socketChannel.read(buffer);
                        System.out.println(new String(buffer.array()));
                    }
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GroupClient groupClient = new GroupClient();
        //创建线程去读 ，因为读写之间不能阻塞，不同步的。
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    groupClient.readInfo();

                }
            }
        }).start();

        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            //当执行到hasNext（）时，它会先扫描缓冲区中是否有字符，有则返回true，继续扫描。直到扫描为空，这时并不返回false，而是将方法阻塞，等待你输入内容然后继续扫描。
            String msg = sc.next();
            groupClient.sendInfo(msg);
        }
    }

}
