package diagrameditor;

import java.util.ArrayList;
import java.util.List;

import pdstore.GUID;
import pdstore.dal.PDInstance;
import pdstore.generic.GenericLinkedList;
import diagrameditor.dal.PDOperation;
import diagrameditor.dal.PDSimpleSpatialInfo;
import diagrameditor.ops.EditorOperation;
import diagrameditor.ops.Ops;

/**
 * Extends GenericLinkedList and implements diagram editor 
 * specific methods.
 * 
 */
public class OperationList extends
		GenericLinkedList<GUID, Object, GUID, PDOperation> {

	private DiagramEditor editor;
	private boolean debug = false;
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param javaType
	 * @param parentInstance
	 * @param collectionRole
	 * @param element
	 * @param nextRole
	 */
	public OperationList(Class<PDOperation> javaType,
			PDInstance parentInstance, GUID collectionRole, GUID element,
			GUID nextRole) {
		super(javaType, parentInstance, collectionRole, element, nextRole);
		this.editor = null;
	}

	/**
	 * Constructor
	 * 
	 * @param javaType
	 * @param parentInstance
	 * @param collectionRole
	 * @param element
	 * @param nextRole
	 * @param editor
	 */
	public OperationList(Class<PDOperation> javaType,
			PDInstance parentInstance, GUID collectionRole, GUID element,
			GUID nextRole, DiagramEditor editor) {
		super(javaType, parentInstance, collectionRole, element, nextRole);
		this.editor = editor;
	}

	/**
	 * Method to move an operation up or down in the list. Will jump commutative
	 * blocks of operations.
	 * 
	 * @param op
	 * @param down
	 */
	public void move(PDOperation op, boolean down) {
		int steps = 1;
		//get number of operations to jump, affect my commutative neighbourhoods
		if (down) {
			steps = getDownCommutative(op).size();
		} else {
			steps = getUpCommutative(op).size();
		}
		//minimum step size if no adjacent commutative neighbourhood
		if( steps < 1){
			steps = 1;
		}
		//remove selected operation
		int index = editor.getOperationList().indexOf(op);
		editor.getOperationList().remove(index);

		//add selected operation into new position
		if (down) {
			editor.getOperationList().add(index + steps, op);
		} else {
			editor.getOperationList().add(index - steps, op);
		}
	}
	
	/**
	 * Method to determine the operations which need to be deleted when
	 * deleting the selected operation (cascading delete)
	 * @param operation, the operation being deleted
	 * @return a list of operations which also need to be deleted when deleting
	 * the selected operation
	 */
	public List<PDOperation> getDeleteOperations(PDOperation operation){
		List<PDOperation> dep = new ArrayList<PDOperation>();
		dep.add(operation);
		List<String> opsToCheck = new ArrayList<String>();
		 
		//list of operations which we check all other operations for dependency on
		opsToCheck.add(((PDSimpleSpatialInfo) operation.getSuperParameter()).getShapeID());
		
		//loop through all operations
		for (PDOperation op : editor.getOperationList()) {
			String opId = null;
			EditorOperation command = editor.instantiateCommand(op);
			
			PDSimpleSpatialInfo pds = (PDSimpleSpatialInfo) op.getSuperParameter();
			opId = command.checkId(pds);
			
			/*
			//get the shape of the operation, or if operation is a copy then the target id
			if (op.getCommand().equals(Ops.COPY)) {
				opId = ((PDSimpleSpatialInfo) op.getSuperParameter()).getTargetID();
		 	} else {
		 		opId = ((PDSimpleSpatialInfo) op.getSuperParameter()).getShapeID();
		 	}
		 	*/
		
			//if operation is not currently in the dependent list and it's shape id links
			//to an operation on the check list add it to the list of dependent operations
			if (opsToCheck.contains(opId) && !dep.contains(op)) {
				dep.add(op);
				if (op.getCommand().equals(Ops.COPY)) {
					opsToCheck.add(((PDSimpleSpatialInfo) op.getSuperParameter()).getShapeID());			
				}
			}
		}
		//return list of dependent operations
 		return dep;
	}

	/**
	 * Method to return a list of PDOperations which have a dependency between
	 * the selected operation's shape and themselves
	 * 
	 * @param operation, the operation to find the dependencies for
	 * @return a list of PDOperations which contain a dependency to the given
	 *         operation, including the given operation.
	 */
	public List<PDOperation> getFamily(PDOperation operation) {
		List<PDOperation> dep = new ArrayList<PDOperation>();

		// List of shapes that are part of the same dependency tree as the selected operation.
		ArrayList<String> shapesToFilter = new ArrayList<String>();

		if (operation != null) {
			PDOperation tempOp = (PDOperation) operation;	
			PDSimpleSpatialInfo pds = (PDSimpleSpatialInfo) tempOp.getSuperParameter();
			
			String shapeID = null;
			// If the selected operation is a Copy, the parent shape's ID is the targetID
			if (tempOp.getCommand().equals(Ops.COPY)) {
				shapeID = pds.getTargetID();
			} else {
				shapeID = pds.getShapeID();
			}
			
			// If the selected operation is a New, then it is the root shape of the tree.
			// If its not a New, then find the root shape.
			if (!tempOp.getCommand().equals(Ops.NEW)) {
				
				while(tempOp.getPrevious() != null) {
					tempOp = tempOp.getPrevious();
					
					// If the operation is a New on the hierarchical path, 
					// then it is the root shape of the tree.
					if (tempOp.getCommand().equals(Ops.NEW)) {
						PDSimpleSpatialInfo temppds = (PDSimpleSpatialInfo) tempOp.getSuperParameter();
						if (temppds.getShapeID().equals(shapeID)) {
							break;
						}
					} // If the operation is a Copy on the hierarchical path, 
					// continue looking using the targetID
					else if (tempOp.getCommand().equals(Ops.COPY)) {
						PDSimpleSpatialInfo temppds = (PDSimpleSpatialInfo) tempOp.getSuperParameter();
						if (temppds.getShapeID().equals(shapeID)) {
							shapeID = temppds.getTargetID();
						}	
					}
				}
			}
			
			PDSimpleSpatialInfo temppds = (PDSimpleSpatialInfo) tempOp.getSuperParameter();
			shapesToFilter.add(temppds.getShapeID());
			
			dep.add(tempOp);
			while(tempOp.getNext() != null) {
				tempOp = tempOp.getNext();
				temppds = (PDSimpleSpatialInfo) tempOp.getSuperParameter();
				
				if (shapesToFilter.contains(temppds.getShapeID())) {
					dep.add(tempOp);
				} else if (tempOp.getCommand().equals(Ops.COPY) && shapesToFilter.contains(temppds.getTargetID())) {
					shapeID = temppds.getShapeID();
					shapesToFilter.add(shapeID);
					dep.add(tempOp);
				}
			}
		}
		//return list of dependent operations
		return dep;
	}

	/**
	 * Method to get the list of operations which part of the same dependency
	 * tree as the selected shape
	 * @param selectedShapeID, the slected shape ID
	 * @return a list of operations which are part of the shape's dependency
	 * tree
	 */
	public List<PDOperation> getFamily(String selectedShapeID) {
		List<PDOperation> dep = new ArrayList<PDOperation>();

		// List of shapes that are part of the same dependency tree as the selected operation.
		ArrayList<String> shapesToFilter = new ArrayList<String>();

		if (this.size() != 0) {
			PDOperation tempOp = null;				
			String shapeID = null;
			
			//find the operation which created the selected shape
			for (PDOperation op : this) {
				PDSimpleSpatialInfo pds = (PDSimpleSpatialInfo) op.getSuperParameter();
				shapeID = pds.getShapeID();
				if (shapeID.equals(selectedShapeID)) {
					tempOp = op;
				}
			}
			
			if(tempOp == null) {
				return dep;
			}
			
			// If the selected operation is a New, then it is the root shape of the tree.
			// If its not a New, then find the root shape.
			if (!tempOp.getCommand().equals(Ops.NEW)) {
				
				while(tempOp.getPrevious() != null) {
					tempOp = tempOp.getPrevious();
					
					// If the operation is a New on the hierarchical path, 
					// then it is the root shape of the tree.
					if (tempOp.getCommand().equals(Ops.NEW)) {
						PDSimpleSpatialInfo temppds = (PDSimpleSpatialInfo) tempOp.getSuperParameter();
						if (temppds.getShapeID().equals(shapeID)) {
							break;
						}
					} // If the operation is a Copy on the hierarchical path, 
					// continue looking using the targetID
					else if (tempOp.getCommand().equals(Ops.COPY)) {
						PDSimpleSpatialInfo temppds = (PDSimpleSpatialInfo) tempOp.getSuperParameter();
						if (temppds.getShapeID().equals(shapeID)) {
							shapeID = temppds.getTargetID();
						}	
					}
				}
			}
			
			PDSimpleSpatialInfo temppds = (PDSimpleSpatialInfo) tempOp.getSuperParameter();
			shapesToFilter.add(temppds.getShapeID());
			
			dep.add(tempOp);
			while(tempOp.getNext() != null) {
				tempOp = tempOp.getNext();
				temppds = (PDSimpleSpatialInfo) tempOp.getSuperParameter();
				
				if (shapesToFilter.contains(temppds.getShapeID())) {
					dep.add(tempOp);
				} else if (tempOp.getCommand().equals(Ops.COPY) && shapesToFilter.contains(temppds.getTargetID())) {
					shapeID = temppds.getShapeID();
					shapesToFilter.add(shapeID);
					dep.add(tempOp);
				}
			}
		}
		//return list of dependent operations
		return dep;
	}
	
	/**
	 * Method to return a list of PDOperations which depend on the given
	 * operation.
	 * 
	 * @param operation
	 * @return a list of operations which are dependent upon the given operation
	 */
	public List<PDOperation> getDependents(PDOperation operation) {
		// Given operation must be a new or copy operation to have other
		// operations depending on it.
		if (!operation.getCommand().equals(Ops.NEW)
				&& !operation.getCommand().equals(Ops.COPY)) {
			return null;
		}
		List<PDOperation> dep = new ArrayList<PDOperation>();
		List<String> opsToCheck = new ArrayList<String>();

		opsToCheck.add(((PDSimpleSpatialInfo) operation.getSuperParameter())
				.getShapeID());

		for (PDOperation op : editor.getOperationList()) {
			String opId = null;
			//get shape of operation, or if operation is a copy the target id
			if (op.getCommand().equals(Ops.COPY)) {
				opId = ((PDSimpleSpatialInfo) op.getSuperParameter())
						.getTargetID();
			} else {
				opId = ((PDSimpleSpatialInfo) op.getSuperParameter())
						.getShapeID();
			}
			//if operation is not currently in the dependent list and it's id links
			//to an operation on the check list add it to the list of dependent operations
			if (opsToCheck.contains(opId) && !dep.contains(op)) {
				dep.add(op);
				if (op.getCommand().equals(Ops.COPY)) {opsToCheck.add(((PDSimpleSpatialInfo)
						op.getSuperParameter()).getShapeID());
				}
			}
		}
		return dep;
	}

	/**
	 * Method to find the commutative neighbourhood above the selected operation.
	 * An operation is commutative with the given operation if the order of their
	 * execution does not affect the resultant diagram.
	 * 
	 * @param selected, the selected PDOperation
	 * @return list containing the operations in the commutative neighbourhood.
	 */
	public List<PDOperation> getUpCommutative(PDOperation selected) {
		List<PDOperation> commutative = new ArrayList<PDOperation>();

		int position = editor.getOperationList().indexOf(selected);
		for (int i = position - 1; i >= 0; i--) {
			//find consecutive commutative operations above the selected operation
			if (isCommutative(editor.getOperationList().get(i), selected)) {
				commutative.add(editor.getOperationList().get(i));
			} else {
				return commutative;
			}
		}
		//return list of operations in the commutative neighbourhood
		return commutative;
	}

	/**
	 * Method to find the commutative neighbourhood below the selected operation.
	 * An operation is commutative with the given operation if the order of their
	 * execution does not affect the resultant diagram.
	 * 
	 * @param selected, the selected PDOperation
	 * @return list containing the operations in the commutative neighbourhood.
	 */
	public List<PDOperation> getDownCommutative(PDOperation selected) {
		List<PDOperation> commutative = new ArrayList<PDOperation>();

		int position = editor.getOperationList().indexOf(selected);
		for (int i = position + 1; i < editor.getOperationList().size(); i++) {
			//find consecutive commutative operations above the selected operation
			if (isCommutative(selected, editor.getOperationList().get(i))) {
				commutative.add(editor.getOperationList().get(i));
			} else {
				return commutative;
			}
		}
		//return list of operations in the commutative neighbourhood
		return commutative;
	}

	/**
	 * Method to return a list of PDOperations which are commutative with the
	 * selected operation. An operation is commutative with the given
	 * operation if the order of their execution does not affect the resultant
	 * diagram.
	 * 
	 * @param selected, the selected operation
	 * @return a list of PDOperations which are commutative operations.
	 */
	public List<PDOperation> commutative(PDOperation selected) {
		//find above commutative neighbourhood
		List<PDOperation> commutative = getUpCommutative(selected);
		//add below commutative neighbourhood
		commutative.addAll(getDownCommutative(selected));
		return commutative;
	}

	/**
	 * Method to determine if two operations are commutative with one another.
	 * An operation is commutative with the given operation if the order of their
	 * execution does not affect the resultant diagram.
	 * 
	 * @param op1, the above operation
	 * @param op2, the below operation
	 * @return
	 */
	public boolean isCommutative(PDOperation op1, PDOperation op2) {
		//Get operation information
		PDSimpleSpatialInfo op1Info = ((PDSimpleSpatialInfo) op1
				.getSuperParameter());
		PDSimpleSpatialInfo op2Info = ((PDSimpleSpatialInfo) op2
				.getSuperParameter());

		if (debug) {
			System.out.println(op1.getCommand() + " " + op1Info.getShapeID());
			System.out.println(op2.getCommand() + " " + op2Info.getShapeID());
			if (op1.getCommand().equals(Ops.COPY)) {
				System.out.println(op1Info.getTargetID());
			}
		}
		// test if they are the same operations
		if (op1.getCommand().equals(op2.getCommand())
				&& op1Info.getShapeID().equals(op2Info.getShapeID())) {
			return false;
		}
		// if operation 2 is dependent on operation 2 they are not commutative
		if (getDependents(op1) != null && getDependents(op1).contains(op2)) {
			return false;
		}
		// test if they are shape disjoint
		// if both operations are copies:
		if (op1.getCommand().equals(Ops.COPY)
				&& op2.getCommand().equals(Ops.COPY)) {
			// if the lower copy is not dependent upon the upper copy
			if (op1Info.getShapeID() != op2Info.getTargetID()
					&& op1Info.getTargetID() != op2Info.getShapeID()) {
				return true;
			} else {
				return false;
			}
		//if op1 is a copy
		} else if (op1.getCommand().equals(Ops.COPY)
				&& !op2.getCommand().equals(Ops.COPY)) {
			if (op2Info.getShapeID() != op1Info.getShapeID()
					&& op2Info.getShapeID() != op1Info.getTargetID()) {
				return true;
			} else {
				return false;
			}
		//if op2 is a copy
		} else if (!op1.getCommand().equals(Ops.COPY)
				&& op2.getCommand().equals(Ops.COPY)) {
			if (op1Info.getShapeID() != op2Info.getTargetID()
					&& op1Info.getShapeID() != op2Info.getShapeID()) {
				return true;
			} else {
				return false;
			}
		// neither operation is a copy operation
		} else {
			if (op1Info.getShapeID() != op2Info.getShapeID()) {
				return true;
			}
		}

		// test if type disjoint and neither operation is a new operation
		if (!op1.getCommand().equals(op2.getCommand())
				&& !op1.getCommand().equals(Ops.NEW)
				&& !op2.getCommand().equals(Ops.NEW)) {
			return true;
		}
		return false;
	}
}
