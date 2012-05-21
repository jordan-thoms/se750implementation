package pdstore.changelog;

import java.sql.Timestamp;

import nz.ac.auckland.se.genoupe.tools.Debug;

import pdstore.Blob;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;

public enum PrimitiveType {
	GUID(0), // IETF RFC 4122 UUID
	INTEGER(1), // int 64-bit 2-complement (java, C++ long)
	DOUBLE(2), // IEEE double (64 bit)
	STRING(3), // (8 byte length info, UTF-8 unicode)
	BLOB(4), // (8 byte length info)
	BOOLEAN(5), // (1 byte)
	TIMESTAMP(6), // (64 bit)
	DECIMAL(7); // (SQL, Binary-coded decimal, 4-byte length info)

	int code;

	PrimitiveType(int code) {
		this.code = code;
	}

	public static PrimitiveType typeOf(Object o) {
		Debug.assertTrue (o != null, "Object should not be null");
		Class<?> type = o.getClass();
		if (type == GUID.class)
			return GUID;
		if (type == Integer.class || type == Long.class)
			return INTEGER;
		if (type == Float.class || type == Double.class) {
			return DOUBLE;
		}
		if (type == String.class)
			return STRING;
		if (type == Blob.class
				|| (type.isArray() && type.getComponentType() == Byte.TYPE))
			return BLOB;
		if (type == Boolean.class)
			return BOOLEAN;
		if (type == Timestamp.class)
			return TIMESTAMP;
		// TODO: find Java type for DECIMAL
		return null;
	}

	public static PrimitiveType typeForCode(int code) {
		if (code == 0)
			return GUID;
		if (code == 1)
			return INTEGER;
		if (code == 2)
			return DOUBLE;
		if (code == 3)
			return STRING;
		if (code == 4)
			return BLOB;
		if (code == 5)
			return BOOLEAN;
		if (code == 6)
			return TIMESTAMP;
		if (code == 7)
			return DECIMAL;
		return null;
	}

	public static Object convertStringToPrimitive(String s) {
		// TODO: Create cases for all pdstore primitive types
		try {
			int value = Integer.parseInt(s);
			return value;
		} catch (NumberFormatException e) {
		}
		try {
			double value = Double.parseDouble(s);
			return value;
		} catch (NumberFormatException e) {
		}
		try {
			float value = Float.parseFloat(s);
			return value;
		} catch (NumberFormatException e) {
		}
		
		return s;
	}

	public static GUID typeIdOf(Object value) {
		Debug.assertTrue (value != null, "Object should not be null");	
		
		Class<?> type = value.getClass();
		if (type == GUID.class)
			return PDStore.GUID_TYPEID;
		if (type == Integer.class || type == Long.class)
			return PDStore.INTEGER_TYPEID;
		if (type == Float.class || type == Double.class)
			return PDStore.DOUBLE_PRECISION_TYPEID;
		if (type == String.class)
			return PDStore.STRING_TYPEID;
		if (type == Blob.class
				|| (type.isArray() && type.getComponentType() == Byte.TYPE))
			return PDStore.BLOB_TYPEID;
		if (type == Boolean.class)
			return PDStore.BOOLEAN_TYPEID;
		if (type == Timestamp.class)
			return PDStore.TIMESTAMP_TYPEID;

		
		throw new PDStoreException("Cannot find PDStore type for Java object "
				+ value);
	}

	/**
	 * Converts/casts the given primitive value to the compatible type that is
	 * supported by PDStore. For example, Integers are converted to Longs.
	 * 
	 * @param value
	 *            the primitive value
	 * @return the normalized primitve value
	 */
	public static Object normalize(Object value) {
		Debug.assertTrue (value != null, "Primitive value shold not be null!");
		Class<?> type = value.getClass();
		if (type == Integer.class)
			return new Long((Integer) value);
		if (type == Float.class)
			return new Double((Float) value);
		if (type.isArray() && type.getComponentType() == Byte.TYPE)
			return new Blob((byte[]) value);
		return value;
	}
}
