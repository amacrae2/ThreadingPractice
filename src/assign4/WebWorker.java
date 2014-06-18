package assign4;

import java.io.*;
import java.net.*;
import java.text.RuleBasedCollator;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;


public class WebWorker extends Thread {
	
	private String urlString;
	private int row;
	private JTable table;
	public static boolean isInterrupted = false;

	public WebWorker(String urlString, int row, JTable table) {
		this.urlString = urlString;
		this.row = row;
		this.table = table;
	}
	
	public void run() {
		WebFrame.threadCount ++;
		WebFrame.updateLabels();
		download();
		WebFrame.threadCount --;
		WebFrame.threadsCompleted ++;
		WebFrame.updateLabels();
		WebFrame.available.release();
		WebFrame.numCompleted++;
	}

	private void download() {
		long startTime = System.currentTimeMillis();
		InputStream input = null;
		StringBuilder contents = null;
		try {
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
		
			// Set connect() to throw an IOException
			// if connection does not succeed in this many msecs.
			connection.setConnectTimeout(5000);
			
			connection.connect();
			input = connection.getInputStream();

			BufferedReader reader  = new BufferedReader(new InputStreamReader(input));
		
			char[] array = new char[1000];
			int len;
			contents = new StringBuilder(1000);
			while ((len = reader.read(array, 0, array.length)) > 0) {
				contents.append(array, 0, len);
				Thread.sleep(100);
				if (WebFrame.interrupt) {
					interrupt();
				}
				if (isInterrupted()) {
					reader.close();
					throw new InterruptedException();
				}
			}
			
			// Successful download if we get here
			long elapsedTime = System.currentTimeMillis() - startTime;
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
			String formattedDate = sdf.format(date);
			String s = contents.toString();
			int bytes = s.length();
			String summary = formattedDate+" " + String.valueOf(elapsedTime)+"ms " + String.valueOf(bytes)+" bytes";
			table.getModel().setValueAt(summary, row, 1);
			
		}
		// Otherwise control jumps to a catch...
		catch(MalformedURLException ignored) {
			table.getModel().setValueAt("err", row, 1);
		}
		catch(InterruptedException exception) {
			table.getModel().setValueAt("interrupted", row, 1);
			isInterrupted = true;
			// deal with interruption
		}
		catch(IOException ignored) {
			table.getModel().setValueAt("err", row, 1);
		}
		// "finally" clause, to close the input stream
		// in any case
		finally {
			try{
				if (input != null) input.close();
			}
			catch(IOException ignored) {}
		}
	}	

	
}
