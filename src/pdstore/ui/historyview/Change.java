package pdstore.ui.historyview;

import java.sql.Timestamp;
import java.util.Date;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.generic.PDChange;

public class Change implements Comparable {
	ChangeType type;
	Object instance1;
	Object instance2;
	GUID role2;
	GUID transactionID;
	Date timestamp;
	JLineRender lr;
	
	public Change(PDChange<GUID, Object, GUID> change){
		type = change.getChangeType();
		instance1 = change.getInstance1();
		instance2 = change.getInstance2();
		role2 = change.getRole2();
		transactionID = change.getTransaction();
		timestamp = change.getRole2().getDate();
	}
	
	public Change(){
		
	}
	public String toString(){
		return "Change";
	}

	@Override
	public int compareTo(Object obj) {
		if(this.transactionID.getBranchID().toString() == ((Change)obj).transactionID.getBranchID().toString()) return 0;
		if(this.transactionID.getBranchID().toString().compareTo(((Change)obj).transactionID.getBranchID().toString()) > 0) return 1;
		else return 0;
			
	}
	
	  
}
