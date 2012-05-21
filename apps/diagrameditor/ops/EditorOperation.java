package diagrameditor.ops;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

import diagrameditor.DiagramEditor;
import diagrameditor.dal.PDSimpleSpatialInfo;

public abstract class EditorOperation implements ActionListener{
	protected final DiagramEditor editor;
	protected JPanel operationPanel;
	
	public EditorOperation(DiagramEditor editor){
		this.editor = editor;
		generateOperationPanel();
	}
	
	public JComponent getUI(){
		return operationPanel;
	}
	
	public abstract String stringRepresentation(PDSimpleSpatialInfo pds);
	public abstract void drawAction(String shapeID, PDSimpleSpatialInfo pds, ArrayList<String> objectsToDraw);
	protected abstract void generateOperationPanel();
	public abstract void closePanel();
	public abstract void actionPerformed(ActionEvent e);
	public abstract String checkId(PDSimpleSpatialInfo pds);
}
