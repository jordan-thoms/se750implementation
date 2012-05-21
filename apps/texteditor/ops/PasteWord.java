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
import texteditor.dal.PDCopy;
import texteditor.dal.PDDelete;
import texteditor.dal.PDInsert;
import texteditor.dal.PDOperation;
import texteditor.dal.PDWord;

public class PasteWord implements ActionListener {

	/**
	 * 
	 */
	
	private final TextEditor newEditor;
	private String status_msg;
	private JPanel pasteButtonPanel;

	public PasteWord(TextEditor newEditor) {
		this.newEditor = newEditor;
		this.newEditor.status.setText("Paste Word");
	}

	public JComponent getUI() {
		JButton copy_button = new JButton("Paste");
		copy_button.setMargin(new Insets(0, 50, 0, 50));
		copy_button.addActionListener(this);
		pasteButtonPanel = new JPanel(new BorderLayout());
		pasteButtonPanel.add(copy_button,BorderLayout.NORTH);
		return pasteButtonPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		int caretPos = newEditor.textPanel.textArea.getCaretPosition();
		
		
		if(!newEditor.clipboard.isEmpty())
		{
			WordAndPos current = null;
			
			for (WordAndPos wordPos : newEditor.wordList)			
			{
				if(wordPos.caretPos <= caretPos && (current == null || wordPos.caretPos > current.caretPos))
				{
					current = wordPos;
				}
			}
			
			
			if(TextEditor.actionStatus == "COPY")
			{
				for (PDWord word : newEditor.clipboard)
				{
					PDOperation op = new PDOperation(TextEditor.store);
					op.setCommand(TextEditor.COPY);
					Date date = new Date();
					op.setTimeStamp(date.getTime());
					op.setUser(TextEditor.userName);
					PDCopy copy = new PDCopy(TextEditor.store);
					copy.setOriginalWord(word);
					copy.setNewWord(word);
					copy.setToAfter(current.word);

					op.setSuperParameter(copy);
				}
			}
			else if(TextEditor.actionStatus == "CUT")
			{
				for (PDWord word : newEditor.clipboard)
				{
					PDOperation op = new PDOperation(TextEditor.store);
					op.setCommand(TextEditor.CUT);
					Date date = new Date();
					op.setTimeStamp(date.getTime());
					op.setUser(TextEditor.userName);
					PDInsert insert = new PDInsert(TextEditor.store);
					insert.setWord(word);
					insert.setAfter(current.word);
					op.setSuperParameter(insert);
					
				}
			}
			TextEditor.store.commit();
		}
	}

}