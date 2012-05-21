package pdstore;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import pdstore.stressTest.*;

/**
 * This runs the most common PDStore tests. Some notable exceptions:
 * GUIDLongRunningTest PDStoreStressTests, (takes some seconds) and
 * PDSToreServerTest (requires running server)
 * 
 * @author clut002
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ GUIDTest.class, PDChangeTest.class, LinkIndexTest.class,
		AggregationIteratorTest.class, ChangeLogStoreTest.class,
		PDStoreTest.class, GetChangesTest.class, BranchTest.class, ReadCommittedTest.class,
		ReadUncommittedTest.class,
		ModelTest.class, ListenerTest.class, OperationTest.class })
public class AllTests {
}
