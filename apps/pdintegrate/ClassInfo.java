package pdintegrate;

import java.util.HashMap;
import java.util.Map;

import pdstore.GUID;

/**
 * This class stores the information required to rewrite a given class at
 * the bytecode level.
 * 
 * From this class we can answer questions such as:
 * 
 * a given class's old name
 * its new name
 * its set of fields that have been rewritten
 * 
 * @author dbra072
 *
 */
public class ClassInfo {
	private String className;
	private Map<String, FieldInfo> uniFields;
	private Map<String, FieldInfo> multiFields;
	private boolean explicit = false;
	public GUID TypeID;

	public ClassInfo(String className) {
		this.className = className.replace('.', '/');
		this.uniFields = new HashMap<String, FieldInfo>();
		this.multiFields = new HashMap<String, FieldInfo>();
	}


	/**
	 * Returns the class's fully qualified name, but with slashes instead of 
	 * dots.
	 * @return
	 */
	public String getName() {
		return className;
	}


	public FieldInfo getUniField(String fieldName) {
		return uniFields.get(fieldName);
	}
	
	public void addUniField(FieldInfo f) {
		if (uniFields.containsKey(f.name)) {
			uniFields.remove(f.name);
		}
		uniFields.put(f.name, f);
	}

	public Map<String, FieldInfo> getUniFields() {
		return uniFields;
	}
	

	public FieldInfo getMultiField(String fieldName) {
		return multiFields.get(fieldName);
	}
	
	public void addMultiField(FieldInfo f) {
		if (multiFields.containsKey(f.name)) {
			multiFields.remove(f.name);
		}
		multiFields.put(f.name, f);
	}

	public Map<String, FieldInfo> getMultiFields() {
		return multiFields;
	}


	public void setExplicit(boolean b) {
		this.explicit = b;
	}
	
	public boolean isExplicit() {
		return this.explicit;
	}


	public GUID getTypeID() {
		if (this.TypeID == null) {
			this.TypeID = new GUID();
		}
		return this.TypeID;
	}
	
	
	
}
