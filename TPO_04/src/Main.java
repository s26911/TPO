import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Main {
    public static void main(String[] args) {
        Client client = new Client("localhost", 50000);
        try {
            ClientGUI gui = new ClientGUI(client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
