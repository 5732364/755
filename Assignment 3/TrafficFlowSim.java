import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TrafficFlowSim implements Runnable {
    public final String direction;

    public TrafficFlowSim(String direction) {
        this.direction = direction;
    }

    @Override
    public void run() {
        System.out.println(direction + " traffic passing！！");
        // Below simulates heavier computation
        try {
            performHeavyComputation();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(direction + " traffic is interrupted");
        }
        System.out.println(direction + " traffic passed");
    }

    // Below is large computation for traffic task
    private void performHeavyComputation() throws InterruptedException {
        long sum = 0;
        // Iterates large range to keep CPU busy just adds delay to simulate heavy task.
        for (int i = 0; i < 1_000_000; i++) {
            sum += i * (i % 2 == 0 ? 1 : -1);
        }
        // Delay
        Thread.sleep(2000);
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        int totalCycleCount = 0;
        int totalCycles = 5;

        while (totalCycleCount < totalCycles) {
            for (String direction : new String[]{"From East to West", "From North to South"}) {
                System.out.println("Green light to: " + direction + " direction");

                for (int i = 0; i < 20; i++) {
                    executor.submit(new TrafficFlowSim(direction));
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("System interrupted");
                    break;
                }
            }
            totalCycleCount++;
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
