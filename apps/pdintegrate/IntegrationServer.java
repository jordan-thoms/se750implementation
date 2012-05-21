package pdintegrate;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.Properties;

import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.rmi.PDStoreServer;

public class IntegrationServer {
	
	private static final String PROPERTIES_FILE = "server.properties";
	private static final String DEFAULT_RMIKEY = "Mediator";
	private static final String DEFAULT_FILENAME = "mediator";

	public static void main(String args[]) {
		Properties config = new Properties();
		try {
			config.load(new FileInputStream("/server.properties"));
		} catch (IOException e) {
			try {
			System.err.println("No server configuration file found.\nConfiguration file \""+PROPERTIES_FILE+"\"" +
					" will be created with default values.");
			config.setProperty("filename", DEFAULT_FILENAME);
			config.setProperty("rmiKey", DEFAULT_RMIKEY);
			config.store(new FileOutputStream(PROPERTIES_FILE), "Automatically generated: default value for pdintegrate store!");
			} catch (IOException e1) {
				System.err.println("There was an error creating the configuration file");
			}
		}
		try {
			PDStoreServer server = new PDStoreServer(new PDStore(config.getProperty("filename", DEFAULT_FILENAME)),
					config.getProperty("rmiKey", DEFAULT_RMIKEY));
			System.out.println("Server running.");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PDStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
