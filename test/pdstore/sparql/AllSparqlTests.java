package pdstore.sparql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import pdstore.stressTest.*;

/**
 * This runs the most common SPARQL tests.
 * 
 * @author clut002
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ CartesianPowerIteratorTest.class, MatchTest.class, IndexIteratorTest.class, QueryExecutionTest.class,OptionalTest.class })
public class AllSparqlTests {
}
