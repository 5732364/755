import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TrafficFlowSim implements Runnable {
    public final String direction;


    // initialize traffic direction
    public TrafficFlowSim(String direction) {
        this.direction = direction;
    }

    @Override
    public void run() {
        synchronized (System.out) {
            System.out.println(direction + " traffic passing!");
        }
        // Starts the simulation
        try {
            performHeavyComputation();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            synchronized (System.out) {
                System.out.println(direction + " traffic is interrupted");
            }
        }
        synchronized (System.out) {
            System.out.println(direction + " traffic passed");
        }
    }



    // perform heavy computation
    private void performHeavyComputation() throws InterruptedException {
        long sum = 0;
        for (int i = 0; i < 1_000_000; i++) {
            sum += i * (i % 2 == 0 ? 1 : -1);
        }
        Thread.sleep(2000);
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

         int totalCount = 0;
        int totalCycle = 5;
        // Loop to simulate the traffic light for number of cycles
        while (totalCount < totalCycle) {
            for (String direction : new String[]{"From East to West", "From North to South"}) {
                System.out.println("Green light to: " + direction + " direction");
                // traffic flow task of 10 threads
                for (int i = 0; i < 10; i++) {
                    executor.submit(new TrafficFlowSim(direction));
                }

                try {
                    Thread.sleep(5000); // delay for light duration
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("System interrupted");
                    break;
                }
            }
             totalCount++;
        }
        // shutdown service
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
        catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
