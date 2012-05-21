package pdstore.ui.treeview;

import java.util.*;

import javax.swing.tree.TreeNode;

import pdstore.GUID;
import pdstore.PDStore;

public class PrimitiveRoleNode implements TreeNode {
	private GUID role;
	private String roleName;
	private Object value;
	private ComplexNode parent;
	
	public PrimitiveRoleNode(ComplexNode parent, GUID role, String roleName, Object value) {
		this.parent = parent;
		this.value = value;
		this.role = role;
		this.roleName = roleName;
	}
	
	public GUID getRole() {
		return role;
	}
	
	public String getRoleName() {
		return roleName;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public Enumeration<?> children() {
		return Collections.enumeration(Collections.emptyList());
	}

	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	@Override
	public int getChildCount() {
		return 0;
	}

	@Override
	public int getIndex(TreeNode node) {
		return -1;
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}
	
	public void setParent(ComplexNode parent) {
		this.parent = parent;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}
	
	@Override
	public String toString() {
		return roleName + ": " + (value != null ? value : "");
	}
}