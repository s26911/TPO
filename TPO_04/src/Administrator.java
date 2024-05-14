import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Administrator {
    SocketChannel socketChannel;
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    public Administrator(String host, int port) {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(host, port));
//            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String addTopic(String topicName) throws IOException {
        socketChannel.write(ByteBuffer.wrap(("ADDTOPIC " + topicName + "\n").getBytes()));
        return readLine();
    }
    public String delTopic(String topicName) throws IOException {
        socketChannel.write(ByteBuffer.wrap(("DELTOPIC " + topicName + "\n").getBytes()));
        return readLine();
    }
    public String send(String topicName, String content) throws IOException {
        content = content.replace("\t", "").replace("\n", "\t");

        socketChannel.write(ByteBuffer.wrap(("SEND " + topicName + " " + content + "\n").getBytes()));
        return readLine();
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
        System.out.println("ADMIN LINE READ: " + line);
        return line.toString();
    }
}
