# **Development Log - Project 2**

# 4/6/2025 1:00 PM
- Created this development log to track changes and document progress.
- Initial setup for the project, deciding on the overall structure and goals.
- Trying to understand the project requirements and expected functionalities.

# 4/6/2025 3:20PM
- Made a skeleton of the file - needs teller and customer classes
- Will need to make them run in main

# 4/8/2025 2:30PM
- Plans - work on teller/customer classes
- Research sephamores
- Look at project guidelines to determine behavior of each class

# 4/8/2025 6:15PM
- Successfully implemented the run() method for the Customer class.
- After a random short delay (0–100 ms), the customer:
- Waits for the bank to open using bankOpen.acquire(); bankOpen.release();
- Acquires the door semaphore to simulate limited entry (max 2 customers at a time)
- Enters the bank and logs the entry
- Enters a critical section (synchronized(lock)) to enqueue itself in customerQueue and notifies any waiting tellers via lock.notifyAll()
- Waits for a teller to be assigned using tellerReady.acquire()
- Logs the teller interaction (selecting teller, introducing themselves, declaring transaction type)
- Signals the teller via assignedTeller.customerReady.release() to proceed
- Waits for the teller to complete the transaction with assignedTeller.transactionDone.acquire()
- Logs completion and releases the teller using assignedTeller.customerLeft.release()
- Finally, exits the bank and releases the door semaphore

# 4/9/2025 12:00PM
- Plan to try to finish the project 
- Implement Teller class and main 
- Implement the logic for each Teller thread to:
- Wait for a customer to appear in the queue
- Assign itself to the customer and signal it via tellerReady
- If the transaction is a Withdrawal, request manager access (via semaphore), simulate manager delay (sleep), and release access
- Acquire access to the safe, simulate transaction time (sleep), and then release it
- Signal the customer that the transaction is complete
- Wait for the customer to leave before handling the next

# 4/9/2025 6:00PM
- Thread sync was the main pain point
- Making sure tellers, customers, manager, and safe all interacted properly without stepping on each other.
- Customer–teller handshake was tricky
- Needed custom semaphores for each to avoid skipped or double-served customers.
- Manager and safe access limits
- Had to make sure only 1 teller saw the manager and only 2 were in the safe — easy to mess up acquire()/release() pairs.
- Forgetting to release() or using notify() instead of notifyAll() caused hangups where nothing moved.
- Tellers didn’t know when to stop
- After all customers were served, tellers just sat there. Thinking about adding a shutdown signal or using a poison pill.
- Console output was messy
- Multiple threads logging at once made it hard to debug. Might add timestamps or color to clean it up later.
- Testing with full load was rough
- Started small (few customers) to test the flow, then scaled up to 50 once everything seemed stable.

- Full Thread Coordination Added in main()
- Tellers are launched first
- bankOpen.release() is called 3 times to simulate the bank opening once all tellers are ready
- Customers are created and launched
- Main thread waits for all customers using join()
- Simulation ends with: Bank is closed.
- Customer-Teller Synchronization Working
- Customers randomly decide between Deposit or Withdraw
- Enter bank through door semaphore (2 max at a time)
- Queue up and wait for a teller
- Go through interaction and exit the bank
- Tellers process customers one at a time
- Use semaphores to avoid race conditions and ensure proper sequencing of each interaction step
- Used Semaphore to coordinate thread communication, simulate limited access to the manager and the safe
- Used synchronized(lock) to safely manipulate the shared customerQueue
- Used wait() / notifyAll() correctly inside synchronized blocks (fixed previous issue with missing queueLock)
- Each customer has its own synchronization points with its assigned teller using private semaphores
- ***Tellers run an infinite loop and don’t stop after all customers finish. Add a shutdown mechanism

