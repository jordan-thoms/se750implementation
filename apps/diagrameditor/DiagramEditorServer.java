package diagrameditor;

import java.net.InetAddress;

import pdstore.PDStore;
import pdstore.rmi.PDStoreServer;

/**
 * Starts a PDStoreServer that can be accessed remotely.
 * Allows editors running on different computers to 
 * interact collaboratively
 */
public class DiagramEditorServer {

	public static void main(String[] args) {
		try {
			new PDStoreServer(new PDStore("DiagramEditor"));
			System.err.println("Server ready.");
			System.err.println("Server IP address: " + InetAddress.getLocalHost().getHostAddress());
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

}
