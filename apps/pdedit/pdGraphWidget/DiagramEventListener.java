package pdedit.pdGraphWidget;

public interface DiagramEventListener {

	void nodeCreated(DiagramEvent d);
	void nodeChanged(DiagramEvent d);
	void nodeSelected(DiagramEvent d);
	void nodeRemoved(DiagramEvent d);
	
	void linkSelected(DiagramEvent d);
	void linkCreated(DiagramEvent d);
	void linkChanged(DiagramEvent d);
	void linkRemoved(DiagramEvent d);
	
	void modelCreated(DiagramEvent d);
	void modelChanged(DiagramEvent d);
	
}
