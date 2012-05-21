package pdstore.sparql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import pdstore.GUID;
import pdstore.generic.PDChange;

import nz.ac.auckland.se.genoupe.tools.Debug;
import nz.ac.auckland.se.genoupe.tools.StatefulIterator;

/**
 * An iterator that iterates over the full n-ary cartesian product of a
 * collection with itself.
 * 
 * The output is a vector of the current cartesian tuple. The position on index
 * 0 changes most frequently, and is called in analogy to numbers the least
 * significant position. The position on index "exponent-1" is the most
 * significant position.
 * 
 * In paticular this class serves as a superclass for more selective iterators,
 * i.e. iterators that omit parts of the cartesian product. These are in
 * particular classes that implement some version of a natural join.
 * 
 * 
 * @author gweb017
 * 
 * @param <E>
 */
public class CartesianPowerIterator<E> extends StatefulIterator<ResultElement<GUID, Object, GUID>> {
	Iterable<E> input;
	int exponent;
	Vector<Iterator<E>> product;
	boolean isFirstStep = true;

	// pdchange is created to hold the next change of one iterator in product,
	// because we don't want to
	// call next() twice. since the type of pdchange is generic, we create
	// method void assignVariable()
	// in its subclass.
	// everytime next() is called, we should maintain variableAssignment.
	// assign varaibles in the most left change in the where-clause is not
	// necessary, i.e. i=0
	E pdchange;

	Vector<E> output;
	List<E> where;

	// query statistics
	int[] changesProcessed;
	long timeSinceLastStatistics;;

	public int[] getChangesProcessed() {
		return changesProcessed;
	}

	public CartesianPowerIterator() {
		timeSinceLastStatistics = System.currentTimeMillis();
	}

	public CartesianPowerIterator(Iterable<E> input, int exponent) {
		this();
		Debug.assertTrue(exponent >= 0, "Exponent should be >= 0");

		this.input = input;
		this.exponent = exponent;

		init();
	}

	public void init() {
		output = new Vector<E>(exponent);
		product = new Vector<Iterator<E>>(exponent);
		for (int i = this.exponent - 1; i >= 0; i--) {
			product.add(null);
			output.add(null);
		}

		// query execution statistics
		changesProcessed = new int[exponent];
		for (int i = 0; i < exponent; i++) {
			changesProcessed[i] = 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ResultElement<GUID, Object, GUID> computeNext() {
		// to address the problems when exponent is 0
		if (exponent == 0)
			return null;

		// the index of the least significant iterator that
		// still has a next.
		int changingIndex;

		if (isFirstStep) {
			// all iterators need to be reset

			isFirstStep = false;

			// the initial state is that the most significant iterator has to be
			// assigned
			// The algorithm uses a trick:
			// The initial case (i.e. all iterators need to be reset) is seen as
			// equivalent
			// to the case that an additional hypothetical iterator with
			// index==exponent
			// has just been incremented, so all previous ones need to be reset.
			changingIndex = exponent;
		} else {
			// determine which of the iterators need to be reset

			// we determine the value for changingindex,
			// i.e. determine the iterator that needs to be incremented next,
			// which is the next possible one from the left
			for (changingIndex = 0; changingIndex < exponent; changingIndex++)
				if (product.get(changingIndex)!=null&&product.get(changingIndex).hasNext()) {
					break;
				}

			if (changingIndex == exponent) {
				// even the most significant iterator has no next, so we are
				// done.
				return null;
			}

			// the first iterator that has next must be incremented.
			pdchange = nextInProduct(changingIndex);
			output.set(changingIndex, pdchange);
			incrementStatistics(changingIndex);
		}

		// The rest of the method gets new iterators for all positions from
		// changingIndex-1
		// downward to 0. Additionally on all these iterators next() must be
		// called
		// and they must return at least one element.
		// The fact that there can be empty iterators means that
		// backtracking is needed, and the loop variable can oscillate.
		// During backtracking, the loop variable increases, possibly up to
		// "exponent".
		// If even the last iterator has no
		// next, it returns false, otherwise true.

		// The loop is still written as a for-loop instead of a while
		// loop following the rationale that it is easier to understand
		// for the case that there are no empty iterators.

		for (int i = changingIndex - 1; i >= 0; i--) {
			// need to reset the iterator
			Iterator<E> inputIterator = getInputIterator(i);

			if (!inputIterator.hasNext()) {
				// the inputIterator must have a next
				// if not we have to go one iteration step back
				// and get the next entry there
				if (i >= exponent - 1) {
					// even the most significant iterator has no next, so we are
					// done.
					return null;
				}
				// the following increment has to be +2 since the continue
				// command in a for-loop does not prevent the decrement --i
				// from happening.
				// TODO: code was changed: when and why?
				while (!product.get(i + 1).hasNext()) {
					if (i + 1 >= exponent - 1) {
						// even the most significant iterator has no next, so we
						// are done.
						return null;
					}
					i++;
				}
				i++;

				output.set(i, nextInProduct(i));
				incrementStatistics(i);

				continue;
			}
			product.set(i, inputIterator);

			pdchange = nextInProduct(i);
			output.set(i, pdchange);
			incrementStatistics(i);
		}

		return new ResultElement<GUID, Object, GUID>((List<PDChange<GUID, Object, GUID>>) output, null,getVariableAssignment());
	}

	public String getStatistics() {
		String result = "WHERE clause:\n";
		for (int i = 0; i < where.size(); i++)
			result += where.get(i) + " (Changes processed: "
					+ changesProcessed[i] + ")  \r\n";
		return result;
	}

	public void incrementStatistics(int changingIndex) {
		long now = System.currentTimeMillis();
		if (now - timeSinceLastStatistics > 30000) {
			Debug.println(getStatistics());
			timeSinceLastStatistics = now;
		}
		changesProcessed[changingIndex]++;
	}

	E nextInProduct(int i) {
		E next = product.get(i).next();
		return next;
	}

	public Iterator<E> getInputIterator(int i) {
		return input.iterator();
	}

	protected List<E> getOutput() {
		return output;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public Vector<Iterator<E>> getProduct() {
		return product;
	}
	public Map<Variable, Object> getVariableAssignment() {
		return new HashMap<Variable, Object>();
	}
}
