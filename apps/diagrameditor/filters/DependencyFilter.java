package diagrameditor.filters;

import java.util.List;

import javax.swing.DefaultListModel;

import diagrameditor.OperationList;
import diagrameditor.dal.PDOperation;
import diagrameditor.dal.PDSimpleSpatialInfo;
import diagrameditor.ops.Ops;
/**
 * Class to create the filters which only shows the operations in the dependency
 * tree of the selected operation, implements Filter.
 *
 */
public class DependencyFilter implements Filter {
	private String rootShapeID;
	
	/**
	 * Method to add the operations in the dependency tree of the selected
	 * operation to the listModel.
	 */
	@Override
	public void display(OperationList operations, DefaultListModel listModel) {
		// Clear list.
		listModel.removeAllElements();
		
		if(rootShapeID != null) {
			List<PDOperation> ops = operations.getFamily(rootShapeID);
	
			for (PDOperation op : ops) {
				listModel.addElement(op);
			}
		}
	}

	/**
	 * Method to change the selected operation
	 */
	@Override
	public void setSelectedOp(PDOperation selectedOp) {
		// No operation selected in the history.
		if (selectedOp == null) {
			rootShapeID = null;
			return;
		}
		
		//TODO: move to OperationList
		
		// Find the root of the selected shape in the history.
		// This will be used to display the entire dependency tree.
			
		PDSimpleSpatialInfo pds = (PDSimpleSpatialInfo) selectedOp.getSuperParameter();
		if (selectedOp.getCommand().equals(Ops.NEW)) {
			rootShapeID = pds.getShapeID();
			return;
		} else if (selectedOp.getCommand().equals(Ops.COPY)) {
			// If the operation is a Copy, the parent shape's ID is the targetID.
			rootShapeID = pds.getTargetID();
		} else {
			rootShapeID = pds.getShapeID();
		}
		
		// If the selected operation is a New, then it is the root shape of the tree.
		// If its not a New, then find the root shape.
		while (selectedOp.getPrevious() != null) {
			selectedOp = selectedOp.getPrevious();
			pds = (PDSimpleSpatialInfo) selectedOp.getSuperParameter();
			
			// If the operation is a New on the hierarchical path, 
			// then it is the root shape of the tree.
			if (selectedOp.getCommand().equals(Ops.NEW)) {
				if (pds.getShapeID().equals(rootShapeID)) {
					rootShapeID = pds.getShapeID();
					break;
				}
			} 
			// If the operation is a Copy on the hierarchical path, 
			// continue looking using the targetID
			else if (selectedOp.getCommand().equals(Ops.COPY)) {
				if (pds.getShapeID().equals(rootShapeID)) {
					rootShapeID = pds.getTargetID();
				}	
			}
		}
	}
}
