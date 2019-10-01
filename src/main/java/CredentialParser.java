import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * CredentialParser reads the user provided credentials.txt file.
 * It assumes prefixes (e.g. username) and ": " as separator.
 */
class CredentialParser {
    public static String getUsername() {
        return getCredential("username");
    }

    public static String getPassword() {
        return getCredential("password");
    }

    public static String getAin() {
        return getCredential("switchAin");
    }

    public static String getMailSender() {
        return getCredential("mailSender");
    }

    public static String getMailPassword() {
        return getCredential("mailPassword");
    }

    public static String getMailReceiver() {
        return getCredential("mailReceiver");
    }

    public static String getSMTPServer() {
        return getCredential("SMTP_Server");
    }

    private static String getCredential(String requested) {
        File credentials = new File("credentials.txt");
        Scanner scanner = null;
        try {
            scanner = new Scanner(credentials);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (scanner != null) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith(requested)) {
                    String[] temp = line.split(": ");
                    return temp[temp.length - 1];
                }
            }
        }
        return null;
    }
}
