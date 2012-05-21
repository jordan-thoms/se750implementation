package pdstore.dal;

import java.util.Dictionary;
import java.util.Hashtable;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;

public class DALClassRegister {
	
	/**
	 * Mapping between PDType GUIDs and their Java data access classes
	 */
	private static Dictionary<GUID, Class<?>> ClassForPDType = new Hashtable<GUID, Class<?>>();

	static {
		// register most important classes
		addDataClass(PDStore.MODEL_TYPEID, PDModel.class);
		addDataClass(PDStore.TYPE_TYPEID, PDType.class);
		addDataClass(PDStore.ROLE_TYPEID, PDRole.class);
	}
	
	/**
	 * Registers a DAL class with the cache, so that the class can be used for
	 * representing instances in memory.
	 * 
	 * @throws PDStoreException
	 */
	public static void addDataClass(GUID typeId, Class<?> dalClass)
			throws PDStoreException {
		ClassForPDType.put(typeId, dalClass);
	}

	public static Class<?> getDataClass(GUID typeId) throws PDStoreException {
		return ClassForPDType.get(typeId);
	}
}
