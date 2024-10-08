package Assignment2;
import java.io.*;

public class TrafficLightSecond {
    private static final String CHECKPOINT_FILE = "checkpoint.txt"; // save the current light status and heartbeat count

    public static void main(String[] args) {
        System.out.println("Second system start."); // Indicates that the backup system started.

        // setting for load state from the checkpoint file if it exists
        try {
            File checkPointFile = new File(CHECKPOINT_FILE);
            if (checkPointFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(checkPointFile));
                String lastLightState = reader.readLine(); // Read last light state
                String lastHeartbeat = reader.readLine(); // Read last heartbeat count
                reader.close();

                System.out.println("Resuming from checkpoint:");
                System.out.println("Last light state: " + lastLightState);
                System.out.println("Last heartbeat: " + lastHeartbeat);

                // Here you would resume the traffic light operation as if nothing happened
                // Example output for testing, in reality, the light switching logic would be here
                System.out.println("Resuming traffic light operations from saved state.....");

                // Traffic light switching logic can be inserted here to continue normal operations
            } else {
                System.out.println("No checkpoint found, starting fresh.");
            }
        } catch (IOException e) {
            System.err.println("Exception while loading: " + e.getMessage());
        }
    }
}
