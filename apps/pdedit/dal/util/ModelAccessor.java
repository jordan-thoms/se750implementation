package pdedit.dal.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import pdedit.PDEditLogger;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDGen;
import pdstore.dal.PDSimpleWorkingCopy;

/**
 * This class is the model accessor for PDStore. It only handles one model at a time.
 * @author tedyeung
 *
 */
public class ModelAccessor {

	public static GUID DiagramModelGUID = new GUID("4c3064a0952611df91630026bb06e946");
	public static GUID NodeTypeGUID = new GUID("4c3064a1952611df91630026bb06e946");
	
	public static GUID hasType = new GUID("0c9fe4409aa911dfb3fa0026bb06e946");
	public static GUID hasXROLEGUID = new GUID("4c3064a2952611df91630026bb06e946");
	public static GUID hasYROLEGUID = new GUID("4c3064a3952611df91630026bb06e946");
	public static GUID hasInstanceROLEGUID = new GUID("4c3064a4952611df91630026bb06e946");
	
	public static GUID IsModel = new GUID("e5d069a0993d11df8bb10026bb06e946");

	private PDStore store;
	private final boolean regen = false;
	private HashMap<String, GUID> kindToIndexMap = new HashMap<String, GUID>();
	private Collection<DALEventListener> listeners = new HashSet<DALEventListener>();
	
	public PDStore getStore() {
		return store;
	}
	
	public void add(DALEventListener l){
		listeners.add(l);
	}
	
	public void commit(){
		store.commit(store.begin());
	}

	private boolean modelStarted = false;
	private GUID model;
	private String modelName;
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	private ArrayList<GUID> types = new ArrayList<GUID>();
	private ArrayList<GUID> roles = new ArrayList<GUID>();

	private DNDHandler dnd;

	public DNDHandler getDnd() {
		return dnd;
	}

	public ModelAccessor(PDStore store){
		this.store = store;
		setUp();
	}
	
	public GUID getGUIDofType(String name){
		return kindToIndexMap.get(name);
	}
	
	private void setUp(){
		kindToIndexMap.put("PDStore.ComplexType", PDStore.GUID_TYPEID);
		kindToIndexMap.put("PDStore.Blob", PDStore.BLOB_TYPEID);
		kindToIndexMap.put("PDStore.Image", PDStore.IMAGE_TYPEID);
		kindToIndexMap.put("PDStore.String", PDStore.STRING_TYPEID);
		kindToIndexMap.put("PDStore.Char", PDStore.CHAR_TYPEID);
		kindToIndexMap.put("PDStore.Boolean", PDStore.BOOLEAN_TYPEID);
		kindToIndexMap.put("PDStore.Double", PDStore.DOUBLE_PRECISION_TYPEID);
		kindToIndexMap.put("PDStore.Integer", PDStore.INTEGER_TYPEID);
		kindToIndexMap.put("PDStore.Time", PDStore.TIMESTAMP_TYPEID);
	}

	public Collection<Object> loadModel(String id){
		GUID t = store.begin();
		model = new GUID(id);
		modelName = (String)store.getInstance(t, model, PDStore.NAME_ROLEID);
		System.out.println(modelName);
		// for testing purpose only
		// get a list of type
		Collection<Object> list = store.getInstances(t, model, PDStore.MODELTYPE_ROLEID);
		//store.commit(t);
		return dnd.getListOfNodes(list);
		//return null;
	}
	
	/**
	 * Creates a Model but does not create a model till first type is created
	 * @param name
	 * @return
	 */
	public GUID createModel(String name){
		model = new GUID();
		modelName = name;
		return model;
	}

	private synchronized void createModel(){
		GUID t = store.begin();
		store.createModel(t, model, modelName);
		//TODO this looks wrong
		//store.addLink(t, model,PDStore.ACCESSIBLE_ROLES_ROLEID, IsModel);
		store.commit(t);
	}

	public void changeModelName(String name){
		GUID t = store.begin();
		store.setName(t, model, name);
		store.commit(t);
	}

	public synchronized GUID createType(String name){
		if (!modelStarted){
			createModel();
			System.out.println("Model Created");
			modelStarted = true;
		}
		
		GUID t = store.begin();
		GUID id = new GUID();
		types.add(id);
		store.createType(t, model, id, name);
		store.commit(t);
		t = store.begin();
		System.out.println("Type: "+store.getName(t, id)+" Created");
		store.commit(t);
		return id;
	}

	/**
	 * Renames a node
	 * @param id The identifier of the node to rename. 
	 * @param name The new name of the node.
	 */
	public void renameType(GUID id, String name){
		GUID t = store.begin();
		Object oldName = store.getName(t, id);
		if (oldName != name){
			store.setName(t, id, name);
			System.out.println("Renaming type '" + oldName + "' to '" + name + "'");
		}
		
		store.commit(t);
	}

	public void removeType(GUID id){
		GUID t = store.begin();
		store.removeLink(t, model, PDStore.MODELTYPE_ROLEID, id);
		store.commit(t);
	}

	public void removeRole(GUID id, GUID instance1, GUID instance2){
		GUID t = store.begin();
		store.removeLink(t, instance1, id, instance2);
		store.commit(t);
	}

	public synchronized  GUID createRelation(GUID type1, String role1, GUID type2, String role2){
		GUID t = store.begin();
		GUID id = new GUID();
		roles.add(id);
		System.out.println(store.getName(t, type1)+","+role1 + ","+role2+","+store.getName(t,type2));
		store.createRelation(t, type1, role1, role2, id, type2);
		store.commit(t);
		t = store.begin();
		System.out.println("Role Name: " +store.getName(t, id)+" created");
		System.out.println("Role Id: " +store.getId(t, role2)+" created");
		store.commit(t);
		return id;
	}
	
	public boolean modelWithNameExist(String name){
		GUID t = store.begin();
		
		//TODO this looks like a misuse of the old role ACCESSOR_ROLEID
		//Collection<Object> kk = store.getInstances(t, ModelAccessor.IsModel,PDStore.ACCESSOR_ROLEID);
		Collection<Object> kk = null;
		String temp = null;
		for (Object s : kk){
			temp = (String)store.getInstance(t, s, PDStore.NAME_ROLEID);
			if (temp == null)
				continue;
			if (temp.toLowerCase().startsWith(name.toLowerCase().trim())){
				return true;
			}
		}
		store.commit(t);
		return false;
	}

	/**
	 * This method generates the Diagram model for pdedit.pds if the PDNode not in pdedit.dal
	 * run PDGen.generateModel("PDEditDiagram", "src", new PDSimpleWorkingCopy(store), "pdedit.dal");
	 */
	public void createDiagramModel() {
		long startTime = System.currentTimeMillis();
		Date n = new Date(startTime);
		PDEditLogger.addToLog("["+n.toString()+"] ModelAccessor: creatingDiagramModel");
		GUID t = store.begin();
		store.createModel(t, DiagramModelGUID, "PDEditDiagram");
		//TODO this looks wrong
		//store.addLink(t, DiagramModelGUID,PDStore.ACCESSIBLE_ROLES_ROLEID, IsModel);
		store.createType(t, DiagramModelGUID, NodeTypeGUID, "node");
		store.createRelation(t, NodeTypeGUID,"","hasX", hasXROLEGUID, PDStore.DOUBLE_PRECISION_TYPEID);
		store.createRelation(t, NodeTypeGUID, "","hasY",hasYROLEGUID, PDStore.DOUBLE_PRECISION_TYPEID);
		store.createRelation(t, NodeTypeGUID,"","hasInstance", hasInstanceROLEGUID, PDStore.STRING_TYPEID);
		store.createRelation(t, NodeTypeGUID,"","hasType", hasType, PDStore.STRING_TYPEID);
		store.commit(t);
		PDEditLogger.addToLog("... Done ["+(System.currentTimeMillis()-startTime)/1000.0+" Secs]");
		PDEditLogger.newLine();
		if(regen){
			regenerateNode();
		}
	}

	private void regenerateNode() {
		PDGen.generate("PDEditDiagram", null, "src", "pdedit.dal", new PDSimpleWorkingCopy(store));
		String file = "";
		if (System.getProperty("os.name").contains("Windows")){
			file = "src\\pdedit\\dal\\PDNode.java";
			com.sun.tools.javac.Main.compile(new String[]{"-d","bin\\",file});

		}else{
			file = "src/pdedit/dal/PDNode.java";
			com.sun.tools.javac.Main.compile(new String[]{"-d","bin/",file});

		}
	}

	public static void LoadPDEditDAL(){
		PDEditLogger.addToLog("PDEdit.ModelAccessor: Registering PDEdit DAL . . . ");
		File dir = new File("apps/pdedit/dal");
		int count = 0;
		for(String name : dir.list()){
			if (!name.equals("util")){
				try {
					Class.forName("pdedit.dal."+name.substring(0, name.indexOf(".")));
					count++;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		PDEditLogger.appendDurationToLastMassage();
		PDEditLogger.addToLogWithoutTimestamp(" ("+count+" DAL found and loaded)");
		PDEditLogger.newLine();
	}
	
	public boolean accessorIntialisedModel(){
		return modelStarted;
	}

	public void start(){
		if (dnd == null){
			dnd = new DNDHandler(null);
			dnd.start();
		}
		dnd.loadDiagramMap(this);
	}

}
