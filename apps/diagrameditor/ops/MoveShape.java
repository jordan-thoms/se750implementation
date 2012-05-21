package diagrameditor.ops;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pdedit.pdShapes.Circle;
import pdedit.pdShapes.Rectangle;
import pdedit.pdShapes.ShapeInterface;

import diagrameditor.DiagramEditor;
import diagrameditor.dal.PDOperation;
import diagrameditor.dal.PDSimpleSpatialInfo;

/**
 * Changes the position of the selected shape to the X and Y coordinates
 * specified by the user.
 */
public class MoveShape extends EditorOperation {
	/* Fields from superclass:
	 * 	protected final DiagramEditor editor;
	 *  protected JPanel operationPanel;
	 */
	
	private boolean panelOpen = false;
	
	private JPanel inputContainer;
	private JTextField xTextField;
	private JTextField yTextField;
	private JButton moveButton;
	private JButton applyButton;
	private JButton cancelButton;
	
	public MoveShape(DiagramEditor newEditor) {
		super(newEditor);
	}

	protected void generateOperationPanel() {
		moveButton = new JButton("Move");
		moveButton.setMargin(new Insets(0, 50, 0, 50));
		moveButton.addActionListener(this);
			
		// movebuttonPanel contains all components.
		operationPanel = new JPanel(new BorderLayout());

		// Create the text fields.
		JPanel inputPanel = new JPanel(new GridLayout(1, 4));
		JLabel xLabel = new JLabel("  X: ");
		JLabel yLabel = new JLabel("  Y: ");
		xTextField = new JTextField();
		xTextField.setColumns(5);
		yTextField = new JTextField();
		yTextField.setColumns(5);
		
		inputPanel.add(xLabel);
		inputPanel.add(xTextField);
		inputPanel.add(yLabel);
		inputPanel.add(yTextField);

		// Create the Apply and Cancel buttons.
		JPanel buttonPanel = new JPanel();
		applyButton = new JButton("Apply");
		applyButton.setMargin(new Insets(0, 11, 0, 11));
		applyButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.setMargin(new Insets(0, 11, 0, 11));
		cancelButton.addActionListener(this);
		
		buttonPanel.add(applyButton);
		buttonPanel.add(cancelButton);

		// Padding to be used in the layout.
		JPanel paddingPanel1 = new JPanel();
		JPanel paddingPanel2 = new JPanel();
		
		// Create the collapsing\expanding panel.
		inputContainer = new JPanel(new BorderLayout());
		inputContainer.add(inputPanel, BorderLayout.NORTH);
		inputContainer.add(paddingPanel1, BorderLayout.CENTER);
		inputContainer.add(buttonPanel, BorderLayout.SOUTH);
		inputContainer.setVisible(false);

		operationPanel.add(moveButton, BorderLayout.NORTH);
		operationPanel.add(paddingPanel2, BorderLayout.CENTER);
		operationPanel.add(inputContainer, BorderLayout.SOUTH);
	}
	
	public void closePanel() {
		panelOpen = false;
		inputContainer.setVisible(panelOpen);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		// Collapse or expand the move panel.
		if (e.getSource().equals(moveButton)) {
			boolean temp = !panelOpen;
			editor.operationPanel.closePanels();
			panelOpen = temp;
			inputContainer.setVisible(panelOpen);
		} 
		// Clear all text fields. 
		else if (e.getSource().equals(cancelButton)) {
			xTextField.setText("");
			yTextField.setText("");
			editor.statusBar.clearStatus();
			closePanel();
		} 
		// Attempt to apply the user's changes.
		else if (e.getSource().equals(applyButton)) {
			// No shape selected, do nothing.
			if (editor.selectBox.getSelectedItem() == null) {
				editor.statusBar.setError("Select a shape to perform the operation");
			} else {
				// Get the X and Y coordinate inputs.
				long X_Coordinate = 0;
				long Y_Coordinate = 0;
				try {
					X_Coordinate = Long.parseLong(xTextField.getText());
					Y_Coordinate = Long.parseLong(yTextField.getText());
				} catch (NumberFormatException e2) {
					editor.statusBar.setError("Please input X and Y coordinates in right format!");
					return;
				}

				// Store the changes.
				String shapeID = editor.selectBox.getSelectedItem().toString();
				PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
				pds.setX(X_Coordinate);
				pds.setY(Y_Coordinate);
				pds.setShapeID(shapeID);
				PDOperation op = new PDOperation(editor.workingCopy);
				op.setCommand(Ops.MOVE);
				op.setSuperParameter(pds);
				editor.getOperationList().add(op);
				
				// Display the coordinates on the status bar.
				editor.statusBar.setCoordinates(xTextField.getText(), yTextField.getText());
				editor.statusBar.clearStatus();
			}
		}
	}
	
	@Override
	public void drawAction(String shapeID, PDSimpleSpatialInfo pds, ArrayList<String> objectsToDraw){
		ShapeInterface tempShape;
		
		// Move the shape to the specified location.
		tempShape = editor.diagramList.get(shapeID);
		Point p = tempShape.getLocation();
		p.translate(pds.getX().intValue(), pds.getY().intValue());
		tempShape.setLocation(p);
	}
	
	@Override
	public String stringRepresentation(PDSimpleSpatialInfo pds){
		return "MoveShape ( " + pds.getShapeID() + " , " + "(" + pds.getX() + "," + pds.getY() + ")" + " ) ";
	}
	
	//Get the shape of the operation.
	public String checkId(PDSimpleSpatialInfo pds){
		return pds.getShapeID();
	}
	
}
