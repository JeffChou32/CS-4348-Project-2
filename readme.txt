Bank Simulation – CS4348 Project 2
Simulates a bank environment using multithreading and semaphores.

Tellers and customers run as individual threads using the Thread class.

Shared resources like the safe and manager are protected using Java Semaphore.

Customers wait outside until the bank opens (after all tellers are ready).

Only 2 customers can be inside the bank at a time.

Tellers wait for customers, handle deposit/withdrawal transactions, and interact with the manager and safe (with delays)

Run the following command to compile all Java files: javac bank.java

To run: java bank