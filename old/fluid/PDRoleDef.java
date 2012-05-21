package fluid;

import pdstore.GUID;
import pdstore.PDStore;

public enum PDRoleDef {
	ACCESSIBLE_ROLE (PDStore.ACCESSIBLE_ROLES_ROLEID),
	ACCESSOR_ROLE (PDStore.ACCESSOR_ROLEID);
	
	private GUID roleType;
	private PDRoleDef(GUID g){
		roleType = g;
	}
	public GUID getGUID(){
		return roleType;
	}
}
