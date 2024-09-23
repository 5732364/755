import java.io.*;
import java.net.*;

public class Heartbeat {
    public static final int PORT = 6000;
    public static final int TIMEOUT = 5000; // Heartbeat timeout 5 sec

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Waiting for Traffic Light Controller to connect");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Traffic Light Controller connected");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            long[] lastHeartbeatTime = {System.currentTimeMillis()};

            // Used to continuously read heartbeat messages from the controller.
            Thread readerThread = new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        if (message.startsWith("HEARTBEAT")) {
                            System.out.println("Received " + message);
                            lastHeartbeatTime[0] = System.currentTimeMillis();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Exception: " + e.getMessage());
                }
            });

            readerThread.start();



            //Waiting for the heartbeat reading thread to end
            readerThread.join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}

