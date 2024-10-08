package Assignment2;
import java.io.*;
import java.net.Socket;
import java.util.Random;

public class TrafficLightControlSys {
    private static final String MONITOR_HOST = "localhost";
    private static final int MONITOR_PORT = 6000; // port number
    private static final int LIGHT_INTERVAL = 2000; // setting light switch for 2 seconds
    private static final String CHECKPOINT_FILE = "checkpoint.txt"; // file for storing checkpoint state

    // enum is used for three traffic light states
    enum Light {
        RedLight, GreenLight, YellowLight
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket(MONITOR_HOST, MONITOR_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            Random random = new Random();
            Light currentLight = Light.RedLight; // initialize with red light
            int heartbeatCount = 0;
            boolean failureOccurred = false; // flag to simulate failure

            // Check if a checkpoint file exists and load state
            File checkpointFile = new File(CHECKPOINT_FILE);
            if (checkpointFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(checkpointFile));
                currentLight = Light.valueOf(reader.readLine()); // Load last saved light state
                heartbeatCount = Integer.parseInt(reader.readLine()); // Load last heartbeat count
                reader.close();
                System.out.println("Resumed from checkpoint with Light: " + currentLight + " and Heartbeat count: " + heartbeatCount);
            } else {
                System.out.println("Starting fresh......");
            }

            while (true) {
                // Simulate random failure with a 20% chance
                if (!failureOccurred && random.nextInt(100) < 20) {
                    failureOccurred = true;
                    System.out.println("A failure occurred in the Traffic Light System. Stopping heartbeats.");
                    // After failure, stop sending heartbeats but keep running
                }

                // Only send heartbeat if no failure occurred
                if (!failureOccurred) {
                    out.println("HEARTBEAT " + heartbeatCount);
                    System.out.println("Sent heartbeat " + heartbeatCount);
                    heartbeatCount++;
                }

                // Save to checkpoint file
                if (!failureOccurred) {
                    PrintWriter writer = new PrintWriter(new FileWriter(CHECKPOINT_FILE));
                    // Save current light state and current heartbeat count
                    writer.println(currentLight);
                    writer.println(heartbeatCount);
                    writer.close();
                }

                // Switch traffic lights
                if (!failureOccurred) {
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

                Thread.sleep(LIGHT_INTERVAL); // Wait for the next light
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}
