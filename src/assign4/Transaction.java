package assign4;

public class Transaction {
	private final Account fromAccount;
	private final Account toAccount;
	private final double amount;
	
	/**
	 * constructor
	 * @param fromAccount the account to withdraw money from
	 * @param toAccount the account to add the money too
	 * @param amount the amount of money
	 */
	public Transaction(Account fromAccount, Account toAccount, double amount) {
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.amount = amount;
	}
	
	/**
	 * withdraws the funds from the fromAccount and adds it to the ToAccount.
	 */
	public void transerFunds() {
		fromAccount.performTransaction(true, amount);
		toAccount.performTransaction(false, amount);
	}
	
	/**
	 * returns true if the transaction is tagged as a stop or "null" transaction.
	 * @return if the transaction is "null" or not.
	 */
	public boolean isNullTransaction() {
		if (fromAccount.getIDNumber() == -1) {
			return true;
		} else {
			return false;
		}
	}
}
