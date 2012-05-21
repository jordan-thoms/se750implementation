package pdtransform;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

import pdstore.dal.PDWorkingCopy;

import java.awt.dnd.*;

/**
 * A wrapper class for a JTree that expands on a regular JTree by adding Drag
 * and Drop functionality
 * 
 * @author Gyurme Dahdul
 * 
 */
public class DNDTree extends JTree {

	Insets autoscrollInsets = new Insets(20, 20, 20, 20);
	protected Hashtable<DefaultMutableTreeNode, Object> TreeToPD;
	protected PDWorkingCopy cache;

	public DNDTree(DefaultMutableTreeNode root,
			Hashtable<DefaultMutableTreeNode, Object> TreeToPD,
			PDWorkingCopy cache) {
		setAutoscrolls(true);
		DefaultTreeModel treemodel = new DefaultTreeModel(root);
		setModel(treemodel);
		setRootVisible(true);
		setShowsRootHandles(false);
		getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		setEditable(false);
		// Enables Drag and Drop by adding in a handler for it that has drag and
		// drop methods
		new DefaultTreeTransferHandler(this, DnDConstants.ACTION_COPY_OR_MOVE);
		this.TreeToPD = TreeToPD;
		this.cache = cache;
	}

	public void autoscroll(Point cursorLocation) {
		Insets insets = getAutoscrollInsets();
		Rectangle outer = getVisibleRect();
		Rectangle inner = new Rectangle(outer.x + insets.left, outer.y
				+ insets.top, outer.width - (insets.left + insets.right),
				outer.height - (insets.top + insets.bottom));
		if (!inner.contains(cursorLocation)) {
			Rectangle scrollRect = new Rectangle(
					cursorLocation.x - insets.left, cursorLocation.y
							- insets.top, insets.left + insets.right,
					insets.top + insets.bottom);
			scrollRectToVisible(scrollRect);
		}
	}

	public Insets getAutoscrollInsets() {
		return (autoscrollInsets);
	}

	public static DefaultMutableTreeNode makeDeepCopy(
			DefaultMutableTreeNode node) {
		DefaultMutableTreeNode copy = new DefaultMutableTreeNode(
				node.getUserObject());
		for (Enumeration e = node.children(); e.hasMoreElements();) {
			copy.add(makeDeepCopy((DefaultMutableTreeNode) e.nextElement()));
		}
		return (copy);
	}

	public static DefaultMutableTreeNode createTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("node1");
		DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("node2");
		root.add(node1);
		root.add(node2);
		node1.add(new DefaultMutableTreeNode("sub1_1"));
		node1.add(new DefaultMutableTreeNode("sub1_2"));
		node1.add(new DefaultMutableTreeNode("sub1_3"));
		node2.add(new DefaultMutableTreeNode("sub2_1"));
		node2.add(new DefaultMutableTreeNode("sub2_2"));
		node2.add(new DefaultMutableTreeNode("sub2_3"));
		return (root);
	}
}
