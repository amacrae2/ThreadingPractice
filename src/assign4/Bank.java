package assign4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;


public class Bank {
	
	private static final int NUM_ACCOUNTS = 20;
	private static final double STARTING_BALANCE = 1000.00;
	private static final int QUEUE_SIZE = 20;

	public BlockingQueue<Transaction> queue = new ArrayBlockingQueue<Transaction>(QUEUE_SIZE); 
	public CountDownLatch latch;
	private static int numThreads; 
	private List<Account> accounts = new ArrayList<Account>();
	private List<Worker> workers = new ArrayList<Worker>();	
	
	// worker subclass
	public class Worker extends Thread {
		
	    private final CountDownLatch latch;
	    
	    /**
	     * constructor
	     * @param latch to countdown with
	     */
	    public Worker(CountDownLatch latch) {
	    	this.latch = latch;
	    }
		
	    /**
	     * workers take transactions from the queue and process them. When they receive the
	     * "null" transaction they decrement the lock and terminate.
	     */
		public void run() {
			try {
				Transaction currTransaction = queue.take();
				while (!currTransaction.isNullTransaction()) {
					currTransaction.transerFunds();
					currTransaction = queue.take();
				}
				latch.countDown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * creates accounts to start with and reads in the transactions from a file and processes
	 * them. Finally prints out the ending results for each account.
	 * @param args 0=file of accounts, 1=number of desired threads.
	 */
	public static void main(String[] args) {
		Bank bank = new Bank();
		bank.makeAccounts();
		numThreads = Integer.parseInt(args[1]);
		bank.runWorkers(args);
		bank.printOutAccounts();
	}

	/**
	 * creates the accounts and initializes their staring balance
	 */
	private void makeAccounts() {
		for (int i = 0; i < NUM_ACCOUNTS; i++) {
			Account account = new Account(i, STARTING_BALANCE);
			accounts.add(account);
		}
	}
	
	/**
	 * creates the workers and starts them, then waits until they are all done.
	 * @param args 0=file of accounts, 1=number of desired threads
	 */
	private void runWorkers(String[] args) {
		CountDownLatch latch = new CountDownLatch(numThreads);
		for (int i = 0; i < numThreads; i++) {
			workers.add(new Worker(latch));
		}
		for (int i = 0; i < numThreads; i++) {
			workers.get(i).start();
		}
		try {
			readInArgs(args);
            latch.await(); 
            //System.out.println("All workers are done");
       } catch(InterruptedException ie){
           ie.printStackTrace();
       }
	}

	/**
	 * reads in the file argument and processes it to add each line to the queue 
	 * as a transaction
	 * @param args 0=file of accounts, 1=number of desired threads
	 */
	private void readInArgs(String[] args) {
		File inFile = null;
		BufferedReader br = null;
		if (0 < args.length) {
			  inFile = new File(args[0]);
			} else {
				System.err.println("Invalid arguments count:" + args.length);
			}
		
	    try {
	    	String currLine;
	        br = new BufferedReader(new FileReader(inFile));
	        while ((currLine = br.readLine()) != null) {
	        	queueTransaction(currLine);
	        }
	        queueNullTransactions();
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    } 
	
	    finally {
	        try {
	            if (br != null)br.close();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	}

	/**
	 * Creates a transaction from the read in line and adds it the the queue
	 * @param currLine The current line read in from the file
	 */
	private void queueTransaction(String currLine) {
		String[] parsedLine;
		parsedLine = currLine.split("\\s+");
		Account fromAccount = accounts.get(Integer.parseInt(parsedLine[0]));
		Account toAccount = accounts.get(Integer.parseInt(parsedLine[1]));
		double amount = Double.parseDouble(parsedLine[2]);
		Transaction currTrans = new Transaction(fromAccount, toAccount, amount);
		try {
			queue.put(currTrans);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * after all of the real transactions have been queued a stop or "null"
	 * transaction is added to the queue for each worker.
	 */
	private void queueNullTransactions() {
		Account account = new Account(-1, 0);
		for (int i = 0; i < numThreads; i++) {
			try {
				queue.put(new Transaction(account, account, 0));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * prints out the final state of the accounts.
	 */
	private void printOutAccounts() {
		for (int i = 0; i < NUM_ACCOUNTS; i++) {
			System.out.println(accounts.get(i).toString());
		}
	}
	

}
