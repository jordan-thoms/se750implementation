package testmanager;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDGen;
import pdstore.dal.PDSimpleWorkingCopy;

public class CreateTestingModel {
	PDStore store = new PDStore("TestManager");

	public final static GUID TESTING_MODELID = new GUID(
			"98f039207e0211e18975842b2b9af4fd");
	public final static GUID TEST_REPO_TYPEID = new GUID(
			"98f060307e0211e18975842b2b9af4fd");
	public final static GUID TEST_SUITE_TYPEID = new GUID(
			"98f060317e0211e18975842b2b9af4fd");
	public final static GUID TEST_CASE_TYPEID = new GUID(
			"98f060327e0211e18975842b2b9af4fd");
	public final static GUID DUMMY98f060337e0211e18975842b2b9af4fd = new GUID(
			"98f060337e0211e18975842b2b9af4fd");
	public final static GUID DUMMY98f060347e0211e18975842b2b9af4fd = new GUID(
			"98f060347e0211e18975842b2b9af4fd");

	private void createModel() {
		GUID transaction = store.begin();

		store.createModel(transaction, TESTING_MODELID,
				"Testing Model");

		store.createType(transaction, TESTING_MODELID,
				TEST_REPO_TYPEID, "Test Repository");
		store.createType(transaction, TESTING_MODELID,
				TEST_SUITE_TYPEID, "Test Suite");
		store.createType(transaction, TESTING_MODELID,
				TEST_CASE_TYPEID, "Test Case");

		store.createRelation(transaction, TEST_REPO_TYPEID,
				"test repository", "test suite", new GUID(
						"98f060357e0211e18975842b2b9af4fd"),
				TEST_SUITE_TYPEID);

		store.createRelation(transaction, TEST_SUITE_TYPEID,
				"test suite", "test case", new GUID(
						"98f060367e0211e18975842b2b9af4fd"),
						TEST_CASE_TYPEID);

		store.createRelation(transaction, TEST_CASE_TYPEID,
				"test case", "succeeds", new GUID(
						"98f060397e0211e18975842b2b9af4fd"),
						PDStore.BOOLEAN_TYPEID);

		store.commit(transaction);
	}

	public void createDALClasses() {
		PDSimpleWorkingCopy copy = new PDSimpleWorkingCopy(store);
		PDGen.generateModel("Testing Model", "apps", copy, "testmanager.dal");
	}

	public static void main(String[] args) {
		CreateTestingModel m = new CreateTestingModel();
		m.createModel();
		m.createDALClasses();
	}
}
