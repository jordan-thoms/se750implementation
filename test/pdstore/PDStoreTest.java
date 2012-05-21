package pdstore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import nz.ac.auckland.se.genoupe.tools.Debug;
import pdstore.generic.PDChange;
import pdstore.sparql.Variable;

/**
 * These are the main tests for PDStore. Note that this tests only functions
 * that do NOT make use of models. Hence, to make debugging easier, the PD
 * metamodel is NOT included in the pds file used for the tests.
 * 
 * All the testing of models (i.e. typed instances and relations) is done in
 * class PDStoreModelsTest.
 * 
 * @author clut002
 * 
 */
public class PDStoreTest extends TestCase {

	// use local embedded store
	protected PDStore store;

	protected GUID guid1;
	protected GUID guid2;
	protected GUID guid3;
	protected GUID guid4;
	protected GUID guid5;
	protected GUID guid6;

	protected GUID roleA;
	protected GUID roleB;
	protected GUID roleC;

	protected String stringHello;
	protected String stringWorld;
	protected String stringGood;
	protected String stringMorning;

	protected Timestamp ts1;
	protected Timestamp ts2;

	protected Blob blob111;
	protected Blob blob999;

	private String fileName;

	public void setUp() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();
		fileName = "PDStoreTest-" + dateFormat.format(date);
		store = new PDStore(fileName);
		VariableInitialization();
	}

	public void VariableInitialization() {

		guid1 = new GUID("111decaf111111111111111111111111");
		guid2 = new GUID("222decaf222222222222222222222222");
		guid3 = new GUID("333decaf333333333333333333333333");
		guid4 = new GUID("444decaf444444444444444444444444");
		guid5 = new GUID("555decaf555555555555555555555555");
		guid6 = new GUID("666decaf666666666666666666666666");

		roleA = new GUID("aaadecafaaaaaaaaaaaaaaaaaaaaaaaa");
		roleB = new GUID("bbbdecafbbbbbbbbbbbbbbbbbbbbbbbb");
		roleC = new GUID("cccdecafcccccccccccccccccccccccc");

		stringHello = "hello";
		stringWorld = "world";
		stringGood = "good";
		stringMorning = "morning";

		ts1 = new Timestamp(0);
		ts2 = new Timestamp(1);
		;

		blob111 = new Blob(new byte[] { 1, 1, 1 });
		blob999 = new Blob(new byte[] { 9, 9, 9 });
	}

	public final void testGetRepositoryGuid() {
		GUID repository = store.getRepository();
		assertTrue(repository != null);
	}

	/* TODO add again when has-type links are again inferred using the new view concept
	public final void testGetHasLink() {
		GUID transaction = store.begin();
 
		assertEquals(PDStore.STRING_TYPEID, store.getInstance(transaction,
				"hello", PDStore.HAS_TYPE_ROLEID));
		assertEquals(PDStore.INTEGER_TYPEID,
				store.getInstance(transaction, 123, PDStore.HAS_TYPE_ROLEID));
		assertEquals(PDStore.DOUBLE_PRECISION_TYPEID,
				store.getInstance(transaction, 0.123, PDStore.HAS_TYPE_ROLEID));
		assertEquals(PDStore.BOOLEAN_TYPEID,
				store.getInstance(transaction, true, PDStore.HAS_TYPE_ROLEID));

		store.commit(transaction);
	}
	*/

	public final void testReadWriteLink_GUID_GUID() {
		// locally regenerate GUIDs to avoid id conflict
		regenerateGUID();
		GUID transaction = store.begin();
		store.addLink(transaction, guid1, roleA, guid2);
		Collection<Object> result = store.getInstances(transaction, guid1,
				roleA);
		assertTrue(result.contains(guid2));

		store.removeLink(transaction, guid1, roleA, guid2);
		result = store.getInstances(transaction, guid1, roleA);

		assertTrue(!result.contains(guid2));

		store.addLink(transaction, guid1, roleA, guid2);
		store.addLink(transaction, guid1, roleA, guid3);
		store.commit(transaction);
		transaction = store.begin();
		result = store.getInstances(transaction, guid1, roleA);
		assertTrue(result.size() >= 2);
		assertTrue(result.contains(guid2));
		assertTrue(result.contains(guid3));

		store.commit(transaction);
	}

	protected void regenerateGUID() {
		List<GUID> l = new GUIDGen().generateGUIDs();
		guid1 = l.get(1);
		guid2 = l.get(2);
		guid3 = l.get(3);
		guid4 = l.get(4);
		guid5 = l.get(5);
		guid6 = l.get(6);
	}

	public final void testReadWriteLink_String_String() {
		GUID transaction = store.begin();
		store.addLink(transaction, stringHello, roleA, stringWorld);
		Collection<Object> result = store.getInstances(transaction,
				stringHello, roleA);
		assertTrue(result.contains(stringWorld));

		store.removeLink(transaction, stringHello, roleA, stringWorld);
		result = store.getInstances(transaction, stringHello, roleA);
		assertTrue(!result.contains(stringWorld));

		store.addLink(transaction, stringHello, roleA, stringGood);
		store.addLink(transaction, stringHello, roleA, stringMorning);
		store.commit(transaction);
		transaction = store.begin();
		result = store.getInstances(transaction, stringHello, roleA);
		assertTrue(result.size() >= 2);
		assertTrue(result.contains(stringGood));
		assertTrue(result.contains(stringMorning));

		store.commit(transaction);
	}

	public final void testTransactionGuids() {
		GUID transaction = store.begin();
		assertTrue(!transaction.isFirst());
		store.addLink(transaction, new GUID(), new GUID(), new GUID());
		GUID durableTransaction = store.commit(transaction);
		assertTrue(durableTransaction.isFirst());
	}

	public final void testGetInstances() {
		GUID transaction = store.begin();

		final GUID myAccount = guid1;
		final GUID numberRoleId = roleA;
		store.addLink(transaction, myAccount, numberRoleId, 99);
		store.addLink(transaction, myAccount, numberRoleId, 98);
		transaction = store.commit(transaction);

		Collection<Object> result = store.getInstances(transaction, myAccount,
				numberRoleId);

		// This must support both number types currently
		// Since cached stores return small ints.
		assertTrue(result.contains(99L) || result.contains(99));
		assertTrue(result.contains(98L) || result.contains(98));
	}

	public final void testGetId() {
		GUID transaction = store.begin();
		store.setName(transaction, guid1, "Hello");
		assertEquals(guid1, store.getId(transaction, "Hello"));
		GUID durableTransaction = store.commit(transaction);
		assertEquals(guid1, store.getId(durableTransaction, "Hello"));
	}

	public final void testGetIds() {
		GUID transaction = store.begin();
		final GUID role2Id = new GUID("67528ef116c116dfb2a21013722c66da");
		store.addLink(transaction, guid1, role2Id, guid2);
		store.addLink(transaction, guid1, role2Id, guid3);
		store.addLink(transaction, guid1, role2Id, guid4);

		store.setName(transaction, guid1, "Hello");
		store.setName(transaction, guid2, "Hello");
		assertEquals(store.getIds(transaction, "Hello").size(), 2);
		store.commit(transaction);
	}

	public final void testSetGetRemoveName() {
		GUID transaction = store.begin();
		store.setName(transaction, guid1, "Hello");
		String name = store.getName(transaction, guid1);
		assertEquals("Hello", name);

		store.setName(transaction, guid1, "Hello2");
		name = store.getName(transaction, guid1);
		assertEquals("Hello2", name);

		// TODO what is the semantics of remove? Does it remove all instances?
		store.removeName(transaction, guid1);
		name = store.getName(transaction, guid1);
		assertTrue(name != "Hello2");

		store.commit(transaction);
	}

	public final void testRemoveIcon() {
		GUID transaction = store.begin();
		final GUID role2Id = new GUID("67528ef116c116dfb2a22014722c66da");
		store.addLink(transaction, guid1, role2Id, guid2);
		store.setIcon(transaction, guid1, blob999);
		store.removeIcon(transaction, guid1);
		assertEquals(store.getIcon(transaction, guid1), null);
	}

	public final void testRemoveName() {
		GUID transaction = store.begin();
		guid1 = new GUID();
		final GUID role2Id = new GUID("67528ef116c1164fb2a22014722c66da");
		store.addLink(transaction, guid1, role2Id, guid2);
		store.setName(transaction, guid1, "HiHello");
		store.removeName(transaction, guid1);
		assertEquals(store.getName(transaction, guid1), null);
	}

	public final void testSetAndGetIcon() {
		GUID transaction = store.begin();
		final GUID role2Id = new GUID("67528ea116c1164fb2a22014722c66da");
		store.addLink(transaction, guid1, role2Id, guid2);
		store.setIcon(transaction, guid1, blob999);
		assertEquals(store.getIcon(transaction, guid1), blob999);
	}

	public final void testRollback() {
		GUID transaction = store.begin();
		store.rollback(transaction);
	}

	public final void testReadWrite_Double() {
		GUID id1 = new GUID();
		GUID r2 = new GUID();

		GUID transaction1 = store.begin();
		store.addLink(transaction1, id1, r2, 9.9);
		Collection<Object> result = store.getInstances(transaction1, id1, r2);
		assertTrue(result.contains(9.9));
		transaction1 = store.commit(transaction1);

		GUID transaction2 = store.begin();
		store.removeLink(transaction2, id1, r2, 9.9);
		store.addLink(transaction2, id1, r2, 1.1);
		result = store.getInstances(transaction2, id1, r2);
		assertTrue(result.contains(1.1));
		transaction2 = store.commit(transaction2);

		result = store.getInstances(transaction1, id1, r2);
		assertTrue(result.contains(9.9));
	}

	public final void testHistoryDoubleInstance() {
		GUID transaction = store.begin();
		final GUID RateRoleId = new GUID();
		final GUID myAccount = new GUID();
		store.addLink(transaction, myAccount, RateRoleId, 9.9);
		assertEquals(9.9, store.getInstance(transaction, myAccount, RateRoleId));
		GUID transaction1 = store.commit(transaction);
		Object result = store.getInstance(transaction1, myAccount, RateRoleId);
		assertEquals(9.9, result);
		transaction = store.begin();
		store.removeLink(transaction, myAccount, RateRoleId, 9.9);
		store.addLink(transaction, myAccount, RateRoleId, 1.1);
		GUID transaction2 = store.commit(transaction);
		Object result2 = store.getInstance(transaction2, myAccount, RateRoleId);
		assertEquals(1.1, result2);
	}

	public final void testBooleanInstance() {
		final GUID accountTypeId = new GUID("abaad46067c211dfadad002170295212");
		final GUID modelId = new GUID("abaad46167c211dfadad002170295223");
		final GUID BooleanRoleId = new GUID("abaad46367c211dfadad002170295245");
		GUID transaction = store.begin();

		final GUID myAccount = new GUID("2b3dba3067c811df948a002170295256");
		store.addLink(transaction, myAccount, BooleanRoleId, true);
		store.commit(transaction);

		transaction = store.begin();
		assertEquals(true,
				store.getInstance(transaction, myAccount, BooleanRoleId));
		assertEquals(myAccount, store.getInstance(transaction, true,
				BooleanRoleId.getPartner()));
	}

	public final void testStringInstance() {
		final GUID role2Id = new GUID("67528ef116c111dfb2e20013722c66da");
		GUID transaction = store.begin();
		store.addLink(transaction, stringHello, role2Id, stringWorld);
		transaction = store.commit(transaction);
		assertEquals(store.getInstance(transaction, stringHello, role2Id),
				stringWorld);
	}

	public final void testTimestampInstance() {
		final GUID role2Id = new GUID("67528ef016c211dfb2e20013722c66da");
		GUID transaction = store.begin();
		store.addLink(transaction, ts1, role2Id, ts2);
		final GUID durableTransaction = store.commit(transaction);

		assertEquals(store.getInstance(durableTransaction, ts1, role2Id), ts2);
		System.err.println(store.getChanges(ts1, role2Id));
		assertEquals(store.getChanges(ts1, role2Id).iterator().next()
				.getTransaction(), durableTransaction);
		assertEquals(store.getChanges(ts2, role2Id.getPartner()).iterator()
				.next().getTransaction(), durableTransaction);
	}

	public final void testReadWriteOneStoreConcurrently() {
		PDStore store1 = store;
		PDStore store2 = store;
		GUID transaction1 = store1.begin();
		GUID transaction2 = store2.begin();
		Collection<Object> result;

		// connection 1
		store1.addLink(transaction1, guid1, roleA, guid6);
		store1.removeLink(transaction1, guid1, roleA, guid3);
		result = store1.getInstances(transaction1, guid1, roleA);
		assertTrue(result.contains(guid6));
		assertTrue(!result.contains(guid3));

		// connection 2
		store2.addLink(transaction2, guid1, roleA, guid3);
		store2.removeLink(transaction2, guid1, roleA, guid6);
		result = store2.getInstances(transaction2, guid1, roleA);
		assertTrue(result.contains(guid3));
		assertTrue(!result.contains(guid6));
		GUID resultTransaction2 = store2.commit(transaction2);

		// check that transaction1 still cannot see transaction2
		result = store1.getInstances(transaction1, guid1, roleA);
		assertTrue(result.contains(guid6));
		assertTrue(!result.contains(guid3));

		GUID resultTransaction1 = store1.commit(transaction1);

		// a new transaction should see committed transaction1 as current
		// database state
		transaction2 = store2.begin();
		result = store.getInstances(resultTransaction1, guid1, roleA);
		assertTrue(result.contains(guid6));
		store2.commit(transaction2);
	}

	public final void testSnapshotIsolation() {
		// TODO: put this test in own test class
		Debug.addDebugTopic("commit");
		GUID guid1 = new GUID();
		GUID guid3 = new GUID();
		GUID guid4 = new GUID();
		GUID guid6 = new GUID("111decaf123456111111111111111111");
		GUID roleA = new GUID();

		// TODO if this is put into own test class, then 
		// the following should be two different connections to server
		// if test is executed remotely
		PDStore store1 = store;
		PDStore store2 = store;
		Collection<Object> result;

		// make sure guid6 is added
		GUID transaction0 = store2.begin();
		result = store.getInstances(transaction0, guid1, roleA);
		store1.addLink(transaction0, guid1, roleA, guid6);

		GUID durableTransaction = store2.commit(transaction0);
		assertTrue(durableTransaction != null);

		// now test two concurrent transactions
		GUID transaction1 = store1.begin();
		GUID transaction2 = store2.begin();

		// make sure state is unchanged at first in t1
		result = store1.getInstances(transaction1, guid1, roleA);
		assertFalse(result.contains(guid3));
		assertFalse(result.contains(guid4));
		assertTrue(result.contains(guid6));

		result = store2.getInstances(transaction2, guid1, roleA);
		assertFalse(result.contains(guid3));
		assertFalse(result.contains(guid4));
		assertTrue(result.contains(guid6));

		// t1 sets value to guid3
		store1.removeLink(transaction1, guid1, roleA, guid6);
		store1.addLink(transaction1, guid1, roleA, guid3);
		result = store1.getInstances(transaction1, guid1, roleA);
		assertFalse(result.contains(guid6));
		assertTrue(result.contains(guid3));

		// t2 sets value to guid4
		result = store2.getInstances(transaction2, guid1, roleA);
		assertTrue(result.contains(guid6));
		store2.removeLink(transaction2, guid1, roleA, guid6);
		store2.addLink(transaction2, guid1, roleA, guid4);
		result = store2.getInstances(transaction2, guid1, roleA);
		assertFalse(result.contains(guid6));
		assertTrue(result.contains(guid4));

		// t2 commits first
		@SuppressWarnings("unused")
		GUID resultTransaction2 = store2.commit(transaction2);

		// check that transaction1 still cannot see transaction2
		result = store1.getInstances(transaction1, guid1, roleA);
		assertTrue(result.contains(guid3));
		assertFalse(result.contains(guid4));

		// commit of t1 should fail because "first committer (t2) wins"
		GUID resultTransaction1 = store1.commit(transaction1);
		assertTrue(resultTransaction1 == null);

		// a new transaction should see committed transaction2
		// but not transaction 1
		GUID transaction9 = store2.begin();
		result = store.getInstances(transaction9, guid1, roleA);
		assertFalse(result.contains(guid3));
		assertTrue(result.contains(guid4));
		store2.commit(transaction9);

		Debug.removeDebugTopic("commit");
	}

	public final void testIsolationLevelNone() {
		// copied from testSnapShotIsolation
		// only some asserts changed/
		// TODO: refactor as parameterized test, 
		// TODO: add more isolevels, 
		Debug.addDebugTopic("commit");
		GUID guid1 = new GUID();
		GUID guid3 = new GUID();
		GUID guid4 = new GUID();
		GUID guid6 = new GUID("111decaf123456111111111111111111");
		GUID roleA = new GUID();

		// TODO if this is put into own test class, then 
		// the following should be two different connections to server
		// if test is executed remotely
		PDStore store1 = store;
		PDStore store2 = store;
		Collection<Object> result;

		// make sure guid6 is added
		GUID transaction0 = store2.begin();
		result = store.getInstances(transaction0, guid1, roleA);
		store1.addLink(transaction0, guid1, roleA, guid6);

		GUID durableTransaction = store2.commit(transaction0);
		assertTrue(durableTransaction != null);

		// now test two concurrent transactions
		GUID transaction1 = store1.begin(IsolationLevel.NONE);
		GUID transaction2 = store2.begin(IsolationLevel.SNAPSHOT);

		// make sure state is unchanged at first in t1
		result = store1.getInstances(transaction1, guid1, roleA);
		assertFalse(result.contains(guid3));
		assertFalse(result.contains(guid4));
		assertTrue(result.contains(guid6));

		result = store2.getInstances(transaction2, guid1, roleA);
		assertFalse(result.contains(guid3));
		assertFalse(result.contains(guid4));
		assertTrue(result.contains(guid6));

		// t1 sets value to guid3
		store1.removeLink(transaction1, guid1, roleA, guid6);
		store1.addLink(transaction1, guid1, roleA, guid3);
		result = store1.getInstances(transaction1, guid1, roleA);
		assertFalse(result.contains(guid6));
		assertTrue(result.contains(guid3));

		// t2 sets value to guid4
		result = store2.getInstances(transaction2, guid1, roleA);
		assertTrue(result.contains(guid6));
		store2.removeLink(transaction2, guid1, roleA, guid6);
		store2.addLink(transaction2, guid1, roleA, guid4);
		result = store2.getInstances(transaction2, guid1, roleA);
		assertFalse(result.contains(guid6));
		assertTrue(result.contains(guid4));

		// t2 commits first
		@SuppressWarnings("unused")
		GUID resultTransaction2 = store2.commit(transaction2);

		result = store1.getInstances(transaction1, guid1, roleA);
		assertTrue(result.contains(guid3));
		// check that transaction1 now can already see transaction2
		assertTrue(result.contains(guid4));

		// commit of t1 should succeed because it has Isolation Level NONE
		GUID resultTransaction1 = store1.commit(transaction1);
		assertTrue(resultTransaction1 != null);

		// a new transaction should see committed transaction2
		// and transaction 1
		GUID transaction9 = store2.begin();
		result = store.getInstances(transaction9, guid1, roleA);
		assertTrue(result.contains(guid3));
		assertTrue(result.contains(guid4));
		store2.commit(transaction9);

		Debug.removeDebugTopic("commit");
	}

	public final void testReadWriteLink_Blob_Blob() {
		assertTrue(blob111.equals(new Blob(new byte[] { 1, 1, 1 })));
		assertTrue(!blob111.equals(blob999));

		GUID transaction = store.begin();
		store.addLink(transaction, blob111, roleC, blob999);
		final GUID durableTransaction = store.commit(transaction);

		Blob result = (Blob) store.getInstance(durableTransaction, blob111,
				roleC);
		assertTrue(result.equals(blob999));
		result = (Blob) store.getInstance(durableTransaction, blob999,
				roleC.getPartner());
		assertTrue(result.equals(blob111));
	}

	public final void testSetAndGet() {
		GUID roleA = new GUID();

		// check if set works for the first time
		GUID transaction = store.begin();
		String instanceA = "A";
		String instanceB = "B";
		String instanceC = "C";
		String instanceD = "D";
		store.setLink(transaction, instanceA, roleA, instanceB);

		// check result within the transaction
		assertTrue(store.getInstance(transaction, instanceA, roleA).equals(instanceB));
		GUID durableTransaction = store.commit(transaction);

		// check result after transaction
		assertTrue(durableTransaction != null);
		assertTrue(store.getInstance(durableTransaction, instanceA, roleA)
				.equals(instanceB));

		// check result in new transaction
		transaction = store.begin();
		assertTrue(store.getInstance(transaction, instanceA, roleA).equals(instanceB));

		// check more calls to set
		store.setLink(transaction, instanceA, roleA, instanceC);
		assertTrue(store.getInstance(transaction, instanceA, roleA).equals(instanceC));
		store.setLink(transaction, instanceA, roleA, instanceD);
		assertTrue(store.getInstance(transaction, instanceA, roleA).equals(instanceD));
		durableTransaction = store.commit(transaction);

		assertTrue(durableTransaction != null);
		assertTrue(store.getInstance(durableTransaction, instanceA, roleA)
				.equals(instanceD));
	}

	public final void testUsesRole() {
		roleA = new GUID();
		GUID transaction = store.begin();
		GUID instance1 = new GUID();
		Object instance2 = new GUID();
		store.addLink(transaction, instance1, roleA, instance2);
		GUID durableTransaction = store.commit(transaction);
		assertTrue(durableTransaction != null);

		// testing setting of USES_ROLE
		Collection<Object> roles = store.getInstances(durableTransaction,
				instance1, PDStore.USES_ROLE_ROLEID);
		assertTrue(roles.contains(roleA));
	}

	public final void testGetChanges() {
		GUID transaction = store.begin();

		final GUID myAccount = guid1;
		final GUID numberRoleId = roleA;
		store.addLink(transaction, myAccount, numberRoleId, 888);
		transaction = store.commit(transaction);
		PDChange<GUID, Object, GUID> change = new PDChange<GUID, Object, GUID>(
				ChangeType.LINK_ADDED, transaction, myAccount, numberRoleId,
				888);
		Collection<PDChange<GUID, Object, GUID>> result = store
				.getChanges(change);
		assertEquals(1, result.size());

		// when instance1 and intance2 are variables
		transaction = store.begin();
		final GUID roleC = new GUID();
		store.addLink(transaction, 1, roleC, 2);
		store.addLink(transaction, 3, roleC, 21);
		transaction = store.commit(transaction);
		change = new PDChange<GUID, Object, GUID>(ChangeType.LINK_ADDED,
				transaction, new Variable("x"), roleC, new Variable("y"));
		result = store.getChanges(change);
		assertEquals(2, result.size());
	}

	public final void testIsInLog() {
		GUID sampleRole = new GUID("d580d29015bc11e185f51cc1dec00ed3");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		Date date = new Date();

		String storename = "IsInLogTest" + dateFormat.format(date);
		// PDStore storefirst = new PDStore(storename);
		PDStore storefirst = store;
		GUID transaction = storefirst.begin();

		final GUID myAccount = guid1;
		final GUID numberRoleId = roleA;
		store.addLink(transaction, myAccount, sampleRole, 78);
		transaction = store.commit(transaction);

		GUID transaction1 = store.begin();
		store.addLink(transaction1, sampleRole.getFirst(),
				PDStore.IS_NOT_IN_LOG_ROLEID, 1);
		store.commit(transaction1);

		GUID transaction11 = store.begin();

		store.addLink(transaction11, myAccount, sampleRole, 79);
		transaction11 = store.commit(transaction11);
		// storefirst.close();
		// storefirst = null;
		PDStore store2 = new PDStore(fileName);
		Collection<Object> result = store2.getInstances(transaction11,
				myAccount, sampleRole);

		// This must support both number types currently
		// Since cached stores return small ints.
		assertTrue(result.contains(78L) || result.contains(78));
		assertFalse(result.contains(79L) || result.contains(79));
	}

	public final void testIsInIndex() {
		GUID sampleRole = new GUID("7f66d24015be11e1a6981cc1dec00ed3");

		GUID transaction = store.begin();

		final GUID myAccount = guid1;
		final GUID numberRoleId = roleA;
		store.addLink(transaction, myAccount, sampleRole, 88);
		transaction = store.commit(transaction);

		GUID transaction1 = store.begin();
		store.addLink(transaction1, sampleRole.getFirst(),
				PDStore.IS_NOT_IN_INDEX_ROLEID, 1);
		store.commit(transaction1);

		GUID transaction11 = store.begin();

		store.addLink(transaction11, myAccount, sampleRole, 89);
		transaction11 = store.commit(transaction11);

		Collection<Object> result = store.getInstances(transaction11,
				myAccount, sampleRole);

		// This must support both number types currently
		// Since cached stores return small ints.
		assertFalse(result.contains(89L) || result.contains(89));
		assertTrue(result.contains(88L) || result.contains(88));
	}

	private static byte[] imagetobytearray(File f) {
		try {
			FileInputStream fis = new FileInputStream(f);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];

			for (int i; (i = fis.read(buf)) != -1;) {
				bos.write(buf, 0, i);
			}
			byte[] bytes = bos.toByteArray();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
