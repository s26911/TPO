import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
    public static void main(String[] args) {
        Server server = new Server("localhost", 50000);
    }

    boolean isRunning = true;
    HashMap<String, ArrayList<SocketChannel>> topicsClients = new HashMap<>();      // key = topic name
    ByteBuffer buffer = ByteBuffer.allocate(1024);                          // value = list of subscribed clients channels
    ExecutorService threadPool = Executors.newCachedThreadPool();
    ReadWriteLock lock = new ReentrantReadWriteLock();
    Lock readLock = lock.readLock(), writeLock = lock.writeLock();

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
                    incoming.register(selector, SelectionKey.OP_READ);
                    System.out.println("CONNECTED");
                } else if (key.isReadable()) {
                    SocketChannel incoming = (SocketChannel) key.channel();
                    handleRequest(incoming);
                }
            }
        }
    }

    private void handleRequest(SocketChannel incoming) throws IOException {
        String input = Util.readLine(incoming, buffer);
        String[] line = input.split(" ");
        switch (line[0]) {
            case "SUBSCRIBE" -> subUnsub(incoming, line[1], line[0]);       // SUBSCRIBE TOPIC_NAME
            case "UNSUBSCRIBE" -> subUnsub(incoming, line[1], line[0]);     // UNSUBSCRIBE TOPIC_NAME
            case "ADDTOPIC" -> addTopic(incoming, line[1]);                 // ADDTOPIC TOPIC_NAME
            case "DELTOPIC" -> delTopic(incoming, line[1]);                 // DELTOOPIC TOPIC_NAME
            case "SEND" ->
                    sendText(line[1], input.substring(("SEND " + line[1]).length()));   // SEND TOPIC_NAME TEXT...
            case "LIST" -> listTopics(incoming);
            case "LISTSUB" -> listSubscribed(incoming);
        }
    }

    private void subUnsub(SocketChannel incoming, String topicName, String mode) throws IOException {
        writeLock.lock();
        var list = topicsClients.get(topicName.toLowerCase());
        if (list != null) {
            switch (mode) {
                case "SUBSCRIBE" -> list.add(incoming);
                case "UNSUBSCRIBE" -> list.remove(incoming);
            }
        }
        writeLock.unlock();
        incoming.write(ByteBuffer.wrap(String.format("Successfully %sd to/from %s\n", mode, topicName).getBytes()));

        if (list == null)
            incoming.write(ByteBuffer.wrap("NONEXISTENT TOPIC\n".getBytes()));
    }

    private void addTopic(SocketChannel incoming, String topicName) throws IOException {
        writeLock.lock();
        var result = topicsClients.putIfAbsent(topicName.toLowerCase(), new ArrayList<>());
        writeLock.unlock();

        if (result == null)
            incoming.write(ByteBuffer.wrap(String.format("Successfully added topic %s\n", topicName).getBytes()));
        else
            incoming.write(ByteBuffer.wrap(String.format("Topic %s already exists\n", topicName).getBytes()));
    }

    private void delTopic(SocketChannel incoming, String topicName) throws IOException {
        writeLock.lock();
        var result = topicsClients.remove(topicName.toLowerCase());
        writeLock.unlock();

        if (result != null)
            incoming.write(ByteBuffer.wrap(String.format("Successfully deleted topic %s\n", topicName).getBytes()));
        else
            incoming.write(ByteBuffer.wrap(String.format("Topic %s didn't exists\n", topicName).getBytes()));
    }

    private void sendText(String topicName, String text) throws IOException {
        threadPool.submit(() -> {
            readLock.lock();
            var list = topicsClients.get(topicName);
            for (var client : list) {
                try {
                    client.write(ByteBuffer.wrap((topicName + ": " + text + "\n").getBytes()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            readLock.unlock();
        });
    }

    private void listTopics(SocketChannel incoming) throws IOException {
        readLock.lock();
        String topics = topicsClients.keySet().stream()
                .reduce((x, y) -> x + " " + y).orElse("");
        readLock.unlock();

        incoming.write(ByteBuffer.wrap(("LISTTOPICS " + topics + "\n").getBytes()));
    }

    private void listSubscribed(SocketChannel incoming) throws IOException {
        readLock.lock();
        String topics = topicsClients.entrySet().stream().filter(x -> x.getValue().contains(incoming))
                .map(Map.Entry::getKey).reduce((x, y) -> x + " " + y).orElse("");
        readLock.unlock();

        incoming.write(ByteBuffer.wrap(("LISTSUBSCRIBED" + topics + "\n").getBytes()));
    }
}
