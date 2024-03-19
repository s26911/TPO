import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil {
	public static void processDir(String dirName, String resultFileName) {


        try (FileChannel outChannel = new FileOutputStream(resultFileName).getChannel()) {

            SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (Files.exists(file) && attrs.isRegularFile()) {
                        FileChannel inChannel = new FileInputStream(file.toFile()).getChannel();
                        ByteBuffer inBuff = ByteBuffer.allocate((int) inChannel.size());

                        inChannel.read(inBuff);
                        inBuff.flip();

                        CharBuffer decoded = Charset.forName("Cp1250").decode(inBuff);
                        outChannel.write(Charset.forName("UTF-8").encode(decoded));

                        inChannel.close();
                    }

                    return FileVisitResult.CONTINUE;
                }
            };

            Files.walkFileTree(Paths.get(dirName), visitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
