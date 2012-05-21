package fluid.table;

import java.util.Collection;

import javax.swing.JButton;

import pdstore.GUID;

public class PDButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GUID role;
	private GUID instance;
	private Collection<Object> instances;
	
	public void setInstance(GUID instance) {
		this.instance = instance;
	}
	public GUID getInstance() {
		return instance;
	}
	public void setRole(GUID role) {
		this.role = role;
	}
	public GUID getRole() {
		return role;
	}
	public void setInstances(Collection<Object> instances) {
		this.instances = instances;
	}
	public Collection<Object> getInstances() {
		return instances;
	}
}
