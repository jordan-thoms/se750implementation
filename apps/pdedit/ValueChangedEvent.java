package pdedit;

import java.util.EventObject;

public class ValueChangedEvent extends EventObject {
	private static final long serialVersionUID = -9019002737307059927L;
	private String value;
	
	public ValueChangedEvent(Object source, String value) {
		super(source);
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
}
