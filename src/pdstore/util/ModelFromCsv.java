package pdstore.util;

import java.io.FileReader;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;

import pdstore.GUID;
import pdstore.GUIDGen;
import pdstore.dal.PDGen;
import pdstore.dal.PDWorkingCopy;


import au.com.bytecode.opencsv.*;
/**
 * Command line tool that generates the PDModel definition from a 
 * comma-separated value list.
 * An example .csv file: (ignore asterisks from javadoc)
 * samplemodel, this is the model name
 * null,Customer,0,1,addresses,Address,1,null,
 * null,Customer,1,1,orders,Order,0,null
 * (EOF)
 * The second and following lines have the format:
 * rolename1, typename1, min1, max1, and the same for the second role.
 * The guids are automatically generated.
 * A PL/SQL script is generated
 * The PDGen classes are generated.
 *
 * @author gweb017
 * 
 */
public class ModelFromCsv {
	
    /**
     * The SQLScript for the model.
     * This is first collated as a string and then printed.
     * Reason: should not be interrupted by debug output.
     */
   	private static String outputSQLScript 
   		= "CONNECT 'pdstore.fdb' user 'sysdba' password 'masterkey';\n";
    /**
     * the PDCache has to be used in several methods
     */
   	private static PDWorkingCopy cache;

    /**
     * @param args
     */
    public static void main(String[] args) {
    	
    if (args.length == 0) {
    	System.out.println("Usage:\tjava pdstore.ModelFromCsv modelfilename.csv ");
    	return;
    }
    String modelfilename = args[0];

 	try {
	    // Create a new cache that is connected to the DB
	    /*cache = new PDSimpleCache("jdbc:firebirdsql:local:pdstore.fdb",
		    "sysdba", "masterkey");*/
	    
	    // get some guids
	    List<GUID> guidlist = GUIDGen.generateGUIDs();
	    
	    // get the source
	    CSVReader reader = new CSVReader(new FileReader(modelfilename));
	    List<String[]> myEntries = reader.readAll();
	    
	    // ignore first line
	    String beginModelLine[] = myEntries.remove(0);
	    if(!beginModelLine[0].equals("beginModel")){
	    	System.out.println("Warning: Model must start with 'beginModel'.");	    	
	    }

	    
	    // Create the model
		GUID model = guidlist.remove(0);
	    String modelString = model.toString();
	    String modelName = myEntries.remove(0)[0];
	    String modelDef = "execute procedure create_model('"+modelString+ 
		"', '"+modelName+"');\n";
	    doStatement(modelDef);
		

	    String typeDef="";
	    
	    Map<String,GUID> typesSoFar = new Hashtable<String,GUID>(); 
	    addPrimitiveType(typesSoFar, new GUID("538a986c4062db11afc0b95b08f50e2f"), 
		"GUID");

	addPrimitiveType(typesSoFar, new GUID("4b8a986c4062db11afc0b95b08f50e2f"), 
		"Integer");

	addPrimitiveType(typesSoFar, new GUID("4c8a986c4062db11afc0b95b08f50e2f"), 
		"Double");

	addPrimitiveType(typesSoFar, new GUID("4d8a986c4062db11afc0b95b08f50e2f"), 
		"Boolean");

	addPrimitiveType(typesSoFar, new GUID("4e8a986c4062db11afc0b95b08f50e2f"), 
		"Timestamp");
		
	addPrimitiveType(typesSoFar, new GUID("508a986c4062db11afc0b95b08f50e2f"), 
		"Char");

	addPrimitiveType(typesSoFar, new GUID("4a8a986c4062db11afc0b95b08f50e2f"), 
		"String");

	addPrimitiveType(typesSoFar, new GUID("4f8a986c4062db11afc0b95b08f50e2f"), 
		"Blob");

	addPrimitiveType(typesSoFar, new GUID("d19fffbbf28bdb118ab1d56a70f8a30f"), 
		"Image");

	    
	    
	    for (String[] relation : myEntries){
	    	// test if first type exists, if not, create
	    	String type1Name = relation[1].trim();
		    GUID type1GUID = typesSoFar.get(type1Name);
			if (type1GUID ==null){
				type1GUID = guidlist.remove(0);
	    		typesSoFar.put(type1Name, type1GUID);
	    		typeDef = generateCreateType(modelString, type1Name, type1GUID.toString());
	    		doStatement(typeDef);
	    	} 
	    	// test if second type exists, if not, create
	    	String type2Name = relation[5].trim();
		    GUID type2GUID = typesSoFar.get(type2Name);
			if (type2GUID==null){
				type2GUID = guidlist.remove(0);
	    		typesSoFar.put(type2Name, type2GUID);
	    		typeDef = generateCreateType(modelString, type2Name, type2GUID.toString());
	    		doStatement(typeDef);
	    	} 
	    	// create relation:
		    String relationDef = 
		    	  generateCreateRelation(guidlist, relation, type1GUID.toString(),
		    			  type2GUID.toString());
     		doStatement(relationDef);
	    	
	    }
	    
	    cache.commit();
	    outputSQLScript += "COMMIT;\n";

		doStatement("execute procedure intercession('"+modelString+"');\n");
		
	    cache.commit();
	    outputSQLScript += "COMMIT;\n";		 
        //doStatement("COMMIT;\n");

	    PDGen.generateModel(modelName, "src", cache, "pdstore");
	} catch (Exception e) {
	    System.out.println(e.toString());
	}
	// Final printout of the SQL script
	System.out.println("Begin SQL Script>>>");

	System.out.println(
			outputSQLScript
			);
	System.out.println("<<<< End SQL Script.");
   }

	private static void addPrimitiveType(Map<String, GUID> typesSoFar, GUID guid,
			String name) {
		typesSoFar.put(name, guid);
	}

	/**
	 * Generates a 'create relation' statement for PDstore.
	 * @param guidlist
	 * @param relation
	 * @param type1GUIDtoString
	 * @param type2GUIDtoString
	 * @return
	 */
	private static String generateCreateRelation(List<GUID> guidlist,
			String[] relation, String type1GUIDtoString,
			String type2GUIDtoString) {
		return "execute procedure create_relation('"
		+guidlist.remove(0)+ "', '" + type1GUIDtoString  // first role GUID and type GUID 
			+ "', " + relation[2]  // fist min multiplicity
			+ ", " + relation[3]  // fist max multiplicity
			+ ", '" + relation[0] // first role name
			+ "', '"
		+guidlist.remove(0)+ "', '" + type2GUIDtoString  // 2nd role GUID and type GUID 
		    + "', " + relation[6]  // 2nd min multiplicity
		    + ", " + relation[7]  // 2nd max multiplicity
			+ ", '" + relation[4] // 2nd role name
			+"');\n";
	}

	/**
	 * Generates a 'create type' call for PDStore
	 * @param modelString
	 * @param typeName1
	 * @param type1GUIDtoString
	 * @return
	 */
	private static String generateCreateType(String modelString,
			String typeName1, String type1GUIDtoString) {
		return "execute procedure create_type('"+type1GUIDtoString+ 
		"', '"+modelString+"', '"+ typeName1+"',null);\n";
	}
    
    /**
     * Method that encapsulates the different processing methods
     * for one statement.
     * Currently:
     * - execute it on PDStore
     * - print it to output
     */
	@Deprecated
    private static void doStatement(String statement) throws SQLException {
    	outputSQLScript += statement;
    	throw new UnsupportedOperationException("No longer supported");
	    //TODO: needs refactoring
    	//cache.connection.createStatement().executeUpdate(statement);
   }
   
 

}
