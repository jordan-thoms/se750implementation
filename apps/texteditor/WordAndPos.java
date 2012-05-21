package texteditor;

import texteditor.dal.*;

public class WordAndPos {
	public PDWord word;
	public int caretPos; //start of the word

	public WordAndPos(PDWord word, int caretPos){
		this.word = word;
		this.caretPos = caretPos;
	}
}
