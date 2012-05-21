package texteditor;


import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import texteditor.dal.PDHistory;
import texteditor.ops.*;

import pdstore.GUID;
import pdstore.dal.PDWorkingCopy;

public class OperationPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TextEditor editor;
	public PDWorkingCopy store;
	public PDHistory history;
	public JComboBox selectBox;

	public OperationPanel(TextEditor editor, PDWorkingCopy workingCopy, GUID id) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.editor = editor;
		store = workingCopy;

		this.add(new CutWord(editor).getUI());
		this.add(new CopyWord(editor).getUI());
		this.add(new PasteWord(editor).getUI());
		this.add(new DeleteWord(editor).getUI());

	}

	@SuppressWarnings("deprecation")
	public void generateMsg(String msg) {
		String message = msg;
		JOptionPane pane = new JOptionPane(message);
		JDialog dialog = pane.createDialog(new JFrame(), "Warning");
		dialog.show();
	}
}