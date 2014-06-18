package assign4;

import java.awt.*; 

import javax.swing.*; 

import java.awt.event.*;

public class JCount extends JPanel {
	
	private static final int NUM_COLS = 20;
	private static final int NUM_JCOUNTS = 4;
	
	private JPanel panel;
	private JTextField textField;
	private JLabel label;
	private JButton start;
	private JButton stop;
	private WorkerThread worker;
	
	// inner worker class
	public class WorkerThread extends Thread {
				
		private static final int COUNT_STEP = 10000;
		private static final int SLEEP_TIME = 100;
		
		private int countToThis;
		private int currCount = 0;
		
		/**
		 * constructor
		 * @param countToThis Then number to be counted up to
		 */
		public WorkerThread(int countToThis) {
			this.countToThis = countToThis;
		}
		 /**
		  * counts up to a given number updating the corresponding JLabel every
		  * 1000 steps.
		  */
		public void run() {
			int count = 0;
			for (int i = 1; i <= countToThis; i++) {
				if (isInterrupted()) {
					currCount = 0;
					break;
				}
				count = i;
				if (count % COUNT_STEP == 0) {
					try {
						currCount = count;
						sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						currCount = 0;
						break;
					}
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							label.setText(Integer.toString(currCount));
				} });
				}
			}
		}
		
	}
	
	/**
	 * constructor
	 * adds a text field, a label, and a start and stop button to a panel.
	 */
	public JCount() {
		super();
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		textField = new JTextField(NUM_COLS);
    	label = new JLabel(" ");
    	start = new JButton("Start");
    	start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (worker != null) {
					worker.interrupt();
				}
				worker = new WorkerThread(Integer.parseInt(textField.getText()));
				worker.start();
			}
		});
    	stop = new JButton("Stop");
    	stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (worker != null) {
					worker.interrupt();
					worker = null;
				}
			}
		});
    	panel.add(textField);
    	panel.add(label);
    	panel.add(start);
    	panel.add(stop);
		panel.add(Box.createRigidArea(new Dimension(0,40)));
	}
	
	/**
	 * gets the panel that is created in the constructor.
	 * @return the JPanel containing elements of a JCount
	 */
	public JPanel getJPanel() {
		return panel;
	}
	
	/**
	 * creates the GUI by adding 4 JCounts to a JFrame.
	 */
    private static void createAndShowGUI() {
		JFrame frame = new JFrame();
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		for (int i = 0; i < NUM_JCOUNTS; i++) {
			JCount jcount = new JCount();
			frame.getContentPane().add(jcount.getJPanel());
		}
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
    	
    }
    
    /**
     * creates the GUI and displays it
     * @param args arguments to be taken in (not used)
     */
	public static void main(String[] args) { 
		SwingUtilities.invokeLater(new Runnable() {
	           public void run() {
	             createAndShowGUI();
	} });
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
	}
	
}
