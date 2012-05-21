package diagrameditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pdstore.GUID;
import pdstore.dal.PDInstance;
import diagrameditor.dal.PDHistory;

/**
 * Class that is used to open or save a diagram when selected
 * from the File menu.
 */
public class HistoryManager implements ListCellRenderer, ActionListener {
	private static final long serialVersionUID = 4652258815806848495L;

	public enum DialogType {
		OPEN, SAVE
	}
	
	private JFrame frame;
	private DiagramEditor editor;
	private JList list;
	private JScrollPane listScroller;
	private JTextField filenameField;
	private JLabel filenameLabel, historyItem;
	private JButton button;
	private JPanel panel;
	private DefaultListModel listModel;
	
	public HistoryManager(DiagramEditor diagramEditor, DialogType type) {
		this.editor = diagramEditor;

		// Create the frame.
		if (type.equals(DialogType.OPEN)) {
			frame = new JFrame("Open");
		} else if (type.equals(DialogType.SAVE)) {
			frame = new JFrame("Save");
		}
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setSize(400, 250);
		frame.setMinimumSize(new Dimension(400, 250));
		frame.setLayout(new BorderLayout());
		
		// Get all instances of PDHistory.
		listModel = new DefaultListModel();
		for (PDInstance h: editor.workingCopy.getAllInstancesOfType(PDHistory.typeId)) {
			PDHistory history = (PDHistory) h;
			
			// Only display histories that have a name (has been manually saved by the user).
			// TODO Fix when name is History
			if (history.getName() != null && !history.getName().equals("History")) {
				listModel.addElement((PDHistory) h);
			}
		}
	
		// Create the JList.
		list = new JList(listModel);
		historyItem = new JLabel();
		historyItem.setOpaque(true);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setCellRenderer(this);
		listScroller = new JScrollPane(list);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Update the textfield to display the name of the selected PDHistory.
				if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) {
					PDHistory history = (PDHistory) list.getSelectedValue();
					filenameField.setText(history.getName().toString());
				}
			}
		});
		
		// Create the JPanel to be displayed at the bottom of the frame.
		panel = new JPanel();
		
		// Create the File Name label.
		filenameLabel = new JLabel("File name:");
		panel.add(filenameLabel);
		
		// Create the File Name textfield.
		filenameField = new JTextField(20);
		panel.add(filenameField);
		
		// Create the open or save button.
		if (type.equals(DialogType.OPEN)) {
			button = new JButton("Open");
			button.setActionCommand(DialogType.OPEN.toString());
		} else if (type.equals(DialogType.SAVE)) {
			button = new JButton("Save");
			button.setActionCommand(DialogType.SAVE.toString());
		}
		
		button.addActionListener(this);
		panel.add(button);
		
		// Add the components to the content pane.
		Container contentPane = frame.getContentPane();
		contentPane.add(listScroller, BorderLayout.CENTER);
		contentPane.add(panel, BorderLayout.SOUTH);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(DialogType.OPEN.toString())) {
			// TODO Use the filename typed in the textfield and not the selected item. 
			if (list.isSelectionEmpty()) {
				System.out.println("Need to select a file");
			} else {
				PDHistory history = (PDHistory) list.getSelectedValue();
				frame.setVisible(false);
				editor.loadHistory(history.getId());

				editor.statusBar.setStatus(history.getName() + " loaded successfully");
				history.setName(history.getName());
				editor.workingCopy.commit();
			}
		} else if (e.getActionCommand().equals(DialogType.SAVE.toString())) {
			if (filenameField.getText().equals("")) {
				System.out.println("Must type a filename");
			} else {
				PDHistory history = PDHistory.load(editor.workingCopy, editor.getHistoryID());
				history.setName(filenameField.getText());
				editor.workingCopy.commit();
				frame.setVisible(false);
				editor.statusBar.setStatus(history.getName() + " saved successfully");
			}
		}
	}
	
	public void refresh() {
		filenameField.setText("");
		listModel.clear();
		
		for (PDInstance h: editor.workingCopy.getAllInstancesOfType(PDHistory.typeId)) {
			PDHistory history = (PDHistory) h;
			
			// Only display histories that have a name (has been manually saved by the user).
			// TODO Fix when name is History
			if (history.getName() != null && !history.getName().equals("History")){
				listModel.addElement((PDHistory) h);
			}
		}
		
		frame.setVisible(true);
		frame.repaint();
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		// JList displays the History's name.
		PDHistory history = (PDHistory) value;
		historyItem.setText(history.getName());			
		
		// Set the colours of the items in the list.
		if (isSelected) {
			historyItem.setBackground(list.getSelectionBackground());
			historyItem.setForeground(list.getSelectionForeground());
		} else {
			historyItem.setBackground(list.getBackground());
			historyItem.setForeground(list.getForeground());
		}
		return historyItem;
	}
}
