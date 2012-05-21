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
 * Creates the new shape specified by the user. 
 *
 */
public class NewShape extends EditorOperation {
	/* Fields from superclass:
	 * 	protected final DiagramEditor editor;
	 *  protected JPanel operationPanel;
	 */
	
	public static final String CIRCLE = "Cir_";
	public static final String RECTANGLE = "Rec_";
	
	private JButton circleButton;
	private JButton rectangleButton;

	public NewShape(DiagramEditor editor) {
		super(editor);
	}
	

	protected void generateOperationPanel() {
		circleButton = new JButton("New Circle");
		circleButton.setMargin(new Insets(0, 11, 0, 11));
		circleButton.addActionListener(this);
		
		rectangleButton = new JButton("New Rectangle");
		rectangleButton.setMargin(new Insets(0, 11, 0, 11));
		rectangleButton.addActionListener(this);
		
		JPanel shapesPanel = new JPanel(new BorderLayout());
		JPanel paddingPanel1 = new JPanel();
		JPanel paddingPanel2 = new JPanel();
		
		shapesPanel.add(circleButton, BorderLayout.NORTH);
		shapesPanel.add(paddingPanel1, BorderLayout.CENTER);
		shapesPanel.add(rectangleButton, BorderLayout.SOUTH);
		
		operationPanel = new JPanel(new BorderLayout());
		operationPanel.add(shapesPanel,BorderLayout.NORTH);
		operationPanel.add(paddingPanel2, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ShapeInterface shape = null;
		String shape_name = "shape";

		// Create the shape and store it in a hash table.
		if (e.getSource().equals(circleButton)) {
			int shapeID = this.editor.circHashtable.size() + 1;
			shape_name = CIRCLE + shapeID;
			shape = new Circle();
			editor.circHashtable.put(shape_name, (Circle) shape);
		} else if (e.getSource().equals(rectangleButton)) {
			int shapeID = this.editor.rectHashtable.size() + 1;
			shape_name = RECTANGLE + shapeID;
			shape = new Rectangle();
			editor.rectHashtable.put(shape_name, (Rectangle) shape);
		}
		editor.diagramList.put(shape_name, shape);

		// Set the shape's parameters.
		int total_count = editor.rectHashtable.size() + editor.circHashtable.size();
		Point center = new Point(total_count * 50, total_count * 50);
		shape.setSize(new Dimension(100, 100));
		shape.setLocation(center);
		shape.setLabel(shape_name);

		// Store the changes.
		PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
		pds.setShapeID(shape_name);
		pds.setHeight((long) 100);
		pds.setWidth((long) 100);
		pds.setX((long) shape.getLocation().x);
		pds.setY((long) shape.getLocation().y);
		PDOperation op = new PDOperation(editor.workingCopy);
		op.setCommand(Ops.NEW);
		op.setSuperParameter(pds);
		editor.getOperationList().add(op);
		
		// Display the coordinates on the status bar.
		editor.statusBar.clearStatus();
		String xCoord = Integer.toString((int) center.getX());
		String yCoord = Integer.toString((int) center.getY());
		editor.statusBar.setCoordinates(xCoord,yCoord);
		
		editor.validate();
	}

	@Override
	public void drawAction(String shapeID, PDSimpleSpatialInfo pds, ArrayList<String> objectsToDraw){
		ShapeInterface tempShape;
		
		if (shapeID.contains("Cir_")) {
			tempShape = new Circle();
			editor.circHashtable.put(shapeID, (Circle) tempShape);
			editor.diagramList.put(shapeID, tempShape);
		} else {
			tempShape = new Rectangle();
			editor.rectHashtable.put(shapeID, (Rectangle) tempShape);
			editor.diagramList.put(shapeID, tempShape);
		}

		// Set its attributes.
		Dimension d = new Dimension(pds.getHeight().intValue(), pds.getWidth().intValue());
		Point p = new Point(pds.getX().intValue(), pds.getY().intValue());
		tempShape.setSize(d);
		tempShape.setLocation(p);
		tempShape.setLabel(shapeID);
		
		// Add the newly created shape to the list of objects to draw.
		objectsToDraw.add(shapeID);
	}
	
	@Override
	public String stringRepresentation(PDSimpleSpatialInfo pds){
		return "NewShape ( " + pds.getShapeID() + " ) ";
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
