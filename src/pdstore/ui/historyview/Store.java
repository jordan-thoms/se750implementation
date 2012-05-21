package pdstore.ui.historyview;

import java.util.Iterator;
import java.util.LinkedList;

import pdstore.GUID;

public class Store {
	LinkedList<Transaction> transactions;
	
	public Store(){
		transactions = new LinkedList<Transaction>();
	}
	
	/**
	 * Adds a Transaction into the current PDstore
	 * @param t The Transaction object will be added into the PDstore
	 */
	public void add(Transaction t){
		transactions.add(t);
	}
	
	/**
	 * Override toString method, always returns "PDStore"
	 */
	public String toString(){
		return "PDStore";
	}
	
	/**
	 * Gets number of transactions in the current PDstore
	 * @return Returns number of transaction
	 */
	public int size(){
		return transactions.size();
	}
	
	/**
	 * Gets number of branches in the current transaction
	 * @return Returns number of branches
	 */
	public int getBranchCount(String transGUID){
		Transaction t;
		Iterator<Transaction> iter = transactions.iterator();
		int bc = 1;
		while (iter.hasNext()){
			t = iter.next();
			if (t.id.toString().equals(transGUID)){
				bc = t.numBranches;
				break;
			}
		}
		
		return bc;
	}
	
	/**
	 * Gets position of branches in the current transaction
	 * @return Returns position number of branches
	 */
	public int getBranchPosition(String transGUID){
		Transaction t;
		Iterator<Transaction> iter = transactions.iterator();
		int bp = 0;
		while (iter.hasNext()){
			t = iter.next();
			if (t.id.toString().equals(transGUID)){
				bp = t.branchPosition;
				break;
			}
		}
		
		return bp;
	}
	
}
