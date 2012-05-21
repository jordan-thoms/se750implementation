package fluid;

import pdstore.GUID;
import pdstore.PDStore;

public enum PDTypeDef {
	BLOB (PDStore.BLOB_TYPEID),
	BOOLEAN (PDStore.BOOLEAN_TYPEID),
	CHAR (PDStore.CHAR_TYPEID),
	DOUBLE (PDStore.DOUBLE_PRECISION_TYPEID),
	INTEGER (PDStore.INTEGER_TYPEID),
	STRING (PDStore.STRING_TYPEID),
	TIMESTAMP (PDStore.TIMESTAMP_TYPEID),
	PDMODELTYPE (PDStore.TYPE_TYPEID);
	
	private GUID typeID;
	private PDTypeDef(GUID g){
		typeID = g;
	}
	
	public boolean isPrimative(){
		if (this == PDMODELTYPE){
			return false;
		}else{
			return true;
		}
	}
	
	public GUID getGUID(){
		return typeID;
	}
	
	public static void main(String[] args) {
		System.out.println(PDTypeDef.class.getName()+" Test:");
		PDTypeDef t = PDTypeDef.INTEGER;
		System.out.println("\tTest Type GUID Equality("+PDTypeDef.class.getSimpleName()+".INTEGER == PDStore.INTEGER_TYPEID):" + (t.getGUID() == PDStore.INTEGER_TYPEID));
		System.out.println("\tTest Type is Primative (on Integer): " + t.isPrimative());
		t = PDTypeDef.PDMODELTYPE;
		System.out.println("\tTest Type is Primative (on PDMODELTYPE): " + t.isPrimative());
		System.out.println("End of test.");
	}
}
