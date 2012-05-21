package pdqueue.concurrency;

import pdqueue.dal.*;
import pdqueue.tools.ItemState;
import pdstore.GUID;
import pdstore.IsolationLevel;
import pdstore.dal.PDInstance;

public interface Operable {

	/**
	 * Starts a new transaction at specified isolation level
	 * 
	 * @param isolationLevel
	 * @return
	 * @throws PDQException
	 */
	public GUID newTrasnaction(IsolationLevel isolationLevel)
			throws PDQException;

	/**
	 * Commits a PDStore transaction
	 * 
	 * @param transaction
	 * @return
	 */
	public boolean commit(GUID transaction);

	/**
	 * Aborts a PDStore transaction, and rollback will be performed
	 * 
	 * @param transaction
	 * @return
	 */
	public boolean abort(GUID transaction);

	/**
	 * Links instance2 to instance1 by the specified role
	 * 
	 * @param transaction
	 * @param instance1
	 * @param role
	 * @param instance2
	 */
	public void insert(GUID transaction, PDInstance instance1, GUID role,
			PDInstance instance2);

	/**
	 * Removes an item from the queue
	 * 
	 * @param transaction
	 * @param item
	 */
	public void remove(GUID transaction, PDItem item, PDQueue queue);

	/**
	 * Collects all instances which have linked to a PDQueue object, filters
	 * them with specified state, and sorts them in FIFO order
	 * 
	 * @param transaction
	 * @param currentState
	 * @return the oldest item
	 */
	public PDItem searchItem(GUID transaction, ItemState currentState, PDQueue queue);

	/**
	 * reads the state of a given PDItem instance
	 * 
	 * @param transaction
	 * @param item
	 * @return
	 */
	public String readState(GUID transaction, PDItem item);

	/**
	 * changes the state of a given PDItem instance
	 * 
	 * @param transaction
	 * @param item
	 * @param newState
	 * @return
	 */
	public void changeState(GUID transaction, PDItem item, ItemState newState);

}
