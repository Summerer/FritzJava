import org.junit.Test;

public class MailTest {
    @Test
    public void testOsDir() {
        System.out.println(System.getProperty("user.dir"));
    }

    @Test
    public void receiverParsing() {
        System.out.println(CredentialParser.getMailReceiver());
    }
}