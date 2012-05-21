package fluid.evaluation;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fluid.dummy.Dummy;


public class TaskOne {

	static long st = 0;
	static long et = 0;
	static JButton start;
	static JButton stop;
	static Dummy runner;
	static JTextField timef;
	static double pTime = 0;
	static double sTime = 0;
	static ButtonGroup group;


	public static void stopAll(){
		stop();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame("Evaluation Task One:");
		runner = new Dummy(true);
		start = new JButton("Start");
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				start();	
			}
		});
		stop = new JButton("Stop");
		stop.setEnabled(false);
		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stop();

			}
		});

		group = new ButtonGroup();
		JRadioButton spreadButton = new JRadioButton("Spreadsheet");
		spreadButton.setActionCommand("SpreadSheet");
		spreadButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				start.setEnabled(true);
				stop.setEnabled(false);
				timef.setText(""+ (sTime/1000) + " sec");
			}
		});
		JRadioButton pdxButton = new JRadioButton("PDXplorer");
		pdxButton.setActionCommand("PDXplorer");
		pdxButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				start.setEnabled(true);
				stop.setEnabled(false);
				timef.setText(""+ (pTime/1000) + " sec");
			}
		});
		spreadButton.setSelected(true);
		group.add(spreadButton);
		group.add(pdxButton);
		
		JPanel panel1 = new JPanel();
		JLabel id = new JLabel("ID:");
		JTextField number = new JTextField();
		number.setPreferredSize(new Dimension(50, 25));
		number.setEditable(false);
		JLabel time = new JLabel("Time:");
		timef = new JTextField();
		timef.setPreferredSize(new Dimension(100, 25));
		timef.setEditable(false);
		timef.setText("0.0 sec");
		panel1.add(id);
		panel1.add(number);
		panel1.add(time);
		panel1.add(timef);
		panel1.setPreferredSize(new Dimension(250,40));

		JPanel option = new JPanel();
		option.add(spreadButton);
		option.add(pdxButton);
		
		JPanel panel2 = new JPanel();
		panel2.add(start);
		panel2.add(stop);
		panel2.setPreferredSize(new Dimension(250,40));

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.add(panel1);
		content.add(option);
		content.add(panel2);
		frame.getContentPane().add(content);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

	private static void start(){
		ButtonModel model = group.getSelection();
		start.setEnabled(false);
		stop.setEnabled(true);
		if (model.getActionCommand().equals("PDXplorer")){
			st = System.currentTimeMillis();
			Thread t = new Thread(new MessageLoop());
			t.start();
		}else{
			st = System.currentTimeMillis();
		}
		
	}

	private static void stop(){
		et = System.currentTimeMillis();
		System.out.println((et-st)+ " MilliSec");
		double t = et-st;
		ButtonModel model = group.getSelection();
		if (model.getActionCommand().equals("PDXplorer")){
			pTime = t;
		}else{
			sTime = t;
		}
		timef.setText(""+ (t/1000) + " sec");
		stop.setEnabled(false);
	}

	private static class MessageLoop implements Runnable{

		@Override
		public void run() {
			runner.createDummy("CreationTest");
		}

	}


}
