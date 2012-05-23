package pdstore.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.List;
import java.util.Collection;

import pdstore.Blob;
import pdstore.GUID;
import pdstore.PDStoreException;
import pdstore.generic.PDChange;
import pdstore.notify.PDListener;

public interface ServerInterface extends Remote {

	GUID begin() throws PDStoreException, RemoteException;

	GUID begin(GUID branch) throws PDStoreException, RemoteException;

	GUID branch(GUID parentTransaction) throws PDStoreException,
			RemoteException;

	void merge(GUID branchId, GUID parentTransaction) throws PDStoreException,
			RemoteException;

	GUID commit(GUID transaction) throws PDStoreException, RemoteException;

	void rollback(GUID transaction) throws PDStoreException, RemoteException;

	Collection<Object> getInstances(GUID transaction, Object instance1, GUID role2)
			throws PDStoreException, RemoteException;

	Object getInstance(GUID transaction, Object instance1, GUID role2)
			throws PDStoreException, RemoteException;

	void addLink(GUID transaction, Object instance1, GUID role2,
			Object instance2) throws PDStoreException, RemoteException;

	void removeLink(GUID transaction, Object instance1, GUID role2,
			Object instance2) throws PDStoreException, RemoteException;

	Collection<Object> getAllInstancesInRole(GUID transaction, GUID role)
			throws PDStoreException, RemoteException;

	Collection<Object> getAllInstancesOfType(GUID transactionId, GUID typeId)
			throws PDStoreException, RemoteException;

	boolean instanceExists(GUID transaction, Object instance)
			throws PDStoreException, RemoteException;

	GUID getId(GUID transaction, String name) throws PDStoreException,
			RemoteException;

	Collection<GUID> getIds(GUID transaction, String name) throws PDStoreException,
			RemoteException;

	String getName(GUID transaction, GUID instanceId) throws PDStoreException,
			RemoteException;

	void setName(GUID transaction, GUID instanceId, String name)
			throws PDStoreException, RemoteException;

	void removeName(GUID transaction, GUID instanceId) throws PDStoreException,
			RemoteException;

	Blob getIcon(GUID transaction, GUID instanceId) throws PDStoreException,
			RemoteException;

	void setIcon(GUID transaction, GUID instanceId, Blob icon)
			throws PDStoreException, RemoteException;

	void removeIcon(GUID transaction, GUID instanceId) throws PDStoreException,
			RemoteException;

	GUID getRepository() throws PDStoreException, RemoteException;

	void createRelation(GUID transaction, GUID type1Id, GUID role2Id,
			GUID typeId2) throws RemoteException;

	void createType(GUID transaction, GUID modelId, GUID typeId, String typeName)
			throws RemoteException;

	void createModel(GUID transaction, GUID modelId, String modelName)
			throws RemoteException;

	void createRelation(GUID transaction, GUID typeId1, String role1Name,
			String role2Name, GUID role2, GUID typeId2) throws RemoteException;

	GUID getAccessorId(GUID transaction, GUID role2Id) throws RemoteException;

	List<PDListener<GUID, Object, GUID>> getDetachedListenerList()
			throws PDStoreException, RemoteException;

	List<PDListener<GUID, Object, GUID>> getInterceptorList()
			throws PDStoreException, RemoteException;
	
	Collection<GUID> getAccessibleRoles(GUID transaction, Object type)  throws RemoteException;
	/**
	 * This method is a method specifically for the remote PDStore
	 * It serves as the reverse implementation of a listener.
	 * The method is blocked until the next transaction commits.
	 * 
	 * @return
	 * @throws RemoteException
	 */
	List<PDChange<GUID, Object, GUID>> nextTransaction() throws RemoteException;

	List<List<PDChange<GUID, Object, GUID>>> newTransactions()  throws RemoteException;

}
