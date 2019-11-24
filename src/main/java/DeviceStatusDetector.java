import com.github.kaklakariada.fritzbox.HomeAutomation;

/**
 * The DeviceStatusDetector detects the state of a device attached to a FritzDect smart outlet.
 */
class DeviceStatusDetector {
    private static final String switchAin = CredentialParser.getAin();
    private static final HomeAutomation homeAutomation = HomeAutomation.connect("http://192.168.178.1", CredentialParser.getUsername(), CredentialParser.getPassword());
    private static boolean deviceRunning = false;
    private static final int activeThreshold = 3; // watt

    public static void main(String[] args) {
        Thread runningDetector = new Thread(new DeviceRunningDetector(), "runningDetector");
        runningDetector.start();
        Thread doneDetector = new Thread(new DeviceDoneDetector(), "doneDetector");
        doneDetector.start();
    }

    /**
     * The DeviceRunningDetector pulls the state of the device attached to the outlet periodically.
     * If the power drawn by the device is above a certain threshold it will switch deviceRunning to true.
     */
    @SuppressWarnings("InfiniteLoopStatement")
    static class DeviceRunningDetector implements Runnable {
        @Override
        public void run() {
            while (true) {
                int counter = 0;
                if (deviceRunning) {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    while (!deviceRunning) {
                        if (homeAutomation.getSwitchState(switchAin)) {
                            Float powerDraw = homeAutomation.getSwitchPowerWatt(switchAin);
                            if (powerDraw >= activeThreshold) {
                                counter++;
                                System.out.println("Current count: " + counter + ". Current load: " + powerDraw + " W");
                            } else {
                                counter = 0;
                                System.out.println("Counter reset to 0 because load too low. " + powerDraw + " W");
                            }
                        } else {
                            System.err.println("The switch is off. Resetting counter to 0.");
                            counter = 0;
                        }
                        if (counter >= 5) {
                            deviceRunning = true;
                            System.out.println("Detected device is running!");
                            break;
                        }
                        try {
                            System.out.println(Thread.currentThread().getName() + " is waiting for 60s!");
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * The DeviceDoneDetector pulls the state of the device attached to the outlet periodically.
     * If the power drawn by the device is below a certain threshold it will switch deviceRunning to false.
     * In addition, it will send an E-Mail to a predefined address.
     */
    @SuppressWarnings("InfiniteLoopStatement")
    static class DeviceDoneDetector implements Runnable {
        @Override
        public void run() {
            while (true) {
                int counter = 0;
                if (!deviceRunning) {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    while (deviceRunning) {
                        if (homeAutomation.getSwitchState(switchAin)) {
                            Float powerDraw = homeAutomation.getSwitchPowerWatt(switchAin);
                            if (powerDraw < activeThreshold) {
                                counter++;
                                System.out.println("Current count: " + counter + ". Current load: " + powerDraw + " W");
                            } else {
                                counter = 0;
                                System.out.println("Counter reset to 0 because load too high. " + powerDraw + " W");
                            }
                        } else {
                            System.err.println("The switch is off. Resetting counter to 0.");
                            counter = 0;
                        }
                        if (counter >= 5) {
                            deviceRunning = false;
                            System.out.println("Detected device is now off!");
                            Mail.sendDeviceIsOffMail();
                            break;
                        }
                        try {
                            System.out.println(Thread.currentThread().getName() + " is waiting for 60s!");
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
