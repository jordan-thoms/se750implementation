package diagrameditor.ops;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import pdedit.pdShapes.Circle;
import pdedit.pdShapes.Rectangle;
import pdedit.pdShapes.ShapeInterface;

import diagrameditor.DiagramEditor;
import diagrameditor.dal.PDSimpleSpatialInfo;

/**
 * Deletes the selected shape.
 *
 */
public class DeleteShape extends EditorOperation {
	/* Fields from superclass:
	 * 	protected final DiagramEditor editor;
	 *  protected JPanel operationPanel;
	 */

	public DeleteShape(DiagramEditor newEditor) {
		super(newEditor);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// Must select a shape in the ComboBox to perform the operation.
		Object selectedItem = this.editor.selectBox.getSelectedItem();
		if (selectedItem == null) {
			editor.statusBar.setError("Select a shape to perform the operation");
			return;
		}
		
		// Clear the status bar.
		editor.statusBar.clearStatus();

		// TODO delete the shape
	}

	@Override
	protected void generateOperationPanel() {
		JButton delete_button = new JButton("Delete");
		delete_button.setMargin(new Insets(0, 50, 0, 50));
		delete_button.addActionListener(this);
		operationPanel = new JPanel(new BorderLayout());
		operationPanel.add(delete_button,BorderLayout.NORTH);
	}

	@Override
	public void drawAction(String shapeID, PDSimpleSpatialInfo pds, ArrayList<String> objectsToDraw){
		//TODO
	}
	
	@Override
	public String stringRepresentation(PDSimpleSpatialInfo pds){
		return "DeleteShape ( " + pds.getShapeID() + " ) ";
	}
	
	@Override
	public void closePanel() {
		// TODO Auto-generated method stub
		
	}
	
	//Get the shape of the operation.
	public String checkId(PDSimpleSpatialInfo pds){
		return pds.getShapeID();
	}
}
