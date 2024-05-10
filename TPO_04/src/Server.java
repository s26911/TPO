import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server{
    // TO IMPLEMENT
    // Maintain topic collection
    // Listen for commands from admin
    // Subscribe the clients and maintain client database
    ArrayList<String> topics = new ArrayList<>();
    HashMap<String, SelectionKey> clients;
    boolean isRunning = true;
    ServerSocketChannel serverSocketChannel;
    Selector selector;
    String address;
    int port;
    HashMap<String, ArrayList<SocketChannel>> topicsClients = new HashMap<>();      // key = topic name
                                                                                    // value = list of subscribed clients channels
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
        while(isRunning){
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
                }
                else if (key.isReadable()) {
                    SocketChannel incoming = (SocketChannel) key.channel();
                    handleRequest(incoming);
                }
                else if (key.isWritable()) {
                    // TODO?
                }
            }
        }
    }

    private void handleRequest(SocketChannel incoming) throws IOException {

    }
}
