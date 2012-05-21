package updatecheck;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;

/**
 * Test class for static analysis of database operations. A static analyzer
 * should identify the following access operations (worst case, i.e. all
 * instructions executed):
 * store.getInstance(transaction, accountId, balanceRoleId);
 * store.removeLink(transaction, accountId, balanceRoleId, ?);
 * store.addLink(transaction, accountId, balanceRoleId, ?);
 * 
 * The static analyzer should identify a select-for-update of (accountId, balanceRoleId)
 * in store.getInstance(transaction, accountId, balanceRoleId);
 * 
 * @author gweb017
 * 
 */
public class ChangeBalance {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PDStore store = new PDStore("ChangeBalance");

		GUID accountId = new GUID("22222222222222222222222222222222");
		GUID balanceRoleId = new GUID("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		int change = 100;
		int balance;
		GUID transaction = store.begin();
		balance = (Integer) store.getInstance(transaction, accountId,
				balanceRoleId);
		if (balance + change > 0) {
			store.removeLink(transaction, accountId, balanceRoleId, balance);
			store.addLink(transaction, accountId, balanceRoleId, balance
					+ change);
		}		
		store.commit(transaction);
		
		
		GUID accountId2 = new GUID("22222222222222222222222222222221");
		GUID balanceRoleId2 = new GUID("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
		GUID transaction2 = store.begin();
		int balance2 = (Integer) store.getInstance(transaction2, accountId2, balanceRoleId2);
		method(store, transaction2, accountId2, balanceRoleId2, balance2);
		store.commit(transaction2);
	}
	
	private static void method(PDStore store, GUID transaction, GUID accountId, GUID balanceRoleId, int balance){
		int change = 100;
		if (balance + change > 0) {
			store.removeLink(transaction, accountId, balanceRoleId, balance);
			store.addLink(transaction, accountId, balanceRoleId, balance+change);
		}
	}
}
