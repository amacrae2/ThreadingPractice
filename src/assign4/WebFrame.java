package assign4;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.concurrent.Semaphore;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.*;
import javax.swing.text.StyleContext.SmallAttributeSet;

public class WebFrame extends JFrame{
	
	private final static String TXT_FILE = "links.txt";
	private final static int MILLISEC_IN_SEC = 1000;
	
	private DefaultTableModel model;
	private JTable table;
	private JButton stFetch;
	private JButton concurFetch;
	private JButton stop;
	private JTextField textField;
	private static JLabel running;
	private static JLabel completed;
	private JLabel elapsed;
	private static JProgressBar progressBar;
	public static Semaphore available; 
	public static int threadCount;
	public static int threadsCompleted;
	public static boolean interrupt = false;
	public static int numCompleted = 0;
	
	public class Launcher extends Thread {
		
		private int numThreads;
		
		public Launcher(int numThreads) {
			this.numThreads = numThreads;
		}
		
		public void run() {
			long startTime = System.currentTimeMillis();
			threadCount ++;
			updateLabels();
			available = new Semaphore(numThreads);
			for (int row = 0; row < model.getRowCount(); row ++) {
				try {
					available.acquire();
					if (WebWorker.isInterrupted) {
						available.release();
						break;
					}
					WebWorker worker = new WebWorker((String)model.getValueAt(row, 0), row, table);
					worker.start();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			threadCount --;
			updateLabels();
			double timeElapsed = (System.currentTimeMillis() - startTime)/MILLISEC_IN_SEC;
			elapsed.setText("Elapsed:" + timeElapsed);
			changeToNonRunningState();	
		}
	}

	
	public WebFrame() {
		super("WebLoader");
		setLocationByPlatform(true);
	    setLayout(new BorderLayout(4,4));
	    
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		// adds table to panel
		model = new DefaultTableModel(new String[] { "url", "status"}, 0); 
		table = new JTable(model); 
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollpane = new JScrollPane(table); 
		scrollpane.setPreferredSize(new Dimension(600,300)); 
		// adds rows to the table 
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(TXT_FILE)));
	        String currLine;
			while ((currLine = br.readLine()) != null) {
				model.addRow(new Object[]{currLine, ""});
	        }
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		panel.add(scrollpane);
		
		// adds buttons to panel
		stFetch = new JButton("Single Thread Fetch");
		stFetch.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				changeToRunningState();
				resetLabels();
				Launcher launcher = new Launcher(1);
				launcher.start();				
			}
		});
		concurFetch = new JButton("Concurrent Fetch");
		concurFetch.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				changeToRunningState();
				resetLabels();
				int numThreads = Integer.parseInt(textField.getText());
				Launcher launcher = new Launcher(numThreads);
				launcher.start();					
			}

		});
		panel.add(stFetch);
		panel.add(concurFetch);
		
		// add textfield to panel
		textField = new JTextField();
		Dimension maximumSize = new Dimension(200, 5);
		textField.setMaximumSize(maximumSize);
		panel.add(textField);
		
		// add labels to panel
		running = new JLabel("Running:0");
		completed = new JLabel("Completed:0");
		elapsed = new JLabel("Elapsed:");
		panel.add(running);
		panel.add(completed);
		panel.add(elapsed);
		
		// add progress bar to panel
		progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(model.getRowCount()-1);
		panel.add(progressBar);
		
		// add stop button to panel
		stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				interrupt = true;
			}
		});
		stop.setEnabled(false);
		panel.add(stop);
		
		add(panel);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	private void changeToRunningState() {
		WebWorker.isInterrupted = false;
		SwingUtilities.invokeLater(new Runnable() {
	           public void run() {
	             stFetch.setEnabled(false);
	             concurFetch.setEnabled(false);
	             stop.setEnabled(true);
	} });
	}
	
	private void changeToNonRunningState() {
		SwingUtilities.invokeLater(new Runnable() {
	           public void run() {
	             stFetch.setEnabled(true);
	             concurFetch.setEnabled(true);
	             stop.setEnabled(false);
	} });		
	}
	
	public static void updateLabels() {
		running.setText("Running:"+threadCount);
		completed.setText("Completed:" + threadsCompleted);
        progressBar.setValue(numCompleted);
	}
	
	private void resetLabels() {
		numCompleted = 0;
		interrupt = false;
		threadCount = 0;
		threadsCompleted = 0;
		updateLabels();
        progressBar.setValue(numCompleted);
		elapsed.setText("Elapsed:");
		for (int row = 0; row < model.getRowCount(); row++) {
			model.setValueAt("", row, 1);
		}
	}
	
	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		WebFrame webFrame = new WebFrame();
	}
	
}
