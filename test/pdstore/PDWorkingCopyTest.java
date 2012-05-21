package pdstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import pdstore.dal.PDModel;
import pdstore.dal.PDRole;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDType;
import pdstore.dal.PDWorkingCopy;

public class PDWorkingCopyTest extends TestCase {

	private static PDWorkingCopy workingCopy = new PDSimpleWorkingCopy(
			new PDStore("pdstore"));

	@Before
	public void setUp() {
	}

	public void testMetatypeType() {
		PDType typeType = PDType.load(workingCopy, PDType.typeId);
		assertEquals(PDType.typeId, typeType.getId());
		assertEquals("Type", typeType.getName());
		assertEquals(PDStore.PDMETAMODEL_ID, typeType.getModel().getId());
		assertEquals(PDStore.PDMETAMODEL_ID, typeType.getModel().getId());

		Collection<PDRole> roles = typeType.getOwnedRoles();
		assertTrue(roles.contains(workingCopy.load(PDRole.typeId,
				PDType.roleHasInstanceId.getPartner())));
		assertTrue(roles.contains(workingCopy.load(PDRole.typeId,
				PDType.roleIsPrimitiveId.getPartner())));
		assertTrue(roles.contains(workingCopy.load(PDRole.typeId,
				PDType.roleModelId.getPartner())));
		assertTrue(roles.contains(workingCopy.load(PDRole.typeId,
				PDType.roleOwnedRoleId.getPartner())));
		workingCopy.commit();
	}

	public void testMetamodel() {
		PDModel pdmodel = PDModel.load(workingCopy, PDStore.PDMETAMODEL_ID);
		assertEquals(PDStore.PDMETAMODEL_ID, pdmodel.getId());
		assertEquals("PD Metamodel", pdmodel.getName());
		assertTrue(pdmodel.getTypes().contains(
				workingCopy.load(PDType.typeId, PDType.typeId)));
		assertTrue(pdmodel.getTypes().contains(
				workingCopy.load(PDType.typeId, PDRole.typeId)));
		workingCopy.commit();
	}

	// TODO: more test cases necessary!
	public void testWorkingCopy() {
		GUID addressId = new GUID();
		GUID postalId = new GUID();
		GUID myModelId = new GUID();
		PDModel pdmodel = PDModel.load(workingCopy, myModelId);
		PDType address = PDType.load(workingCopy, addressId);
		PDType postal = PDType.load(workingCopy, postalId);
		// TODO: some more ops, assertions
	}

}