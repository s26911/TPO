import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Main {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            Server server = new Server("localhost", 50000);
        });
        t1.start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Thread t2 = new Thread(() -> {
            Client client = new Client("localhost", 50000);
            try {
                ClientGUI gui = new ClientGUI(client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t2.start();

    }
}
