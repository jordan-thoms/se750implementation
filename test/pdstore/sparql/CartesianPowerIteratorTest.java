package pdstore.sparql;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import pdstore.GUID;
import pdstore.generic.PDChange;

public class CartesianPowerIteratorTest extends TestCase {

	public final void testEmptySet() {
		List<String> input = new ArrayList<String>();
		CartesianPowerIterator<String> cartesianPower = new CartesianPowerIterator<String>(
				input, 0);
		assertTrue(!cartesianPower.hasNext());

		cartesianPower = new CartesianPowerIterator<String>(input, 1);
		assertTrue(!cartesianPower.hasNext());

		cartesianPower = new CartesianPowerIterator<String>(input, 3);
		assertTrue(!cartesianPower.hasNext());
	}

	public final void testOneElement() {
		List<String> input = new ArrayList<String>();
		input.add("a");
		CartesianPowerIterator<String> cartesianPower = new CartesianPowerIterator<String>(
				input, 0);
		assertTrue(!cartesianPower.hasNext());

		cartesianPower = new CartesianPowerIterator<String>(input, 1);
		List<String> output = new ArrayList<String>();
		output.add("a");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(!cartesianPower.hasNext());

		cartesianPower = new CartesianPowerIterator<String>(input, 2);
		output.add("a");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(!cartesianPower.hasNext());
	}

	public final void testTwoElements() {
		List<String> input = new ArrayList<String>();
		input.add("a");
		input.add("b");

		CartesianPowerIterator<String> cartesianPower = new CartesianPowerIterator<String>(
				input, 0);
		assertTrue(!cartesianPower.hasNext());

		cartesianPower = new CartesianPowerIterator<String>(input, 1);
		List<String> output = new ArrayList<String>();
		output.add("a");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());
		output.set(0, "b");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(!cartesianPower.hasNext());

		cartesianPower = new CartesianPowerIterator<String>(input, 2);
		output.set(0, "a");
		output.add("a");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());

		output.set(0, "b");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());

		output.set(0, "a");
		output.set(1, "b");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());

		output.set(0, "b");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(!cartesianPower.hasNext()); // end of product
	}

	public final void testThreeElements() {
		List<String> input = new ArrayList<String>();
		input.add("a");
		input.add("b");
		input.add("c");
		CartesianPowerIterator<String> cartesianPower = new CartesianPowerIterator<String>(
				input, 0);
		assertTrue(!cartesianPower.hasNext());

		cartesianPower = new CartesianPowerIterator<String>(input, 1);
		List<String> output = new ArrayList<String>();
		output.add("a");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());
		output.set(0, "b");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());
		output.set(0, "c");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(!cartesianPower.hasNext());

		cartesianPower = new CartesianPowerIterator<String>(input, 2);
		output.set(0, "a");
		output.add("a");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());

		output.set(0, "b");
		output.set(1, "a");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());

		output.set(0, "c");
		output.set(1, "a");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());

		output.set(0, "a");
		output.set(1, "b");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());

		output.set(0, "b");
		output.set(1, "b");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());

		output.set(0, "c");
		output.set(1, "b");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());

		output.set(0, "a");
		output.set(1, "c");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());

		output.set(0, "b");
		output.set(1, "c");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());

		output.set(0, "c");
		output.set(1, "c");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(!cartesianPower.hasNext()); // end of product

		cartesianPower = new CartesianPowerIterator<String>(input, 3);
		output.set(0, "a");
		output.set(1, "a");
		output.add("a");
		assertTrue(cartesianPower.hasNext());
		assertTrue(cartesianPower.next().whereTuples.equals(output));
		assertTrue(cartesianPower.hasNext());

		cartesianPower = new CartesianPowerIterator<String>(input, 3);
		int count = 0;
		while (cartesianPower.hasNext()) {
			cartesianPower.next();
			count++;
		}
		assertTrue(count == 27);
	}

	public void printResult(CartesianPowerIterator<String> cartesianPower) {
		while (cartesianPower.hasNext()) {
			for (PDChange<GUID, Object, GUID> output : cartesianPower.next().whereTuples) {
				System.out.print(output + " ");
			}
			System.out.println();
		}
	}
}
