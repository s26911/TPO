import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    // TO IMPLEMENT
    // Maintain topic collection
    // Listen for commands from admin
    // Subscribe the clients and maintain client database
    boolean isRunning = true;
    HashMap<String, ArrayList<SocketChannel>> topicsClients = new HashMap<>();      // key = topic name
    ByteBuffer buffer = ByteBuffer.allocate(1024);                          // value = list of subscribed clients channels

    ServerSocketChannel serverSocketChannel;
    Selector selector;
    String address;
    int port;

    public Server(String address, int port) {
        this.address = address;
        this.port = port;

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(address, port));
            selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            handleConnections();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void handleConnections() throws IOException {
        while (isRunning) {
            selector.select();
            var keys = selector.selectedKeys();
            var iter = keys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                if (key.isAcceptable()) {
                    SocketChannel incoming = serverSocketChannel.accept();
                    incoming.configureBlocking(false);
                    incoming.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                } else if (key.isReadable()) {
                    SocketChannel incoming = (SocketChannel) key.channel();
                    handleRequest(incoming);
                } else if (key.isWritable()) {
                    // TODO?
                }
            }
        }
    }

    private String readLine (SocketChannel incoming) throws IOException {
        buffer.clear();

        StringBuilder line = new StringBuilder();
        outer:
        while (true) {
            if (incoming.read(buffer) > 0) {
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

        return line.toString();
    }

    private void handleRequest(SocketChannel incoming) throws IOException {

    }
}
