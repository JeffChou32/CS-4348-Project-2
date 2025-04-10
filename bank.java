import java.util.concurrent.*;
import java.util.*;

public class bank {  
    static Semaphore bankOpen = new Semaphore(0); //signals when bank opens
    static Semaphore door = new Semaphore(2); //2 customers in bank at a time
    static Semaphore safe = new Semaphore(2); //2 tellers in safe at a time
    static Semaphore manager = new Semaphore(1); //1 teller can talk to manager at a time

    static Queue<Customer> customerQueue = new LinkedList<>(); //customers waiting for teller
    static Teller[] tellers = new Teller[3]; //all teller threads stored

    static Object lock = new Object(); //synchronizing customerQueue

    public static void main(String[] args) throws InterruptedException {
        //start tellers
        for (int i = 0; i < 3; i++) {
            tellers[i] = new Teller(i);
            tellers[i].start(); //calls class's run method in a seperate thread
        }

        //tellers ready before bank opens
        for (int i = 0; i < 3; i++) {
            bankOpen.release(); 
        }

        Thread.sleep(100); 

        //start customers
        Customer[] customers = new Customer[50];
        for (int i = 0; i < 50; i++) {
            customers[i] = new Customer(i);
            customers[i].start(); //calls run in seperate thread
        }

        //waiting for customers
        for (int i = 0; i < 50; i++) {
            customers[i].join();
        }        
    }
    
    //represents a Teller as a separate thread
    static class Teller extends Thread {
        int id;
        Semaphore ready = new Semaphore(0); //semaphores for synchronizing interaction with a customer
        Semaphore done = new Semaphore(0);
        Semaphore customerLeft = new Semaphore(0);
        Customer currentCustomer;

        Teller(int id) {
            this.id = id;
        }

        public void run() {
            System.out.println("Teller " + id + " [Teller " + id + "]: is ready");
            bankOpen.release(); //signal that this teller is ready
            
            while (true) {
                Customer customer = waitForCustomer(); //wait for a customer to appear in the queue          
                serveCustomer(customer);
            }
        }        

        private Customer waitForCustomer() {  //blocks until a customer is available in the queue
            synchronized (lock) {
                while (customerQueue.isEmpty()) {
                    try {
                        lock.wait(); //wait until notified by a customer entering the queue
                    } catch (InterruptedException e) { 
                        return null;  //thread was interrupted
                    }
                }
                return customerQueue.poll(); //get the next customer
            }
        }
        
        private void serveCustomer(Customer customer) {  //handles a complete transaction with a customer
            currentCustomer = customer;
            System.out.println("Teller " + id + " [Teller " + id + "]: serving Customer " + customer.id);
            customer.assignedTeller = this;  //let customer know which teller they're with
            customer.tellerReady.release(); //teller is ready
        
            try {
                ready.acquire();  //wait for customer to tell transaction type
        
                if (customer.transaction.equals("Withdraw")) { //if withdrawal, interact with manager
                    handleManager();
                }
        
                handleSafe(); //transaction goes to safe
        
                done.release();
                customerLeft.acquire();
        
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        private void handleManager() throws InterruptedException { //interaction for withdrawals
            System.out.println("Teller " + id + " [Teller " + id + "]: requesting manager permission");
            manager.acquire();
            System.out.println("Teller " + id + " [Teller " + id + "]: talking to manager");
            Thread.sleep(new Random().nextInt(26) + 5);
            System.out.println("Teller " + id + " [Teller " + id + "]: done with manager");
            manager.release();
        }
        
        private void handleSafe() throws InterruptedException { //safe access for transactions
            System.out.println("Teller " + id + " [Teller " + id + "]: waiting for safe access");
            safe.acquire();  //only two tellers can access the safe at once
            System.out.println("Teller " + id + " [Teller " + id + "]: using the safe");
            Thread.sleep(new Random().nextInt(41) + 10); //simulated safe access delay (10-50ms)
            System.out.println("Teller " + id + " [Teller " + id + "]: done with the safe");
            safe.release();
        }
    }

    static class Customer extends Thread { //customer in seperate thread
        int id;
        String transaction; //deposit/withdraw
        Teller assignedTeller;  
        Semaphore tellerReady = new Semaphore(0);

        Customer(int id) {
            this.id = id;
        }

        public void run() {
            try {
                transaction = new Random().nextBoolean() ? "Deposit" : "Withdraw"; //decides deposit/withdraw randomly
                Thread.sleep(new Random().nextInt(101)); //delay before arriving at bank 0-100ms
                bankOpen.acquire(); //wait for bank to be open
                bankOpen.release(); 

                door.acquire();  //max 2 customers per bank
                System.out.println("Customer " + id + " [Customer " + id + "]: enters bank");

                synchronized (lock) { //add self to queue and notify tellers
                    customerQueue.add(this);
                    lock.notifyAll();  //wakes up tellers
                }

                tellerReady.acquire();  //wait for tellers to be ready
                System.out.println("Customer " + id + " [Teller " + assignedTeller.id + "]: selects teller");
                System.out.println("Customer " + id + " [Teller " + assignedTeller.id + "]: introduces themselves");
                System.out.println("Customer " + id + " [Teller " + assignedTeller.id + "]: requests a " + transaction);
                assignedTeller.ready.release(); //tell the teller the transaction is ready to proceed

                assignedTeller.done.acquire(); //wait for teller to complete transaction
                System.out.println("Customer " + id + " [Teller " + assignedTeller.id + "]: transaction done");
                System.out.println("Customer " + id + " [Teller " + assignedTeller.id + "]: leaves teller");
                assignedTeller.customerLeft.release();

                System.out.println("Customer " + id + " [Customer " + id + "]: exits the bank");
                door.release();

            } catch (InterruptedException e) {
                System.err.println("Customer " + id + " interrupted");
            }
        }        
    }    
}