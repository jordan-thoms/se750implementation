package diagrameditor.ops;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import pdedit.pdShapes.Circle;
import pdedit.pdShapes.Rectangle;
import pdedit.pdShapes.ShapeInterface;

import diagrameditor.DiagramEditor;
import diagrameditor.dal.PDOperation;
import diagrameditor.dal.PDSimpleSpatialInfo;

/**
 * Changes the fill color of the selected shape. 
 * 
 */
public class ChangeColor extends EditorOperation {
	public ChangeColor(DiagramEditor newEditor) {
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

		// Get the current color of the shape.
		String shapeID = selectedItem.toString();
		ShapeInterface shape = this.editor.diagramList.get(shapeID);
		Color currentColor = shape.getColour();

		// Ask the user to select a color.
		Color tempColor = JColorChooser.showDialog(this.editor,
				"Please choose a color", currentColor);
		if (tempColor == null)
			return;

		// Format the color: #RRGGBB
		String rgb = Integer.toHexString(tempColor.getRGB());
		rgb = "#" + rgb.substring(2, rgb.length());

		// Store the changes.
		PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setColor(rgb);
		pds.setShapeID(shapeID);
		PDOperation op = new PDOperation(editor.workingCopy);
		op.setCommand(Ops.COLOR);
		op.setSuperParameter(pds);
		editor.getOperationList().add(op);
		this.editor.validate();
	}

	@Override
	protected void generateOperationPanel() {
		JButton colorButton = new JButton("Color");
		colorButton.setMargin(new Insets(0, 50, 0, 50));
		colorButton.addActionListener(this);
		operationPanel = new JPanel(new BorderLayout());
		operationPanel.add(colorButton, BorderLayout.NORTH);
	}

	@Override
	public void drawAction(String shapeID, PDSimpleSpatialInfo pds, ArrayList<String> objectsToDraw){		
		ShapeInterface tempShape;
		
		// Change the shape's colour to that specified.
		tempShape = editor.diagramList.get(shapeID);
		Color tempColor = Color.decode(pds.getColor());
		tempShape.setColour(tempColor);
	}
	
	@Override
	public String stringRepresentation(PDSimpleSpatialInfo pds){
		return "ChangeColor ( " + pds.getShapeID() + " , " + pds.getColor() + " ) ";
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
