package pdstore.stressTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import pdstore.stressTest.*;

/**
 * This runs the most common PDStore tests. 
 * Some notable exceptions: GUIDTest
 * (takes some seconds and is rarely used) and PDSToreServerTest (requires
 * running server)
 * 
 * @author clut002
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { AddingNodes.class, RetrievingNodes.class})
public class AllStressTests {
}
