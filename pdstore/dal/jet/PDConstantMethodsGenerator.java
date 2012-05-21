package pdstore.dal.jet;

import pdstore.*;
import java.sql.*;
import pdstore.dal.*;

public class PDConstantMethodsGenerator
{
  protected static String nl;
  public static synchronized PDConstantMethodsGenerator create(String lineSeparator)
  {
    nl = lineSeparator;
    PDConstantMethodsGenerator result = new PDConstantMethodsGenerator();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\tstatic {" + NL + "\t\tDALClassRegister.addDataClass(typeId, ";
  protected final String TEXT_2 = ".class);" + NL + "\t}";
  protected final String TEXT_3 = NL + "\tprivate PDWorkingCopy pdWorkingCopy;" + NL + "\tprivate GUID id;";
  protected final String TEXT_4 = NL + "\tpublic String toString() {" + NL + "\t\tString name = getName();" + NL + "\t\tif(name!=null)" + NL + "\t\t\treturn \"";
  protected final String TEXT_5 = ":\" + name;" + NL + "\t\telse" + NL + "\t\t\treturn \"";
  protected final String TEXT_6 = ":\" + id;" + NL + "\t}";
  protected final String TEXT_7 = NL + "\t/**" + NL + "\t * Creates an ";
  protected final String TEXT_8 = " object representing the given instance in the given cache." + NL + "\t * @param workingCopy the working copy the instance should be in" + NL + "\t */" + NL + "\tpublic ";
  protected final String TEXT_9 = "(PDWorkingCopy workingCopy) {" + NL + "\t\tthis(workingCopy, new GUID());" + NL + "\t}" + NL + "\t" + NL + "\t/**" + NL + "\t * Creates an ";
  protected final String TEXT_10 = " object representing the given instance in the given cache." + NL + "\t * @param workingCopy the working copy the instance should be in" + NL + "\t * @param id GUID of the instance" + NL + "\t */" + NL + "\tpublic ";
  protected final String TEXT_11 = "(PDWorkingCopy workingCopy, GUID id) {" + NL + "\t\tthis.pdWorkingCopy = workingCopy;" + NL + "\t\tthis.id = id;" + NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_12 = NL + "\t\t// set has-type link as specified in the model, if it does not exist yet" + NL + "\t\tif (workingCopy.getInstance(this, PDStore.HAS_TYPE_ROLEID) == null)" + NL + "\t\t\tworkingCopy.addLink(id, PDStore.HAS_TYPE_ROLEID, typeId);" + NL + "\t\t";
  protected final String TEXT_13 = NL + "\t}";
  protected final String TEXT_14 = NL + "\t/**" + NL + "\t * Loads an instance object of this type into a cache." + NL + "\t * If the instance is already in the cache, the cached instance is returned." + NL + "\t * @param PDWorkingCopy pdWorkingCopy to load the instance into" + NL + "\t * @param id GUID of the instance" + NL + "\t * Do not directly call this method. Use the newInstance() method in PDCache which would call this method" + NL + "\t */" + NL + "\tpublic static ";
  protected final String TEXT_15 = " load(PDWorkingCopy pdWorkingCopy, GUID id) {" + NL + "\t\tPDInstance instance = pdWorkingCopy.load(typeId, id);" + NL + "\t\treturn (";
  protected final String TEXT_16 = ")instance;" + NL + "\t}" + NL;
  protected final String TEXT_17 = NL + "\t/**" + NL + "\t * Gets the pdWorkingCopy this object is stored in." + NL + "\t */" + NL + "\tpublic PDWorkingCopy getPDWorkingCopy() {" + NL + "\t\treturn pdWorkingCopy;" + NL + "\t}";
  protected final String TEXT_18 = NL + "\t/**" + NL + "\t * Gets the GUID of the instance represented by this object." + NL + "\t */" + NL + "\tpublic GUID getId() {" + NL + "\t\treturn id;" + NL + "\t}";
  protected final String TEXT_19 = NL + "\t/**" + NL + "\t * Gets the GUID of the type of the instance represented by this object." + NL + "\t */" + NL + "\tpublic GUID getTypeId() {" + NL + "\t\treturn typeId;" + NL + "\t}";
  protected final String TEXT_20 = NL + "\t/**" + NL + "\t * Gets the name of this instance." + NL + "\t * In PDStore every instance can be given a name." + NL + "\t * @return name the instance name" + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic String getName() throws PDStoreException {" + NL + "\t\treturn pdWorkingCopy.getName(id);" + NL + "\t}";
  protected final String TEXT_21 = NL + "\t/**" + NL + "\t * Sets the name of this instance." + NL + "\t * In PDStore every instance can be given a name." + NL + "\t * If the instance already has a name, the name will be overwritten." + NL + "\t * If the given name is null, an existing name will be removed." + NL + "\t * @return name the new instance name" + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void setName(String name) throws PDStoreException {" + NL + "\t\tpdWorkingCopy.setName(id, name);" + NL + "\t}";
  protected final String TEXT_22 = NL + "\t/**" + NL + "\t * Removes the name of this instance." + NL + "\t * In PDStore every instance can be given a name." + NL + "\t * If the instance does not have a name, nothing happens." + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void removeName() throws PDStoreException {" + NL + "\t\tpdWorkingCopy.removeName(id);" + NL + "\t}";
  protected final String TEXT_23 = NL + "\t/**" + NL + "\t * Gets the icon of this instance." + NL + "\t * In PDStore every instance can be given an icon." + NL + "\t * @return icon the instance icon" + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic Blob getIcon() throws PDStoreException {" + NL + "\t\treturn pdWorkingCopy.getIcon(id);" + NL + "\t}";
  protected final String TEXT_24 = NL + "\t/**" + NL + "\t * Sets the icon of this instance." + NL + "\t * In PDStore every instance can be given an icon." + NL + "\t * If the instance already has an icon, the icon will be overwritten." + NL + "\t * If the given icon is null, an existing icon will be removed." + NL + "\t * @return icon the new instance icon" + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void setIcon(Blob icon) throws PDStoreException {" + NL + "\t\tpdWorkingCopy.setIcon(id, icon);" + NL + "\t}";
  protected final String TEXT_25 = NL + "\t/**" + NL + "\t * Removes the icon of this instance." + NL + "\t * In PDStore every instance can be given an icon." + NL + "\t * If the instance does not have an icon, nothing happens." + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void removeIcon() throws PDStoreException {" + NL + "\t\tpdWorkingCopy.removeIcon(id);" + NL + "\t}" + NL + "" + NL + "\t" + NL + "\t";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	/*this class generates the methods which are the same for every generated class 
     *as they are always the same, it could be more efficient,for example converting
     *this all into one string constant, but this gives us a nice way to edit it. 
     */ 
	String className = ((ConstantMethodsGeneratorArgument)argument).className;
	PDType type = ((ConstantMethodsGeneratorArgument)argument).type;

	/* generate the static initializer that registers the wrapper */
    stringBuffer.append(TEXT_1);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_2);
    	/* generate private id and cache variables */
    stringBuffer.append(TEXT_3);
    	/* generate toString() */
    stringBuffer.append(TEXT_4);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_6);
    	/* generate constructors */ 
    stringBuffer.append(TEXT_7);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_11);
    
		PDRole hasTypeRole =  (PDRole) type.getPDWorkingCopy().load(PDStore.ROLE_TYPEID, PDStore.HAS_TYPE_ROLEID);
		if (type.getAccessibleRoles().contains(hasTypeRole)) { 
    stringBuffer.append(TEXT_12);
    }
    stringBuffer.append(TEXT_13);
    	/* generate load */ 
    stringBuffer.append(TEXT_14);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(className);
    stringBuffer.append(TEXT_16);
    	/* generate getPDWorkingCopy */ 
    stringBuffer.append(TEXT_17);
    	/* generate getId */
    stringBuffer.append(TEXT_18);
    	/* generate getTypeId */ 
    stringBuffer.append(TEXT_19);
    	/* generate getName */ 
    stringBuffer.append(TEXT_20);
    	/* generate setName */ 
    stringBuffer.append(TEXT_21);
    	/* generate removeName */ 
    stringBuffer.append(TEXT_22);
    	/* generate getIcon */ 
    stringBuffer.append(TEXT_23);
    	/* generate setIcon */ 
    stringBuffer.append(TEXT_24);
    	/* generate removeIcon */ 
    stringBuffer.append(TEXT_25);
    return stringBuffer.toString();
  }
}
