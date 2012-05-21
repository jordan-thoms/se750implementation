package diagrameditor.filters;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import diagrameditor.OperationList;
import diagrameditor.dal.PDOperation;
import diagrameditor.dal.PDSimpleSpatialInfo;
import diagrameditor.ops.Ops;

/**
 * Class to create the filter which only shows operations which affect the
 * selected operations shape.
 */
public class ShapeFilter implements Filter{

	private boolean colour, move, resize;	//test if these variables have been set for the selected shape
	private String selectedShapeID;
	
	/**
	 * Method to add the operations which affect the shape of the selected
	 * operation to the listModel.
	 */
	@Override
	public void display(OperationList operations, DefaultListModel listModel) {
		
		// Clear list.
		listModel.removeAllElements();
		
		if(selectedShapeID != null){
			String inheritedShapeID = selectedShapeID;
			List<PDOperation> dep = operations.getFamily(selectedShapeID);
			
			colour = false;
			move = false;
			resize = false;
			
			/**
			 * Get all the copy and new operations from the list of dependencies
			 * and put them in a separate list, named tree.
			 */
			List<PDOperation> tree = new ArrayList<PDOperation>();
			for(PDOperation op: dep){
				if(op.getCommand().equals(Ops.NEW) || op.getCommand().equals(Ops.COPY)){
					tree.add(op);
				}
			}
				
			/**
			 * Loop through all the operations starting from the back/end/bottom.
			 * If they are included in the tree list add them to the model.
			 * If they reference the selected shape add them to the model.
			 * If they are a generalised operation inherited by the shape and 
			 * still affect the properties of the shape add them to the model.
			 */
			for(int i = operations.size()-1; i >= 0; i--){
				//Variables
				PDOperation op  = operations.get(i);
				PDSimpleSpatialInfo info = ((PDSimpleSpatialInfo)op.getSuperParameter());
				String opShapeID = info.getShapeID();
				
				if(tree.contains(op)){
					//If the tree list contains the operation add it to the model
					listModel.add(0, op);
					/**
					 * Check if the operation is a copy operation and has the inherited shape id as 
					 * its shapeID, if so then determine the shape which was targeted in the copy 
					 * operation. Check to see if colour, move or resize was set in relation to that
					 * targeted shape and has been inherited by the shape we filtered on
					 */
					if(op.getCommand().equals(Ops.COPY) && info.getShapeID().equals(inheritedShapeID)){
						inheritedShapeID = info.getTargetID();
					}
					
				}else if(opShapeID.equals(selectedShapeID) && !listModel.contains(op)){
					//If the operation references the selected shape add it to the model
					listModel.add(0, op);
					setParameter(op);
				}
				/**
				 * Check to operation to see if it is the most recent operation to set one of the 
				 * shape parameters, if it is add it to the model.
				 */
				if(opShapeID.equals(inheritedShapeID)){
					if(setParameter(op) && !listModel.contains(op)){
						listModel.add(0, op);
					}
				}
			}			
		} else {
			return;
		}
	}
	
	/**
	 * Method to determine if the given operation is the most recent operation to
	 * set a parameter of the shape, i.e. if this operation determines the shape
	 * colour, size or position.
	 * @param op, operation
	 * @return true if the operation is the first to set a parameter of the shape
	 */
	private boolean setParameter(PDOperation op){
		String command = op.getCommand();
		if(command.equals(Ops.COLOR) && colour == false){
			colour = true;
			return true;
		}else if(command.equals(Ops.MOVE) && move == false){
			move = true;
			return true;
		}else if(command.equals(Ops.RESIZE) && resize == false){
			resize = true;
			return true;
		}	
		return false;
	}
  
	@Override
	public void setSelectedOp(PDOperation selectedOp) {
		// No operation selected in the history.
		if (selectedOp == null) {
			selectedShapeID = null;
			return;
		}
		
		// Get the selected shape's ID.
		PDSimpleSpatialInfo pds = (PDSimpleSpatialInfo) selectedOp.getSuperParameter();
		selectedShapeID = pds.getShapeID();
	}
	
}
