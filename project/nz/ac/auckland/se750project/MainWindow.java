package nz.ac.auckland.se750project;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;

public class MainWindow {
	static PDStore store;
	static PDWorkingCopy wc;

	private JFrame frame;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		store = new PDStore("Tabula");
		wc = new PDSimpleWorkingCopy(store);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(new DataSetTableModel(new GUID("20479fc0a3f711e1a319742f68b11197"), store,  wc));
	}

}
