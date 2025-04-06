import java.util.concurrent.*;
import java.util.*;

public class bank {
    static final int NUM_TELLERS = 3;
    static final int NUM_CUSTOMERS = 50;

    static Semaphore bankOpen = new Semaphore(0);
    static Semaphore door = new Semaphore(2);
    static Semaphore safe = new Semaphore(2);
    static Semaphore manager = new Semaphore(1);    

    static Queue<Customer> customerQueue = new LinkedList<>();
    static Teller[] tellers = new Teller[NUM_TELLERS];

    static Object lock = new Object(); // For queue operations

    public static void main(String[] args) throws InterruptedException {
        
    }
    
    static class Teller extends Thread {
        int id;
        Semaphore customerReady = new Semaphore(0);
        Semaphore transactionDone = new Semaphore(0);
        Semaphore customerLeft = new Semaphore(0);
        Customer currentCustomer;

        Teller(int id) {
            this.id = id;
        }

        public void run() {
            
        }
    }

    static class Customer extends Thread {
        int id;
        String transaction;
        Teller assignedTeller;
        Semaphore tellerReady = new Semaphore(0);

        Customer(int id) {
            this.id = id;
        }

        public void run() {
            
        }
    }
    
}