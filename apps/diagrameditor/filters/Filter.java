package diagrameditor.filters;

import javax.swing.DefaultListModel;

import diagrameditor.OperationList;
import diagrameditor.dal.PDOperation;

/**
 * An interface defining method which are required to be implemented
 * by each filter class.
 *
 */
public interface Filter{
	
	void setSelectedOp(PDOperation operation);
	void display(OperationList operations, DefaultListModel listModel);
	
}
