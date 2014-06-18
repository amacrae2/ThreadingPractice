package assign4;

import java.util.concurrent.CountDownLatch;

public class CrackerWorker extends Thread {
	
	private CountDownLatch latch;
	private char[] charsToCheck;
	private String hashValue;
	private int pwLenLim;
	
	/**
	 * constructor
	 * @param latch countdownlatch to keep track of when the thread has finished running
	 * @param charsToCheck characters that the thread is supposed to be responsible for
	 * @param hashValue the hashvalue the thread is looking to match
	 * @param pwLenLim the max length of the password
	 */
	public CrackerWorker(CountDownLatch latch, char[] charsToCheck, String hashValue, int pwLenLim) {
		this.latch = latch;
		this.charsToCheck = charsToCheck;
		this.hashValue = hashValue;
		this.pwLenLim = pwLenLim;
	}
	
	/**
	 * for each character the thread is responsible it generates character combinations
	 * starting with those characters and checks their hashcodes to see if they match
	 * the passwords hashcode.
	 */
	public void run() {
		String password = "";
		for (char ch: charsToCheck) {
			String test = "";
			test = test + ch;
			password = bruteForceSeachForPassword(test, pwLenLim);
			if (!password.equals("")) {
				System.out.println(password);
				latch.countDown();
				return;
			}
		}
		latch.countDown();
	}

	/**
	 * recursively uses a brute force approach to cycle through each possible combination of characters
	 * starting with the test string. If the test string is not a match each other letter is 
	 * concatenated to the end of the string and passed back into the function until the max length
	 * of the password is reached
	 * @param test the string to check to s
	 * @param len the amount of length left before the max length of the password is reached
	 * @return the password if it is found, otherwise the empty string
	 */
	private String bruteForceSeachForPassword(String test, int len) {
		if (Cracker.determineHashCode(test).equals(hashValue)) {
			return test;
		} else if (len > 1){
			for (int i = 0; i < Cracker.CHARS.length; i++) {
				String result = bruteForceSeachForPassword(test+Cracker.CHARS[i], len-1);
				if (!result.equals("")) {
					return result;
				}
			}
		}
		return "";
	}
	
}