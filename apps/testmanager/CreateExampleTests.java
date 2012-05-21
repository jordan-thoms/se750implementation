package testmanager;

import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;
import testmanager.dal.*;

public class CreateExampleTests {

	public static void main(String[] args) {
		PDStore store = new PDStore("TestManager");
		PDSimpleWorkingCopy copy = new PDSimpleWorkingCopy(store);
		
		PDTestRepository repo = new PDTestRepository(copy);
		repo.setName("Rahul's test repo");
		
		PDTestSuite suite = new PDTestSuite(copy);
		suite.setName("Rahul's test suite");
		repo.addTestSuite(suite);
		
		PDTestCase testCase = new PDTestCase(copy);
		testCase.setName("my test case");
		suite.addTestCase(testCase);
		
		copy.commit();
	}

}
