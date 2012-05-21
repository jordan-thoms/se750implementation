package pdstore.ui.treeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;

/**
 * TreeModel implementation for representing PDStore objects and links as
 * tree nodes. Allows support for editing and dynamic updates to view.
 * 
 * */
public class PDTreeModel extends DefaultTreeModel implements PDListener<GUID, Object, GUID> {
	private static final long serialVersionUID = 122201688579380771L;
	
	protected PDStore store;
	//private Object[] rootInstances;
	private Object clipboardItem;
	
	//Store the mapping from each instance to all TreeNodes that represent it
	private Multimap<Object, ComplexNode> instanceNodeMap = LinkedHashMultimap.create();
	private Multimap<Object, PrimitiveRoleNode> primitiveRoleMap = LinkedHashMultimap.create();
	private Multimap<Object, ComplexRoleNode> complexRoleMap = LinkedHashMultimap.create();
	
	public PDTreeModel(Object[] rootInstances, PDStore store) {
		super(new PDRootNode("PDStore"));
		//this.rootInstances = rootInstances;
		this.store = store;
		
		//Set up the root instances by creating a tree node for each
		for (Object rootInstance : rootInstances) {
			//Grab the descriptive properties of this object
			GUID transaction = store.begin();
			String instanceName = store.getName(transaction, rootInstance);
			GUID instanceType = store.getType(transaction, rootInstance);
			String instanceTypeName = store.getName(transaction, instanceType);
			store.commit(transaction);
			
			PDRootNode rootNode = (PDRootNode)root;
			//Create a tree node
			ComplexNode node = new ComplexNode(rootNode, rootInstance, instanceName, instanceTypeName);
			rootNode.add(node);
			//register this node with the underlying model (used for model updates)
			instanceNodeMap.put(rootInstance, node);
			//recursively load children
			reloadFromModel(node);
		}
		
		//Not utilised at the moment... Would be nice if it was!
		store.getListenerDispatcher().addListener(this,
				new PDChange<GUID, Object, GUID>(null, null, null, null, null));
	}

	/** Invoked whenever a change is made to the underlying PDStore model. Updates the tree structure
	 * to reflect the new model */
	public void transactionCommitted(List<PDChange<GUID, Object, GUID>> transaction, List<PDChange<GUID, Object, GUID>> matchedChanges, PDCoreI<GUID, Object, GUID> core) {
		//TODO not used at the moment. Updates to treeview are done "manually"...
	}
	
	public void rename(ComplexNode node, String newName) {
		//For complex types, just "rename"
		GUID transaction = store.begin();
		store.setName(transaction, node.getValue(), newName);
		store.commit(transaction);
		//Update affected tree nodes
		reloadNodesPertainingTo(node.getValue());
	}

	public void changeValue(PrimitiveRoleNode node, Object newValue) {
		//for primitive types, a bit tricky - link the name role to the new string
		TreeNode parent = node.getParent();
		if (parent == null || !(parent instanceof ComplexNode)) {
			//this theoretically shouldn't be true!!
			return;
		}
		//Get the role for which this primitive is the object; then the subject for that role
		Object parentInstance = ((ComplexNode)node.getParent()).getValue();
		GUID transaction = store.begin();
		//Unlink the primitive from the role and replace it with the new primitive
		if (node.getValue() != null)
			store.removeLink(transaction, parentInstance, node.getRole(), node.getValue());
		store.addLink(transaction, parentInstance, node.getRole(), newValue);
		store.commit(transaction);
		//Update affected tree nodes
		reloadNodesPertainingTo(parentInstance);
	}

	public void remove(ComplexNode node) {
		TreeNode parent = node.getParent();
		//"remove" action doesn't apply to the top-most instance node (which has PDRootNode as parent)
		if (parent == null || !(parent instanceof ComplexRoleNode))
			return;
		//Get the role for which this node is the object; then the subject for that role
		ComplexRoleNode parentRoleNode = (ComplexRoleNode)parent;
		Object parentInstance = ((ComplexNode)parentRoleNode.getParent()).getValue();
		GUID parentRole = parentRoleNode.getRole();
		GUID transaction = store.begin();
		//unlink the object from the role
		store.removeLink(transaction, parentInstance, parentRole, node.getValue());
		store.commit(transaction);
		//Update affected tree nodes
		reloadNodesPertainingTo(parentRole);
		reloadNodesPertainingTo(node.getValue());
	}

	public void add(ComplexRoleNode node) {
		GUID role = node.getRole();
		GUID transaction = store.begin();
		
		//Create a new instance and register it
		
		//- determine the type of the object of the role (i.e. the instance to be created)
		GUID type = store.getOwnerType(transaction, role);
		Object newInstance = new GUID();
		store.setType(transaction, newInstance, type);
		//then link it to the role
		Object instance1 = ((ComplexNode)node.getParent()).getValue();
		store.addLink(transaction, instance1, role, newInstance);
		store.commit(transaction);
		//Update affected tree nodes
		reloadNodesPertainingTo(role);
	}
	
	public void copy(ComplexNode node) {
		clipboardItem = node.getValue();
	}

	public void paste(ComplexRoleNode node) {
		if (clipboardItem == null)
			return;
		GUID transaction = store.begin();
		//Get the subject of the role to paste under
		Object instance1 = ((ComplexNode)node.getParent()).getValue();
		//Then link 'em
		store.addLink(transaction, instance1, node.getRole(), clipboardItem);
		store.commit(transaction);
		//Update affected tree nodes
		reloadNodesPertainingTo(node.getRole());
		reloadNodesPertainingTo(clipboardItem);
	}

	public ComplexNode find(ComplexNode node, boolean forward) {
		//Basically a brute-force search for the next node
		TreeNode[] path = getPathToRoot(node);
		Object instance = node.getValue();
		//"bubble" the search up the ancestry chain, starting from immediate parent
		for (int i = path.length - 2; i > 0; i--) {
			TreeNode parent = path[i-1];
			TreeNode child = path[i];
			ComplexNode next = findChild(parent, parent.getIndex(child) + (forward ? 1 : -1), forward, instance);
			if (next != null)
				return next;
		}
		return null;
	}
	
	private ComplexNode findChild(TreeNode root, int beginningChildIndex, boolean forward, Object match) {
		if (root instanceof ComplexNode) {
			ComplexNode node = (ComplexNode)root;
			if (node.getValue().equals(match))
				return node;
		}
		int childCount = root.getChildCount();
		for (int i = beginningChildIndex; (forward && i < childCount) || (!forward && i >= 0); i += (forward ? 1 : -1)) {
			TreeNode childAt = root.getChildAt(i);
			ComplexNode matched = findChild(childAt, forward ? 0 : childAt.getChildCount() - 1, forward, match);
			if (matched != null)
				return matched;
		}
		return null;
	}
	
	public Object getClipboardItem() {
		return clipboardItem;
	}

	public void refresh(ComplexNode selected) {
		reloadFromModel(selected);
		reload(selected);
	}
	
	public void refresh(ComplexRoleNode selected) {
		reloadFromModel(selected);
		reload(selected);
	}
	
	/** Reloads the tree node based on its underlying instance object */
	private void reloadFromModel(ComplexNode complexNode) {
		for (ComplexRoleNode node : complexNode.getComplexRoleNodes()) {
			complexNode.removeComplexRoleNode(node);
			complexRoleMap.remove(node.getRole(), node);
		}
		for (PrimitiveRoleNode node : complexNode.getPrimitiveRoleNodes()) {
			complexNode.removePrimitiveRoleNode(node);
			primitiveRoleMap.remove(node.getRole(), node);
		}
		
		GUID transaction = store.begin();
		complexNode.setName(store.getName(transaction, complexNode.getValue()));
		store.commit(transaction);
		
		if (complexNode.canLoadChildren()) {
			transaction = store.begin();
			GUID type = store.getType(transaction, complexNode.getValue());
			//Recursively add child nodes based on accessible roles
			Collection<GUID> accessibleRoles = store.getAccessibleRoles(transaction, type);
			store.commit(transaction);
			for (GUID role : accessibleRoles) {
				transaction = store.begin();
				GUID childType = store.getOwnerType(transaction, role);
				String roleName = store.getName(transaction, role);
				store.commit(transaction);
				if ((Boolean)store.getInstance(transaction, childType, PDStore.ISPRIMITIVE_ROLEID)) {
					transaction = store.begin();
					//If primitive type role, then add special node
					Object child = store.getInstance(transaction, complexNode.getValue(), role); //Note this will only get single instance
					store.commit(transaction);
					//@TODO revisit the assumption that primitive-type relationships are one-to-one
					PrimitiveRoleNode node = new PrimitiveRoleNode(complexNode, role, roleName, child);
					complexNode.addPrimitiveRoleNode(node);
					primitiveRoleMap.put(role, node);
				}
				else {
					//Otherwise, add a role node, which in turn will recursively populate children
					ComplexRoleNode node = new ComplexRoleNode(complexNode, role, roleName);
					complexNode.addComplexRoleNode(node);
					complexRoleMap.put(role, node);
					reloadFromModel(node);
				}
			}
		}
	}
	
	/** Reloads the tree node based on its underlying role information */
	private void reloadFromModel(ComplexRoleNode roleNode) {
		for (ComplexNode node : roleNode.getComplexNodes()) {
			roleNode.remove(node);
			instanceNodeMap.remove(node.getValue(), node);
		}
		
		GUID transaction = store.begin();
		Collection<Object> i = store.getInstances(transaction, ((ComplexNode)roleNode.getParent()).getValue(), roleNode.getRole());
		store.commit(transaction);
		for (Object instance : i) {
			transaction = store.begin();
			String instanceName = store.getName(transaction, instance);
			GUID type = store.getType(transaction, instance);
			String typeName = store.getName(transaction, type);
			store.commit(transaction);
			ComplexNode node = new ComplexNode(roleNode, instance, instanceName, typeName);
			instanceNodeMap.put(instance, node);
			roleNode.add(node);
			reloadFromModel(node);
		}
	}
	
	private void reloadNodesPertainingTo(Object value) {
		Collection<ComplexNode> nodes = new ArrayList<ComplexNode>(instanceNodeMap.get(value));
		for (ComplexNode node : nodes) {
			reloadFromModel(node);
			reload(node);
		}
	}
	
	private void reloadNodesPertainingTo(GUID complexRole) {
		Collection<ComplexRoleNode> nodes = new ArrayList<ComplexRoleNode>(complexRoleMap.get(complexRole));
		for (ComplexRoleNode node : nodes) {
			reloadFromModel(node);
			reload(node);
		}
	}


	Set<PDChange<GUID, Object, GUID>> matchingTemplates
	   = new HashSet<PDChange<GUID, Object, GUID>>();


	@Override
	public Set<PDChange<GUID, Object, GUID>> getMatchingTemplates() {
		// TODO Auto-generated method stub
		return matchingTemplates;
	}
}
