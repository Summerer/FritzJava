import com.github.kaklakariada.fritzbox.HomeAutomation;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

class Monitor {
    private static final String switchAin = CredentialParser.getAin();
    private static final HomeAutomation homeAutomation = HomeAutomation.connect("http://192.168.178.1", CredentialParser.getUsername(), CredentialParser.getPassword());

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    private static final LocalDateTime currentTime = LocalDateTime.now();
    private static PrintWriter writer = null;
    private static String filename;
    private static boolean wantsMail = false;

    public static void main(String[] args) {
        askUserForMail();
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        Thread logger = new Thread(new DeviceMonitor(), "logger");
        logger.start();
    }

    private static void askUserForMail() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Would you like to receive this log per mail? (yes/no)");
        String userResponse = sc.nextLine();
        if (userResponse.equals("yes") || userResponse.equals("y")) {
            wantsMail = true;
        } else {
            System.out.println("Okay.");
        }
    }

    private static void closeWriter() {
        if (writer != null) {
            writer.close();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    static class DeviceMonitor implements Runnable {
        @Override
        public void run() {
            try {
                filename = "powerDraw_" + dtf.format(currentTime) + ".log";
                writer = new PrintWriter(filename, StandardCharsets.UTF_8);
                while (true) {
                    LocalDateTime now = LocalDateTime.now();
                    if (homeAutomation.getSwitchState(switchAin)) {
                        String output = "Current load: " + homeAutomation.getSwitchPowerWatt(switchAin) + " W  " + dtf.format(now);
                        writer.println(output);
                    } else {
                        String output = "The switch is off!  " + dtf.format(now);
                        writer.println(output);
                    }
                    writer.flush();
                    Thread.sleep(60000);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            // this line will never be called
            closeWriter();
        }
    }

    private static class ShutdownThread extends Thread {
        @Override
        public void run() {
            closeWriter();
            if (wantsMail) {
                Mail.sendMailWithAttachment("Device Log", "Here is your requested log from: " + dtf.format(currentTime), filename);
            }
        }
    }
}
