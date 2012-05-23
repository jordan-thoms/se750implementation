package nz.ac.auckland.se750project;

import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JTable;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDInstance;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;
import texteditor.dal.PDInsert;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import javax.swing.JTextField;
import java.awt.Panel;
import java.awt.FlowLayout;
import javax.swing.JButton;

import nz.ac.auckland.se750project.dal.PDDataRecord;
import nz.ac.auckland.se750project.dal.PDDataSet;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MainWindow {
	static PDStore store;
	static PDWorkingCopy wc;

	private JFrame frame;
	private JTable table;
	private Panel panel;
	private JLabel lblNewLabel;
	private JTextField textField;
	private JButton btnNewButton;

	private static GUID dataSetGUID;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		store = new PDStore("Tabula");
//		store = PDStore.connectToServer("localhost");
		wc = new PDSimpleWorkingCopy(store);
		Collection<PDInstance> dataSets = wc.getAllInstancesOfType(PDDataSet.typeId);
		if (dataSets.size() == 0) {
			PDDataSet dataSet = new PDDataSet(wc);
			wc.commit();
			System.out.println("Added: " + dataSet.getId());
			dataSets.add(dataSet);
			PDDataRecord record = new PDDataRecord(wc);
			dataSet.addRecord(record);
			record.setRow1(5l);
			record.setRow2(10l);
			record.setRow3(15l);
			wc.commit();
		}
		
		dataSetGUID = ((PDInstance) dataSets.toArray()[0]).getId();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
					
					MainWindow window2 = new MainWindow();
					window2.frame.setVisible(true);
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
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		frame.getContentPane().add(scrollPane);
		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(new DataSetTableModel(dataSetGUID, store,  wc));
		
		panel = new Panel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		GUID transaction = store.begin();
		GUID type = PDDataRecord.typeId;
		Collection<GUID> accessibleRoles = store.getAccessibleRoles(transaction, type);
		final Map<GUID, JTextField> fields = new HashMap<GUID, JTextField>();
		for (GUID guid : accessibleRoles) {
			String str = store.getName(transaction, guid);
			if (!str.equals("contained_in")) {
				lblNewLabel = new JLabel(str);
				panel.add(lblNewLabel);
				
				textField = new JTextField();
				panel.add(textField);
				textField.setColumns(10);
				fields.put(guid, textField);
			}
		}
		store.commit(transaction);
		
		btnNewButton = new JButton("Save");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GUID transaction = store.begin();
				PDDataSet set  = new PDDataSet(wc, dataSetGUID);
				PDDataRecord record = new PDDataRecord(wc);
				set.addRecord(record);
				GUID recordId = record.getId();
				for (Entry<GUID, JTextField> entry : fields.entrySet()) {
					wc.setLink(recordId, entry.getKey(), Long.valueOf(entry.getValue().getText()));
					entry.getValue().setText("");
				}
				wc.commit();
				store.commit(transaction);
				
			}
		});
		panel.add(btnNewButton);
	}

}
