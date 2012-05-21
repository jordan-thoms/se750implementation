package pdstore.dal;

import pdstore.*;

public class GenerateMetamodelDAL {

	public static void addExtraCode(PDWorkingCopy copy) {
		String extraCode = "	public Collection<PDRole> getAccessibleRoles() throws PDStoreException {\n"
				+ "		Set<PDRole> result = new HashSet<PDRole>();\n"
				+ "		for (PDRole role1 : getOwnedRoles())\n"
				+ "			result.add(role1.getPartner());\n"
				+ "		return result;\n"
				+ "	}\n";
		copy.addLink(PDStore.TYPE_TYPEID, PDGen.EXTRA_DAL_CODE_ROLEID,
				extraCode);
	}

	public static void main(String[] args) {
		PDStore store = new PDStore();
		PDSimpleWorkingCopy copy = new PDSimpleWorkingCopy(store);
		addExtraCode(copy);
		PDGen.generateModel("PD Metamodel", "src", copy, "pdstore.dal");
		copy.commit();
	}
}
