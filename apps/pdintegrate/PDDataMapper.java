package pdintegrate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.sun.org.apache.bcel.internal.generic.Type;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.changelog.PrimitiveType;

/**
 * This class connects to a (possibly) remote PDIntegration server.
 * 
 * All domain object models shall be modified to update this object when they change.
 * 
 */
public class PDDataMapper {
	private static final String PROPERTIES_FILE = "/pdlayer.properties";
	private static final String DEFAULT_HOST = "";
	private static final String DEFAULT_RMIKEY = "Mediator";

	public static final boolean BUFFERED = false;
	
	private static final Map<Field, GUID> roleIDs = new HashMap<Field, GUID>();


	private static PDStore store;

	static {
		Properties config = new Properties();
		try {
			config.load(new FileInputStream(PROPERTIES_FILE));
		} catch (IOException e) {
			try {
				System.err.println("No client extraction layer configuration file found.\nConfiguration file \"" + PROPERTIES_FILE
						+ "\"" +
				" will be created with default values.");
				config.setProperty("host", DEFAULT_HOST);
				config.setProperty("rmiKey", DEFAULT_RMIKEY);
				config.store(new FileOutputStream(PROPERTIES_FILE), "Automatically generated: default value for pdintegrate store!");
			} catch (IOException e1) {
				System.err.println("There was an error creating the configuration file");
			}
		}
		try
		{
			PDDataMapper.store = PDStore.connectToServer(config.getProperty("host", DEFAULT_HOST), config.getProperty("rmiKey", DEFAULT_RMIKEY));
		} catch (Exception e) {
			// Could not initialise PDStore.  Layer is going to be useless.
			store = null;
		}
	}

	/**
	 * Returns the identity GUID if object o is a Domain Model Object.
	 * 
	 * Otherwise returns o.
	 * 
	 * @param o Either a Domain Model Object with a PDIdentity field or a 
	 * Java primitive.
	 * @return o.PDIdentity if it exists, or otherwise o.
	 */
	public static Object extractGUIDorPrimitive(Object o) {
		try {
			Field f = o.getClass().getDeclaredField("PDIdentity");
			if (f.get(o) == null) f.set(o, new GUID());
			return f.get(o);
		} catch (NoSuchFieldException nsfe) {
			// Can happen if this is a primitive object
		} catch (IllegalArgumentException e) {
			System.err.println("Must never happen if weaving is done correctly.");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Must never happen if weaving is done correctly.");
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * This method is called when Domain Model Objects have links added.
	 * 
	 * @param instance1 either a DMO or a primitive
	 * @param role   A GUID representing the role
	 * @param instance2  either a DMO or a primitive
	 */
	public static void addLink(Object instance1, GUID role, Object instance2) {
		try {
			if (store != null) {
				GUID transaction = store.begin();
				store.addLink(transaction,
						extractGUIDorPrimitive(instance1),
						role,
						PrimitiveType.normalize(extractGUIDorPrimitive(instance2)));
				store.commit(transaction);
			}
		} catch (PDStoreException e) {
			store = null;
		}
	}

	/**
	 * This method is called when Domain Model Objects have links added.
	 * 
	 * @param instance1 either a DMO or a primitive
	 * @param role   A GUID representing the role
	 * @param instance2  either a DMO or a primitive
	 */
	public static void removeLink(Object instance1, GUID role, Object instance2) {
		try {
			if (store != null) {
				GUID transaction = store.begin();
				store.removeLink(transaction,
						extractGUIDorPrimitive(instance1),
						role,
						extractGUIDorPrimitive(instance2));
				store.commit(transaction);
			}
		} catch (PDStoreException e) {
			store = null;
		}
	}

	public static void registerType(Class c) {
		if (store != null) {
			try {
				GUID transaction = store.begin();

				try {
					Field modelIdField = c.getDeclaredField("PDModelID");
					GUID modelId = (GUID) modelIdField.get(null); // Static field

					Field typeIdField = c.getDeclaredField("PDTypeID");
					GUID typeId = (GUID) typeIdField.get(null); // Static field

					String typeName = c.getCanonicalName();


					store.createType(transaction, modelId, typeId, typeName);

					store.commit(transaction);

				} catch (IllegalArgumentException e) {
					System.err.println("Must never happen if weaving is done correctly.");
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					System.err.println("Must never happen if weaving is done correctly.");
					e.printStackTrace();
				} catch (SecurityException e) {
					System.err.println("Must never happen if weaving is done correctly.");
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					System.err.println("Must never happen if weaving is done correctly.");
					e.printStackTrace();
				}
			} catch (PDStoreException e) {
				store = null;
			}
		}
	}



	public static void registerRole(Class c, String fieldName, GUID roleID) {
		if (store != null) {
			try {
				GUID transaction = store.begin();

				try {

					Field roleField = c.getDeclaredField(fieldName);

					Field typeIdField = c.getDeclaredField("PDTypeID");

					GUID typeId2 = getTypeGUID(typeIdField.getType());

					GUID typeId = (GUID) typeIdField.get(null); // Static field
					
					roleIDs.put(roleField, roleID);

					store.createRelation(transaction, typeId, roleID, typeId2);


					store.commit(transaction);

				} catch (IllegalArgumentException e) {
					System.err.println("Must never happen if weaving is done correctly.");
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					System.err.println("Must never happen if weaving is done correctly.");
					e.printStackTrace();
				} catch (SecurityException e) {
					System.err.println("Must never happen if weaving is done correctly.");
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					System.err.println("Must never happen if weaving is done correctly.");
					e.printStackTrace();
				}
			} catch (PDStoreException e) {
				store = null;
			}
		}
	}

	private static GUID getTypeGUID(Class<?> type) {

		try {
			return (GUID) type.getDeclaredField("PDTypeID").get(null); //static field
		} catch (IllegalArgumentException e) {
			System.err.println("Must never happen if weaving is done correctly.");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Must never happen if weaving is done correctly.");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Must never happen if weaving is done correctly.");
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// Primitive field!
			if (type.equals(Type.BOOLEAN))
				return PDStore.BOOLEAN_TYPEID;
			if (type.equals(Type.CHAR))
				return PDStore.CHAR_TYPEID;
			if (type.equals(Type.DOUBLE) || type.equals(Type.FLOAT))
				return PDStore.DOUBLE_PRECISION_TYPEID;
			if (type.equals(GUID.class))
				return PDStore.GUID_TYPEID;
			if (type.equals(Type.INT) || type.equals(Type.LONG) || type.equals(Type.SHORT))
				return PDStore.INTEGER_TYPEID;
			if (type.equals(Type.STRING))
				return PDStore.STRING_TYPEID;
		} 
		return PDStore.BLOB_TYPEID;
	}


}
