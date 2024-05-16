import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {
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
    protected String readLine() throws IOException {
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
