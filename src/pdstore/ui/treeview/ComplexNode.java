package pdstore.ui.treeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import pdstore.GUID;
import pdstore.PDStore;

/**
 * A tree node that represents a complex type. Displays relationships as child nodes.
 * */
public class ComplexNode implements TreeNode {
	private LinkedHashMap<GUID, ComplexRoleNode> complexRoleNodes = new LinkedHashMap<GUID, ComplexRoleNode>();
	private LinkedHashMap<GUID, PrimitiveRoleNode> primitiveRoleNodes = new LinkedHashMap<GUID, PrimitiveRoleNode>();
	
	private String instanceName;
	private String typeName;
	private TreeNode parent;
	private boolean canLoadChildren;
	private Object instanceValue;
	
	public ComplexNode(TreeNode parent, Object value, String instanceName, String typeName) {
		this.parent = parent;
		this.instanceValue = value;
		this.instanceName = instanceName;
		this.typeName = typeName;

		canLoadChildren = true;
		//Check if this instance is already present in one of the ancestor nodes
		//in order to prevent infinite recursion
		for (TreeNode currentParent = parent; currentParent != null; currentParent = currentParent.getParent()) {
			if (currentParent instanceof ComplexNode 
					&& value.equals(((ComplexNode)currentParent).getValue())) {
				canLoadChildren = false;
				break;
			}
		}
		
		//Hook onto primitive link value change events on this instance
		/*store.getListenerDispatcher().addListener(this,
				new PDChange<GUID, Object, GUID>(null, null, value, null, null));*/
	}
	
	public void addComplexRoleNode(ComplexRoleNode node) {
		complexRoleNodes.put(node.getRole(), node);
		node.setParent(this);
	}
	
	public void removeComplexRoleNode(ComplexRoleNode node) {
		complexRoleNodes.remove(node.getRole());
		node.setParent(null);
	}
	
	public List<ComplexRoleNode> getComplexRoleNodes() {
		return new ArrayList<ComplexRoleNode>(complexRoleNodes.values());
	}
	
	public void addPrimitiveRoleNode(PrimitiveRoleNode node) {
		primitiveRoleNodes.put(node.getRole(), node);
		node.setParent(this);
	}
	
	public void removePrimitiveRoleNode(PrimitiveRoleNode node) {
		primitiveRoleNodes.remove(node.getRole());
		node.setParent(null);
	}
	
	public List<PrimitiveRoleNode> getPrimitiveRoleNodes() {
		return new ArrayList<PrimitiveRoleNode>(primitiveRoleNodes.values());
	}
	
	@Override
	public Enumeration<?> children() {
		Vector<TreeNode> children = new Vector<TreeNode>();
		children.addAll(primitiveRoleNodes.values());
		children.addAll(complexRoleNodes.values());
		return children.elements();
	}

	@Override
	public boolean getAllowsChildren() {
		return canLoadChildren;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		if (childIndex < primitiveRoleNodes.size())
			return getPrimitiveRoleNodes().get(childIndex);
		return getComplexRoleNodes().get(childIndex - primitiveRoleNodes.size());
	}

	@Override
	public int getChildCount() {
		return primitiveRoleNodes.size() + complexRoleNodes.size();
	}

	@Override
	public int getIndex(TreeNode node) {
		int index = getPrimitiveRoleNodes().indexOf(node);
		if (index >= 0)
			return index;
		index = getComplexRoleNodes().indexOf(node);
		if (index >= 0)
			return index + primitiveRoleNodes.size();
		return -1;
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public boolean isLeaf() {
		return primitiveRoleNodes.isEmpty() && complexRoleNodes.isEmpty();
	}
	
	@Override
	public String toString() {
		return typeName + (instanceName != null ? " ["+instanceName+"]" : "");
	}
	
	public boolean canLoadChildren() {
		return canLoadChildren;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public String getName() {
		return instanceName;
	}
	
	public void setName(String name) {
		instanceName = name;
	}
	
	public Object getValue() {
		return instanceValue;
	}
	
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
}