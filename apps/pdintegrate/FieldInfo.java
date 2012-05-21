/**
 * 
 */
package pdintegrate;

import pdstore.GUID;

public class FieldInfo {
	public int access;
	public String name;
	public String desc;
	public String signature;
	public Object value;
	public GUID roleID;

	public FieldInfo(int access, String name, String desc, String signature, Object value) {
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.value = value;
	}

	public GUID getRoleID() {
		if (this.roleID == null) {
			this.roleID = new GUID();
		}
		return roleID;
	}
}