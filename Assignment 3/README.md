# Traffic Light System

### Overview
This program will simulates a traffic light system with thread pooling to help handle traffic flow tasks. There will be two directions from East-West and North-South, it simulates with multiple vehicles passing as tasks distributed across pool of threads. The system make sure traffic flow is managed efficiently using reusable threads to help reducing overhead.

### Team members
- Xu
- Chen

### What the Program Do
The program shows a traffic light system with a pool of threads for managing traffic tasks. Each traffic task represents a vehicle moving through a intersection. We use the thread pool to let the program reuse threads in a efficient way and handling multiple traffic tasks all together. With 20 traffic tasks per direction in each cycle the green light will change between directions every 5 seconds.

### Instructions
1. Run javac TrafficFlowSim.java - This will simulate traffic flow by thread pooling with 10 threads.

### Frameworks Used
- import java.util.concurrent.ExecutorService;
-  import java.util.concurrent.Executors;
-  import java.util.concurrent.TimeUnit;
