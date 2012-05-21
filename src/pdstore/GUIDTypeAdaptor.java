package pdstore;

import pdstore.generic.TypeAdapter;

public class GUIDTypeAdaptor extends GenericTypeAdapter<GUID, Object, GUID>{
	
	@Override
	public Object instanceFromRole(GUID t) {
		return t;
	}

	@Override
	public Object instanceFromTransaction(GUID t) {
		return t;
	}
	
	@Override
	public GUID RoleFromInstance(Object t) {
		return (GUID) t;
	}

	@Override
	public GUID TransactionFromInstance(Object t) {
		return (GUID) t;
	}
	@Override
	public GUID getOpenID(GUID t) {
		// use non-first GUIDs for open transaction
		if(t==null) return null;
		return t.getFirst().getPartner();
	}

	@Override
	public GUID getDurableID(GUID branch) {
		// use first guids as durable GUIDS
		return GUID.newTransactionId(branch).getFirst();
	}

	@Override
	public GUID getBranchID(GUID t) {
		if(t==null) return null;
		return t.getBranchID();
	}
	
	@Override
	public GUID newTransactionId(GUID branch) {
		return GUID.newTransactionId(branch);
	}
	
	@Override
	public GUID getFirst(GUID t) {
		return t.getFirst();
	}
	
	@Override
	public GUID getPartner(GUID t) {
		return t.getPartner();
	}

	@Override
	public boolean isDurable(GUID t) {
		//TODO: the status of the null transaction must be fixed
//		if(t==null) return false;
		return t.isFirst();
	}

	@Override
	public GUID USES_ROLE_ROLEID() {
		return PDStore.USES_ROLE_ROLEID;
	}

	@Override
	public GUID ISOLATIONLEVEL_ROLEID() {
		return PDStore.ISOLATIONLEVEL_ROLEID;
	}

	@Override
	public GUID maxTransactionId(GUID branch) {
		GUID maxTransactionId = GUID.maxTransactionId();
		GUID.setBranchId(maxTransactionId, branch);
		return maxTransactionId;
	}


}
