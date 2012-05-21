package pdstore.sparql;

import pdstore.GUID;


public class Variable extends GUID {
	
	String name;
	boolean isSelected = false;
	
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Variable (String name){
		super();
		GUID.load(this);
		this.name =  name;
	}
	
	public String toString(){
		return "?" + name;
	}
	
}
