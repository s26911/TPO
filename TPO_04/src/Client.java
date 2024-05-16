import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    SocketChannel socketChannel;
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    public Client(String host, int port) {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void subscribe(String topicName) throws IOException {
        socketChannel.write(ByteBuffer.wrap(("SUBSCRIBE " + topicName + "\n").getBytes()));
    }

    public void unsubscribe(String topicName) throws IOException {
        socketChannel.write(ByteBuffer.wrap(("UNSUBSCRIBE " + topicName + "\n").getBytes()));
    }

    public void getSubscribed() throws IOException {
        socketChannel.write(ByteBuffer.wrap(("LISTSUB\n").getBytes()));
    }

    public void getTopics() throws IOException {
        socketChannel.write(ByteBuffer.wrap(("LIST\n").getBytes()));
    }
}
