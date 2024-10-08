package Assignment2;// Heartbeat.java
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicLong;

public class Heartbeat {
    public static final int PORT = 6000;
    public static final int TIMEOUT = 5000; // Heartbeat timeout 5 sec

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Waiting for Traffic Light Controller to connect...");
            Socket clientSocket = serverSocket.accept(); // Wait for the traffic light system to connect
            System.out.println("Traffic Light Controller connected");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            AtomicLong lastHeartbeatTime = new AtomicLong(System.currentTimeMillis());

            // continuously read heartbeat from the controllers
            Thread readerThread = new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        if (message.startsWith("HEARTBEAT")) {
                            System.out.println("Received " + message); // Log the heartbeat message
                            lastHeartbeatTime.set(System.currentTimeMillis()); //update last heartbeat time
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Exception: " + e.getMessage());
                }
            });

            readerThread.start();

            // Monitor thread to check for missed heartbeats
            Thread monitorThread = new Thread(() -> {
                while (true) {
                    if (System.currentTimeMillis() - lastHeartbeatTime.get() > TIMEOUT) {
                        System.out.println("Traffic Light Controller has stopped responding (missed heartbeat).");
                        // Reset the last heartbeat time to prevent repeated notifications
                        lastHeartbeatTime.set(System.currentTimeMillis());
                    }

                    try {
                        Thread.sleep(TIMEOUT / 2); // Sleep for half the timeout before checking again
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });

            monitorThread.start();

            // Setting for Keep the program running after crush.
            readerThread.join();
            monitorThread.join();

        } catch (IOException | InterruptedException e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}