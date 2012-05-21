package diagrameditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import diagrameditor.dal.PDOperation;
import diagrameditor.dal.PDSimpleSpatialInfo;
import diagrameditor.ops.Ops;

/**
 * Class to set up the history panel section of the diagram editor,
 * listen to the history buttons and perform the required actions caused 
 * by pressing these buttons.
 *
 */
public class HistoryPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String upString = "Move up";
	private static final String downString = "Move down";
	private DiagramEditor editor;

	public JList list;
	//History panel buttons
	private JButton upButton;
	private JButton downButton;
	private JButton deleteButton;

	/**
	 * Constructor
	 * @param editor, diagram editor
	 */
	public HistoryPanel(DiagramEditor editor) {
		super(new BorderLayout());
		this.editor = editor;
		setUpLayout();
	}

	/**
	 * Method to set up the layout of the history panel
	 */
	private void setUpLayout() {
		this.setMinimumSize(new Dimension(this.getPreferredSize().width, 100));
		this.list = editor.drawPanel.list;
		this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.list.setSelectedIndex(0);
		this.list.setDragEnabled(true);
		this.list.setCellRenderer(new HistoryPanelRenderer(editor));

		this.list.addListSelectionListener(new ListSelectionListener() {			
			// Clear the x and y coordinates on the status bar when an operation
			// in the history panel is selected.
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting() && list.getSelectedIndex() != -1) {
					editor.statusBar.clearStatus();
					editor.statusBar.clearCoordinates();
				}
			}
		});

		// Create the Up Arrow button.
		JScrollPane listScrollPane = new JScrollPane(list);
		ImageIcon icon = createImageIcon("up");
		if (icon != null) {
			this.upButton = new JButton(icon);
			this.upButton.setSize(5, 5);
			this.upButton.setMargin(new Insets(0, 0, 0, 0));
		} else {
			this.upButton = new JButton("UP");
		}
		this.upButton.setToolTipText("Move the currently selected operation higher.");
		this.upButton.addActionListener(upButton(editor, list));
		this.upButton.setActionCommand(upString);

		// Create the Down Arrow button.
		icon = createImageIcon("down");
		if (icon != null) {
			this.downButton = new JButton(icon);
			this.downButton.setMargin(new Insets(0, 0, 0, 0));
		} else {
			this.downButton = new JButton("Down");
		}
		this.downButton.setToolTipText("Move the currently selected operation lower.");
		this.downButton.addActionListener(downButton(editor, list));
		this.downButton.setActionCommand(downString);

		// Create the Delete button.
		icon = createImageIcon("delete");
		if (icon != null) {
			this.deleteButton = new JButton(icon);
			this.deleteButton.setMargin(new Insets(0, 0, 0, 0));
		} else {
			this.deleteButton = new JButton("Delete");
		}
		this.deleteButton.setToolTipText("Delete the selected operation.");
		this.deleteButton.addActionListener(deleteButton(editor, list));
		this.deleteButton.setActionCommand("Delete");

		// Add buttons to the layout.
		JPanel upDownPanel = new JPanel(new GridLayout(1, 3));
		upDownPanel.add(upButton);
		upDownPanel.add(downButton);
		upDownPanel.add(deleteButton);

		JPanel buttonPane = new JPanel();
		buttonPane.add(upDownPanel);

		this.add(buttonPane, BorderLayout.NORTH);
		this.add(listScrollPane, BorderLayout.CENTER);
	}

	/**
	 * Method to create an image
	 * @param imageName
	 * @return an ImageIcon of the image
	 */
	private static ImageIcon createImageIcon(String imageName) {
		String imgLocation = imageName + ".jpg";
		java.net.URL imageURL = HistoryPanel.class.getResource(imgLocation);
		if (imageURL == null) {
			System.err.println("Resource not found: " + imgLocation);
			return null;
		} else {
			return new ImageIcon(imageURL);
		}
	}
	
	/**
	 * Method to listen to the delete button action events and delete the selected operation
	 * from the history list.
	 * @param editor, the diagram editor
	 * @param list, the history list
	 * @return an action listener for thedelete button
	 */
	public ActionListener deleteButton(final DiagramEditor editor, final JList list) {
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PDOperation currentOp = (PDOperation) list.getSelectedValue();

				//No operation has been selected
				if (currentOp == null) {
					String message = "Please select one operation to delete";
					editor.statusBar.setError(message);

				} else {
					editor.statusBar.clearStatus();

					//check if we need to do cascading delete
					if (currentOp.getCommand().equals(Ops.NEW) || currentOp.getCommand().equals(Ops.COPY)){
						//get the list of all operations which need to be deleted
						List<PDOperation> removing = editor.getOperationList().getDeleteOperations(currentOp);

						//remove operations
						for (int i = removing.size() - 1; i >= 0; i--) {
							PDOperation op = removing.get(i);
							editor.getOperationList().remove(op);
							//check if any operations we are deleting cause shapes to be deleted from the 
							//diagram
							if (op.getCommand().equals(Ops.NEW) || op.getCommand().equals(Ops.COPY)){
								String opId = ((PDSimpleSpatialInfo)op.getSuperParameter()).getShapeID();

								//remove a shape from the diagram
								editor.diagramList.remove(opId);
								if (opId.contains("Cir_")){
									editor.circHashtable.remove(opId);
								} else if (opId.contains("Rec_")) {
									editor.rectHashtable.remove(opId);
								}
							}
						}
					} else {
						//remove the selected operation
						editor.getOperationList().remove(currentOp);
					}
					//push change to the database
					editor.workingCopy.commit();
				}
			}
		};
		return listener;
	}

	/**
	 * Method to listen to the down button action events and move the selected operation
	 * down in the history list.
	 * @param editor, the diagram editor
	 * @param list, the history list
	 * @return an action listener for the down button
	 */
	public ActionListener downButton(final DiagramEditor editor, final JList list) {
		ActionListener listener =  new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PDOperation currentOp = (PDOperation) list.getSelectedValue();
				int listSize = list.getModel().getSize();
				int currentIndex = list.getSelectedIndex();

				if (currentOp == null) {
					String message = "Please select one operation to move down";
					editor.statusBar.setError(message);

				} else if (currentIndex == listSize - 1) {
					// Cannot move the last operation downwards.
					String message = "Bottom operation! Can not move down again!";
					editor.statusBar.setError(message);

				} else {
					editor.statusBar.clearStatus();
					PDOperation nextOp = (PDOperation) list.getModel().getElementAt(currentIndex + 1);

					PDSimpleSpatialInfo nextSuperPara = (PDSimpleSpatialInfo) nextOp
					.getSuperParameter();
					PDSimpleSpatialInfo curSuperPara = (PDSimpleSpatialInfo) currentOp
					.getSuperParameter();

					if (checkValidDown(currentOp, nextOp, editor)) {
						// Case where a Copy operation moves below an operation which acts on it.
						// The other operation now links to the Copied shape's parent.
						if (currentOp.getCommand().equals(Ops.COPY) && 
								curSuperPara.getShapeID().equals(nextSuperPara.getShapeID())) {
							nextSuperPara.setShapeID(curSuperPara.getTargetID());
						}

						// Case where a Copy operation moves below a second Copy
						// operation that targets it. Set the second Copy
						// operation target to be the same target as the Copy
						// operation we are moving.
						if (currentOp.getCommand().equals(Ops.COPY) && 
								curSuperPara.getShapeID().equals(nextSuperPara.getTargetID())) {
							nextSuperPara.setTargetID(curSuperPara.getTargetID());
						}

						// Switch the position of the two operations
						editor.getOperationList().move(currentOp, true);
					}
				}
			}
		};
		return listener;
	}

	/**
	 * Method to listen to the up button action events and move the selected operation
	 * up in the history list.
	 * @param editor, the diagram editor
	 * @param list, the history list
	 * @return an action listener for the up button
	 */
	public ActionListener upButton(final DiagramEditor editor, final JList list) {
		ActionListener listener=  new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				PDOperation currentOp = (PDOperation) list.getSelectedValue();
				int currentIndex = list.getSelectedIndex();

				// No operation has been selected to move
				if (currentOp == null) {
					String message = "Please select one operation to move up";
					editor.statusBar.setError(message);
				// Cannot move the top operation upwards.
				} else if (currentIndex == 0) {
					String message = "Top operation! Can not move up again!";
					editor.statusBar.setError(message);
				//move the selected operation
				} else {
					editor.statusBar.clearStatus();
					PDOperation previousOp = (PDOperation) list.getModel()
					.getElementAt(currentIndex - 1);

					//get operation information
					PDSimpleSpatialInfo preSuperPara = (PDSimpleSpatialInfo) previousOp
					.getSuperParameter();
					PDSimpleSpatialInfo curSuperPara = (PDSimpleSpatialInfo) currentOp
					.getSuperParameter();

					// Can if it is a valid swap
					if (checkValidUp(currentOp, previousOp, editor)) {
						
						// Case where an operation on a copied shape moves above it's Copy operation.
						// The operation will now be linked to the copied shape's parent.
						if (previousOp.getCommand().equals(Ops.COPY) && 
								preSuperPara.getShapeID().equals(curSuperPara.getShapeID())) {
							curSuperPara.setShapeID(preSuperPara.getTargetID());
						}

						// Case where a Copy operation, on an already copied
						// shape, is moved above it's parent's Copy operation.
						// The operation will now be linked to it's grandparent.
						if (previousOp.getCommand().equals(Ops.COPY) && 
								preSuperPara.getShapeID().equals(curSuperPara.getTargetID())) {
							curSuperPara.setTargetID(preSuperPara.getTargetID());
						}

						// Switch the position of the two operations
						editor.getOperationList().move(currentOp, false);
					}
				}
			}
		};
		return listener;
	}

	
	/**
	 * Method to check if it is a valid move to move currentOp above previousOp
	 * @param currentOp, the operation to move up
	 * @param previousOp, the operation it will be moved above
	 * @param editor
	 * @return true if the move is valid, false otherwise
	 */
	private boolean checkValidUp(PDOperation currentOp, PDOperation previousOp, DiagramEditor editor) {
		//variables
		PDSimpleSpatialInfo preSuperPara = (PDSimpleSpatialInfo) previousOp
		.getSuperParameter();
		PDSimpleSpatialInfo curSuperPara = (PDSimpleSpatialInfo) currentOp
		.getSuperParameter();

		//check if moving an operation above the new operation which created the 
		//shape it works on
		if (previousOp.getCommand().equals(Ops.NEW)
				&& preSuperPara.getShapeID().equals(curSuperPara.getShapeID())) {
			String message = "Cannot do operations before shape is created!";
			editor.statusBar.setError(message);
			return false;
		//check if moving a copy operation above the new operation which created the 
		//shape it copies
		} else if (previousOp.getCommand().equals(Ops.NEW)
				&& preSuperPara.getShapeID().equals(curSuperPara.getTargetID())) {
			String message = "Cannot do copy operations before shape is created!";
			editor.statusBar.setError(message);
			return false;
		}
		//otherwise it is a valid move
		return true;
	}

	/**
	 * Method to check if it is a valid move to move the currentOp below nextOp
	 * @param currentOp, the operation to move down
	 * @param nextOp, the operation it will be moved after
	 * @param editor
	 * @return true if the move is valid, false otherwise
	 */
	private boolean checkValidDown(PDOperation currentOp, PDOperation nextOp, DiagramEditor editor) {
		//operation information
		PDSimpleSpatialInfo nextSuperPara = (PDSimpleSpatialInfo) nextOp
		.getSuperParameter();
		PDSimpleSpatialInfo curSuperPara = (PDSimpleSpatialInfo) currentOp
		.getSuperParameter();

		//check if moving a new operation below an operation which works on the shape 
		//it produces
		if (currentOp.getCommand().equals(Ops.NEW)
				&& curSuperPara.getShapeID().equals(nextSuperPara.getShapeID())) {
			String message = "Cannot do operations before shape is created!";
			editor.statusBar.setError(message);
			return false;
		//check if moving a copy operation below an operation which works on the shape
		//it produced
		} else if (currentOp.getCommand().equals(Ops.NEW)
				&& curSuperPara.getShapeID().equals(nextSuperPara.getTargetID())) {
			String message = "Cannot do operations before shape is created!";
			editor.statusBar.setError(message);
			return false;
		}
		//otherwise it is a valid move
		return true;
	}

}
