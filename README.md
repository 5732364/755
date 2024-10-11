# Traffic Light System

### Overview
Implementing a fail to recover strategy through backup system code. Expanded on the content of the last assignment. We add TrafficLightBackup.java. This part will record when you crush by txt file. And help TrafficLightControlSys.java continue execution from where it failed.

### Team members
- Liu Xu
- Weijie Chen


### Instructions
1. Run HeartBeat.java - Connecting and turning on the monitoring system. Make sure the System is running by continuously receiving heartbeat. If the heartbeat messages are not received it can also take action when system fails.
2. Run TrafficLightControlSys - When heartbeat is running, the switch traffic light will have a heartbeat every time it is switched and only stops when fault occurs. After you run the TrafficLightSecondary then run it again, it will start counting from the last recorded.
3. Run TrafficLightSecondary-  The last failure will be recorded. And the system will continue if the original system fails to show passive redundancy and recovery.

### Frameworks Used
- Java libraries
- Java.io.*
- Java.net.*
- Java.util.random