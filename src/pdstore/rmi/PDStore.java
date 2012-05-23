package pdstore.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nz.ac.auckland.se.genoupe.tools.Debug;
import pdstore.Blob;
import pdstore.GUID;
import pdstore.IsolationLevel;
import pdstore.PDStoreException;
import pdstore.dal.PDWorkingCopy;
import pdstore.generic.PDChange;
import pdstore.notify.PDListener;

public class PDStore extends pdstore.PDStore {
	private ServerInterface server;
	private ReverseListener reverseListener;
	private Thread reverseListenerThread;
	private List<PDListener<GUID, Object, GUID>> interceptorList = new ArrayList<PDListener<GUID, Object, GUID>>();
	private List<PDListener<GUID, Object, GUID>> detachedListenerList = new ArrayList<PDListener<GUID, Object, GUID>>();

	public ServerInterface getServer() {
		return server;
	}

	public PDStore(String host) {
		this(host, "PDStore");
	}
	
	public PDStore(String host, String rmiKey) {
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(host);
			server = (ServerInterface) registry.lookup(rmiKey);
		} catch (Exception e) {
			throw new PDStoreException("Cannot connect to server", e);
		}
		reverseListener = new ReverseListener(this);
		reverseListenerThread = new Thread(reverseListener);
		reverseListenerThread.start();
	}

	/**
	 * Link an instance to another through a role
	 * 
	 * @param transaction
	 *            - current transaction id
	 * @param instance1
	 *            - accessor instance
	 * @param type1
	 *            - type id of the accessor instance
	 * @param role2
	 *            - role to link with
	 * @param instance2
	 *            - object to be linked to
	 */
	public void addLink(GUID transaction, Object instance1, GUID type1,
			GUID role2, Object instance2) throws PDStoreException {
		try {
			server.addLink(transaction, instance1, role2, instance2);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	/**
	 * Gets all the instances of the given type that are stored in the database.
	 * 
	 * @param transactionId
	 *            transaction id on which to search
	 * @param typeid
	 *            ID of the type
	 * @return all stored instances of that type
	 * @throws PDStoreException
	 */
	public Collection<Object> getAllInstancesOfType(GUID transaction, GUID type)
			throws PDStoreException {
		try {
			return server.getAllInstancesOfType(transaction, type);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	/**
	 * @see PDWorkingCopy#getId(String)
	 * @param transaction
	 *            - current transaction id
	 * @param name
	 * @return
	 * @throws PDStoreException
	 */
	public GUID getId(GUID transaction, String name) throws PDStoreException {
		try {
			return server.getId(transaction, name);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	/**
	 * @see PDWorkingCopy#getIds(String)
	 * @param transaction
	 *            - current transaction id
	 * @param name
	 * @return
	 * @throws PDStoreException
	 */
	public Collection<GUID> getIds(GUID transaction, String name)
			throws PDStoreException {
		try {
			return server.getIds(transaction, name);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	/**
	 * @see PDWorkingCopy#getName(GUID)
	 * @param transaction
	 *            - current transaction id
	 * @param instanceId
	 * @return
	 * @throws PDStoreException
	 */
	public String getName(GUID transaction, GUID instanceId)
			throws PDStoreException {
		try {
			return server.getName(transaction, instanceId);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	/**
	 * @see PDWorkingCopy#removeName(GUID)
	 * @param transaction
	 *            - current transaction id
	 * @param instanceId
	 * @throws PDStoreException
	 */
	public void removeName(GUID transaction, GUID instanceId)
			throws PDStoreException {
		try {
			server.removeName(transaction, instanceId);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
		return;
	}

	/**
	 * @see PDWorkingCopy#getIcon(GUID)
	 * @param transaction
	 *            - current transaction id
	 * @param instanceId
	 * @return
	 * @throws PDStoreException
	 */
	public Blob getIcon(GUID transaction, GUID instanceId)
			throws PDStoreException {
		try {
			return server.getIcon(transaction, instanceId);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	/**
	 * Removes the icon of the given instance using the given transaction.
	 * Having this more abstract method on the low level of a PDStore is
	 * necessary because the PDSQLStore needs a special implementation of these
	 * methods.
	 * 
	 * @see PDWorkingCopy#removeIcon(GUID)
	 * @param transaction
	 *            - current transaction id
	 * @param instanceId
	 * @throws PDStoreException
	 */
	public void removeIcon(GUID transaction, GUID instanceId)
			throws PDStoreException {
		try {
			server.removeIcon(transaction, instanceId);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public GUID getRepository() throws PDStoreException {
		try {
			return server.getRepository();
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	/***
	 * This creates a PDModel and adds it to the model
	 * 
	 * 
	 * @param transaction
	 * @param modelId
	 * @param typeId
	 * @param typeName
	 */
	public void createModel(GUID transaction, GUID modelId, String modelName) {
		try {
			server.createModel(transaction, modelId, modelName);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	/***
	 * This create a PDType and adds it to the model
	 * 
	 * @param transaction
	 * @param modelId
	 * @param typeId
	 * @param typeName
	 */
	public void createType(GUID transaction, GUID modelId, GUID typeId,
			String typeName) {
		try {
			server.createType(transaction, modelId, typeId, typeName);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	/***
	 * This method creates a PDRole between two types with 2 roles
	 * 
	 * @param transaction
	 * @param type1Id
	 * @param role2Id
	 * @param typeId2
	 */
	public void createRelation(GUID transaction, GUID type1Id, GUID role2Id,
			GUID typeId2) {
		try {
			server.createRelation(transaction, type1Id, role2Id, typeId2);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	/***
	 * This method creates a PDRole between two types with 2 roles with role
	 * names
	 * 
	 * @param transaction
	 * @param typeId1
	 * @param role1Name
	 * @param role2Name
	 * @param role2Id
	 * @param typeId2
	 */
	public void createRelation(GUID transaction, GUID typeId1,
			String role1Name, String role2Name, GUID role2, GUID typeId2) {
		try {
			server.createRelation(transaction, typeId1, role1Name, role2Name,
					role2, typeId2);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public GUID getAccessorType(GUID transaction, GUID role2Id) {
		try {
			return server.getAccessorId(transaction, role2Id);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}
	
	public Collection<GUID> getAccessibleRoles(GUID transaction, Object type) {
		try {
			return server.getAccessibleRoles(transaction, type);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public void addLink(GUID transaction, Object instance1, GUID role2,
			Object instance2) throws PDStoreException {
		Debug.assertTrue(instance1 != null, "instance1 must not be null.");
		Debug.assertTrue(role2 != null, "role2 must not be null.");
		Debug.assertTrue(instance2 != null, "instance2 must not be null.");

		try {
			server.addLink(transaction, instance1, role2, instance2);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public GUID begin() throws PDStoreException {
		try {
			return server.begin();
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public GUID branch(GUID parentTransaction) throws PDStoreException {
		try {
			return server.branch(parentTransaction);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}

	}

	public Collection<Object> getAllInstancesInRole(GUID transaction, GUID role)
			throws PDStoreException {
		try {
			return server.getAllInstancesInRole(transaction, role);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public Object getInstance(GUID transaction, Object instance1, GUID role2)
			throws PDStoreException {
		Debug.assertTrue(instance1 != null, "instance1 must not be null.");
		Debug.assertTrue(role2 != null, "role2 must not be null.");

		try {
			return server.getInstance(transaction, instance1, role2);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public Collection<Object> getInstances(GUID transaction, Object instance1,
			GUID role2) throws PDStoreException {
		Debug.assertTrue(instance1 != null, "instance1 must not be null.");
		Debug.assertTrue(role2 != null, "role2 must not be null.");

		try {
			return server.getInstances(transaction, instance1, role2);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public boolean instanceExists(GUID transaction, Object instance)
			throws PDStoreException {
		Debug.assertTrue(instance != null,
				"instance to look for in instanceExists must not be null.");

		try {
			return server.instanceExists(transaction, instance);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public void merge(GUID transaction, GUID parentTransaction)
			throws PDStoreException {
		try {
			server.merge(transaction, parentTransaction);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public void removeLink(GUID transaction, Object instance1, GUID role2,
			Object instance2) throws PDStoreException {
		Debug.assertTrue(instance1 != null, "instance1 must not be null.");
		Debug.assertTrue(role2 != null, "role2 must not be null.");
		Debug.assertTrue(instance2 != null, "instance2 must not be null.");

		try {
			server.removeLink(transaction, instance1, role2, instance2);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public void addChange(PDChange<GUID, Object, GUID> change)
			throws PDStoreException {
		throw new PDStoreException("Unsupported when using a remote server");
	}

	public GUID begin(GUID branch, IsolationLevel isolationLevel) throws PDStoreException {
		try {
			return server.begin(branch);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public GUID commit(GUID transaction) throws PDStoreException {
		try {
			return server.commit(transaction);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public void rollback(GUID transaction) throws PDStoreException {
		try {
			server.rollback(transaction);
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public Iterator<PDChange<GUID, Object, GUID>> iterator() {
		throw new PDStoreException("Unsupported when using a remote server");
	}

	public List<PDListener<GUID, Object, GUID>> getDetachedListenerList()
			throws PDStoreException {
		return detachedListenerList;
	}

	public List<PDListener<GUID, Object, GUID>> getInterceptorList()
			throws PDStoreException {
		return interceptorList;
	}

	public List<PDChange<GUID, Object, GUID>> nextTransaction()
			throws PDStoreException {
		try {
			return server.nextTransaction();
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}

	public List<List<PDChange<GUID, Object, GUID>>> newTransactions() 
	  throws PDStoreException {
		try {
			return server.newTransactions();
		} catch (RemoteException e) {
			throw new PDStoreException("Server error", e);
		}
	}
}
