package pdstore.ui.treeview;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import nz.ac.auckland.se.genoupe.tools.Debug;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDInstance;

public class PDTreeView extends JTree {
	private static final long serialVersionUID = -458975406734331705L;

	private PDStore store;
	private JPopupMenu popup;
	private AbstractAction rename, modify, add, refresh, remove, copy, paste,
			findNext, findPrev;

	/**
	 * Creates an editable tree view of instances and roles. The tree view uses
	 * metadata (types and roles) to enable type-safe editing of the data it
	 * shows.
	 * 
	 * @param store
	 *            the store to take the data for the tree view from
	 * @param rootInstances
	 *            the instances that are shown as roots in the tree view.
	 *            Several roots means several trees are shown.
	 */
	public PDTreeView(PDStore store, Object... rootInstances) {
		Debug.assertTrue(rootInstances != null && store != null,
				"The PDTreeView arguments must not be null.");

		/*
		 * Internally the tree view works only with GUIDs. So if a root instance
		 * is given as DAL object (subclass of PDInstance), then use its GUID
		 * instead.
		 */
		for (int i = 0; i < rootInstances.length; i++) {
			if (rootInstances[i] instanceof PDInstance)
				rootInstances[i] = ((PDInstance) rootInstances[i]).getId();
		}

		setModel(new PDTreeModel(rootInstances, store));
		this.store = store;

		popup = new JPopupMenu("Operations");
		// Register popup-menu actions
		rename = new AbstractAction("Rename") {
			@Override
			public void actionPerformed(ActionEvent e) {
				doRename();
			}
		};
		rename.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getAWTKeyStroke(KeyEvent.VK_F2, 0));
		modify = new AbstractAction("Modify") {
			@Override
			public void actionPerformed(ActionEvent e) {
				doModify();
			}
		};
		modify.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getAWTKeyStroke(KeyEvent.VK_F2, 0));
		add = new AbstractAction("Add") {
			@Override
			public void actionPerformed(ActionEvent e) {
				doAdd();
			}
		};
		remove = new AbstractAction("Remove") {
			@Override
			public void actionPerformed(ActionEvent e) {
				doRemove();
			}
		};
		remove.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getAWTKeyStroke(KeyEvent.VK_DELETE, 0));
		copy = new AbstractAction("Copy") {
			@Override
			public void actionPerformed(ActionEvent e) {
				doCopy();
			}
		};
		copy.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getAWTKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		paste = new AbstractAction("Paste") {
			@Override
			public void actionPerformed(ActionEvent e) {
				doPaste();
			}
		};
		paste.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getAWTKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		refresh = new AbstractAction("Refresh") {
			@Override
			public void actionPerformed(ActionEvent e) {
				doRefresh();
			}
		};
		refresh.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getAWTKeyStroke(KeyEvent.VK_F5, 0));
		findNext = new AbstractAction("Find next instance") {
			@Override
			public void actionPerformed(ActionEvent e) {
				doFind(true);
			}
		};
		findNext.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getAWTKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		findPrev = new AbstractAction("Find previous instance") {
			@Override
			public void actionPerformed(ActionEvent e) {
				doFind(false);
			}
		};
		findPrev.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getAWTKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK
						| InputEvent.SHIFT_MASK));

		setCellRenderer(new PDTreeRenderer());
		// register mouse listener to handle right-click popup menu interaction
		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					setSelectionRow(getRowForLocation(e.getX(), e.getY()));
					TreeNode selected = currentlySelectedNode();
					if (selected != null) {
						showPopup(selected, e.getX(), e.getY());
					}
				}
			}
		});
	}

	private PDTreeModel getPDTreeModel() {
		return (PDTreeModel) getModel();
	}

	private void doRename() {
		TreeNode selected = currentlySelectedNode();
		if (selected != null && selected instanceof ComplexNode) {
			ComplexNode selectedNode = (ComplexNode) selected;
			String oldName = selectedNode.getName();
			String newName = JOptionPane.showInputDialog(PDTreeView.this,
					"Enter new name", oldName);
			if (newName != null && !newName.isEmpty()
					&& !newName.equals(oldName)) {
				getPDTreeModel().rename(selectedNode, newName);
			}
		}
	}

	private void doModify() {
		TreeNode selected = currentlySelectedNode();
		if (selected != null && selected instanceof PrimitiveRoleNode) {
			PrimitiveRoleNode selectedNode = (PrimitiveRoleNode) selected;
			Object oldValue = selectedNode.getValue();
			// @TODO parse this value to appropriate type
			String newValue = JOptionPane.showInputDialog(PDTreeView.this,
					"Enter new value", oldValue);
			if (newValue != null && !newValue.isEmpty()
					&& !newValue.equals(oldValue)) {
				getPDTreeModel().changeValue(selectedNode, newValue);
			}
		}
	}

	private void doAdd() {
		TreeNode selected = currentlySelectedNode();
		if (selected != null && selected instanceof ComplexRoleNode) {
			ComplexRoleNode selectedRoleNode = (ComplexRoleNode) selected;
			// Create a new instance and register it
			GUID transaction = store.begin();
			// - determine the type of the owner of the role (i.e. the instance
			// to be created)
			GUID type = store.getOwnerType(transaction,
					selectedRoleNode.getRole());
			store.commit(transaction);
			getPDTreeModel().add((ComplexRoleNode) selected);
		}
	}

	private void doRemove() {
		TreeNode selected = currentlySelectedNode();
		if (selected != null && selected instanceof ComplexNode) {
			getPDTreeModel().remove((ComplexNode) selected);
		}
	}

	private void doCopy() {
		TreeNode selected = currentlySelectedNode();
		if (selected != null && selected instanceof ComplexNode) {
			getPDTreeModel().copy((ComplexNode) selected);
		}
	}

	private void doPaste() {
		TreeNode selected = currentlySelectedNode();
		if (selected != null && selected instanceof ComplexRoleNode) {
			getPDTreeModel().paste((ComplexRoleNode) selected);
		}
	}

	private void doRefresh() {
		TreeNode selected = currentlySelectedNode();
		if (selected != null && selected instanceof ComplexNode) {
			getPDTreeModel().refresh((ComplexNode) selected);
		} else if (selected != null && selected instanceof ComplexRoleNode) {
			getPDTreeModel().refresh((ComplexRoleNode) selected);
		}
	}

	private void doFind(boolean forward) {
		TreePath matchPath = getSelectionPath();
		if (matchPath != null) {
			Object pathEnd = matchPath.getLastPathComponent();
			if (pathEnd instanceof ComplexNode) {
				ComplexNode next = getPDTreeModel().find((ComplexNode) pathEnd,
						forward);
				if (next != null) {
					matchPath = new TreePath(getPDTreeModel().getPathToRoot(
							next));
					setSelectionPath(matchPath);
				} else {
					JOptionPane.showMessageDialog(this,
							"No instance found after this node");
				}
			}
		}
	}

	private void showPopup(TreeNode selected, int x, int y) {
		// The content of the popup menu depends on the node type: instance or
		// role
		if (selected instanceof ComplexNode) {
			ComplexNode selectedIN = (ComplexNode) selected;
			if (selectedIN.isLeaf())
				return;
			popup.removeAll();
			popup.add(rename);
			popup.add(copy);
			popup.add(remove);
			popup.addSeparator();
			popup.add(findNext);
			popup.add(findPrev);
			popup.addSeparator();
			popup.add(refresh);

			// remove doesn't apply to the top-most instance node (which has
			// PDRootNode as parent
			remove.setEnabled(selectedIN.getParent() instanceof ComplexRoleNode);
			popup.show(this, x, y);
		} else if (selected instanceof PrimitiveRoleNode) {
			popup.removeAll();
			popup.add(modify);
			popup.show(this, x, y);
		} else if (selected instanceof ComplexRoleNode) {
			ComplexRoleNode selectedRN = (ComplexRoleNode) selected;
			popup.removeAll();
			GUID role = selectedRN.getRole();
			GUID transaction = store.begin();
			GUID objectType = store.getOwnerType(transaction, role);
			String typeName = store.getName(transaction, objectType);
			store.commit(transaction);
			// customise "add" text according to expected type.
			popup.add(add);
			popup.add(paste);
			popup.addSeparator();
			popup.add(refresh);
			add.setEnabled(true);
			add.putValue(Action.NAME, "Add new '" + typeName + "'");
			// Restrict paste based on type of clipboard item (must equal to the
			// expected type)
			paste.setEnabled(false);
			Object clipboardItem = getPDTreeModel().getClipboardItem();
			if (clipboardItem != null) {
				transaction = store.begin();
				String clipboardItemName = store.getName(transaction,
						clipboardItem);
				GUID clipboardItemType = store.getType(transaction,
						clipboardItem);
				String clipboardItemTypeName = store.getName(transaction,
						clipboardItemType);
				store.commit(transaction);
				paste.setEnabled(objectType.equals(clipboardItemType));
				paste.putValue(Action.NAME, "Paste " + clipboardItemTypeName
						+ " [" + clipboardItemName + "]");
			}
			popup.show(this, x, y);
		}
	}

	private TreeNode currentlySelectedNode() {
		TreePath path = getSelectionPath();
		if (path != null) {
			Object selected = path.getLastPathComponent();
			if (selected instanceof TreeNode) {
				return (TreeNode) selected;
			}
		}
		return null;
	}
}
