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
import texteditor.dal.*;

public class DeleteWord implements ActionListener 
{

	/**
	 * 
	 */
	private final TextEditor newEditor;
	private JPanel deleteButtonPanel;

	/**
	 * @param newEditor
	 */
	public DeleteWord(TextEditor newEditor) 
	{
		this.newEditor = newEditor;
	}
	
	public JComponent getUI() 
	{
		JButton delete_button = new JButton("Delete");
		delete_button.setMargin(new Insets(0, 50, 0, 50));
		delete_button.addActionListener(this);
		deleteButtonPanel = new JPanel(new BorderLayout());
		deleteButtonPanel.add(delete_button,BorderLayout.NORTH);
		return deleteButtonPanel;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) 
	{
	
	/*	String text = this.newEditor.textPanel.textArea.getSelectedText();
		text = (text!=null)? text.trim():"No selected text";
		System.out.println(text);*/
		String selectedText = this.newEditor.textPanel.textArea.getSelectedText();
		int endCaretPos = this.newEditor.textPanel.textArea.getCaretPosition();
		int startCaretPos = endCaretPos - selectedText.length();
	
		System.out.println(selectedText);
		
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
			for (PDWord word : selectedWords)
			{
				Date d = new Date();
				PDOperation op = new PDOperation(TextEditor.store);
				op.setCommand("DELETE");
				System.out.println("delete");
				PDDelete delete = new PDDelete(TextEditor.store);
				op.setTimeStamp(d.getTime());
				op.setSuperParameter(delete);
				op.setUser(TextEditor.userName);
				delete.setWord(word);
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

