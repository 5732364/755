package Assignment2;
import java.io.*;
import java.net.Socket;
import java.util.Random;

public class TrafficLightControlSys {
    private static final String MONITOR_HOST = "localhost";
    private static final int MONITOR_PORT = 6000; // port num
    private static final int LIGHT_INTERVAL = 2000; // setting light switch for 2 second
    private static final String CHECKPOINT_FILE = "checkpoint.txt"; //storing checkpoint state

    // setting three traffic light
    enum Light {
        RedLight, GreenLight, YellowLight
    }

    public static void main(String[] args) {
        // initialize light to red, and heartbeat count
        Light currentLight = Light.RedLight;
        int heartbeatCount = 0;
        boolean failTimes = false;

        // reads from the old checkpoint file
        try {
            File cpFile = new File(CHECKPOINT_FILE);
            if (cpFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(cpFile));
                // reads current light and count of heartbeat
                currentLight = Light.valueOf(reader.readLine());
                heartbeatCount = Integer.parseInt(reader.readLine());

                reader.close();
                System.out.println("checkpoint Light " + currentLight + ", Heartbeat count " + heartbeatCount);
            } else {
                System.out.println("Starting.....");
            }
        } catch (IOException e) {
            System.err.println("Exception: " + e.getMessage());
        }

        // socket connection
        try (Socket socket = new Socket(MONITOR_HOST, MONITOR_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            Random random = new Random();

            while (true) {
                // setting random failure 20%
                if (!failTimes && random.nextInt(100) < 20) {
                    // if failure then stop, it was flagged false at start
                    failTimes = true;
                    System.out.println("A failure occurred and the process stopped.");
                }

                // continue sending without fails.
                if (!failTimes) {
                    out.println("HEARTBEAT " + heartbeatCount);
                    System.out.println("Sent heartbeat " + heartbeatCount);
                    heartbeatCount++;
                }
                if (!failTimes) {
                    // saving to checkpointing file.
                    PrintWriter writer = new PrintWriter(new FileWriter(CHECKPOINT_FILE));
                    writer.println(currentLight);
                    writer.println(heartbeatCount);
                    writer.close();
                }

                // switch for Switching lights
                if (!failTimes) {
                    switch (currentLight) {
                        case RedLight:
                            currentLight = Light.GreenLight;
                            System.out.println("Switching to GREEN light");
                            break;
                        case GreenLight:
                            currentLight = Light.YellowLight;
                            System.out.println("Switching to YELLOW light");
                            break;
                        case YellowLight:
                            currentLight = Light.RedLight;
                            System.out.println("Switching to RED light");
                            break;
                    }
                }


                Thread.sleep(LIGHT_INTERVAL);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}
