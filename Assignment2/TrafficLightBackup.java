package Assignment2;
import java.io.*;
import java.net.Socket;

public class TrafficLightBackup {
    private static final String MONITOR_HOST = "localhost";
    private static final int MONITOR_PORT = 6000; // port num
    private static final int LIGHT_INTERVAL = 2000; //setting light switch for 2 second
    private static final String CHECKPOINT_FILE = "checkpoint.txt"; //storing checkpoint state

    // setting three traffic light
    enum Light {
        RedLight, GreenLight, YellowLight
    }

    public static void main(String[] args) {
        System.out.println("Backup traffic light system start.");
        // initialize light to red, and heartbeat count
        Light currentLight = Light.RedLight;
        int heartbeatCount = 0;

        // reads from the checkpoint file
        try {
            File checkpointFile = new File(CHECKPOINT_FILE);
            if (checkpointFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(checkpointFile));

                // gets the saved light
                String savedLight = reader.readLine();
                try {
                    currentLight = Light.valueOf(savedLight);
                } catch (IllegalArgumentException e) {
                    System.err.println("No light color found restarting.");
                    currentLight = Light.RedLight;
                }

                // gets the heartbeat count from the checkpoint file
                String savedHeartbeatCount = reader.readLine();
                if (savedHeartbeatCount != null && !savedHeartbeatCount.isEmpty()) {
                    heartbeatCount = Integer.parseInt(savedHeartbeatCount);
                } else {
                    System.err.println("No heartbeat count found restarting");
                    heartbeatCount = 0;
                }

                reader.close();
                System.out.println("checkpoint Light is " + currentLight + ", Heartbeat count is " + heartbeatCount);

            } else {

                System.out.println("No checkpoint found");

            }
        } catch (IOException e) {
            System.err.println("Exception with the checkpoint file " + e.getMessage());
        }

        // socket connection
        try (Socket socket = new Socket(MONITOR_HOST, MONITOR_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            while (true) {
                // Send heartbeat and print the current light state
                out.println("HEARTBEAT " + heartbeatCount);
                System.out.println("Sent heartbeat " + heartbeatCount);
                heartbeatCount++;

                PrintWriter writer = new PrintWriter(new FileWriter(CHECKPOINT_FILE));
                // saving to checkpointing file.
                writer.println(currentLight);
                writer.println(heartbeatCount);
                writer.close();

                // switch for Switching lights
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

                Thread.sleep(LIGHT_INTERVAL);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}
