/**
 * 
 */
package texteditor.ops;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import texteditor.OperationPanel;
import texteditor.TextEditor;
import texteditor.WordAndPos;
import texteditor.dal.*;

public class CutWord implements ActionListener {

	private final TextEditor newEditor;
	private JPanel cutButtonPanel;

	/**
	 * @param operationPanel
	 */
	public CutWord(TextEditor newEditor) {
		this.newEditor = newEditor;
	}

	public JComponent getUI() {
		JButton cut_button = new JButton("Cut");
		cut_button.setMargin(new Insets(0, 50, 0, 50));
		cut_button.addActionListener(this);
		cutButtonPanel = new JPanel(new BorderLayout());
		cutButtonPanel.add(cut_button,BorderLayout.NORTH);
		return cutButtonPanel;
	}

	public void actionPerformed(ActionEvent e) {
		String selectedText = this.newEditor.textPanel.textArea.getSelectedText();
		int endCaretPos = this.newEditor.textPanel.textArea.getCaretPosition();
		int startCaretPos = endCaretPos - selectedText.length();
	
		ArrayList<PDWord> selectedWords = new ArrayList<PDWord>();
		
		for (WordAndPos wordPos : this.newEditor.wordList)
		{
			if((wordPos.caretPos >= startCaretPos && wordPos.caretPos <= endCaretPos) || 
					(wordPos.caretPos <= startCaretPos && (wordPos.caretPos + wordPos.word.getText().length()) >= startCaretPos) )
			{
				selectedWords.add(wordPos.word);
				this.newEditor.wordList.remove(wordPos);
			}
		}
		
		
		if (!selectedWords.isEmpty()) 
		{
			TextEditor.actionStatus = "CUT";
			for (PDWord word : selectedWords)
			{
				this.newEditor.clipboard.clear();
				this.newEditor.clipboard = selectedWords;
				
				Date d = new Date();
				PDOperation op = new PDOperation(TextEditor.store);
				op.setCommand("CUT");
				System.out.println("cut");
				PDCut cut = new PDCut(TextEditor.store);
				op.setTimeStamp(d.getTime());
				op.setSuperParameter(cut);
				op.setUser(TextEditor.userName);
				cut.setWord(word);
				TextEditor.history.addOperation(op);
			}
			TextEditor.store.commit();
		} 
		else 
		{
			String message = "No word is selected";
			JOptionPane pane = new JOptionPane(message);
			JDialog dialog = pane.createDialog(new JFrame(), "Dialog");
			dialog.show();
		}
		
	}
	
}