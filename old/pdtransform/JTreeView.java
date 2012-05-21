package pdtransform;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.sun.tools.hat.internal.model.Root;

import alm.ALMLayout;
import alm.PropertiesWindow;
import avm.dal.PDWidget;

import pdstore.Blob;
import pdstore.dal.PDInstance;
import pdstore.dal.PDRole;
import pdstore.dal.PDType;
import pdstore.dal.PDWorkingCopy;
import pdtransform.dal.PDMap;

/**
 * Responsible for the creation and display of the JTree View of the input
 * model,output template and final output model. Initializes a right click popup
 * menu that allows direct manipulation of the models.
 * 
 * @author Philip Booth and Gyurme Dahdul
 * 
 */

public class JTreeView implements TreeView, TreeModelListener,
		TreeWillExpandListener {

	private static final long serialVersionUID = 1L;
	private DNDTree tree;

	// Used to maintain a relation between the TreeNodes and PDNodes.
	// Enables update of the model from the tree
	private Hashtable<Object, DefaultMutableTreeNode> PDtoTree = new Hashtable<Object, DefaultMutableTreeNode>();
	protected Hashtable<DefaultMutableTreeNode, Object> TreeToPD = new Hashtable<DefaultMutableTreeNode, Object>();

	private JPopupMenu popup;
	private PDWorkingCopy cache;
	@SuppressWarnings("unused")
	private PDInstance application;
	private PopupHandler handler;

	private PropertiesWindow propertiesWindow;
	private ALMLayout alm;

	public JTreeView(PDInstance root, PDWorkingCopy pdcache,
			PDInstance application) {
		this(root, pdcache, application, null);
	}

	public JTreeView(PDInstance root, PDWorkingCopy pdcache,
			PDInstance application, PropertiesWindow properties) {
		// super(new GridLayout(1,0));

		cache = pdcache;
		this.application = application;
		this.propertiesWindow = properties;
		alm = propertiesWindow.getALMEditor().getALMEngine();

		// Create the nodes.
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(root.getName());

		PDtoTree.put(root, top);
		TreeToPD.put(top, root);

		// Create a tree that allows one selection at a time.
		tree = new DNDTree(top, TreeToPD, cache);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Set to editable for the AVM spec editor.
		tree.setEditable(true);
		tree.getModel().addTreeModelListener(this);

		// Add the popup menu
		popup = new JPopupMenu();
		popup.setInvoker(tree);
		handler = new PopupHandler(tree, popup, TreeToPD, cache, application,
				alm);

		// Add the options on the popup menu
		popup.add(getMenuItem("Cut", handler));
		popup.add(getMenuItem("Copy", handler));
		popup.add(getMenuItem("Paste", handler));
		popup.add(new JPopupMenu.Separator());
		popup.add(getMenuItem("Insert new", handler));
		popup.add(getMenuItem("Remove", handler));
		// popup.add(new JPopupMenu.Separator());
		// popup.add(getMenuItem("Transform", handler));
		// popup.add(getMenuItem("Serialize", handler));

		DefaultTreeCellRenderer cellRenderer = new IconRenderer();
		tree.setCellRenderer(cellRenderer);
		tree.setCellEditor(new SelectiveTreeCellEditor(tree, cellRenderer));
	}

	/**
	 * Gets a new object from pdtransform and adds it to the tree
	 * 
	 * @author Philip Booth
	 */
	public void addNode(PDInstance parent, Object child, PDRole relation) {
		DefaultMutableTreeNode childNode = null;
		DefaultMutableTreeNode parentNode = null;
		DefaultMutableTreeNode roleNode = null;

		if (child instanceof PDInstance) {
			childNode = new DefaultMutableTreeNode(
					((PDInstance) child).getName());
			PDtoTree.put(child, childNode);
			TreeToPD.put(childNode, child);
		} else {
			if (child instanceof String) {
				childNode = new DefaultMutableTreeNode(child);
				TreeToPD.put(childNode, child);
			} else if (child != null && child != "")
				childNode = new DefaultMutableTreeNode(child);
		}

		parentNode = PDtoTree.get(parent);

		// Try to find the existing role node for this relation
		Enumeration en = parentNode.children();
		while (en.hasMoreElements()) {
			DefaultMutableTreeNode node;
			node = (DefaultMutableTreeNode) en.nextElement();
			if (relation.getName().equals(node.getUserObject())) {
				roleNode = node;
			}
		}

		// There is not a role node for this relation yet. Create a
		// role node for this relation.
		if (roleNode == null) {
			roleNode = new DefaultMutableTreeNode(relation.getName());
			parentNode.add(roleNode);
		}

		roleNode.add(childNode);

		PDtoTree.put(relation, roleNode);
		TreeToPD.put(roleNode, relation);
	}

	/**
	 * Specialist method for PDTransform to add maps to the model
	 * 
	 * @author Philip Booth
	 */
	@SuppressWarnings("static-access")
	public void addMaps(ArrayList<PDMap> maps, PDInstance parent) {
		DefaultMutableTreeNode parentNode = PDtoTree.get(parent);
		DefaultMutableTreeNode mapNode, inputR, inputI, outputR, linkNode, inputType, inputRole;

		for (PDMap map : maps) {
			linkNode = new DefaultMutableTreeNode("mapping");
			mapNode = new DefaultMutableTreeNode(map.getName());
			inputI = new DefaultMutableTreeNode("input type");
			inputR = new DefaultMutableTreeNode("input role");
			outputR = new DefaultMutableTreeNode("output role");

			inputType = new DefaultMutableTreeNode(map.getInputType().getName());
			inputRole = new DefaultMutableTreeNode(map.getInputRole().getName());

			inputI.add(inputType);
			inputR.add(inputRole);

			TreeToPD.put(inputType, map.getInputType());
			TreeToPD.put(inputRole, map.getInputRole());

			for (PDRole outputRole : map.getOutputRoles()) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(
						outputRole.getName());
				outputR.add(node);

				TreeToPD.put(node, outputRole);
			}

			mapNode.add(inputI);
			mapNode.add(inputR);
			mapNode.add(outputR);

			parentNode.add(linkNode);
			linkNode.add(mapNode);

			PDRole inputrole = (PDRole) cache.load(PDRole.typeId,
					map.roleInputRoleId);
			TreeToPD.put(inputR, inputrole);
			PDRole inputinst = (PDRole) cache.load(PDRole.typeId,
					map.roleInputTypeId);
			TreeToPD.put(inputI, inputinst);
			PDRole outputRole = (PDRole) cache.load(PDRole.typeId,
					map.roleOutputRoleId);
			TreeToPD.put(outputR, outputRole);

			TreeToPD.put(mapNode, map);
		}
	}

	/**
	 * Adds a empty role to the tree. Empty roles are necessary to enable
	 * building of the tree
	 * 
	 * @author Philip Booth
	 */
	public void addEmptyRole(PDRole role, PDInstance parent) {
		DefaultMutableTreeNode parentNode, roleNode;

		roleNode = new DefaultMutableTreeNode(role.getName());
		parentNode = PDtoTree.get(parent);
		parentNode.add(roleNode);
		PDtoTree.put(role, roleNode);
		TreeToPD.put(roleNode, role);

	}

	public void expand() {
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	}

	public void showGUI() {

		// Create and set up the window.
		JFrame frame = new JFrame("PDViewer");// application.getName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		// frame.add(tree);
		frame.setLocation(100, 100);
		frame.setSize(new Dimension(500, 600));

		JScrollPane scrollpane = new JScrollPane(tree);
		frame.add(scrollpane);

		// Display the window.
		// frame.pack();
		frame.setVisible(true);
	}

	public JTree getTree() {
		return tree;
	}

	// Add a new menu item to the pop up menu
	public JMenuItem getMenuItem(String s, ActionListener al) {
		JMenuItem menuItem = new JMenuItem(s);
		menuItem.setActionCommand(s.toUpperCase());
		menuItem.addActionListener(al);
		return menuItem;

	}

	/**
	 * Responsible for the creation and display of icons for the tree nodes
	 * 
	 * @author Philip Booth
	 * 
	 */
	private class IconRenderer extends DefaultTreeCellRenderer {

		public IconRenderer() {
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			@SuppressWarnings("unused")
			Icon icon = null;

			if (TreeToPD.get(node) instanceof String) {
				icon = new ImageIcon("icons\\example\\string.png");
			} else {
				PDInstance pdnode = (PDInstance) TreeToPD.get(node);

				icon = new ImageIcon("icons\\nuvola_selected\\ledorange.png");

				if (pdnode instanceof PDRole) {
					icon = new ImageIcon("icons\\example\\"
							+ makeCamelCase(pdnode.getName()) + "Role.png");
				} else if (pdnode instanceof PDMap) {
					icon = new ImageIcon("icons\\example\\map.png");
				} else if (node.toString() == "mapping") {
					icon = new ImageIcon("icons\\example\\mapRole.png");
				} else if (pdnode instanceof PDType) {
					icon = new ImageIcon("icons\\example\\"
							+ makeCamelCase(pdnode.getName()) + "Type.png");
				}

				else if (pdnode != null && !(pdnode instanceof PDRole)) {
					PDType type = (PDType) cache.load(PDType.typeId,
							pdnode.getTypeId());
					Blob b = type.getIcon();
					Icon custom_icon = null;
					if (b != null) {
						custom_icon = new ImageIcon(b.getData());
					}

					if (custom_icon != null)
						icon = custom_icon;
				}

			}
			// temp. temporarily commented out to see the text-only tree view.
			// setIcon(icon);

			return this;
		}

		private String makeCamelCase(String name) {
			String result = "";
			name = name.trim();
			for (String part : name.split(" ")) {
				result += makeFirstBig(part);
			}
			return result;
		}

		private String makeFirstBig(String s) {
			return s.substring(0, 1).toUpperCase() + s.substring(1);
		}

	}

	@Override
	public void treeNodesChanged(TreeModelEvent ev) {
		for (Object o : ev.getChildren()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
			Object element = TreeToPD.get(node);
			String newStr = (String) node.getUserObject();

			DefaultMutableTreeNode roleNode = null;
			PDRole role = null;
			if (!node.equals(node.getRoot())) {
				roleNode = (DefaultMutableTreeNode) node.getParent();
				role = (PDRole) TreeToPD.get(roleNode);
			}

			if (role != null && role.getId().equals(PDWidget.roleTranscludedId)) {
				// Handle the case for editing transclusion

				// Get the transcluding widget
				DefaultMutableTreeNode transcludingNode;
				PDWidget transcludingWidget;
				transcludingNode = (DefaultMutableTreeNode) roleNode
						.getParent();
				transcludingWidget = (PDWidget) TreeToPD.get(transcludingNode);

				// Try to find the transcluded widget with the name that matches
				// the new string that is specified by the user. Set the
				// transclusion if it is found. Remove the transclusion if not.
				boolean found = false;
				Collection<PDInstance> allPDWidgets;

				allPDWidgets = cache.getAllInstancesOfType(PDWidget.typeId);
				for (PDInstance inst : allPDWidgets) {
					if (inst.getName().equals(newStr)) {
						transcludingWidget.setTranscluded(inst.getId());
						found = true;
						break;
					}
				}

				if (!found) {
					transcludingWidget.removeTranscluded();
				}

				cache.commit();

			} else if (element instanceof PDInstance) {
				// TODO refactor. Move this to the handler class.
				// Rename the PDInstance
				PDInstance pdInst = (PDInstance) element;
				System.out.println("out");
				propertiesWindow.getWidgetsPanel().setOldWidgetKey(
						pdInst.getName());
				pdInst.setName(newStr);
				propertiesWindow.getWidgetsPanel().setNewWidgetKey(newStr);
				propertiesWindow.getWidgetsPanel().widgetRenameEvent();
				cache.commit();
			} else if (element instanceof String) {
				// The normal case of changing a widget value or property.
				// Change the property value in the AVM spec in PDStore
				handler.replaceString(node, newStr);
			}
		}

		//TODO commented out as PDStore notifies listeners to update
		// Updates but not replaces widgets
		// alm.updateWidgetsProperties();
	}

	@Override
	public void treeNodesInserted(TreeModelEvent ev) {
		propertiesWindow.getWidgetsPanel().updateComponentsFromTree(
				propertiesWindow);

		alm.updateWidgetsProperties();

	}

	@Override
	public void treeNodesRemoved(TreeModelEvent ev) {
		DefaultMutableTreeNode node = null;
		for (Object o : ev.getChildren()) {
			node = (DefaultMutableTreeNode) o;
			Object element = TreeToPD.get(node);

			if (node != null && element instanceof PDWidget) {
				propertiesWindow.aLMEditor.removeContent(node.toString());
				propertiesWindow.removeFromInvis(node.toString());
				propertiesWindow.resetWidgetPreview();
			}
		}

		alm.updateWidgetsProperties();
	}

	@Override
	public void treeStructureChanged(TreeModelEvent ev) {

	}

	private class SelectiveTreeCellEditor extends DefaultTreeCellEditor {

		public SelectiveTreeCellEditor(JTree tree,
				DefaultTreeCellRenderer renderer, TreeCellEditor editor) {
			super(tree, renderer, editor);
		}

		public SelectiveTreeCellEditor(JTree tree,
				DefaultTreeCellRenderer renderer) {
			super(tree, renderer);
		}

		public boolean isCellEditable(EventObject event) {
			boolean editable = false;
			editable = super.isCellEditable(event);

			// System.out.println("JTreeView > event: " + event); //debug

			TreePath path = tree.getSelectionPath();
			if (path != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				Object pdObject = TreeToPD.get(node);
				if (pdObject instanceof PDRole) {
					editable = false;
				}
			}
			// System.out.println("JTreeView > editable: " + editable);
			return editable;
		}
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent ev)
			throws ExpandVetoException {

	}

	@Override
	public void treeWillExpand(TreeExpansionEvent ev)
			throws ExpandVetoException {
		// System.out.println("treeWillExpand");
		// TreePath path = ev.getPath();
		// TODO for later
		// if () {
		//
		// }
	}

}