package pdintegrate.ASM;

import java.util.HashMap;
import java.util.Map;

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
	private Class<?> classObj;
	private Map<String, FieldInfo> foundFields;

	public ClassInfo(Class<?> classObj) {
		this.classObj = classObj;
		this.foundFields = new HashMap<String, FieldInfo>();
	}


	/**
	 * Returns the class's fully qualified name, but with slashes instead of 
	 * dots.
	 * @return
	 */
	public String getOldName() {
		return classObj.getCanonicalName().replace('.', '/');
	}

	/**
	 * Returns the fully qualified name of the translated version of this class,
	 * but with slashes instead of dots.
	 * @return
	 */
	public String getNewName() {
		return classObj.getCanonicalName().replace('.', '/') + "Rewrite";
	}

	public FieldInfo getVictimField(String fieldName) {
		return foundFields.get(fieldName);
	}
	
	public void addVictimField(FieldInfo f) {
		if (foundFields.containsKey(f.name)) {
			foundFields.remove(f.name);
		}
		foundFields.put(f.name, f);
	}

	public Map<String, FieldInfo> getVictimFields() {
		return foundFields;
	}
	
	
}
