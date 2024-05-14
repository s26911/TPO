import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {
    // TO IMPLEMENT:
    // Connect
    // Subscribe to a topic
    // Unsubscribe from a topic
    // Listen and print the messages
    // Disconnect

    SocketChannel socketChannel;
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    public Client(String host, int port) {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(host, port));
//            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String subscribe(String topicName) throws IOException {
        socketChannel.write(ByteBuffer.wrap(("SUBSCRIBE " + topicName + "\n").getBytes()));
        return readLine();
    }
    public String unsubscribe(String topicName) throws IOException {
        socketChannel.write(ByteBuffer.wrap(("UNSUBSCRIBE " + topicName + "\n").getBytes()));
        return readLine();
    }
    public String[] getSubscribed() throws IOException {
        socketChannel.write(ByteBuffer.wrap(("LISTSUB\n").getBytes()));
        return readLine().split(" ");
    }
    public String[] getTopics() throws IOException {
        socketChannel.write(ByteBuffer.wrap(("LIST\n").getBytes()));
        return readLine().split(" ");
    }
    private String readLine() throws IOException {
        buffer.clear();

        StringBuilder line = new StringBuilder();
        outer:
        while (true) {
            if (socketChannel.read(buffer) > 0) {
                buffer.flip();
                CharBuffer cbuf = StandardCharsets.UTF_8.decode(buffer);
                while (cbuf.hasRemaining()) {
                    char c = cbuf.get();
                    if (c == '\n')
                        break outer;
                    line.append(c);
                }
            }
        }
        System.out.println("CLIENT LINE READ: \"" + line + "\"");
        return line.toString();
    }
}
