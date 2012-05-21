package diagrameditor;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import diagrameditor.dal.PDOperation;
import diagrameditor.dal.PDSimpleSpatialInfo;
import diagrameditor.ops.EditorOperation;
import diagrameditor.ops.Ops;

/**
 * Class used to convert the operations from the format they were stored in the
 * database in into the format seen in the diagram editor and to change their
 * background colours to indicate commutative operations
 *
 */
public class HistoryPanelRenderer extends JLabel implements ListCellRenderer {
	private static final long serialVersionUID = -623360668768283832L;

	private Color[] rowColors = new Color[5];
	private DiagramEditor editor;
	/**
	 * Constructor method
	 */
	public HistoryPanelRenderer(DiagramEditor editor) {
		setOpaque(true);
		this.editor = editor;
		setUp();
	}

	/**
	 * Method to format the operations text and set their background colours.
	 */
	public Component getListCellRendererComponent(final JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		PDOperation operation = (PDOperation) value;
		PDSimpleSpatialInfo pds = (PDSimpleSpatialInfo) operation.getSuperParameter();
		
		EditorOperation command = editor.instantiateCommand(operation);
		setText(command.stringRepresentation(pds));
		
		/*
		String cmd = operation.getCommand();
		//change text displayed for each operation
		if (cmd.equals(Ops.COPY)) {
			setText(cmd + " ( " + pds.getTargetID() + " , " + pds.getShapeID() + " ) ");
		} else if (cmd.equals(Ops.NEW)) {
			setText(cmd + " ( " + pds.getShapeID() + " ) ");
		} else if (cmd.equals(Ops.COLOR)) {
			setText(cmd + " ( " + pds.getShapeID() + " , " + pds.getColor() + " ) ");
		} else if (cmd.equals(Ops.MOVE)) {
			setText(cmd + " ( " + pds.getShapeID() + " , " + "(" + pds.getX() + "," + pds.getY() + ")" + " ) ");
		} else if (cmd.equals(Ops.RESIZE)) {
			setText(cmd + " ( " + pds.getShapeID() + " , " + "(" + pds.getHeight() + "," + pds.getWidth() + ")"	+ " ) ");
		} else {
			throw new RuntimeException(
					"Need to implement a string representation for Commands of type " + cmd);
		}
		*/

		//change background of operations to highlight commutative operations
		PDOperation selected = getSelected(list);
		PDOperation op = (PDOperation)editor.drawPanel.listModel.get(index);
		List<PDOperation> dependent = new ArrayList<PDOperation>();
		if(selected != null){
			// Change to getFamily if want to view dependencies in both directions.
			dependent = editor.getOperationList().getDependents(selected);
			List<PDOperation> commutative = editor.getOperationList().commutative(selected);
			
			// Set background colours
			if(isSelected){
				//selected operation
				setBackground(rowColors[0]);
			} else if (dependent == null || !dependent.contains(op)) {
				if (commutative != null && commutative.contains(op)) {
					//operation is commutative but no dependent
					setBackground(rowColors[3]);
				} else {
					//operation not commutative and not dependent
					setBackground(list.getBackground());
				}
			} else if (dependent.contains(op)) {
				if (commutative != null && commutative.contains(op)) {
					//operation is dependent but not commutative
					//setBackground(rowColors[1]);
					setBackground(list.getBackground());
				} else {
					//operation is dependent and commutative
					//setBackground(rowColors[2]);
					setBackground(list.getBackground());
				}
			}
		}else{
			setBackground(list.getBackground());
		}
		//listener to check if a new operation has been selected
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				list.repaint();
			}
		});
		return this;
	}
	

	/**
	 * Method to get which operation is currently selected.
	 * @param list, the JList containg the history
	 * @return the PDOperation or null if no operation is
	 * currently selected
	 */
	private PDOperation getSelected(JList list){
		int index = list.getSelectedIndex();
		if (index == -1) {
			return null;
		} else {
			return (PDOperation) editor.drawPanel.listModel.elementAt(index);
		}
	}
	
	/**
	 * Method to set up the background colours and listen for changes
	 * in the selected item in the list in order to repaint backgrounds.
	 */
	private void setUp(){
		// TODO: choose correct colours
		// selected colour
		rowColors[0] = UIManager.getColor("ComboBox.selectionBackground");
		// is dependent and commutative
		rowColors[1] = new Color((float)0.576470, (float)0.576470, (float)1.0);
		// is dependent but not commutative
		rowColors[2] = new Color((float)0.576470, (float)0.796078, (float)0.901960);
		// not dependent but is commutative
		rowColors[3] = new Color((float)0.903921, (float)0.788235, (float)0.788235, (float)0.5);
		// not dependent or commutative
		rowColors[4] = Color.white;
	}
	
}
