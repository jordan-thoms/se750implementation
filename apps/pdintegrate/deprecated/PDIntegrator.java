package pdintegrate.deprecated;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import pdintegrate.DAL.DAL;
import pdintegrate.model.DataSource;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;

/**
 * This class is a front end to 
 * 
 * @author Danver Braganza
 *
 */
public class PDIntegrator {
	
	/**
	 * 
	 * Used when running this as a Stand-alone java application.
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		// TODO Handle arguments nicely.  Consider what options we wish to provide here.
		// TODO Run in update-only, read-only or normal mode.

		String serverID;
		if (args.length >= 1) { 
			serverID = args[0];
		} else {
			System.err.println("Missing server id.  Using default.");
			serverID = "";
		}

		//Build an instance of this Integrator class.

		try {
			PDIntegrator integrator = new PDIntegrator(serverID);
		} catch (PDStoreException e) {
			System.err.println("Could not connect to PDServer. Original exception was:");
			e.printStackTrace();
			//System.exit(1);
		}

		List<DataSource> sources = new ArrayList<DataSource>();

		// Read in sources from a config file. Instantiate the sources and return a list.

		Properties config = new Properties();
		try {
			config.load(new InputStreamReader(config.getClass().getClassLoader().getResourceAsStream("/sources.properties")));
		} catch (IOException e) {
			System.err.println("No sources configuration file found. Original exception was:");
			e.printStackTrace();
		}

		//For each source
		for (DataSource d : sources) {
			// Set up a thread pool, to poll the source every time specified in the config file.

			//new ThreadWorker(d).start();

			//If there are changes, the thread reads the changes, commits a transaction to the server
			//TODO Make this thread safe! Cache the transaction in memory before committing.
		}

	}

	private class ResultListener implements Runnable {

		private ResultListener() {

		}

		public void run() {
			// TODO Auto-generated method stub

		}
	}

	private PDStore repository;

	/**
	 * This method creates an instance of the PDIntegrater.
	 * @param serverID
	 * 
	 * TODO: Create connection Exception
	 */

	public PDIntegrator(String serverID) throws PDStoreException {
		try {
			this.repository = PDStore.connectToServer(serverID);

			//If the repository doesn't have the model
			if (!modelExists(repository, "PDIntegratorModel")) {
				DAL.init(repository);
			}

			//So we are guaranteed that a model exists.


		} catch (PDStoreException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * This method loads the data from a dataSource
	 * 
	 * Must be fast!
	 * 
	 * Think about: adds, deletes and modifies.
	 * 
	 * 
	 * @param s data source to read
	 * @return
	 */
	public boolean loadData(DataSource s) {
		Date t = new Date();
		if (s.updatedSince(t)) {
			s.getUpdate(t).process(this.repository);
			return true;
		}
		return false;
	}

	public boolean modelExists(PDStore repository, String name) {
		GUID t = repository.begin();
		Collection<Object> kk = repository.getAllInstancesOfType(t, PDStore.MODEL_TYPEID);
		String temp = null;
		for (Object s : kk){
			temp = (String)repository.getInstance(t, s, PDStore.NAME_ROLEID);
			if (temp == null)
				continue;
			if (temp.equals(name)){
				return true;
			}
		}
		repository.commit(t);
		return false;
	}

}

