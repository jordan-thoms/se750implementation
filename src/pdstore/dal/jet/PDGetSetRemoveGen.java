package pdstore.dal.jet;

import pdstore.*;
import java.util.Date;
import pdstore.dal.*;
import nz.ac.auckland.se.genoupe.tools.Debug;

public class PDGetSetRemoveGen
{
  protected static String nl;
  public static synchronized PDGetSetRemoveGen create(String lineSeparator)
  {
    nl = lineSeparator;
    PDGetSetRemoveGen result = new PDGetSetRemoveGen();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "\t/**" + NL + "\t * Returns the instance connected to this instance through the role \"";
  protected final String TEXT_4 = "\"." + NL + "\t * @return the connected instance" + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\t public ";
  protected final String TEXT_5 = " get";
  protected final String TEXT_6 = "() throws PDStoreException {" + NL + "\t \treturn (";
  protected final String TEXT_7 = ")pdWorkingCopy.getInstance(this, role";
  protected final String TEXT_8 = "Id);" + NL + "\t }" + NL + "" + NL + "\t/**" + NL + "\t * Returns the instance(s) connected to this instance through the role \"";
  protected final String TEXT_9 = "\"." + NL + "\t * @return the connected instance(s)" + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\t public Collection<";
  protected final String TEXT_10 = "> get";
  protected final String TEXT_11 = "s() throws PDStoreException {" + NL + "\t \tSet<";
  protected final String TEXT_12 = "> result = new HashSet<";
  protected final String TEXT_13 = ">();" + NL + "\t \tGUID ";
  protected final String TEXT_14 = "TypeId = new GUID(\"";
  protected final String TEXT_15 = "\");" + NL + "\t\tpdWorkingCopy.getInstances(this, role";
  protected final String TEXT_16 = "Id, ";
  protected final String TEXT_17 = ".class, ";
  protected final String TEXT_18 = "TypeId, result);" + NL + "\t \treturn result;" + NL + "\t }" + NL + "\t ";
  protected final String TEXT_19 = NL + "   /**" + NL + "\t * Connects this instance to the given instance using role \"";
  protected final String TEXT_20 = "\"." + NL + "\t * If the given instance is null, nothing happens." + NL + "\t * @param ";
  protected final String TEXT_21 = " the instance to connect" + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void add";
  protected final String TEXT_22 = "(";
  protected final String TEXT_23 = " ";
  protected final String TEXT_24 = ") throws PDStoreException {" + NL + "" + NL + "\t\t\tif (";
  protected final String TEXT_25 = " != null) {" + NL + "\t\t\t\t";
  protected final String TEXT_26 = NL + "\t\t\t\tpdWorkingCopy.addLink(this.id, role";
  protected final String TEXT_27 = "Id, ";
  protected final String TEXT_28 = ");" + NL + "\t\t\t}" + NL + "" + NL + "\t}" + NL;
  protected final String TEXT_29 = NL + "\t/**" + NL + "\t * Connects this instance to the given instances using role \"";
  protected final String TEXT_30 = "\"." + NL + "\t * If the given collection of instances is null, nothing happens." + NL + "\t * @param ";
  protected final String TEXT_31 = " the Collection of instances to connect" + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void add";
  protected final String TEXT_32 = "s(Collection<";
  protected final String TEXT_33 = "> ";
  protected final String TEXT_34 = "s) throws PDStoreException {" + NL + "\t\tif (";
  protected final String TEXT_35 = "s == null)" + NL + "\t\t\treturn;" + NL + "" + NL + "\t\tfor (";
  protected final String TEXT_36 = " instance : ";
  protected final String TEXT_37 = "s)" + NL + "\t\t\tadd";
  protected final String TEXT_38 = "(instance);" + NL + "\t}";
  protected final String TEXT_39 = NL;
  protected final String TEXT_40 = NL + "\t/**" + NL + "\t * Connects this instance to the given instance using role \"";
  protected final String TEXT_41 = "\"." + NL + "\t * If the given instance is null, nothing happens." + NL + "\t * @param ";
  protected final String TEXT_42 = " the instance to connect" + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void add";
  protected final String TEXT_43 = "(";
  protected final String TEXT_44 = " ";
  protected final String TEXT_45 = ") throws PDStoreException {" + NL + "\t\tif (";
  protected final String TEXT_46 = " != null) {" + NL + "\t\t\tadd";
  protected final String TEXT_47 = "(";
  protected final String TEXT_48 = ".getId());" + NL + "\t\t}\t\t" + NL + "\t}" + NL + "\t" + NL + "\t/**" + NL + "\t * Connects this instance to the given instance using role \"";
  protected final String TEXT_49 = "\"." + NL + "\t * If the given collection of instances is null, nothing happens." + NL + "\t * @param ";
  protected final String TEXT_50 = " the Collection of instances to connect" + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void add";
  protected final String TEXT_51 = "s(Collection<";
  protected final String TEXT_52 = "> ";
  protected final String TEXT_53 = "s) throws PDStoreException {" + NL + "\t\tif (";
  protected final String TEXT_54 = "s == null)" + NL + "\t\t\treturn;" + NL + "\t\t" + NL + "\t\tfor (";
  protected final String TEXT_55 = " instance : ";
  protected final String TEXT_56 = "s)" + NL + "\t\t\tadd";
  protected final String TEXT_57 = "(instance);\t" + NL + "\t}";
  protected final String TEXT_58 = NL;
  protected final String TEXT_59 = NL + "\t/**" + NL + "\t * Removes the link from this instance through role \"";
  protected final String TEXT_60 = "\"." + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void remove";
  protected final String TEXT_61 = "() throws PDStoreException {" + NL + "\t\tpdWorkingCopy.removeLink(this.id, role";
  protected final String TEXT_62 = "Id, " + NL + "\t\t\tpdWorkingCopy.getInstance(this, role";
  protected final String TEXT_63 = "Id));" + NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * Removes the link from this instance through role \"";
  protected final String TEXT_64 = "\" to the given instance, if the link exists." + NL + "\t * If there is no such link, nothing happens." + NL + "\t * If the given instance is null, nothing happens." + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void remove";
  protected final String TEXT_65 = "(Object ";
  protected final String TEXT_66 = ") throws PDStoreException {" + NL + "\t\tif (";
  protected final String TEXT_67 = " == null)" + NL + "\t\t\treturn;" + NL + "\t\tpdWorkingCopy.removeLink(this.id, role";
  protected final String TEXT_68 = "Id, ";
  protected final String TEXT_69 = ");" + NL + "\t}" + NL;
  protected final String TEXT_70 = NL + "\t/**" + NL + "\t * Removes the links from this instance through role \"";
  protected final String TEXT_71 = "\" to the instances " + NL + "\t * in the given Collection, if the links exist." + NL + "\t * If there are no such links or the collection argument is null, nothing happens." + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void remove";
  protected final String TEXT_72 = "s(Collection<";
  protected final String TEXT_73 = "> ";
  protected final String TEXT_74 = "s) throws PDStoreException {" + NL + "\t\tif (";
  protected final String TEXT_75 = "s == null)" + NL + "\t\t\treturn;" + NL + "\t\t" + NL + "\t\tfor (";
  protected final String TEXT_76 = " instance : ";
  protected final String TEXT_77 = "s)" + NL + "\t\t\tpdWorkingCopy.removeLink(this.id, role";
  protected final String TEXT_78 = "Id, instance);" + NL + "\t}";
  protected final String TEXT_79 = NL;
  protected final String TEXT_80 = NL + "   /**" + NL + "\t * Connects this instance to the given instance using role \"";
  protected final String TEXT_81 = "\"." + NL + "\t * If there is already an instance connected to this instance through role \"";
  protected final String TEXT_82 = "\", the link will be overwritten." + NL + "\t * If the given instance is null, an existing link is removed.\"" + NL + "\t * @param ";
  protected final String TEXT_83 = " the instance to connect" + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void set";
  protected final String TEXT_84 = "(";
  protected final String TEXT_85 = " ";
  protected final String TEXT_86 = ") throws PDStoreException {" + NL + "\t\tpdWorkingCopy.setLink(this.id,  role";
  protected final String TEXT_87 = "Id, ";
  protected final String TEXT_88 = ");\t" + NL + "\t}";
  protected final String TEXT_89 = NL + "\t/**" + NL + "\t * Connects this instance to the given instance using role \"";
  protected final String TEXT_90 = "\"." + NL + "\t * If there is already an instance connected to this instance through role \"";
  protected final String TEXT_91 = "\", the link will be overwritten." + NL + "\t * If the given instance is null, an existing link is removed.\"" + NL + "\t * @param ";
  protected final String TEXT_92 = " the instance to connect" + NL + "\t * @throws PDStoreException" + NL + "\t */" + NL + "\tpublic void set";
  protected final String TEXT_93 = "(";
  protected final String TEXT_94 = " ";
  protected final String TEXT_95 = ") throws PDStoreException {" + NL + "\t\tset";
  protected final String TEXT_96 = "(";
  protected final String TEXT_97 = ".getId());" + NL + "\t}" + NL;
  protected final String TEXT_98 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
PDRole role2 = (PDRole) argument;
String roleName = role2.getName();
String camelName = PDGen.makeFirstSmall(PDGen.makeCamelCase(roleName));
String upperCamelName = PDGen.makeFirstBig(camelName);
PDType type2 = role2.getOwner();

// determine the kind of object to use as parameter in a getter/setter
boolean useGeneratedClass = 
	// use generated class if the type is not primitive...
	!((type2.getIsPrimitive()==null)? false : type2.getIsPrimitive()) 
	// ...or if the type is Object
	|| type2.getId().equals(PDStore.OBJECT_TYPEID);

// determine the corresponding java type to use for the parameter
String javaTypeName = null;
Class<?> javaValueType = GUID.class;

if (useGeneratedClass) {
	Class<?> registeredClass = DALClassRegister.getDataClass(type2.getId());
	if (registeredClass != null) {
    	// if a DAL class was already registered, use its name
    	javaTypeName = registeredClass.getSimpleName();
	} else if (type2.getId().equals(PDStore.OBJECT_TYPEID)) {
		// if the relation is to Object, then use PDInstance
		javaTypeName = "PDInstance";
	} else {
	    // if no class was registered, use the default name
	    // for generated access classes
	    javaTypeName = "PD" + PDGen.makeCamelCase(type2.getName());
	}
} else {
	// if it is a primitive non-Object type (e.g. String), 
	// then use corresponding Java primitive type
	javaValueType = PDGen.getJavaValueType(type2);
	javaTypeName = javaValueType.getSimpleName();
}

    stringBuffer.append(TEXT_2);
    //GETTER METHOD GENERATION
    stringBuffer.append(TEXT_3);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(type2.getId());
    stringBuffer.append(TEXT_15);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_18);
    //ADDER METHOD GENERATION
    stringBuffer.append(TEXT_19);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(javaValueType.getSimpleName());
    stringBuffer.append(TEXT_23);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_25);
    //since we are using addLink it shouldn't matter whether it is an add or a set
    stringBuffer.append(TEXT_26);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_28);
    	// If no DAL class is used for the owner type of this role, 
	// then offer power version of add() for the primitive type
	// associated with the owner type of the role. This is to avoid 
	// a Java type system conflict between the two add..s(),one with 
	// the primitive and one with a DAL type (see below). 
	if (!useGeneratedClass) { 
    stringBuffer.append(TEXT_29);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(javaValueType.getSimpleName());
    stringBuffer.append(TEXT_33);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(javaValueType.getSimpleName());
    stringBuffer.append(TEXT_36);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_38);
    }
    stringBuffer.append(TEXT_39);
    	// If a DAL class is used for the owner type of this role, 
	// then offer to use it in another version of add().
	if (useGeneratedClass) { 
    stringBuffer.append(TEXT_40);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_57);
    }
    stringBuffer.append(TEXT_58);
    //REMOVER METHOD GENERATION
    stringBuffer.append(TEXT_59);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_69);
    	// If a DAL class is used for the owner type of this role, 
	// then offer to use it in a power version of remove().
	if (useGeneratedClass) { 
    stringBuffer.append(TEXT_70);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_77);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_78);
    }
    stringBuffer.append(TEXT_79);
    //SETTER METHOD GENERATION
    stringBuffer.append(TEXT_80);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_81);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(javaValueType.getSimpleName());
    stringBuffer.append(TEXT_85);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_87);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_88);
    	if (useGeneratedClass) { //overload it
    stringBuffer.append(TEXT_89);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(roleName);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_93);
    stringBuffer.append(javaTypeName);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_95);
    stringBuffer.append(upperCamelName);
    stringBuffer.append(TEXT_96);
    stringBuffer.append(camelName);
    stringBuffer.append(TEXT_97);
    	}
    stringBuffer.append(TEXT_98);
    return stringBuffer.toString();
  }
}
