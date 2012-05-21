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
 * Resizes the selected shape to the user specified width and height.
 * 
 */
public class ResizeShape extends EditorOperation {
	private boolean panelOpen = false;
	
	public JPanel inputContainer;
	private JTextField widthField;
	private JTextField heightField;
	private JButton resizeButton;	
	private JButton applyButton;
	private JButton cancelButton;
	
	public ResizeShape(DiagramEditor newEditor) {
		super(newEditor);
	}

	public void closePanel() {
		panelOpen = false;
		inputContainer.setVisible(panelOpen);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		// Collapse or expand the move panel.
		if (e.getSource().equals(resizeButton)) {
			boolean temp = !panelOpen;
			editor.operationPanel.closePanels();
			panelOpen = temp;
			inputContainer.setVisible(panelOpen);
		} 
		// Clear all text fields. 
		else if (e.getSource().equals(cancelButton)) {
			heightField.setText("");
			widthField.setText("");
			editor.statusBar.clearStatus();
			closePanel();
		}
		// Attempt to apply the user's changes.
		else if (e.getSource().equals(applyButton)) {
			// No shape selected, do nothing.
			if (editor.selectBox.getSelectedItem() == null) {
				editor.statusBar.setError("Select a shape to perform the operation");
			} else {
				long height = 0;
				long width = 0;
				try {
					height = Long.parseLong(heightField.getText());
					width = Long.parseLong(widthField.getText());
				} catch (NumberFormatException e2) {
					editor.statusBar.setError("Please input Width and Height coordinates in right format!");
					return;
				}
				
				// Store the changes.
				String shapeID = editor.selectBox.getSelectedItem().toString();
				PDSimpleSpatialInfo pds = new PDSimpleSpatialInfo(editor.workingCopy);
				pds.setHeight(width);
				pds.setWidth(height);
				pds.setShapeID(shapeID);
				PDOperation op = new PDOperation(editor.workingCopy);
				op.setCommand(Ops.RESIZE);
				op.setSuperParameter(pds);
				editor.getOperationList().add(op);
				
				editor.statusBar.clearStatus();
			}
		}
	}

	@Override
	public void drawAction(String shapeID, PDSimpleSpatialInfo pds, ArrayList<String> objectsToDraw){
		/* debug
		System.out.println("ResizeShape - passed in arraylist:");
		for (int i = 0; i<objectsToDraw.size(); i++){
			System.out.println("rs1"+objectsToDraw.get(i));
		}
		*/
		
		ShapeInterface tempShape;
		
		// Resize the shape to the specified dimensions.
		tempShape = editor.diagramList.get(shapeID);
		Dimension d = new Dimension(pds.getHeight().intValue(), pds.getWidth().intValue());
		tempShape.setSize(d);
	}
	
	@Override
	protected void generateOperationPanel() {
		resizeButton = new JButton("Resize");
		resizeButton.setMargin(new Insets(0, 50, 0, 50));
		resizeButton.addActionListener(this);
		
		// resizebuttonPanel contains all components.
		operationPanel = new JPanel(new BorderLayout());

		// Create the text fields.
		JPanel inputPanel = new JPanel(new GridLayout(1, 4));
		widthField = new JTextField();
		widthField.setColumns(5);
		heightField = new JTextField();
		heightField.setColumns(5);
		JLabel widthLabel = new JLabel("Width");
		JLabel heightLabel = new JLabel("Height");

		inputPanel.add(widthLabel);
		inputPanel.add(widthField);
		inputPanel.add(heightLabel);
		inputPanel.add(heightField);

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

		inputContainer = new JPanel(new BorderLayout());
		inputContainer.add(inputPanel, BorderLayout.NORTH);
		inputContainer.add(paddingPanel1, BorderLayout.CENTER);
		inputContainer.add(buttonPanel, BorderLayout.SOUTH);
		inputContainer.setVisible(false);

		operationPanel.add(resizeButton, BorderLayout.NORTH);
		operationPanel.add(paddingPanel2, BorderLayout.CENTER);
		operationPanel.add(inputContainer, BorderLayout.SOUTH);
	}
	
	@Override
	public String stringRepresentation(PDSimpleSpatialInfo pds){
		return "ResizeShape ( " + pds.getShapeID() + " , " + "(" + pds.getHeight() + "," + pds.getWidth() + ")"	+ " ) ";
	}
	
	//Get the shape of the operation.
	public String checkId(PDSimpleSpatialInfo pds){
		return pds.getShapeID();
	}

}
