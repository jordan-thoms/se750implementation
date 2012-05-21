package pdstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Functions that make use of PD models are tested here. As a triplestore,
 * PDStore does not require the definition or use of a model. However, PDStore
 * does support optional typing of instances and relations. An example of this
 * is the PD metamodel.
 * 
 * @author clut002
 * 
 */
public class ModelTest extends TestCase {

	public PDStore store;

	@Before
	public void setUp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		// create the usual store including the metamodel by default
		store = new PDStore("ModelTest-" + dateFormat.format(date));
	}

	public final void testInstanceExists() {
		GUID transaction = store.begin();
		assertTrue(store.instanceExists(transaction, PDStore.TYPE_TYPEID));
		assertTrue(store.instanceExists(transaction, PDStore.MODEL_TYPEID));
		assertTrue(store.instanceExists(transaction, PDStore.ROLE_TYPEID));
		assertTrue(store.instanceExists(transaction, PDStore.PARTNER_ROLEID));
		store.commit(transaction);
	}

	public final void testGetAllInstancesOfType() {
		GUID transaction = store.begin();
		Collection<Object> result = store.getAllInstancesOfType(transaction,
				PDStore.MODEL_TYPEID);
		assertTrue(result.contains(PDStore.PDMETAMODEL_ID));

		result = store.getAllInstancesOfType(transaction, PDStore.TYPE_TYPEID);
		assertTrue(result.contains(PDStore.TYPE_TYPEID));
		assertTrue(result.contains(PDStore.ROLE_TYPEID));
		assertTrue(result.contains(PDStore.MODEL_TYPEID));
		assertTrue(result.contains(PDStore.BOOLEAN_TYPEID));
		assertTrue(result.contains(PDStore.STRING_TYPEID));
		assertTrue(result.contains(PDStore.INTEGER_TYPEID));
		assertTrue(result.contains(PDStore.DOUBLE_PRECISION_TYPEID));
		assertTrue(result.contains(PDStore.BLOB_TYPEID));
		assertTrue(result.contains(PDStore.TIMESTAMP_TYPEID));
		assertTrue(result.contains(PDStore.GUID_TYPEID));

		result = store.getAllInstancesOfType(transaction, PDStore.ROLE_TYPEID);
		assertTrue(result.contains(PDStore.OWNED_ROLE_ROLEID));
		assertTrue(result
				.contains(PDStore.OWNED_ROLE_ROLEID.getPartner()));
		assertTrue(result.contains(PDStore.PARTNER_ROLEID));
		assertTrue(result.contains(PDStore.PARTNER_ROLEID.getPartner()));
		assertTrue(result.contains(PDStore.ISPRIMITIVE_ROLEID));
		assertTrue(result.contains(PDStore.ISPRIMITIVE_ROLEID.getPartner()));
		assertTrue(result.contains(PDStore.NAME_ROLEID));
		assertTrue(result.contains(PDStore.NAME_ROLEID.getPartner()));
		assertTrue(result.contains(PDStore.ICON_ROLEID));
		assertTrue(result.contains(PDStore.ICON_ROLEID.getPartner()));
		assertTrue(result.contains(PDStore.MODELTYPE_ROLEID));
		assertTrue(result.contains(PDStore.MODELTYPE_ROLEID.getPartner()));
		assertTrue(result.contains(PDStore.BRANCH_ROLEID));
		assertTrue(result.contains(PDStore.BRANCH_ROLEID.getPartner()));

		store.commit(transaction);
	}

	public final void testReadMetamodel() {
		GUID transaction = store.begin();
		Collection<Object> result = store.getInstances(transaction,
				PDStore.PDMETAMODEL_ID, PDStore.MODELTYPE_ROLEID);
		assertTrue(result.contains(PDStore.TYPE_TYPEID));
		assertTrue(result.contains(PDStore.ROLE_TYPEID));
		store.commit(transaction);
	}

	public final void testRoleCreation() {
		GUID t = store.begin();
		GUID model = new GUID();
		store.createModel(t, model, "Dummy");
		store.commit(t);
		System.out.println("Model Created");

		t = store.begin();
		GUID type1 = new GUID();
		store.createType(t, model, type1, "Person");
		store.commit(t);

		t = store.begin();
		GUID type2 = new GUID();
		store.createType(t, model, type1, "Name");
		store.commit(t);

		t = store.begin();
		GUID id = new GUID();
		String role1 = "hasPerson";
		String role2 = "hasName";
		store.createRelation(t, type1, role1, role2, id, type2);
		store.commit(t);
		t = store.begin();
		System.out.println("Role Name: " + store.getName(t, id) + " created");
		System.out.println("Role Id: " + store.getId(t, role2) + " created");
		assertEquals(role2, store.getName(t, id));
		GUID instP = new GUID();
		GUID instN = new GUID();
		store.addLink(t, instP, id, instN);
		store.commit(t);
		t = store.begin();
		GUID g = (GUID) store.getInstance(t, instP, id);
		System.out.println((store.getAllInstancesOfType(t, type1)).size());
		assertEquals(instN, g);
	}

	public final void testGetAccessorType() {
		GUID t = store.begin();
		assertEquals(PDStore.ROLE_TYPEID, store.getAccessorType(t,
				PDStore.MIN_MULT_ROLEID));
		assertEquals(PDStore.ROLE_TYPEID, store.getAccessorType(t,
				PDStore.MAX_MULT_ROLEID));
		assertEquals(PDStore.OBJECT_TYPEID, store.getAccessorType(t,
				PDStore.NAME_ROLEID));
		assertEquals(PDStore.MODEL_TYPEID, store.getAccessorType(t,
				PDStore.MODELTYPE_ROLEID));
		assertEquals(PDStore.OBJECT_TYPEID, store.getAccessorType(t,
				PDStore.HAS_TYPE_ROLEID));
		store.commit(t);
	}
	
	public final void testGetOwnerType() {
		GUID t = store.begin();
		assertEquals(PDStore.INTEGER_TYPEID, store.getOwnerType(t,
				PDStore.MIN_MULT_ROLEID));
		assertEquals(PDStore.INTEGER_TYPEID, store.getOwnerType(t,
				PDStore.MAX_MULT_ROLEID));
		assertEquals(PDStore.STRING_TYPEID, store.getOwnerType(t,
				PDStore.NAME_ROLEID));
		assertEquals(PDStore.TYPE_TYPEID, store.getOwnerType(t,
				PDStore.MODELTYPE_ROLEID));
		assertEquals(PDStore.TYPE_TYPEID, store.getOwnerType(t,
				PDStore.HAS_TYPE_ROLEID));
		store.commit(t);
	}
}