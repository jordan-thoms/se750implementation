package diagrameditor.filters;

import javax.swing.DefaultListModel;

import diagrameditor.OperationList;
import diagrameditor.dal.PDOperation;

/**
 * Class to show all operations with no filter applied, 
 * implements Filter.
 */
public class NoFilter implements Filter{

	@Override
	public void display(OperationList operations, DefaultListModel listModel) {
		// Clear the list.
		listModel.removeAllElements();

		// No filter, add all operations to the list.
		for (PDOperation op : operations) {
			listModel.addElement(op);
		}
	}

	@Override
	public void setSelectedOp(PDOperation operation) {
		
	}

}
