package pdedit.dal.util;

import java.util.EventObject;

public class DALEvent extends EventObject {

	/**
	 * 
	 */
	public static int MODEL_CREATED = 0;
	public static int TYPE_CREATED = 1;
	public static int ROLE_CREATED = 2;
	public static int MODEL_NAME_CHANGED = 4;
	
	private static final long serialVersionUID = 1L;
	
	private ModelAccessor editor;
	private DNDHandler handler;
	private int evt = -1;

	public DALEvent(Object source) {
		super(source);
		if (source instanceof ModelAccessor){
			editor = ModelAccessor.class.cast(source);
		}else if (source instanceof DNDHandler){
			handler = DNDHandler.class.cast(source);
		}
	}
	
	public DALEvent(Object source, int event){
		this(source);
		evt = event;
	}
	
	public int getEvent() {
		return evt;
	}

	public Object getSource(){
		if (editor != null){
			return editor;
		}else if (handler != null){
			return handler;
		}
		return null;
	}

}
