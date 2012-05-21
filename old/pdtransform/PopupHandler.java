package pdtransform;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import alm.ALMLayout;
import avm.dal.PDWidget;
import avm.dal.PDAvmSpec;

import pdstore.*;
import pdstore.dal.PDInstance;
import pdstore.dal.PDRole;
import pdstore.dal.PDType;
import pdstore.dal.PDWorkingCopy;
import pdtransform.dal.PDGenerator;
import pdtransform.dal.PDGeneratorApplication;
import pdtransform.dal.PDMap;
import pdtransform.dal.PDSerializerApplication;
/**
 * Defines the operations associated with the right click popup menu
 * @author Gyurme Dahdul and Philip Booth
 *
 */
class PopupHandler implements ActionListener {
	private DNDTree tree;
	private JPopupMenu popup;
	private JFrame frame;
	private Point loc;
	private DefaultMutableTreeNode buffer;
	private Hashtable<DefaultMutableTreeNode, Object> TreeToPD;
	private PDWorkingCopy cache;
	private PDInstance application;
	private JPopupMenu insert_popup;
	private boolean stable = true;
	private int nameCount = 0;
	private ALMLayout alm;

	public PopupHandler(DNDTree tree, JPopupMenu popup, Hashtable<DefaultMutableTreeNode, Object> treetopd, 
			PDWorkingCopy cache, PDInstance application) {
		this.tree = tree;
		this.popup = popup;
		tree.addMouseListener(ma);
		TreeToPD = treetopd;
		this.cache = cache;
		this.application = application;
	}
	
	public PopupHandler(DNDTree tree, JPopupMenu popup, Hashtable<DefaultMutableTreeNode, Object> treetopd, 
			PDWorkingCopy cache, PDInstance application, ALMLayout alm) {
		this.tree = tree;
		this.popup = popup;
		tree.addMouseListener(ma);
		TreeToPD = treetopd;
		this.cache = cache;
		this.application = application;
		this.alm = alm;
	}

	public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();
		//TreePath path  = tree.getPathForLocation(loc.x, loc.y);
		// TODO 
		TreePath path  = tree.getSelectionPath();
		//System.out.println("path = " + path);
		//System.out.printf("loc = [%d, %d]%n", loc.x, loc.y);
		if(ac.equals("CUT"))
			cut(path);  
		if(ac.equals("COPY"))
			copy(path);
		if(ac.equals("PASTE"))
			paste(path);
		if(ac.equals("REMOVE"))
			remove(path);
		if(ac.equals("TRANSFORM"))
			transform(path);
		if(ac.equals("SERIALIZE"))
			try {
				serialize(path);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if(ac.equals("INSERT NEW"))
				insert(path);
			if(ac.equals("INSERT TYPE"))
				insertType(path, e);
			if(ac.equals("INSERT ROLE"))
				insertRole(path, e);

		
		// Some operation don't trigger tree node changed/inserted/removed
		// events. updateWidgetsProperties is called to make sure the editor
		// shows the updated widgets.
		alm.updateWidgetsProperties();
	}


	/**
	 * Allows for the insertion of new types into roles and the addition of 
	 * insertion options into the popup menu
	 * @param path  - point in the JTree where the selected node is
	 */
	private void insert(TreePath path) 
	{
		PDInstance pdnode = null;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
		if(!(TreeToPD.get(node) instanceof String)){
			pdnode = (PDInstance)TreeToPD.get(node);
		}

		if (pdnode instanceof PDRole)
		{
			PDRole role = (PDRole)pdnode;
			int count = node.getChildCount();

			if (role.getMaxMult() == null || count < role.getMaxMult())
			{
				PDType insType = role.getPartner().getAccessor();
				// In case of a string 

				if (role.getId().equals(PDWidget.roleTranscludedId))
				{
					// Insert a String in the tree. (but not in PDStore)
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(" ");
					//TreeToPD.put(newNode, new String(" "));
					DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
					model.insertNodeInto(newNode, node, count);
				}
				else if(insType.toString().equals("PDType:String")){
					
					//newPDNode.setName(insType.getName());
					//DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(" ");
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(" ");
					TreeToPD.put(newNode,new String(" "));
					DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
					model.insertNodeInto(newNode, node, count);

					// TODO insert a String " " to PDStore
					// Note: the code above only inserts a new node in the tree.
					PDInstance parent = (PDInstance)TreeToPD.get(node.getParent());
					cache.addLink(parent.getId(), role.getId(), " ");
					expand(path);


				}
				else if(role.toString().equals("PDRole:input type"))
				{
					
					PDGenerator generator = ((PDGeneratorApplication)application).getGenerator();
					PDType first = generator.getInputType();

					ArrayList <PDType>list =  findTypes(first);
					insert_popup = new JPopupMenu();
					for(PDType t: list){
						JMenuItem menuItem = new JMenuItem(t.getName());
						menuItem.setActionCommand("INSERT TYPE");
						menuItem.addActionListener(this);
						insert_popup.add(menuItem);
					}

					insert_popup.show(tree, loc.x, loc.y);
					expand(path);
				}
				else if(role.toString().equals("PDRole:input role"))
				{
					
					PDGenerator generator = ((PDGeneratorApplication)application).getGenerator();
					PDType first = generator.getInputType();
					ArrayList<PDType> visited = new ArrayList<PDType>();
					ArrayList <PDRole>list =  findRoles(first, visited);
					insert_popup = new JPopupMenu();
					for(PDRole t: list){
						JMenuItem menuItem = new JMenuItem(t.getName());
						menuItem.setActionCommand("INSERT ROLE");
						menuItem.addActionListener(this);
						insert_popup.add(menuItem);
					}

					insert_popup.show(tree, loc.x, loc.y);
					expand(path);
				}
				//Otherwise
				else{
					
					PDInstance newPDNode = cache.newInstance(insType.getId());
					String root =((DefaultMutableTreeNode)path.getPathComponent(0)).toString();
					GUID rootGUID = cache.getId(root);
					PDAvmSpec avmSpec = (PDAvmSpec) cache.load(
							PDAvmSpec.typeId, rootGUID);
					for (PDWidget widget : avmSpec.getWidgets()){
						if (widget.getName().equals(insType.getName() + nameCount)){
							nameCount++;
						}
					}
					newPDNode.setName(insType.getName() + nameCount);
					nameCount ++;

					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(((PDInstance)newPDNode).getName());
					//PDtoTree.put(newPDNode, newNode);
					TreeToPD.put(newNode, newPDNode);

					DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
					model.insertNodeInto(newNode, node, count);

					// find parent and add
					PDInstance parent = (PDInstance)TreeToPD.get(node.getParent());

					cache.addLink(parent.getId(), role.getId(), newPDNode.getId());

					// Insert roles
					for (PDRole r : insType.getAccessibleRoles())
					{
						if (r.getName() != null)
						{
							//PDRole newRole = (PDRole)cache.newInstance(r.getTypeId());
							//newRole.setName(r.getName());
							//newRole.setAccessor(r.getAccessor());
							//newRole.setPartner(role);
							DefaultMutableTreeNode newRoleNode = new DefaultMutableTreeNode(r.getName());

							TreeToPD.put(newRoleNode, r);
							newNode.add(newRoleNode);
						}
					}
				}	
				

				cache.commit();

			}

			else{
				JOptionPane.showMessageDialog(frame,
						"Insertion would exceed " + role + "'s max multiplicity",
						"Error",
						JOptionPane.ERROR_MESSAGE);		
			}			
		}
		else
			//System.out.println("Can only insert new instances on roles");
			JOptionPane.showMessageDialog(frame,"Can only insert new instances on " +
					"roles","Error", JOptionPane.ERROR_MESSAGE);	

	}

	/**
	 * Allows for the insertion of types into the PDModel
	 * @param path - point in the JTree where the selected Node is
	 * @param e
	 */
	private void insertType(TreePath path, ActionEvent e)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();

		String typename = ((JMenuItem)e.getSource()).getText();
		//System.out.println(typename);
		PDInstance newIns = cache.newInstance(cache.getId("PD" + typename));

		//newType.setName(typename);
		PDType newType = (PDType)cache.load(PDType.typeId, newIns.getTypeId());
		//System.out.println(newIns + "\n" + newType);

		DefaultMutableTreeNode inputType = new DefaultMutableTreeNode(typename);

		TreeToPD.put(inputType, newType);

		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		model.insertNodeInto(inputType, node, 0);

		//find parent and add
		PDRole role = (PDRole)TreeToPD.get(node);
		PDInstance parent = (PDInstance)TreeToPD.get(node.getParent());

		cache.addLink(parent.getId(), role.getId(), newType.getId());
		
		//System.out.println(stable);
		stable = true;
		cache.commit();

		//expand(path);
	}

	/**
	 * Insert a role into the Jtree
	 * @param path  - point in the JTree where the selected Node is
	 * @param e - invoked Action event 
	 */
	private void insertRole(TreePath path, ActionEvent e)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();

		String typename = ((JMenuItem)e.getSource()).getText();
		//System.out.println(typename);
		PDRole newRole = (PDRole)cache.newInstance(PDRole.typeId);

		//newType.setName(typename);
		//System.out.println(newIns + "\n" + newRole);

		DefaultMutableTreeNode inputRole = new DefaultMutableTreeNode(typename);

		TreeToPD.put(inputRole, newRole);

		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		model.insertNodeInto(inputRole, node, 0);

		//find parent and add
		PDRole role = (PDRole)TreeToPD.get(node);
		PDInstance parent = (PDInstance)TreeToPD.get(node.getParent());
		
		cache.addLink(parent.getId(), role.getId(), newRole.getId());

		cache.commit();

		//expand(path);
	}

	/**	Remove the selected node, store it in the buffer
	 * 
	 * @param path -point in the JTree where the selected Node is
	 */
	private void cut(TreePath path) 
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();

		if(isValid("CUT", node))
		{
			buffer = node;
			remove(path);
		}
		else
		{
			JOptionPane.showMessageDialog(frame,
					"Can only cut instances",
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Store the subTree in a buffer
	 * @param subRoot
	 * @param sourceTree
	 * @return
	 */
	private DefaultMutableTreeNode copySubTree(DefaultMutableTreeNode subRoot, DefaultMutableTreeNode sourceTree)  
	{  
		if (sourceTree == null)  
		{  
			return subRoot;  
		}  
		for (int i = 0; i < sourceTree.getChildCount(); i++)  
		{  
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)sourceTree.getChildAt(i);  
			DefaultMutableTreeNode clone = new DefaultMutableTreeNode(child.getUserObject());   
			subRoot.add(clone);
			if (TreeToPD.get(child) != null)
				TreeToPD.put(clone, TreeToPD.get(child));
			copySubTree(clone, child);  
		}  
		return subRoot;  
	} 

	/**Store selected node in buffer
	 * 
	 * @param path - point in the JTree where the selected Node is
	 */
	private void copy(TreePath path) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();

		if(isValid("COPY", node))
		{	

			// Make a copy of node and children
			DefaultMutableTreeNode newRoot = (DefaultMutableTreeNode)node.clone();
			TreeToPD.put(newRoot, TreeToPD.get(node));
			buffer = copySubTree(newRoot, node);
		}
		else
		{
			JOptionPane.showMessageDialog(frame,
					"Can only copy instances",
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
	} 

	/**
	 * Add the previously cut/copied node to the same parent as selected node
	 * 
	 * @param path
	 */
	private void paste(TreePath path) 
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();

		if((buffer != null) && isValid("PASTE", node))
		{
			int count = node.getChildCount();

			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			model.insertNodeInto(buffer, node, count);

			// Update PDModel
			PDInstance pdnode = (PDInstance)TreeToPD.get(buffer);
			buffer = null;
			// find parent and add
			PDRole role = (PDRole)TreeToPD.get(node);
			PDInstance parent = (PDInstance)TreeToPD.get(node.getParent());

			cache.addLink(parent.getId(), role.getId(), pdnode.getId());

			expand(path);

			cache.commit();

		}
		else
		{
			if (buffer == null)
				JOptionPane.showMessageDialog(frame,
						"Nothing to paste",
						"Alert",
						JOptionPane.INFORMATION_MESSAGE);

			else
				JOptionPane.showMessageDialog(frame,
						"Can only paste on roles",
						"Error",
						JOptionPane.ERROR_MESSAGE);				
		}
	}

	private void expand(TreePath path)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();

		for (int i = 0; i < node.getChildCount(); i++) 
		{
			TreePath newPath = path.pathByAddingChild(node.getChildAt(i));
			tree.expandPath(newPath);
			expand(newPath);
		}

	}

	/**
	 * Handles the removal of nodes and adjusts the model based on the tyepe of node removed
	 * @param path - place in the JTree where the node is located 
	 */
	private void remove(TreePath path)
	{

		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();

		if (isValid("REMOVE", node))
		{
			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();

			// Update model
			PDInstance pdnode = null;
			Object element = TreeToPD.get(node);
			if (element instanceof PDInstance) {
				pdnode = (PDInstance)TreeToPD.get(node);
			} else if (element instanceof String) {
				// TODO ... is this needed here?
			}

			// System.out.println(node.getParent());
			// find parent and remove
			if (!node.isRoot()){
				PDRole role = (PDRole)TreeToPD.get(node.getParent());
				PDInstance parent = (PDInstance)TreeToPD.get(node.getParent().getParent());
				if(role.getName().equals("input type")){
					stable = false;

				}

				// Handle the case of removing a widget name in the transcluded role
				if (role.getId().equals(PDWidget.roleTranscludedId)){
					PDWidget transcludingWidget = (PDWidget) parent;
					transcludingWidget.removeTranscluded();
				}
				// If the path refers to a String
				else if (element instanceof String) {
					cache.removeLink(parent.getId(), role.getId(), element);
				}
				// if the node is connected to a mapping remove it from that mapping
				else if (parent instanceof PDMap)
				{
					// Map doesn't know about instances only roles and types
					PDMap map = (PDMap)parent;

					if (role.getId() == PDMap.roleInputRoleId)
						map.removeInputRole();
					if (role.getId() == PDMap.roleInputTypeId)
						map.removeInputType();
					if (role.getId() == PDMap.roleOutputRoleId)
						map.removeOutputRole(role);
				}
				// If the node is a mapping remove it and update the PDcache
				else if (pdnode instanceof PDMap)
				{
					((PDGeneratorApplication)application).getGenerator().removeMap((PDMap)pdnode);
					cache.commit();
				}
				else
				{
					cache.removeLink(parent.getId(), role.getId(), pdnode.getId());
				}
				model.removeNodeFromParent(node);

				cache.commit();
				//System.out.println(((PDInstance)cache.load(parent.getTypeId(), parent.getId())).getInstances(role.getId()));
			}
		}
	}

	/**
	 * Invokes the Transformation Algorithm	
	 * @param path
	 */
	private void transform(TreePath path)
	{
		if(!stable){
			JOptionPane.showMessageDialog(frame,
					"An Input Type is not defined, please check the model",
					"Error",
					JOptionPane.ERROR_MESSAGE);

		}
		else if (application instanceof PDGeneratorApplication)
		{
			//DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			//PDInstance root = (PDInstance)TreeToPD.get(model.getRoot());

			cache.commit();

			//System.out.println("Transforming");
			new Transform(cache, (PDGeneratorApplication)application);
		}
	}

	/**
	 * Invokes the serialization algorithm
	 * 
	 * *** Warning ****
	 * the method expects the serializer to have the name
			 in the format generatorApplicationName_serializer this will need to be adhered to or changed
	 * @param path
	 * @throws IOException
	 */
	private void serialize(TreePath path) throws IOException
	{
		//System.out.println("Serializing");
		GUID guid = cache.getId(application.getName() + PDTransform.serializer);
		PDSerializerApplication sa = (PDSerializerApplication)cache.load(PDSerializerApplication.typeId, guid);

		if (sa != null)
		{
			new Serialize(sa, cache);

			GUID input = sa.getInput();
			PDType inputType = sa.getInputType();

			PDInstance startElement = cache.load(inputType.getId(), input);

			//System.out.println("Viewing");
			new Viewer(startElement, cache, sa);
		}
		else
			JOptionPane.showMessageDialog(frame,
					"Cannot load " + application.getName() + PDTransform.serializer + ". \n" + 
					"Please ensure it is committed to the database",
					"Error",
					JOptionPane.ERROR_MESSAGE);	

	}

	/**
	 * Detemines if cut,copy,paste or remove operations from the popup menu are able
	 * to be perfomed
	 * @param operation - Cut,copy,paste or remove operation identifier
	 * @param node - node on which the operation is meant to take place
	 * @return
	 */
	public boolean isValid(String operation, DefaultMutableTreeNode node)
	{
		if (operation == "COPY" || operation == "CUT" || operation == "REMOVE")
		{
			if(TreeToPD.get(node) instanceof PDRole)
			{
				return false;
			}
			else
				return true;
		}
		else if (operation == "PASTE")
		{
			if (TreeToPD.get(node) instanceof PDRole)
			{
				// Should be instance
				PDInstance ins = (PDInstance)TreeToPD.get(buffer);
				// Should be role
				PDRole role = (PDRole)TreeToPD.get(node);

				PDType insType = role.getPartner().getAccessor();

				if (ins.getTypeId() == insType.getId())
					return true;
			}
			else
				return false;

		}

		return false;
	}


	private MouseListener ma = new MouseAdapter() {
		private void checkForPopup(MouseEvent e) {
			if(e.isPopupTrigger()) {
				loc = e.getPoint();
				TreePath path  = tree.getPathForLocation(loc.x, loc.y);
				if(path!=null){
					tree.setSelectionPath(path);
					popup.show(tree, loc.x, loc.y);
				}
			}
		}

		public void mousePressed(MouseEvent e)  { checkForPopup(e); }
		public void mouseReleased(MouseEvent e) { checkForPopup(e); }
		public void mouseClicked(MouseEvent e)  { checkForPopup(e); }
	};

	/**
	 * Finds types associated with the input type
	 * @param t - input type
	 * @return - Array list of associated types
	 */
	private ArrayList<PDType> findTypes(PDType t)
	{
		ArrayList<PDType> list = new ArrayList<PDType>();
		list.add(t);
		Collection<PDRole> roles = t.getAccessibleRoles();
		for (PDRole r : roles)
		{
			if(r.getName()!=null){
				PDType x = r.getPartner().getAccessor();
				//				System.out.println("type" + x.toString());
				if (!list.contains(x))
				{					
					ArrayList<PDType> list2 = findTypes(x);
					for (PDType type: list2){
						if (!list.contains(type)){
							list.add(type);
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * Finds roles associated with the input type
	 * @param t - input type
	 * @param visited - list of previously visited roles 
	 * @return - Array list of associated roles
	 */
	private ArrayList<PDRole> findRoles(PDType t, ArrayList<PDType> visited)
	{
		ArrayList<PDRole> list = new ArrayList<PDRole>();
		//ArrayList<PDType> visited = new ArrayList<PDType>();
		visited.add(t);
		Collection<PDRole> roles = t.getAccessibleRoles();
		for (PDRole r : roles)
		{
			if(r.getName()!=null){
				PDType x = r.getPartner().getAccessor();
				//System.out.println("type" + x.toString());
				if (!visited.contains(x))
				{		
					list.add(r);
					ArrayList<PDRole> list2 = findRoles(x, visited);
					for (PDRole role: list2){
						if (!list.contains(role)){
							list.add(role);
						}
					}
				}
			}
		}
		return list;
	}


	/** 
	 * Replace the String in PDStore that is associated with the specified node, 
	 * using the specified newStr.
	 * There must be an existing String value that is to be replaced.
	 * i.e. the tree node must have a corresponding String instance in PDStore. 
	 * @param node    the tree node of the udpated String 
	 * @param newStr  the String value to set
	 * @return        the previous String associated with the specified node, or null if it did not have one 
	 */
	public String replaceString(DefaultMutableTreeNode node, String newStr) {
		// Find the existing entry in the TreeToPD lookup
		Object oldObj = TreeToPD.get(node);
		if (oldObj instanceof String == false) {
			System.err.println("Error: There is not an existing String " +
					"associated with the node " + node);
			return null;
		}
		String oldStr = (String) oldObj;

		// Find the role and the parent instance in PDStore
		DefaultMutableTreeNode parentRoleNode = (DefaultMutableTreeNode) 
		node.getParent();
		Object parentRoleObject = TreeToPD.get(parentRoleNode);
		PDRole role = null;
		if (parentRoleObject instanceof PDRole) {
			role = (PDRole) parentRoleObject;
		} else {
			// It should not enter this condition.
			System.err.println("The path must be a role.");
			return null;
		}

		DefaultMutableTreeNode parentInstNode = (DefaultMutableTreeNode) 
		parentRoleNode.getParent();
		PDInstance parent = (PDInstance) TreeToPD.get(parentInstNode);

		// Replace the String in PDStore
		cache.removeLink(parent.getId(), role.getId(), oldStr);
		cache.addLink(parent.getId(), role.getId(), newStr);		
		
		cache.commit();

		// Update the entry in the TreeToPD lookup
		TreeToPD.put(node, newStr);

		return oldStr;
	}

	//	public void reloadWidgets() {
	//		ALMLayout alm = new ALMLayout();
	//		//alm.loadWidgetsFromAvmSpec(avmSpec);
	//		
	//		// Reload all widgets from the modified AVM spec. This creates new 
	//		// instances of Java widgets.
	//		alm.loadWidgetsFromAllAvmSpec(alm.getDefaultPDCache());
	//		
	//		// Replace the Java widget of the area with the new widget
	//		for (Area area : alm.getAreas()) {
	//			JComponent oldWidget = area.getContent();
	//			JComponent newWidget = null;
	//			for (JComponent c : alm.savedControls) {
	//				if (c.getName().equals(oldWidget.getName())) {
	//					newWidget = c;
	//					parent.add(newWidget); // Use this or wait for contentsToAdd to be added to parent?
	//					area.setContent(newWidget);
	//					parent.remove(oldWidget);
	//				}
	//			}
	//		}
	//	}


}