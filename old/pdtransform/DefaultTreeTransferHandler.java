package pdtransform;

import java.awt.*;
import javax.swing.tree.*;

import pdstore.dal.PDInstance;
import pdstore.dal.PDRole;
import pdstore.dal.PDType;

import java.awt.dnd.*;

/**
 * Ensures the drag and drop operations on the JTrees conform to the structure
 * of a PDModel e.g. impossible operations like dropping mappings on types re
 * prevented
 * 
 * @author Gyurme Dahdul and Philip Booth
 * 
 */
public class DefaultTreeTransferHandler extends AbstractTreeTransferHandler {

	public DefaultTreeTransferHandler(DNDTree tree, int action) {
		super(tree, action, true);
	}

	/**
	 * Checks if drag and drop operation is valid by checking the type of
	 * dragged node and the node it is being dropped on. If this operation is
	 * allowed by the PD Model return true, otherwise return false.
	 * 
	 * @param target
	 *            - the Jtree
	 * @param draggedNode
	 *            - the node being dragged
	 * @param action
	 *            - the type of action
	 * @param location
	 *            - the point where it is dropped
	 * @return - true if action is possible else false
	 */
	public boolean canPerformAction(DNDTree target,
			DefaultMutableTreeNode draggedNode, int action, Point location) {
		TreePath pathTarget = target.getPathForLocation(location.x, location.y);

		if (pathTarget == null) {
			target.setSelectionPath(null);
			return (false);
		}
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) pathTarget
				.getLastPathComponent();
		if (target.TreeToPD.get(parentNode) instanceof PDRole) {
			// Should be instance
			PDInstance ins = (PDInstance) target.TreeToPD.get(draggedNode);
			// Should be role
			PDRole role = (PDRole) target.TreeToPD.get(parentNode);

			PDType insType = role.getPartner().getAccessor();

			if (ins.getTypeId() == insType.getId())
				return true;
			else
				return false;
		} else
			return false;
	}

	/**
	 * Defines what happens when a drop is successfully executed
	 * 
	 * @param tree
	 *            - the Jtree
	 * @param draggedNode
	 *            - the node being dragged
	 * @param newParentNode
	 *            - the node that is being dropped on
	 * @param action
	 *            - the type of action
	 * @return - true if drop can be executed else false
	 */
	public boolean executeDrop(DNDTree target,
			DefaultMutableTreeNode draggedNode,
			DefaultMutableTreeNode newParentNode, int action) {
		if (action == DnDConstants.ACTION_MOVE) {
			draggedNode.removeFromParent();
			((DefaultTreeModel) target.getModel()).insertNodeInto(draggedNode,
					newParentNode, newParentNode.getChildCount());
			TreePath treePath = new TreePath(draggedNode.getPath());
			target.scrollPathToVisible(treePath);
			// target.setSelectionPath(treePath);
			
			// Update PDModel
			PDInstance pdnode = (PDInstance) target.TreeToPD.get(draggedNode);
			
			// Find parent and add
			PDRole role = (PDRole) target.TreeToPD.get(newParentNode);
			PDInstance parent = (PDInstance) target.TreeToPD.get(newParentNode
					.getParent());

			parent.getPDWorkingCopy().addLink(parent.getTypeId(), role.getId(),
					pdnode.getId());

			target.cache.commit();
			target.expandPath(treePath.pathByAddingChild(newParentNode));
			return true;
		}
		return false;
	}
}
