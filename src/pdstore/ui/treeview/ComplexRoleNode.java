package pdstore.ui.treeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import pdstore.GUID;
import pdstore.PDStore;

public class ComplexRoleNode implements TreeNode {
	private List<ComplexNode> instances = new ArrayList<ComplexNode>();
	private GUID role;
	private String roleName;
	
	private ComplexNode parent;
	
	public ComplexRoleNode(ComplexNode parent, GUID role, String roleName) {
		this.parent = parent;
		this.role = role;
		this.roleName = roleName;
	}
	
	public void add(ComplexNode node) {
		instances.add(node);
		node.setParent(this);
	}
	
	public void remove(ComplexNode node) {
		instances.remove(node);
		node.setParent(null);
	}
	
	public List<ComplexNode> getComplexNodes() {
		return new ArrayList<ComplexNode>(instances);
	}
	
	public ComplexNode getChildWithInstance(Object instance) {
		for (ComplexNode i : instances) {
			if (i.getValue() == instance) {
				return i;
			}
		}
		return null;
	}
	
	public Object getAccessor() {
		return parent.getValue();
	}
	
	public GUID getRole() {
		return role;
	}
	
	public String getRoleName() {
		return roleName;
	}

	@Override
	public Enumeration<?> children() {
		return new Vector<ComplexNode>(instances).elements();
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public TreeNode getChildAt(int index) {
		return instances.get(index);
	}

	@Override
	public int getChildCount() {
		return instances.size();
	}

	@Override
	public int getIndex(TreeNode arg0node) {
		return instances.indexOf(arg0node);
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public boolean isLeaf() {
		return instances.isEmpty();
	}

	@Override
	public String toString() {
		return roleName;
	}
	
	public void setParent(ComplexNode parent) {
		this.parent = parent;
	}
}
