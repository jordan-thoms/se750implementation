package texteditor;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import texteditor.dal.*;
//import texteditor.dal.PDHistory;
//import texteditor.dal.PDOperation;
//import texteditor.dal.PDSimpleSpatialInfo;

import pdstore.GUID;
import pdstore.dal.PDInstance;
import pdstore.dal.PDWorkingCopy;

public class TextPanel extends JPanel implements KeyListener {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -6586020874129525506L;

	private TextEditor editor;

	public PDWorkingCopy store;
	public PDHistory history;
	public JList list = new JList();
	private Point start_point = null;
	public JTextArea textArea;

	public boolean inDrag = false;
	public String oldPosition = "";
	public String oldSize = "";

	final static boolean debug = true;

	public TextPanel(TextEditor ne, PDWorkingCopy workingCopy, GUID id) {
		super(new BorderLayout());
		editor = ne;
		store = workingCopy;
		history = PDHistory.load(store, id);
		new Dimension(100, 100);

		//int w = getWidth();
		//int h = getHeight();
		//new Point(w / 2, h / 2);
		
		textArea = new JTextArea();	
		textArea.setAlignmentX(LEFT_ALIGNMENT);
		textArea.setAlignmentY(TOP_ALIGNMENT);

		
		this.add(textArea);

		textArea.addKeyListener(this);
		
	}

	public void paint(Graphics g) {
		super.paint(g);
		List<PDOperation> operations = null;
				
		operations = new ArrayList<PDOperation>(history.getOperations());
		Collections.sort(operations, new OperationComparatorByTime());
		System.out.println(operations.size() + " operations retrieved");
		editor.wordList.clear();
		editor.wordList.add(new WordAndPos(editor.originator, 0));
		if(operations != null)
		{
			for (PDOperation o : operations)
			{
				//System.out.println(o.toString()); 
				PDInstance superParameter = o.getSuperParameter();
				String command = o.getCommand();
				if(command == editor.COPY){
					PDCopy c = (PDCopy)superParameter;
					for(WordAndPos wp : editor.wordList){
						if(wp.word.getId() == c.getToAfter().getId()){
							editor.wordList.add(new WordAndPos(c.getNewWord(), wp.caretPos+wp.word.getText().length()));
							textArea.setText(textArea.getText().substring(0, wp.caretPos+wp.word.getText().length()) + c.getNewWord().getText() + textArea.getText().substring(wp.word.getText().length()));
							break;
						}
					}
					
				}else if(command == editor.DELETE || command == editor.CUT){
					PDDelete d = (PDDelete)superParameter;
					for(WordAndPos wp : editor.wordList){
						if(wp.word.getId() == d.getWord().getId()){
							editor.wordList.remove(wp);
							textArea.setText(textArea.getText().substring(0, wp.caretPos)+ textArea.getText().substring(wp.caretPos+wp.word.getText().length()));
							break;
						}
					}
				}else if(command == editor.INSERT){
					PDInsert i = (PDInsert)superParameter;
					for(WordAndPos wp : editor.wordList){
						if(wp.word.getId() == i.getAfter().getId()){
							editor.wordList.add(new WordAndPos(i.getWord(), wp.caretPos+wp.word.getText().length()));
							textArea.setText(textArea.getText().substring(0, wp.caretPos+wp.word.getText().length()) + i.getWord().getText() + textArea.getText().substring(wp.word.getText().length()));
							break;
						}
					}
				}/*else if(command == MOVE){
					PDMove m = (PDMove)superParameter;
					
				}*/
			}
			ArrayList<PDOperation> parseOperations = new ArrayList<PDOperation>();
			for (PDOperation operation : operations) {
				if (operation.toString() != null) {
					parseOperations.add(operation);
				}
			}
			list.setListData(parseOperations.toArray());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
	 * )
	 */
	
	public String uncommitedText = "";
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		if (!arg0.isActionKey()) {

			char c = arg0.getKeyChar();

			if (c == ' ' || c == '\n') {
				uncommitedText += c;
				System.out.println("you typed the word: \"" + uncommitedText +"\"");
				
				/*
				 * Code for communicating with PDStore here
				 * 1. Create PDWord instance?
				 * 2. Commit to PDStore?
				 */
				PDWord word = new PDWord(store);
				word.addText(uncommitedText);
				PDOperation op = new PDOperation(store);
				op.setCommand(editor.INSERT);
				Date date = new Date();
				op.setTimeStamp(date.getTime());
				op.setUser(TextEditor.userName);
				PDInsert insert = new PDInsert(store);
				insert.setWord(word);
				insert.setAfter(word);
				op.setSuperParameter(insert);
				
				history.addOperation(op);
				
				store.commit();
				
				//this.repaint();
				
				uncommitedText = "";
			} else if (c == '\u0008') { //backspace character
				int i = uncommitedText.length();
				uncommitedText = uncommitedText.substring(0, i-1);
			} else {
				uncommitedText += c;
			}

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}