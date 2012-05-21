package pdstore.ui.graphview;

import javax.swing.*;

import pdstore.GUID;
import pdstore.dal.PDWorkingCopy;
import pdstore.ui.graphview.dal.PDNode;

import java.awt.*;
import java.awt.event.*;

class NewNodeDialog extends JDialog {
	JOptionPane optionPane;
	JTextField textField;

	public NewNodeDialog(Point location) {
		super((Frame) null, true);
		setSize(280, 150);
		setLocation(location);

		setTitle("New Node");

		textField = new JTextField(40);
		Object[] items = { "New name for x :", textField };

		// Create an array specifying the number of dialog buttons
		// and their text.
		Object[] options = { "Cancel", "OK" };

		// Create the JOptionPane.
		optionPane = new JOptionPane(items, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.YES_NO_OPTION, null, options, options[0]);
				
		// Make this dialog display it.
		setContentPane(optionPane);

		// Ensure the text field always gets the first focus.
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
				textField.requestFocusInWindow();
			}
		});
	}
}