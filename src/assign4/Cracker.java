package assign4;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Cracker {
	// Array of chars used to produce strings
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();	
	
	/**
	 * determines how many arguments are given and then acts accordingly.
	 * If only one argument is given, finds the hashcode of the argument
	 * If more than one argument is given finds the password associated with
	 * the given hashcode.
	 * @param args arguments represented as strings
	 */
	public static void main(String[] args) {
		boolean crack = false;
		if (args.length > 1) {
			crack = true;
		}		
		if (crack) {
			String hashValue = args[0];
			int pwLenLim = Integer.parseInt(args[1]);
			int numThreads = Integer.parseInt(args[2]);
			runWorkers(hashValue, pwLenLim, numThreads);
		} else {
			String password = args[0];
			String hashValue = determineHashCode(password);
			System.out.println(hashValue);
		}
	}

	/**
	 * Initializes the workers and starts them off doing a brute force search
	 * to determine the password of the given hashValue.
	 * @param hashValue the hashcode of the password to be found
	 * @param pwLenLim The max length of the password
	 * @param numThreads the number of threads that should be run
	 */
	private static void runWorkers(String hashValue, int pwLenLim,int numThreads) {
		List<CrackerWorker> workers = new ArrayList<CrackerWorker>();
		CountDownLatch latch = new CountDownLatch(numThreads);
		for (int i = 0; i < numThreads; i++) {
			char[] charsToCheck = determineCharsToCheck(i,numThreads);
			workers.add(new CrackerWorker(latch,charsToCheck,hashValue,pwLenLim));
		}
		for (int i = 0; i < numThreads; i++) {
			workers.get(i).start();
		}
		try {
		    latch.await(); 
		    System.out.println("All workers are done");
      } catch(InterruptedException ie){
		   ie.printStackTrace();
      }
	}

	/**
	 * splits up the chars possible to appear in the password and 
	 * returns the chars for a particular thread
	 * @param i the thread number
	 * @param numThreads the total number of threads
	 * @return the chars that thread is responsible for
	 */
	private static char[] determineCharsToCheck(int i, int numThreads) {
		int start = CHARS.length*i/numThreads;
		int stop = (CHARS.length*(i+1)/numThreads);
		char[] result = Arrays.copyOfRange(CHARS, start, stop);
		return result;
	}

	/**
	 * finds the hashcode of a string
	 * @param s the string to be used to find its hashcode
	 * @return the hashcode as a string
	 */
	public static String determineHashCode(String s) {
		 MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		 byte[] bytes = s.getBytes();
		 md.update(bytes);
		 byte[] passwordDigest = md.digest();
		 String hashValue = hexToString(passwordDigest);
		 return hashValue;
	}

	/*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/
	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val<16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}
	
	/*
	 Given a string of hex byte values such as "24a26f", creates
	 a byte[] array of those values, one byte value -128..127
	 for each 2 chars.
	 (provided code)
	*/
	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length()/2];
		for (int i=0; i<hex.length(); i+=2) {
			result[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
		}
		return result;
	}
	


}
