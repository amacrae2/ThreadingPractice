package assign4;

public class Account {

	private int idNumber;
	private double currBalance;
	private int numTransactions = 0;
	private Object accountLock = new Object();
	
	/**
	 * constructor
	 * @param idNumber the account number
	 * @param startBalance the starting balance of the account.
	 */
	public Account(int idNumber, double startBalance) {
		this.idNumber = idNumber;
		this.currBalance = startBalance;
	}
	
	/**
	 * returns the id number of the account
	 * @return the id number
	 */
	public int getIDNumber() {
		return idNumber;
	}
	
	/**
	 * returns the current balance of the account
	 * @return the current balance
	 */
	public double getBalance() {
		synchronized (accountLock) {
			return currBalance;
		}
	}
	
	/**
	 * returns the number of transactions the account has seen
	 * @return the number of transactions
	 */
	public int getNumTransactions() {
		synchronized (accountLock) {
			return numTransactions;
		}
	}
	
	/**
	 * adds or removes money from the account and increases the number of transactions by 1.
	 * @param withdraw true if the transaction is a withdraw
	 * @param amount the transaction money amount.
	 */
	public void performTransaction(boolean withdraw, double amount) {
		if (withdraw) {
			amount = -amount;
		}
		synchronized (accountLock) {
			currBalance += amount;
			numTransactions ++;
		}
	}
	
	/**
	 * converts the account to a string representation
	 */
	public String toString() {
		String result = "acct:" + idNumber + " bal:" + currBalance + " trans:" + numTransactions;
		return result;
	}
	
	
}
