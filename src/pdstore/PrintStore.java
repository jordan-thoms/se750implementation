package pdstore;

import pdstore.generic.PDChange;

public class PrintStore {

	/**
	 * Prints the changes of the store with the given filename to the console
	 * 
	 * @param args
	 *            the filename of the store to print
	 */
	public static void main(String[] args) {
		PDStore store = new PDStore(args[0]);
		for (PDChange<GUID, Object, GUID> change : store)
			System.out.println(change);
		store.close();
	}

}
