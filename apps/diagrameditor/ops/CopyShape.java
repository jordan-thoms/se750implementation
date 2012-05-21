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
import diagrameditor.dal.PDOperation;
import diagrameditor.dal.PDSimpleSpatialInfo;

/**
 * Creates a copy of the selected shape and positions this new shape next to the
 * original shape.
 */
public class CopyShape extends EditorOperation {
	/* Fields from superclass:
	 * 	protected final DiagramEditor editor;
	 *  protected JPanel operationPanel;
	 */

	public CopyShape(DiagramEditor newEditor) {
		super(newEditor);
	}


	protected void generateOperationPanel() {
		JButton copy_button = new JButton("Copy");
		copy_button.setMargin(new Insets(0, 50, 0, 50));
		copy_button.addActionListener(this);
		
		JPanel paddingPanel1 = new JPanel();
		
		operationPanel = new JPanel(new BorderLayout());
		operationPanel.add(copy_button,BorderLayout.NORTH);
		operationPanel.add(paddingPanel1, BorderLayout.CENTER);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// Must select a shape in the ComboBox to perform the operation.
		Object selectedItem = this.editor.selectBox.getSelectedItem();
		if (selectedItem == null) {
			editor.statusBar.setError("Select a shape to perform the operation");
			return;
		}
		
		//TODO Use the last number rather than size
		
		// Name the new shape.
		String shapeName;
		String targetShapeID = selectedItem.toString();
		if (targetShapeID.contains("Cir_")) {
			int shapeID = this.editor.circHashtable.size() + 1;
			shapeName = "Cir_" + shapeID;
		} else {
			int shapeID = this.editor.rectHashtable.size() + 1;
			shapeName = "Rec_" + shapeID;
		}
		
		// Store the changes.
		PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setTargetID(targetShapeID);
		pds.setShapeID(shapeName);
		PDOperation op = new PDOperation(editor.workingCopy);
		op.setCommand(Ops.COPY);
		op.setSuperParameter(pds);
		editor.getOperationList().add(op);
		
		// Clear the status bar.
		editor.statusBar.clearStatus();
		
		// Display the coordinates on the status bar.
		// The new shape is not drawn on top of the original shape (displaced).
		ShapeInterface targetShapeInterface = editor.diagramList.get(targetShapeID);
		int displacement = 50;
		String xCoord = Integer.toString((int) targetShapeInterface.getLocation().getX() + displacement);
		String yCoord = Integer.toString((int) targetShapeInterface.getLocation().getY() + displacement);
		editor.statusBar.setCoordinates(xCoord, yCoord);
		
		this.editor.validate();
	}

	@Override
	public void drawAction(String shapeID, PDSimpleSpatialInfo pds, ArrayList<String> objectsToDraw){
		ShapeInterface targetShape;	// The shape being copied.
		ShapeInterface tempShape;
		
		// Retrieve the targetShape and create a new shape object (the copy).
		if (pds.getTargetID().contains("Cir_")) {
			targetShape = editor.circHashtable.get(pds.getTargetID());
			tempShape = new Circle();
			editor.circHashtable.put(shapeID, (Circle) tempShape);
			editor.diagramList.put(shapeID, tempShape);
		} else {
			targetShape = editor.rectHashtable.get(pds.getTargetID());
			tempShape = new Rectangle();
			editor.rectHashtable.put(shapeID, (Rectangle) tempShape);
			editor.diagramList.put(shapeID, tempShape);
		}
		
		// Set the attributes of the new copied shape.
		int x = targetShape.getLocation().x;
		int y = targetShape.getLocation().y;
		tempShape.setLocation(new Point(x + 50, y + 50));
		tempShape.setLabel(shapeID);
		tempShape.setBorderColour(targetShape.getBorderColour());
		tempShape.setColour(targetShape.getColour());
		tempShape.setSize(targetShape.getSize());
		
		// Add the newly created shape to the list of objects to draw.
		objectsToDraw.add(shapeID);
	}
	
	@Override
	public String stringRepresentation(PDSimpleSpatialInfo pds){
		return "CopyShape ( " + pds.getTargetID() + " , " + pds.getShapeID() + " ) ";
	}
	
	@Override
	public void closePanel() {
		// TODO Auto-generated method stub
		
	}
	
	//Get target id.
	public String checkId(PDSimpleSpatialInfo pds){
		return pds.getTargetID();
	}
}
