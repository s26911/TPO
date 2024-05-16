import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Administrator {
    SocketChannel socketChannel;
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    public Administrator(String host, int port) {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTopic(String topicName) throws IOException {
        socketChannel.write(ByteBuffer.wrap(("ADDTOPIC " + topicName + "\n").getBytes()));
    }

    public void delTopic(String topicName) throws IOException {
        socketChannel.write(ByteBuffer.wrap(("DELTOPIC " + topicName + "\n").getBytes()));
    }

    public void send(String topicName, String content) throws IOException {
        content = content.replace("\t", "").replace("\n", "\t");

        socketChannel.write(ByteBuffer.wrap(("SEND " + topicName + " " + content + "\n").getBytes()));
    }

    public void getTopics() throws IOException {
        socketChannel.write(ByteBuffer.wrap(("LIST\n").getBytes()));
    }
}
