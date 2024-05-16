import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Util {
    public static String readLine(SocketChannel incoming, ByteBuffer buffer) throws IOException {
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
        System.out.println("LINE READ: \"" + line + "\"");
        return line.toString();
    }
}
