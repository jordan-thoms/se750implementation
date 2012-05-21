package pdtransform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDInstance;
import pdstore.dal.PDRole;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;
import pdtransform.dal.PDGenerator;
import pdtransform.dal.PDGeneratorApplication;
import pdtransform.dal.PDMap;
import alm.PropertiesWindow;
import avm.dal.PDWidget;
import avm.dal.PDAvmSpec;

public class AvmSpecViewer {

	private JTreeView treeView;
	private PDInstance startElement;
	private PDWorkingCopy cache;
	private PDInstance application;

	public TreeView getTreeView() {
		return treeView;
	}

	private TreeWalker walker = new TreeWalker();
	private JFrame frame = new JFrame("PDViewer");
	// private Container container = frame.getContentPane();

	private JScrollPane scrollPane = new JScrollPane();

	private PropertiesWindow propertiesWindow;

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	public AvmSpecViewer(PDInstance startElement, PDWorkingCopy cache,
			PDInstance application) {
		this(startElement, cache, application, null);

		// container.setLayout(new FlowLayout());
		// treeView.showGUI();
		// Debugging only. Comment it out for the application.

	}

	public AvmSpecViewer(PDInstance startElement, PDWorkingCopy cache,
			PDInstance application, PropertiesWindow properties) {
		this.propertiesWindow = properties;

		this.startElement = startElement;
		this.cache = cache;
		this.application = application;

		run(startElement, cache, application);
		JTree jTree = (JTree) treeView.getTree();
		scrollPane.setViewportView(jTree);

	}

	/**
	 * Called when PDStore notifies the listeners that the JTree has changed.
	 */
	public void refresh() {
		JTree oldTree = (JTree) treeView.getTree();

		// Save the path of the selected node if any
		TreePath selectedPath = oldTree.getSelectionPath();

		// Get the paths of all the expanded tree nodes
		List<TreePath> expandedNodes = new ArrayList<TreePath>();
		getExpandedPaths(oldTree, new TreePath(oldTree.getModel().getRoot()),
				expandedNodes);

		// Reload the AVM spec from the database
		run(startElement, cache, application);
		JTree newTree = (JTree) treeView.getTree();
		scrollPane.setViewportView(newTree);

		// Expand all the tree nodes that were previously expanded
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) newTree
				.getModel().getRoot();

		for (TreePath tp : expandedNodes) {
			// Ignore the root node
			if (tp.getPathCount() >= 2) {
				TreePath path = findPath(newTree, root, tp, 1);

				if (path != null)
					newTree.expandPath(path);
			}
		}

		// Return the selection to the same node as before the change
		if (selectedPath != null) {
			selectedPath = findPath(newTree, root, selectedPath, 1);
			newTree.setSelectionPath(selectedPath);
			newTree.scrollPathToVisible(selectedPath);
		}
	}

	/**
	 * Searches the given JTree for the particular path
	 * 
	 * @param tree
	 *            The JTree to search
	 * @param startNode
	 *            The starting node (typically the root)
	 * @param treePath
	 *            The TreePath to find
	 * @param element
	 *            The path component to start at
	 * @return the found TreePath or null if not found
	 */
	private TreePath findPath(JTree tree, TreeNode startNode,
			TreePath treePath, int element) {
		TreeNode foundNode = null;
		Object pathComponent = treePath.getPathComponent(element);

		if (startNode != null && pathComponent != null) {
			// Find the corresponding tree node
			for (int i = 0; i < startNode.getChildCount(); i++) {

				TreeNode child = startNode.getChildAt(i);
				if (child.toString() == null
						|| child.toString().equals(pathComponent.toString())) {
					foundNode = child;
					break;
				}
			}

			// The node was not found but the start node contained children.
			// Expand the start node (this is the case when searched node
			// is actually the one that was changed)
			if (foundNode == null && startNode.getChildCount() != 0) {
				foundNode = startNode.getChildAt(0);
			}

			// If there are more nodes in the path to find then find them
			if (++element < treePath.getPathCount()) {
				return findPath(tree, foundNode, treePath, element);
			}
			// Else get the full TreePath to the node
			else {
				if (foundNode != null) {
					List<TreeNode> nodes = new ArrayList<TreeNode>();
					nodes.add(foundNode);
					foundNode = foundNode.getParent();
					while (foundNode != null) {
						nodes.add(0, foundNode);
						foundNode = foundNode.getParent();
					}
					return new TreePath(nodes.toArray());
				}
			}
		}
		return null;
	}

	/**
	 * Stores the TreePaths of the expanded nodes into the list
	 * 
	 * @param tree
	 *            The JTree displayed
	 * @param parent
	 *            The parent node
	 * @param list
	 *            The list to store the expanded TreePaths
	 */
	private boolean getExpandedPaths(JTree tree, TreePath parent,
			List<TreePath> list) {
		// TODO Remove paths from the list if it is a subpath of another path

		if (tree.isVisible(parent)) {
			TreeNode node = (TreeNode) parent.getLastPathComponent();
			if (node.getChildCount() >= 0) {
				for (Enumeration e = node.children(); e.hasMoreElements();) {
					TreeNode n = (TreeNode) e.nextElement();
					TreePath path = parent.pathByAddingChild(n);

					// If parent is expanded and not in the list then add it
					if (getExpandedPaths(tree, path, list)
							&& !list.contains(parent)) {
						list.add(parent);
					}
				}
				return true;
			}
		}
		return false;
	}

	public void run(PDInstance startElement, PDWorkingCopy cache,
			PDInstance application) {
		// Set up tree
		treeView = new JTreeView(startElement, cache, application,
				propertiesWindow);

		// Traverse output template
		walker.gaOutputCursor.push(startElement);
		traverse_output(startElement, null, cache, application);

		// treeView.expand();
		// container.add((Component) treeView.getTree());

	}

	public void showGUI() {

		// Create and set up the window.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.

		frame.setLocation(100, 100);
		// frame.setSize(new Dimension(300, 500));

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public Object lookUpPD(DefaultMutableTreeNode node) {
		JTreeView jTreeView = (JTreeView) treeView;
		Object pdObj = jTreeView.TreeToPD.get(node);
		return pdObj;
	}

	/**
	 * Main recursive method, traverses through the output template cloning the
	 * element then checking if it has an associated mapping, executing that
	 * mapping if it does and then moves to the children by calling this method
	 * on them. This approach results in a depth first recursion. <br>
	 * <br>
	 * 
	 * @param element
	 *            - PDInstance of the output template instance to be processed
	 * @param parentRole
	 *            - Relation the instance "element" is to its parent, this is
	 *            used in the cloning process
	 * @param cache
	 *            - PDCache instance associated with this Generator Application
	 * @param ga
	 *            - PDGeneratorApplication instance which PDTransform is being
	 *            run on
	 */
	private void traverse_output(PDInstance element, PDRole parentRole,
			PDWorkingCopy cache, PDInstance application) {
		if (parentRole != null) {
			// System.out.println("parent: " + walker.gaOutputCursor.peek() +
			// "; role: " + parentRole + "; child: " + element);
			addNode(element, parentRole, walker.gaOutputCursor.peek());
		}

		// if (parentRole.getId().equals(PDWidget.roleTranscludedId)
		// || parentRole.getId().equals(PDWidget.roleTranscludingId)) {
		// return;
		// }

		// if (role.getId().equals(PDWidget.roleTranscludedId)
		// || role.getId().equals(PDWidget.roleTranscludingId)) {
		// continue;
		// }

		if (application instanceof PDGeneratorApplication) {
			ArrayList<PDMap> maps = checkMapping(element,
					(PDGeneratorApplication) application);
			addMaps(maps, element);
		}

		ArrayList<PDRole> roles = walker.getRoles(element, cache);

		for (PDRole role : roles) {
			if (role.getId().equals(PDWidget.roleTranscludingId)) {
				continue;
			}

			Collection<Object> children = cache.getInstances(element,
					role.getId());

			if (!children.isEmpty()) {
				// System.out.println(children);
				for (Object c : children) {
					if (c == null) {
						addEmptyRole(role, element);
						continue;
					}
					if (role.getId().equals(PDWidget.roleTranscludedId)
							|| role.getId().equals(PDWidget.roleTranscludingId)) {
						addNode(((PDWidget) c).getName(), role, element);
						// old version
						// addNode(c, role, element);
						// walker.gaOutputCursor.pop();
						continue;
					}

					if (c instanceof PDInstance) {
						PDInstance child = (PDInstance) c;
						traverse_output(child, role, cache, application);
					} else {
						addNode(c, role, element);
					}
				}
			} else {
				addEmptyRole(role, element);
			}

		}

		walker.gaOutputCursor.pop();
	}

	private void addNode(Object element, PDRole role, PDInstance parent) {
		// Add node to tree
		treeView.addNode(parent, element, role);
		if (element instanceof PDInstance) {
			walker.gaOutputCursor.push((PDInstance) element);
		}
	}

	private void addMaps(ArrayList<PDMap> maps, PDInstance parent) {
		// Add map to tree
		treeView.addMaps(maps, parent);
	}

	private void addEmptyRole(PDRole role, PDInstance parent) {
		treeView.addEmptyRole(role, parent);
	}

	private ArrayList<PDMap> checkMapping(PDInstance element,
			PDGeneratorApplication ga) {
		PDGenerator gen = ga.getGenerator();
		Collection<PDMap> maps = gen.getMaps();
		ArrayList<PDMap> mapped = new ArrayList<PDMap>();

		for (PDMap map : maps) {
			GUID outputInstanceID = map.getOutputInstance();

			if (outputInstanceID.equals(element.getId())) {
				// ArrayList<PDInstance> mapped = executeMapping(map, element,
				// ga, walker);
				mapped.add(map);
			}
		}
		return mapped;
	}

	public static void main(String[] args) {
		String dbString = "";
		String appName = "";

		if (args.length == 2) {
			dbString = args[0];
			appName = args[1];
		} else if (args.length == 1) {
			dbString = PDTransform.defaultDB;
			appName = args[0];
		} else {
			System.out.println("Need Application name as input");
			return;
		}

		// Create a new cache that is connected to the DB
		PDWorkingCopy workingCopy = new PDSimpleWorkingCopy(new PDStore(
				dbString));

		// Load a PDAvmSpec instance into memory if it exists
		if (workingCopy.instanceExists(PDAvmSpec.typeId, appName)) {
			GUID appGUID = workingCopy.getId(appName);
			PDAvmSpec avmSpec = (PDAvmSpec) workingCopy.load(PDAvmSpec.typeId,
					appGUID);
			new AvmSpecViewer(avmSpec, workingCopy, avmSpec);
		} else {
			System.out.println(appName
					+ " does not exist in the database, please commit");
		}

	}
}
