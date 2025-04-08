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