package diagrameditor;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

import pdedit.pdShapes.Circle;
import pdedit.pdShapes.Rectangle;
import pdedit.pdShapes.ShapeInterface;
import diagrameditor.dal.PDOperation;
import diagrameditor.dal.PDSimpleSpatialInfo;
import diagrameditor.filters.Filter;
import diagrameditor.ops.EditorOperation;
import diagrameditor.ops.Ops;

/**
 * Class to set up the draw panel section of the diagram editor,
 * listens to changes and acts accordingly.
 *
 */
public class DrawPanel extends JPanel implements MouseListener,
		MouseMotionListener {

	private static final long serialVersionUID = -6586020874129525506L;

	protected JList list;
	private DiagramEditor editor;
	public DefaultListModel listModel;
	private ArrayList<String> objectsToDraw;
	private ShapeInterface selectedShape = null;
	private Point startClickPoint = null;
	private Point startDragPoint = null;

	public boolean inDrag = false;

	/**
	 * Constructor
	 * @param editor, diagram editor
	 */
	public DrawPanel(DiagramEditor editor) {
		this.editor = editor;
		objectsToDraw = new ArrayList<String>();
		listModel = new DefaultListModel();
		list = new JList(listModel);

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (!inDrag) {
			// Store the currently selected operation in the history panel.
			Object selectedOperation = list.getSelectedValue();
			
			// Clear everything.
			editor.circHashtable.clear();
			editor.rectHashtable.clear();
			editor.diagramList.clear();
			objectsToDraw.clear();
			
			for (PDOperation op : editor.getOperationList()) {
				
				PDSimpleSpatialInfo pds = (PDSimpleSpatialInfo) op.getSuperParameter();
				String shapeID = pds.getShapeID();
				//ShapeInterface tempShape;
				
				EditorOperation command = editor.instantiateCommand(op);
				command.drawAction(shapeID, pds, objectsToDraw);
				
				/* debug
				System.out.println("DrawPanel - arraylist:{");
				for (int i = 0; i<objectsToDraw.size(); i++){
					System.out.println("\tdp "+objectsToDraw.get(i));
				}
				*/

				/* in the stages of being deprecated. 
				if (op.getCommand().equals(Ops.NEW)) {	
					// Create the shape object.
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

				} else if (op.getCommand().equals(Ops.COPY)) {
					ShapeInterface targetShape;	// The shape being copied.
					
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
					
				} else if (op.getCommand().equals(Ops.MOVE)) {
					// Move the shape to the specified location.
					tempShape = editor.diagramList.get(shapeID);
					Point p = tempShape.getLocation();
					p.translate(pds.getX().intValue(), pds.getY().intValue());
					tempShape.setLocation(p);
				
				} else if (op.getCommand().equals(Ops.RESIZE)) {
					// Resize the shape to the specified dimensions.
					tempShape = editor.diagramList.get(shapeID);
					Dimension d = new Dimension(pds.getHeight().intValue(), pds.getWidth().intValue());
					tempShape.setSize(d);
				
				} else if (op.getCommand().equals(Ops.COLOR)) {
					// Change the shape's colour to that specified.
					tempShape = editor.diagramList.get(shapeID);
					Color tempColor = Color.decode(pds.getColor());
					tempShape.setColour(tempColor);
				}*/
			}

			// Add operations to the history panel.
			Filter filter = editor.menuBar.selectedFilter; 
			filter.display(editor.getOperationList(), listModel);
			
			// If nothing is selected in the history panel, scroll to the most recent operation.
			if (selectedOperation == null) {
				int lastIndex = list.getModel().getSize() - 1;
				if (lastIndex >= 0) {
					list.ensureIndexIsVisible(lastIndex);
				}
			} // Else reselect the same operation in the history panel.
			else {
				list.setSelectedValue(selectedOperation, true);
			}
			
			// Refresh the JCombobox.
			Object selectedItem = editor.selectBox.getSelectedItem();
			editor.selectBox.removeAllItems();
			for (String string : editor.circHashtable.keySet()) {
				editor.selectBox.addItem(string);
			}
			for (String string : editor.rectHashtable.keySet()) {
				editor.selectBox.addItem(string);
			}
			
			if(selectedItem != null) 
				editor.selectBox.setSelectedItem(selectedItem);
			
		}

		// TODO this would break commutivity between New and Copy operations.
		/*
		// Draw the shapes in the order they were created.
		Graphics2D g2 = (Graphics2D) g;
		for (String s : objectsToDraw) {
			editor.diagramList.get(s).drawShape(g2);
		}
		*/
		
		// Draw the shapes.
		Graphics2D g2 = (Graphics2D) g;
		for (Circle s : editor.circHashtable.values()) {
			s.drawShape(g2);
		}
		for (Rectangle s : editor.rectHashtable.values()) {
			s.drawShape(g2);
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Do nothing if there are no shapes.
		if (editor.selectBox.getItemCount() == 0)
			return;

		String shapeName = editor.selectBox.getSelectedItem().toString();
		selectedShape = editor.diagramList.get(shapeName);

		// Check if the shape selected in the ComboBox is clicked on the draw panel.
		if (selectedShape.containsPoint(e.getPoint())) {
			startClickPoint = e.getPoint();
			startDragPoint = e.getPoint();
			inDrag = true;
		}
		// If it wasn't clicked, check if any of the other shapes were clicked.
		else {
			// TODO Search through the shapes checking the most recently drawn
			// shape first. This ensures that selectedShape will be the correct
			// shape if multiple shapes are drawn overlapped.
			
			for (String shapeKey : editor.diagramList.keySet()) {
				ShapeInterface shape = editor.diagramList.get(shapeKey);
				if (shape.containsPoint(e.getPoint())) {
					selectedShape = shape;
					editor.selectBox.setSelectedItem(shapeKey);
					startClickPoint = e.getPoint();
					startDragPoint = e.getPoint();
					inDrag = true;
					break;
				}
			}
		}
		
		// If a shape was clicked, make the appropriate changes to the status
		// bar and history panel.
		if (inDrag) {
			// Deselect operation in history panel.
			list.clearSelection();
			
			// Display the coordinates on the status bar.
			editor.statusBar.clearStatus();
			editor.statusBar.setCoordinates(Integer.toString(selectedShape.getLocation().x), 
					Integer.toString(selectedShape.getLocation().y));
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// Do nothing if no shape is dragged.
		if(!inDrag)
			return;
			
		// TODO:    the following code should be executed, if the resize panel is selected in the list of
		// operations. this code is currently Ask-what-kind.
		// it should move to the operation.

		//			if (editor.ResizeButton.isSelected()) {
		//				int diff_x = e.getX() - start_point.x;
		//				int diff_y = e.getY() - start_point.y;
		//				int new_width = 100 + diff_x;
		//				int new_height = 100 + diff_y;
		//				Dimension new_size = new Dimension(new_width, new_height);
		//				shape.setSize(new_size);
		//				this.repaint();
		//			} else 
		{
			int diff_x = e.getX() - startDragPoint.x;
			int diff_y = e.getY() - startDragPoint.y;
			Point new_point = new Point(selectedShape.getLocation().x + diff_x,	selectedShape.getLocation().y + diff_y);
			selectedShape.setLocation(new_point);
			startDragPoint = e.getPoint();

			// Display the coordinates on the status bar.
			editor.statusBar.setCoordinates(Integer.toString((int)new_point.getX()), 
					Integer.toString((int)new_point.getY()));

			this.repaint();
		}
	}

	
	@Override
	public void mouseReleased(MouseEvent e) {
		// Do nothing if no shape is dragged.
		if(!inDrag)
			return;
		
		inDrag = false;
		Point releasePoint = e.getPoint();

		// Don't add a new operation if the shape did not change coordinates.
		if(startClickPoint.equals(releasePoint))
			return;
			
		// TODO:    the following code should be executed, if the resize panel is selected in the list of
		// operations. this code is currently Ask-what-kind.
		// it should move to the operation.

		//		if (editor.ResizeButton.isSelected()) {
		//			PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(store);
		//			pds.setWidth((long) shape.getSize().getHeight());
		//			pds.setHeight((long) shape.getSize().getWidth());
		//			pds.setShapeID(shape.getLabel());
		//			editor.operationInstantiator.newOperation("Resize", pds);
		//		} else 
		{
			// Store the Move operation.
			PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
			int dx = releasePoint.x - startClickPoint.x;
			int dy = releasePoint.y - startClickPoint.y;
			pds.setX((long) dx);
			pds.setY((long) dy);
			pds.setShapeID(selectedShape.getLabel());
			PDOperation op = new PDOperation(editor.workingCopy);
			op.setCommand(Ops.MOVE);
			op.setSuperParameter(pds);
			editor.getOperationList().add(op);
			
			// Display the coordinates on the status bar.
			editor.statusBar.setCoordinates(Integer.toString(selectedShape.getLocation().x), 
					Integer.toString(selectedShape.getLocation().y));
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
}