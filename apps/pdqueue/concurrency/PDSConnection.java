package pdqueue.concurrency;

import java.util.ArrayList;
import java.util.List;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;

/**
 * PDStore Connector.
 * Limits that at most one PDStore instance exist all the time. 
 * @author mcai023
 *
 */
public class PDSConnection {
	
	private static PDStore store = new PDStore("QueueBase");;
	private static PDSimpleWorkingCopy singletonCopy = new PDSimpleWorkingCopy(store);
	
	/**
	 * Returns the instance of PDStore
	 * @return
	 */
	public static PDStore getStore() {
		return store;
	}
	
	/**
	 * Returns a new copy of PDSimpleWorkingCopy
	 * @return
	 */
	public static PDSimpleWorkingCopy getNewCopy() {
		return new PDSimpleWorkingCopy(store);
	}
	
	public static PDSimpleWorkingCopy getSingletonCopy() {
		return singletonCopy;
	}

}
