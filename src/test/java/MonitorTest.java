import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

class MonitorTest {

    public static void main(String[] args) throws IOException {
        PrintWriter writer = new PrintWriter("test.txt", StandardCharsets.UTF_8);
        writer.println("Hello");
        writer.close();
    }

}