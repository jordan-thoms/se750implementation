/**
 * 
 */
package texteditor.ops;


import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import texteditor.TextEditor;
import texteditor.WordAndPos;
import texteditor.dal.PDDelete;
import texteditor.dal.PDOperation;
import texteditor.dal.PDWord;

public class CopyWord implements ActionListener {

	/**
	 * 
	 */
	
	private final TextEditor newEditor;
	private String status_msg;
	private JPanel copyButtonPanel;

	public CopyWord(TextEditor newEditor) {
		this.newEditor = newEditor;
		this.newEditor.status.setText("Copy Word");
	}

	public JComponent getUI() {
		JButton copy_button = new JButton("Copy");
		copy_button.setMargin(new Insets(0, 50, 0, 50));
		copy_button.addActionListener(this);
		copyButtonPanel = new JPanel(new BorderLayout());
		copyButtonPanel.add(copy_button,BorderLayout.NORTH);
		return copyButtonPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.newEditor.status.setText(status_msg);
		String selectedText = this.newEditor.textPanel.textArea.getSelectedText();
		if(selectedText != "")
		{
			int endCaretPos = this.newEditor.textPanel.textArea
					.getCaretPosition();
			int startCaretPos = endCaretPos - selectedText.length();

			ArrayList<PDWord> selectedWords = new ArrayList<PDWord>();

			for (WordAndPos wordPos : this.newEditor.wordList) {
				if ((wordPos.caretPos >= startCaretPos && wordPos.caretPos <= endCaretPos)
						|| (wordPos.caretPos <= startCaretPos && (wordPos.caretPos + wordPos.word
								.getText().length()) >= startCaretPos)) {
					selectedWords.add(wordPos.word);
				}
			}

			if (!selectedWords.isEmpty()) {
				TextEditor.actionStatus = "COPY";
				for (PDWord word : selectedWords) {
					this.newEditor.clipboard.clear();
					this.newEditor.clipboard = selectedWords;
				}

			} else {
				String message = "No word is selected";
				JOptionPane pane = new JOptionPane(message);
				JDialog dialog = pane.createDialog(new JFrame(), "Dialog");
				dialog.show();
			}
		}
	}

}