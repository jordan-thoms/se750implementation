package pdintegrate.deprecated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import pdintegrate.DAL.Entity;
import pdintegrate.DAL.Mapping;
import pdintegrate.DAL.Match;
import pdintegrate.DAL.Relation;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;

/**
 * This class is a front end to 
 * 
 * @author Danver Braganza
 *
 */
public class PDIntegrationLoad {

	/**
	 * 
	 * Used when running this as a Stand-alone java application.
	 * 
	 * @param args
	 */


	private PDStore repository;

	/**
	 * This method creates an instance of the PDIntegrater.
	 * @param serverID
	 * 
	 * TODO: Create connection Exception
	 */
	public PDIntegrationLoad(String serverID) throws PDStoreException {
		try {
			this.repository = PDStore.connectToServer(serverID);
		} catch (PDStoreException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Currently ignore datetime
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws PDStoreException 
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 */
	public <T> Collection<T> getObjectsSince(Class<T> c, Match m, Date t) throws PDStoreException, InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, SecurityException, NoSuchFieldException {
		ArrayList<T> retval = new ArrayList<T>();
		GUID transaction = this.repository.begin();

		/*
		 * 
		 * How does this algorithm work?
		 * 
		 * First we query the match to find the left hand side of all mappings,
		 * that is, all the source types.
		 * 
		 * For each source type, we need to create the given object if it is composite,
		 * or save the value of the number or string otherwise
		 * 
		 * Then we must make sure they are all connected.
		 * 
		 * Then we return all objects that match class c.
		 * 
		 */
		HashMap<String, String> localMapping = new HashMap<String, String>();
		HashMap<GUID, Object> outputs = new HashMap<GUID, Object>();
		// From source to object

		for (Object o : m.getMappings()) {
			Mapping map = (Mapping)o;
			localMapping.put(map.getSourceEntity(), map.getObjectEntity());
			//TODO: Ignoring map.getOperation for now.
		}
		PDWorkingCopy wc = new PDSimpleWorkingCopy(repository);
		
		HashMap<String, GUID> typeIDs = new HashMap<String, GUID>();
		for (Object o : this.repository.getAllInstancesOfType(transaction, Relation.TYPE_ID)) {
			try {
				Relation r = (Relation)wc.load(Relation.TYPE_ID, (GUID)o);
				System.out.println("Relation :" + r.getEntity().getName() + " : " + r.getId());
				typeIDs.put(r.getEntity().getName(), r.getId());
			} catch (ClassCastException e) {
				System.out.println("ClassCastError at relations: " + o);
				continue;
			}
		}

		for (Object o : this.repository.getInstances(transaction, Entity.TYPE_ID, transaction)) {
			try {
				Entity e = (Entity)wc.load(Entity.TYPE_ID, (GUID)o);
				for (Object oo : this.repository.getAllInstancesOfType(transaction, e.getId())) {
					System.out.println("We found something, and it's id is ");
				}
				System.out.println("Name: " + e.getName());
				T frog = c.newInstance();
				c.getField("dateOfBirth").set(frog, new Integer((int)(Math.random() * 12 + 0.5)));
				c.getField("name").set(frog, "Bob");
				c.getField("email").set(frog, "bill@example.com");
				c.getField("gender").set(frog, "M");
				retval.add(frog);
				for (Object oo : this.repository.getAllInstancesOfType(transaction, e.getId())) {
					System.out.println("found something");
					GUID g = (GUID) oo;
					if (e.getType().equals("Composite")  && e.getName().equals("player")) {
						System.out.println(this.repository.getInstance(transaction, g, typeIDs.get("dateOfBirth")));				
					}
				}
				//				if (localMapping.containsKey(e.getName())) {
				//					System.out.println("Hit!");
				//					for (Object oo : this.repository.getAllInstancesOfType(transaction, e.getId())) {
				//						System.out.println("found something");
				//						GUID g = (GUID) oo;
				//						if (e.getType().equals("String")) {
				//							outputs.put(g, wc.load(PDStore.STRING_TYPEID, g));
				//							System.out.println("String pulled out");;
				//						} else if (e.getType().equals("Integer")) {
				//							outputs.put(g, wc.load(PDStore.INTEGER_TYPEID, g));
				//							System.out.println("Integer pulled out");;
				//						} else if (e.getType().equals("Composite")) {
				//							outputs.put(g, Class.forName(e.getName()).newInstance());
				//						}
				//					}
				//				}
			} catch (ClassCastException e) {
				System.out.println("ClassCastError: " + o);
				continue;
			}

		}

		return retval;
	}



}

