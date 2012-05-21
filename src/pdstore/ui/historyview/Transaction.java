package pdstore.ui.historyview;

import java.awt.Canvas;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import pdstore.GUID;

public class Transaction {
	LinkedList<Change> changeList;
	GUID id = null;
	GUID branchID = null;
	Date timestamp;
	Canvas canv;
	JDotRender dr;
	int numBranches;
	int branchPosition;
	
	public Transaction(){
		changeList = new LinkedList<Change>();
		canv = new Canvas();
		numBranches = 1;
	}
	
	/**
	 * Adds a Change into the current Transaction
	 * @param c The Change object will be added into the Transaction
	 */
	public void addChange(Change c){
		changeList.add(c);
		id = c.transactionID;
		if (branchID == null){
			branchID = c.transactionID.getBranchID();
			timestamp = c.transactionID.getDate();
		}
	}
	
	/**
	 * Override toString method, always returns "Transaction"
	 */
	public String toString(){
		return "Transaction";
	}
	
	/**
	 * Gets number of changes in the current transaction
	 * @return Returns number of changes
	 */
	public int size(){
		return changeList.size();
	}
	
	/**
	 * Gets Transaction ID
	 * @return Returns the Transaction ID
	 */
	public GUID getTransactionID(){
		return id;
	}
}
