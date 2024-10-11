package Assignment2;
import java.io.*;
import java.net.*;

public class Heartbeat {
    public static final int PORT = 6000;
    public static final int TIMEOUT = 5000; // Heartbeat timeout 5 sec
    private static volatile long lastHeartbeatTime = System.currentTimeMillis(); // Track last heartbeat time

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            Socket clientSocket = serverSocket.accept();
            System.out.println("Traffic Light controller is connected");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // continuously read heartbeat messages from the controller.
            Thread readerThread = new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        if (message.startsWith("HEARTBEAT")) {
                            System.out.println("Received " + message);
                            // update the last heartbeat
                            lastHeartbeatTime = System.currentTimeMillis();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Exception: " + e.getMessage());
                }
            });

            readerThread.start();

            // method to check for missed heartbeats
            Thread monitorThread = new Thread(() -> {
                while (true) {
                    if (System.currentTimeMillis() - lastHeartbeatTime > TIMEOUT) {
                        System.out.println("Traffic Light controller stopped responding");
                        // reset the last heartbeat time
                        lastHeartbeatTime = System.currentTimeMillis();
                    }
                    try {
                        Thread.sleep(TIMEOUT / 2); // timeout before checking again
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });

            monitorThread.start();



            // here keep the program running
            readerThread.join();
            monitorThread.join();

        } catch (IOException | InterruptedException e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}
