package nz.ac.auckland.se750project;

import pdstore.PDStore;
import pdstore.rmi.PDStoreServer;
import pdstore.rmi.ServerInterface;

public class TabulaServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PDStore store = new PDStore("Tabula");
			new PDStoreServer(store);
			System.err.println("Server ready.");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

}
