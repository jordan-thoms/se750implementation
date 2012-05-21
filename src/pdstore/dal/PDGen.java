package pdstore.dal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import nz.ac.auckland.se.genoupe.tools.Debug;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.dal.jet.PDConstantMethodsGenerator;
import pdstore.dal.jet.PDGetSetRemoveGen;

public class PDGen {

	static {
		Debug.addDebugTopic("PDGen");
	}
	
	public final static GUID EXTRA_DAL_CODE_ROLEID = new GUID(
	        "127a8d50248411e19ec000224300a31a");

	PDWorkingCopy workingCopy;
	String typeName;
	String className;
	String packageName;
	String sourceRoot;
	PDType type1;
	PrintWriter writer;

	public static void main(String[] args) {
		if (args.length == 0 || args.length > 2) {
			System.out.println("PDGen");
			System.out
					.println("Generates Java accessor classes for Models or individual Types in PDStore.");
			System.out
					.println("All accessor classes are generated into package pdstore.");
			System.out.println("Usage (arguments in [] are optional):");
			System.out
					.println("\tjava pdstore.PDGen \"Model name\" [pdstoreFile]");
			System.out.println("or");
			System.out
					.println("\tjava pdstore.PDGen \"Type name\" [pdstoreFile]");
			System.out.println("where");
			System.out
					.println("pdstoreFile is a path from the working directory to the PDStore");
			System.out
					.println("\tfile where the model to generate from is stored.");
			System.out
					.println("\tIf this is not provided, then the PDStore default file name is used.");
			return;
		}

		try {
			String instanceName = args[0];
			String sourceRoot = "src";
			String packageName = "pdstore.dal.generated";

			PDWorkingCopy workingCopy;
			if (args.length >= 2)
				workingCopy = new PDSimpleWorkingCopy(new PDStore(args[1]));
			else
				workingCopy = new PDSimpleWorkingCopy(new PDStore("pdstore"));

			if (workingCopy.instanceExists(PDStore.MODEL_TYPEID,
					workingCopy.getId(instanceName))) {
				// argument is model name
				generateModel(instanceName, sourceRoot, workingCopy,
						packageName);

			} else if (workingCopy.instanceExists(PDStore.TYPE_TYPEID,
					workingCopy.getId(instanceName))) {
				// argument is type name
				String className = "PD" + makeCamelCase(instanceName);
				PDType type1 = (PDType) workingCopy.load(PDType.typeId,
						workingCopy.getId(instanceName));
				if (type1.getIsPrimitive())
					throw new Exception(
							"Cannot generate a wrapper class for a primitive type.");

				PDGen g = new PDGen(type1, className, packageName, sourceRoot);
				g.generate();

			} else
				throw new Exception(
						"First argument is neither a Model name nor a Type name.");

		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();
		}
	}

	/**
	 * this method can be used from another class to run pdgen and specify
	 * additional arguments such as the working copy to be used
	 */
	public static void generate(String instanceName, String className,
			String sourceRoot, String packageName, PDWorkingCopy cache) {
		if (sourceRoot == null || sourceRoot == "") {
			sourceRoot = "src";
		}
		if (packageName == null || packageName == "") {
			packageName = "pdstore";
		}
		try {
			if (cache.instanceExists(PDModel.typeId, cache.getId(instanceName))) {

				generateModel(instanceName, sourceRoot, cache, packageName);

			} else if (cache.instanceExists(PDType.typeId,
					cache.getId(instanceName))) {
				// argument is type name
				if (className == null || className == "") {
					className = "PD" + makeCamelCase(instanceName);
				}

				PDType type1 = (PDType) cache.load(PDType.typeId,
						cache.getId(instanceName));
				if (type1.getIsPrimitive())
					throw new RuntimeException(
							"Cannot generate a wrapper class for a primitive type.");

				PDGen g = new PDGen(type1, className, packageName, sourceRoot);
				g.generate();

			} else
				throw new RuntimeException(
						"Could not generate with those arguments");

		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();
		}
	}

	public static void generateModel(String modelName, String sourceRoot,
			PDWorkingCopy workingCopy, String packageName)
			throws RuntimeException {
		Debug.println("Generating DAL classes for model \"" + modelName + "\"");
		GUID modelId = workingCopy.getId(modelName);
		if (modelId == null) {
			throw new PDStoreException("Cannot find a model with name \""
					+ modelName + "\" in the store.");
		}

		PDModel model = PDModel.load(workingCopy, modelId);
		generateModel(sourceRoot, packageName, workingCopy, model);
	}

	public static void generateModel(String sourceRoot, String packageName,
			PDWorkingCopy workingCopy, PDModel model) throws RuntimeException {
		try {
			Class.forName("pdstore.dal.PDType");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		for (PDType type1 : model.getTypes()) {
			// generate only classes for types with names
			String typeName = type1.getName();
			if (typeName == null) {
				Debug.println("Skipping type \"" + type1.getId()
						+ "\" because it does not have a name.");
				continue;
			}
			if (type1.getIsPrimitive() == true) {
				Debug.println("Skipping type \"" + typeName
						+ "\" because it is primitive.");
				continue;
			}

			String className = "PD" + makeCamelCase(typeName);
			PDGen g = new PDGen(type1, className, packageName, sourceRoot);
			g.generate();
		}
		Debug.println("Finished DAL generation.");
	}

	public PDGen(PDType type1, String className, String packageName,
			String sourceRoot) throws RuntimeException {
		this.workingCopy = type1.getPDWorkingCopy();
		this.type1 = type1;
		this.typeName = type1.getName();
		this.className = className;
		this.packageName = packageName;
		this.sourceRoot = sourceRoot;
		File file = null;

		// TODO is this necessary?
		if (System.getProperty("os.name").contains("Windows")) {
			file = new File(
					new File(sourceRoot).getAbsolutePath()
							+ "\\"
							+ new File(packageName.replace(".", "\\\\") + "\\")
									.getPath() + "\\" + className + ".java");
		} else {
			file = new File(new File(sourceRoot).getAbsolutePath() + "/"
					+ new File(packageName.replace(".", "//") + "/").getPath()
					+ "/" + className + ".java");
		}

		// Create folders if they do not exist
		File path = new File(file.getParent());
		if (!path.exists()) {
			path.mkdirs();
		}
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void generate() throws RuntimeException {
		Debug.println("Generating DAL class " + className + " for type \""
				+ typeName + "\"... ");

		/* generating package and imports */
		writer.println("package " + packageName + ";");
		writer.println();
		writer.println("import java.util.*;");
		if (!packageName.equals("pdstore")) {
			writer.println("import pdstore.*;");
			writer.println("import pdstore.dal.*;");
		}
		writer.println();
		writer.println("/**");
		writer.println(" * Data access class to represent instances of type \""
				+ typeName + "\" in memory.");
		writer.println(" * Note that this class needs to be registered with PDCache by calling:");
		writer.println(" *    Class.forName(\"" + packageName + "." + className
				+ "\");");
		writer.println(" * @author PDGen");
		writer.println(" */");
		writer.println("public class " + className + " implements PDInstance {");
		writer.println();
		writer.println("\tpublic static final GUID typeId = new GUID(\""
				+ type1.getId() + "\"); ");
		writer.println();

		/* Generating the role constants */
		for (PDRole role1 : type1.getOwnedRoles()) {
			PDRole role2 = role1.getPartner();

			Debug.println("Generating the constant for role \""
					+ role2.getName() + "\"...");

			// generate code only for names roles
			if (role2.getName() == null)
				continue;

			String constName = "role" + makeCamelCase(role2.getName()) + "Id";
			writer.println("\tpublic static final GUID " + constName
					+ " = new GUID(\"" + role2.getId() + "\");");
		}
		writer.println();

		Debug.println("Generating the constant methods... ");
		PDConstantMethodsGenerator constGen = new PDConstantMethodsGenerator();
		writer.print(constGen.generate(new ConstantMethodsGeneratorArgument(
				className, type1)));

		Debug.println("Generating the getters, setters and removers... ");
		generateGettersSettersAndRemovers();

		for (Object extraCode : workingCopy.getInstances(type1, EXTRA_DAL_CODE_ROLEID)) {
			Debug.println("Adding user-defined extra code... ");
			
			writer.print(extraCode);
		}
		
		writer.println("}");
		writer.close();
	}

	/**
	 * generates either a setR() or an addR() method, getter and remover methods
	 * for each named accessible role R
	 * 
	 * @throws Exception
	 */
	void generateGettersSettersAndRemovers() throws RuntimeException {
		PDGetSetRemoveGen pdGenerator = new PDGetSetRemoveGen();
		for (PDRole role1 : type1.getOwnedRoles()) {
			PDRole role2 = role1.getPartner();

			// generate code only for named roles
			if (role2.getName() == null)
				continue;

			Debug.println("Generating getters, setters and removers for role \""
					+ role2.getName() + "\"...");
			writer.print(pdGenerator.generate(role2));
		}
	}

	/**
	 * This method returns the string with its first letter in lower case.
	 * 
	 * @param s
	 */
	public static String makeFirstSmall(String s) {
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	/**
	 * This method returns the string supplied with its front letter in upper
	 * case.
	 * 
	 * @param s
	 */
	public static String makeFirstBig(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	/**
	 * This method returns the string as parameter with its first letter in
	 * upper case. If there is more than one word in the string supplied, they
	 * would we be joined together into one "word" without any space between
	 * them. The first letter of each word would be in upper case in the joined
	 * "word".
	 * 
	 * @param name
	 * @return result
	 */
	public static String makeCamelCase(String name) {
		String result = "";
		name = name.trim();
		for (String part : name.split(" ")) {
			result += makeFirstBig(part);
		}
		return result;
	}

	/**
	 * Converts a name without whitespace to a valid Java identifier by checking
	 * for Java keyword collisions and appending a postfix if necessary.
	 * 
	 * @param name
	 *            name without whitespace
	 * @return a valid Java identifier
	 */
	public static String makeIdentifier(String name) {
		if (name.equals("abstract") || name.equals("assert")
				|| name.equals("boolean") || name.equals("break")
				|| name.equals("byte") || name.equals("case")
				|| name.equals("catch") || name.equals("char")
				|| name.equals("class") || name.equals("const")
				|| name.equals("default") || name.equals("do")
				|| name.equals("double") || name.equals("else")
				|| name.equals("enum") || name.equals("extends")
				|| name.equals("final") || name.equals("finally")
				|| name.equals("float") || name.equals("for")
				|| name.equals("goto") || name.equals("if")
				|| name.equals("implements") || name.equals("import")
				|| name.equals("instanceof") || name.equals("int")
				|| name.equals("interface") || name.equals("long")
				|| name.equals("native") || name.equals("new")
				|| name.equals("package") || name.equals("private")
				|| name.equals("protected") || name.equals("public")
				|| name.equals("return") || name.equals("short")
				|| name.equals("static") || name.equals("strictfp")
				|| name.equals("super") || name.equals("switch")
				|| name.equals("synchronized") || name.equals("this")
				|| name.equals("throw") || name.equals("throws")
				|| name.equals("transient") || name.equals("try")
				|| name.equals("void") || name.equals("volatile")
				|| name.equals("while"))
			name += "Value";
		return name;
	}

	/**
	 * This method returns the java value type corresponding to the SQL type of
	 * the PDType object supplied as a parameter.
	 * 
	 * @param type
	 * @return
	 * @throws PDStoreException
	 */
	public static Class<?> getJavaValueType(PDType type)
			throws PDStoreException {
		if (type.getId().equals(PDStore.GUID_TYPEID)) {
			return GUID.class;
		} else if (type.getId().equals(PDStore.INTEGER_TYPEID)) {
			return Long.class;
		} else if (type.getId().equals(PDStore.BOOLEAN_TYPEID)) {
			return Boolean.class;
		} else if (type.getId().equals(PDStore.TIMESTAMP_TYPEID)) {
			return java.util.Date.class;
		} else if (type.getId().equals(PDStore.CHAR_TYPEID)) {
			return Character.class;
		} else if (type.getId().equals(PDStore.STRING_TYPEID)) {
			return String.class;
		} else if (type.getId().equals(PDStore.BLOB_TYPEID)) {
			return byte[].class;
		} else if (type.getId().equals(PDStore.DOUBLE_PRECISION_TYPEID)) {
			return Double.class;
		}
		throw new PDStoreException("Unknown type: " + type.getId());
	}
}
