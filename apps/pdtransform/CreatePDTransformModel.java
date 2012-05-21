package pdtransform;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDGen;
import pdstore.dal.PDModel;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;

/**
 * Generates the PDTransform DAL files
 * @author cbue001
 *
 */
public class CreatePDTransformModel {
	private PDStore store;
	private GUID transaction;
	
	GUID pdTransformModelId = new GUID("b29d1c278e0ede11980f9a097666e103");
	GUID addressBookModelId = new GUID("e75c1f278e0ede11980f9a097666e103");
	GUID htmlModelId = new GUID("f15c1f278e0ede11980f9a097666e103");
	GUID serializerModelId = new GUID("4ef784348e0ede11980f9a097666e103");
	
	GUID generatorApplicationTypeId = new GUID("e45c1f278e0ede11980f9a097666e103");
	GUID textTypeId = new GUID("caba85348e0ede11980f9a097666e103");
	GUID generatorTypeId = new GUID("fa5c1f278e0ede11980f9a097666e103");
	GUID mapTypeId = new GUID("fb5c1f278e0ede11980f9a097666e103");
	GUID orderTypeId = new GUID("b2ba85348e0ede11980f9a097666e103");
	GUID orderedPairTypeId = new GUID("b3ba85348e0ede11980f9a097666e103");
	GUID addressBookTypeId = new GUID("e85c1f278e0ede11980f9a097666e103");
	GUID contactTypeId = new GUID("e95c1f278e0ede11980f9a097666e103");
	GUID HTMLTagTypeId = new GUID("f25c1f278e0ede11980f9a097666e103");
	GUID ULTagTypeId = new GUID("f35c1f278e0ede11980f9a097666e103");
	GUID LITagTypeId = new GUID("f45c1f278e0ede11980f9a097666e103");
	GUID serializerTypeId = new GUID("5f1e85348e0ede11980f9a097666e103");
	GUID printInstructionTypeId = new GUID("621e85348e0ede11980f9a097666e103");
	GUID serializerApplicationTypeId = new GUID("a3ba85348e0ede11980f9a097666e103");
	
	public CreatePDTransformModel() {
		String dbString = PDTransform.defaultDB;
		
		// Create database on PDStore and start a new transaction.
		store = new PDStore(dbString);
		transaction = store.begin();
	}

	private void createPDTransformModel() {
		// Create the GUIDs
		GUID contentRoleId = new GUID("c8ba85348e0ede11980f9a097666e103");
		GUID inputRoleId = new GUID("e65c1f278e0ede11980f9a097666e103");
		GUID outputRoleId = new GUID("ed5c1f278e0ede11980f9a097666e103");
		GUID inputTypeRoleId = new GUID("fd5c1f278e0ede11980f9a097666e103");
		GUID outputTemplateRoleId = new GUID("ff5c1f278e0ede11980f9a097666e103");
		GUID outputTypeRoleId = new GUID("0b5d1f278e0ede11980f9a097666e103");
		GUID orderRoleId = new GUID("b5ba85348e0ede11980f9a097666e103");
		GUID orderedPairsRoleId = new GUID("b7ba85348e0ede11980f9a097666e103");
		GUID prevRoleId = new GUID("b9ba85348e0ede11980f9a097666e103");
		GUID nextRoleId = new GUID("bbba85348e0ede11980f9a097666e103");
		GUID mapRoleId = new GUID("015d1f278e0ede11980f9a097666e103");
		GUID inputTypeRoleId2 = new GUID("095d1f278e0ede11980f9a097666e103");
		GUID inputRoleRoleId = new GUID("035d1f278e0ede11980f9a097666e103");
		GUID outputInstanceRoleId = new GUID("055d1f278e0ede11980f9a097666e103");
		GUID outputRoleRoleId = new GUID("075d1f278e0ede11980f9a097666e103");
		GUID generatorRoleId = new GUID("f05c1f278e0ede11980f9a097666e103");

		/* Definition of the PDTransform data model */
		store.createModel(transaction, pdTransformModelId, "PDTransform Model");
		
		store.createType(transaction, pdTransformModelId, generatorApplicationTypeId, "Generator Application");
		store.createType(transaction, pdTransformModelId, textTypeId, "Text");

		/* Relation between Text and String */		
		store.createRelation(transaction, textTypeId, null, "content",
				contentRoleId, PDStore.STRING_TYPEID);	
		
		/* Relation between Generator Application and input Object */
		store.createRelation(transaction, generatorApplicationTypeId, null, "input",
				inputRoleId, PDStore.GUID_TYPEID);	
		
		/* Relation between Generator Application and output Object */
		store.createRelation(transaction, generatorApplicationTypeId, null, "output",
				outputRoleId, PDStore.GUID_TYPEID);	
		
		store.createType(transaction, pdTransformModelId, generatorTypeId, "Generator");
		store.createType(transaction, pdTransformModelId, mapTypeId, "Map");
		store.createType(transaction, pdTransformModelId, orderTypeId, "Order");
		store.createType(transaction, pdTransformModelId, orderedPairTypeId, "Ordered Pair");
		
		/* Relation between Generator and Input Type */
		store.createRelation(transaction, generatorTypeId, null, "input type",
				inputTypeRoleId, PDStore.TYPE_TYPEID);	

		/* Relation between Generator and Output Object */
		store.createRelation(transaction, generatorTypeId, null, "output template",
				outputTemplateRoleId, PDStore.GUID_TYPEID);	
		
		/* Relation between Generator and Output Type*/
		store.createRelation(transaction, generatorTypeId, null, "output type",
				outputTypeRoleId, PDStore.TYPE_TYPEID);	
		
		/* Relation between Generator and Order */
		store.createRelation(transaction, generatorTypeId, null, "order",
				orderRoleId, orderTypeId);	
		
		/* Relation between Order and Ordered Pair */
		store.createRelation(transaction, orderTypeId, null, "ordered pairs",
				orderedPairsRoleId, orderedPairTypeId);	
		
		/* Relation between Ordered Pair and Previous Object */
		store.createRelation(transaction, orderedPairTypeId, null, "prev",
				prevRoleId, PDStore.GUID_TYPEID);	

		/* Relation between Ordered Pair and Previous Object */
		store.createRelation(transaction, orderedPairTypeId, null, "next",
				nextRoleId, PDStore.GUID_TYPEID);	
		
		/* Relation between Generator and Map */
		store.createRelation(transaction, generatorTypeId, null, "map",
				mapRoleId, mapTypeId);	
		
		/* Relation between Map and Input Type */
		store.createRelation(transaction, mapTypeId, null, "input type",
				inputTypeRoleId2, PDStore.TYPE_TYPEID);	
		
		/* Relation between Map and Input Role */
		store.createRelation(transaction, mapTypeId, null, "input role",
				inputRoleRoleId, PDStore.ROLE_TYPEID);
		
		/* Relation between Map and Output Instance */
		store.createRelation(transaction, mapTypeId, null, "output instance",
				outputInstanceRoleId, PDStore.GUID_TYPEID);
		
		/* Relation between Map and Output Role */
		store.createRelation(transaction, mapTypeId, null, "output role",
				outputRoleRoleId, PDStore.ROLE_TYPEID);
		
		/* Relation between Generator Application and Generator */
		store.createRelation(transaction, generatorApplicationTypeId, "generator applications", "generator",
				generatorRoleId, generatorTypeId);	
	}

	private void createAddressBookModel() {
		// Create the GUIDs
		GUID contactRoleId = new GUID("eb5c1f278e0ede11980f9a097666e103");
		GUID contactsNameRoleId = new GUID("0d5d1f278e0ede11980f9a097666e103");
		GUID addressRoleId = new GUID("0f5d1f278e0ede11980f9a097666e103");
		GUID subAddressBookRoleId = new GUID("115d1f278e0ede11980f9a097666e103");
		GUID labelRoleId = new GUID("afba85348e0ede11980f9a097666e103");
		
		/* Definition of the Address Book data model */
		store.createModel(transaction, addressBookModelId, "Address Book Model");
		
		store.createType(transaction, addressBookModelId, addressBookTypeId, "Address Book");
		store.createType(transaction, addressBookModelId, contactTypeId, "Contact");
		
		/* Relation between Address Book and Contact */
		store.createRelation(transaction, addressBookTypeId, null, "contact",
				contactRoleId, contactTypeId);
		
		/* Relation between Contact and Name String */
		store.createRelation(transaction, contactTypeId, null, "contacts name",
				contactsNameRoleId, PDStore.STRING_TYPEID);
		
		/* Relation between Contact and Address String */
		store.createRelation(transaction, contactTypeId, null, "address",
				addressRoleId, PDStore.STRING_TYPEID);
		
		/* Relation between Address Book and Sub Address Book */
		store.createRelation(transaction, addressBookTypeId, null, "sub address book",
				subAddressBookRoleId, addressBookTypeId);
		
		/* Relation between Address Book and String */
		store.createRelation(transaction, addressBookTypeId, null, "label",
				labelRoleId, PDStore.STRING_TYPEID);
	}
	
	private void createHTMLModel() {
		// Create the GUIDs
		GUID childRoleId = new GUID("f65c1f278e0ede11980f9a097666e103");
		GUID childRoleId2 = new GUID("f85c1f278e0ede11980f9a097666e103");
		GUID textRoleId = new GUID("135d1f278e0ede11980f9a097666e103");
		GUID textRoleId2 = new GUID("adba85348e0ede11980f9a097666e103");
		
		/* Definition of the HTML data model */
		store.createModel(transaction, htmlModelId, "HTML Model");
		
		store.createType(transaction, htmlModelId, HTMLTagTypeId, "HTML tag");
		store.createType(transaction, htmlModelId, ULTagTypeId, "UL tag");
		store.createType(transaction, htmlModelId, LITagTypeId, "LI tag");

		/* Relation between HTML tag and UL tag */
		store.createRelation(transaction, HTMLTagTypeId, null, "child",
				childRoleId, ULTagTypeId);
		
		/* Relation between UL tag and LI tag */
		store.createRelation(transaction, ULTagTypeId, null, "child",
				childRoleId2, LITagTypeId);
		
		/* Relation between LI tag and Text */
		store.createRelation(transaction, LITagTypeId, null, "text",
				textRoleId, textTypeId);
		
		/* Relation between HTML tag and Text */
		store.createRelation(transaction, HTMLTagTypeId, null, "text",
				textRoleId2, textTypeId);	
	}
	
	private void createSerializerModel() {
		// Create the GUIDs
		GUID orderRoleId = new GUID("bdba85348e0ede11980f9a097666e103");
		GUID instructionsRoleId = new GUID("611e85348e0ede11980f9a097666e103");
		GUID typeRoleId = new GUID("641e85348e0ede11980f9a097666e103");
		GUID printBeforeRoleId = new GUID("661e85348e0ede11980f9a097666e103");
		GUID printAfterRoleId = new GUID("681e85348e0ede11980f9a097666e103");
		GUID inputRoleId = new GUID("a5ba85348e0ede11980f9a097666e103");
		GUID inputTypeRoleId = new GUID("a7ba85348e0ede11980f9a097666e103");
		GUID outputRoleId = new GUID("a9ba85348e0ede11980f9a097666e103");
		GUID serializerRoleId = new GUID("abba85348e0ede11980f9a097666e103");

		/* Definition of the Serializer data model */
		store.createModel(transaction, serializerModelId, "Serializer Model");
		
		store.createType(transaction, serializerModelId, serializerTypeId, "Serializer");
		store.createType(transaction, serializerModelId, printInstructionTypeId, "Print Instruction");
		store.createType(transaction, serializerModelId, serializerApplicationTypeId, "Serializer Application");

		/* Relation between Serializer Application and Order */
		store.createRelation(transaction, serializerApplicationTypeId, null, "order",
				orderRoleId, orderTypeId);

		/* Relation between the serializer and print instruction */
		store.createRelation(transaction, serializerTypeId, null, "instruction",
				instructionsRoleId, printInstructionTypeId);

		/* Relation between the print instruction and type */
		store.createRelation(transaction, printInstructionTypeId, null, "type",
				typeRoleId, PDStore.TYPE_TYPEID);
		
		/* Relation between the print instruction and print Before */
		store.createRelation(transaction, printInstructionTypeId, null, "print before",
				printBeforeRoleId, PDStore.STRING_TYPEID);	
		
		/* Relation between the print instruction and print After */
		store.createRelation(transaction, printInstructionTypeId, null, "print after",
				printAfterRoleId, PDStore.STRING_TYPEID);	
		
		/* Relation between Serializer Application and input Object  */
		store.createRelation(transaction, serializerApplicationTypeId, null, "input",
				inputRoleId, PDStore.GUID_TYPEID);	
		
		/* Relation between Serializer Application and input Type */
		store.createRelation(transaction, serializerApplicationTypeId, null, "input type",
				inputTypeRoleId, PDStore.TYPE_TYPEID);	
		
		/* Relation between Serializer Application and output string */
		store.createRelation(transaction, serializerApplicationTypeId, null, "output",
				outputRoleId, PDStore.STRING_TYPEID);	
		
		/* Relation between Serializer Application and Serializer */
		store.createRelation(transaction, serializerApplicationTypeId, null, "serializer",
				serializerRoleId, serializerTypeId);	
	}
	
	private void createDALClasses() {
		PDWorkingCopy pdWorkingCopy = new PDSimpleWorkingCopy(store);

		// Create the model
		PDModel model = (PDModel) pdWorkingCopy.newInstance(PDModel.typeId);
		model.setName("PDTransform");
		store.commit(transaction);
		
		// Generate the DAL files.
		PDGen.generateModel("PDTransform Model", "apps", pdWorkingCopy, "pdtransform.dal");
		PDGen.generateModel("Address Book Model", "apps", pdWorkingCopy, "pdtransform.dal");
		PDGen.generateModel("HTML Model", "apps", pdWorkingCopy, "pdtransform.dal");
		PDGen.generateModel("Serializer Model", "apps", pdWorkingCopy, "pdtransform.dal");
	}

	public static void main(String[] args) {
		CreatePDTransformModel m = new CreatePDTransformModel();
		m.createPDTransformModel();
		m.createAddressBookModel();
		m.createHTMLModel();
		m.createSerializerModel();
		m.createDALClasses();
	}
}
