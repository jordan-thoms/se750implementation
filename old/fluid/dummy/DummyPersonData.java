package fluid.dummy;

import java.util.HashMap;
import pdstore.GUID;

public class DummyPersonData {
	public final static String person = "Person";
	public final static String name = "Name";
	public final static String hasName = "name";
	public final static String FirstName = "firstName";
	public final static String LastName = "lastName";

	public final static String [][] data = {
		{"Peter" , "Lattimer"},
		{"Myka" , "Bering"},
		{"Claudia" , "Donovan"}
	};
	
	public final static HashMap<String, GUID> GUIDMap = new HashMap<String, GUID>();
	
}
