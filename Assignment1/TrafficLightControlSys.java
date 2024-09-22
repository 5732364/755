// TrafficLightController.java
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class TrafficLightControlSys {
    private static final String MONITOR_HOST = "localhost";
    private static final int MONITOR_PORT = 6000; // port num
    private static final int LIGHT_INTERVAL = 2000; // setting light switch for 2 second
// setting three traffic light
    enum Light {
        RedLight, GreenLight, YellowLight
    }

    public static void main(String[] args) {
        // socket connection
        try (Socket socket = new Socket(MONITOR_HOST, MONITOR_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            Random random = new Random();
            Light currentLight = Light.RedLight; // setting init light is red
            int heartbeatCount = 0;

            while (true) {
                // setting random failure 20%
                if (random.nextInt(100) < 20) {
                    // if failure then stop
                    System.out.println("A failure occurred and the process stopped.");
                    System.exit(1);
                }

                // Send heartbeat message
                out.println("HEARTBEAT " + heartbeatCount);
                System.out.println("Sent heartbeat " + heartbeatCount);
                heartbeatCount++;

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
        }

            catch (IOException | InterruptedException e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}
