/**
 * 
 */
package pdintegrate.ASM;

public class FieldInfo {
	public int access;
	public String name;
	public String desc;
	public String signature;
	public Object value;

	public FieldInfo(int access, String name, String desc, String signature, Object value) {
		this.access = access;
		this.name = name;
		this.desc = desc;
		this.signature = signature;
		this.value = value;
	}
}