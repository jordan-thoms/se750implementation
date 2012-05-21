package pdstore.generic;

import pdstore.GUID;
import pdstore.GUIDTypeAdaptor;

/**
 * Class to access the adaptor object that encapsulates all operations on
 * generic arguments. Currently this is set to GUIDTypeAdaptor. If different
 * types are used, the classes that initialize GeneriIndexStore would have to
 * set the right adaptor.
 * 
 * @author Gerald, Christof
 * 
 */
public class GlobalTypeAdapter {
	final public static TypeAdapter<GUID, Object, GUID> typeAdapter = new GUIDTypeAdaptor();
}
