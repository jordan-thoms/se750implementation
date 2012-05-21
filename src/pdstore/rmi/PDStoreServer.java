package pdstore.rmi;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

import java.util.List;
import pdstore.Blob;
import pdstore.GUID;
import pdstore.IsolationLevel;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.generic.PDChange;
import pdstore.notify.PDListener;
import pdstore.notify.PDStoreListenerService;

/**
 * RMi server for PDStore.
 * 
 * NOTE:
 * 
 * If automatic server setup as done in static initializer does not work you
 * have to ensure the following steps manually:
 * 
 * 1. First, you have to run the rmiregistry tool from the Java JRE.
 * 
 * 2. YOU HAVE TO RUN THIS CLASS WITH THE FOLLOWING VM ARGUMENT:
 * -Djava.rmi.server.codebase=file:${workspace_loc}/pdstore/bin/
 * 
 * You can set the argument in the run configuration.
 * 
 * NOTE:
 * 
 * Before you run PDStoreServer, make sure NO instance of rmiregistry.exe is
 * running already. Terminate it in the task manager if it is already running.
 * 
 * @author clut002
 * 
 */
public class PDStoreServer implements ServerInterface {

	static Process rmiregistry;

	static {
		System.setProperty("java.rmi.server.codebase", "file:"
				+ System.getProperty("user.dir") + "\\bin\\");
		try {
			rmiregistry = Runtime.getRuntime().exec(
					System.getProperty("java.home") + "\\bin\\rmiregistry.exe");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void finalize() {
		// terminate rmiregistry.exe, so there will be only one instance
		// if PDStoreServer is restarted.
		// This is not working yet, probably due to Java.
		rmiregistry.destroy();
	}

	PDStore store;

	public static void main(String[] args) {
		try {
			new PDStoreServer();
			System.err.println("Server ready.");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	public PDStoreServer() throws RemoteException, AlreadyBoundException {
		this(new PDStore("pdstore"));
	}
	
	public PDStoreServer(PDStore store) throws RemoteException, AlreadyBoundException {
		this(store, "PDStore");
	}

	public PDStoreServer(PDStore store, String rmiKey) throws RemoteException,
			AlreadyBoundException {
		this.store = store;
		store.getDetachedListenerList().add(new ServerSideMainListener(service));
		ServerInterface stub = (ServerInterface) UnicastRemoteObject
				.exportObject(this, 0);

		// Bind the remote object's stub in the registry
		Registry registry = LocateRegistry.getRegistry();
		registry.rebind(rmiKey, stub);
	}

	public void addLink(GUID transaction, Object instance1, GUID role2,
			Object instance2) throws PDStoreException, RemoteException {
		store.addLink(transaction, instance1, role2, instance2);
	}

	public GUID begin() throws PDStoreException, RemoteException {
		System.err.println("Beginning transaction.");
		return store.begin();
	}

	public GUID begin(GUID branch) throws PDStoreException, RemoteException {
		System.err.println("Beginning transaction.");
		return store.begin(branch, IsolationLevel.SNAPSHOT);
	}

	public GUID branch(GUID parentTransaction) throws PDStoreException,
			RemoteException {
		System.err.println("Beginning transaction.");
		return store.branch(parentTransaction);
	}

	public GUID commit(GUID transaction) throws PDStoreException,
			RemoteException {
		System.err.println("Committing transaction " + transaction + ".");
		return store.commit(transaction);
	}

	public void rollback(GUID transaction) throws PDStoreException,
			RemoteException {
		System.err.println("Rolling back transaction " + transaction + ".");
		store.rollback(transaction);
	}

	public Collection<Object> getAllInstancesInRole(GUID transaction, GUID role)
			throws PDStoreException, RemoteException {
		return store.getAllInstancesInRole(transaction, role);
	}

	public Collection<Object> getAllInstancesOfType(GUID transaction,
			pdstore.GUID typeId) throws PDStoreException, RemoteException {
		return store.getAllInstancesOfType(transaction, typeId);
	}

	public Object getInstance(GUID transaction, Object instance1, GUID role2)
			throws PDStoreException, RemoteException {
		return store.getInstance(transaction, instance1, role2);
	}

	public Collection<Object> getInstances(GUID transaction, Object instance1,
			GUID role2) throws PDStoreException, RemoteException {
		return store.getInstances(transaction, instance1, role2);
	}

	public boolean instanceExists(GUID transaction, Object instance)
			throws PDStoreException, RemoteException {
		return store.instanceExists(transaction, instance);
	}

	public void merge(GUID transaction, GUID parentTransaction)
			throws PDStoreException, RemoteException {
		store.merge(transaction, parentTransaction);
	}

	public void removeLink(GUID transaction, Object instance1, GUID role2,
			Object instance2) throws PDStoreException, RemoteException {
		store.removeLink(transaction, instance1, role2, instance2);
	}

	public GUID getId(GUID transaction, String name) throws PDStoreException,
			RemoteException {
		return store.getId(transaction, name);
	}

	public Collection<GUID> getIds(GUID transaction, String name)
			throws PDStoreException, RemoteException {
		return store.getIds(transaction, name);
	}

	public String getName(GUID transaction, GUID instanceId)
			throws PDStoreException {
		return store.getName(transaction, instanceId);
	}

	public void setName(GUID transaction, GUID instanceId, String name)
			throws PDStoreException, RemoteException {
		store.setName(transaction, instanceId, name);
	}

	public void removeName(GUID transaction, GUID instanceId)
			throws PDStoreException, RemoteException {
		store.removeName(transaction, instanceId);
	}

	public Blob getIcon(GUID transaction, GUID instanceId)
			throws PDStoreException, RemoteException {
		return store.getIcon(transaction, instanceId);
	}

	public void setIcon(GUID transaction, GUID instanceId, Blob icon)
			throws PDStoreException, RemoteException {
		store.setIcon(transaction, instanceId, icon);
	}

	public void removeIcon(GUID transaction, GUID instanceId)
			throws PDStoreException, RemoteException {
		store.removeIcon(transaction, instanceId);
	}

	public GUID getRepository() throws PDStoreException, RemoteException {
		return store.getRepository();
	}

	public void createRelation(GUID transaction, GUID type1Id, GUID role2Id,
			GUID typeId2) throws RemoteException {
		store.createRelation(transaction, type1Id, role2Id, typeId2);
	}

	public void createType(GUID transaction, GUID modelId, GUID typeId,
			String typeName) throws RemoteException {
		store.createType(transaction, modelId, typeId, typeName);
	}

	public void createModel(GUID transaction, GUID modelId, String modelName)
			throws RemoteException {
		store.createModel(transaction, modelId, modelName);
	}

	public void createRelation(GUID transaction, GUID typeId1,
			String role1Name, String role2Name, GUID role2, GUID typeId2)
			throws RemoteException {
		store.createRelation(transaction, typeId1, role1Name, role2Name, role2,
				typeId2);
	}

	public GUID getAccessorId(GUID transaction, GUID role2Id)
			throws RemoteException {
		return store.getAccessorType(transaction, role2Id);
	}

	public List<PDListener<GUID, Object, GUID>> getDetachedListenerList()
			throws PDStoreException, RemoteException {
		return store.getDetachedListenerList();
	}

	public List<PDListener<GUID, Object, GUID>> getInterceptorList()
			throws PDStoreException, RemoteException {
		return store.getInterceptorList();
	}

	public List<PDChange<GUID, Object, GUID>> nextTransaction()
			throws RemoteException {
		return getService().nextMessage();
	}
	
	public List<List<PDChange<GUID, Object, GUID>>> newTransactions() {
		return getService().newMessages();
	} 
	
	private PDStoreListenerService<List<PDChange<GUID, Object, GUID>>> service = new PDStoreListenerService<List<PDChange<GUID, Object, GUID>>>();
	
	public PDStoreListenerService<List<PDChange<GUID, Object, GUID>>> getService() {
		return service;
	}

}
