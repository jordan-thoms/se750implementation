package pdintegrate.deprecated;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.*;

import pdintegrate.DAL.DAL;
import pdintegrate.DAL.Entity;
import pdintegrate.DAL.Match;
import pdintegrate.DAL.ObjectSchema;
import pdintegrate.DAL.PlayerExample;
import pdintegrate.DAL.Schema;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.PDStoreException;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;


/**
 * This class is for parsing an XML document and pushing it into the PDStore.
 * 
 * @author Danver Braganza
 *
 */
public class XMLTestClass {
	
	public static void main(String[] args) {
		
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		
		XMLReader parser;
		try {
			PDStore p = null;
			
			System.out.println("Remember to delete PDStore.pds\n Press enter when completed");
			System.in.read();
			p = PDStore.connectToServer(null);
			DAL.init(p);
			PDWorkingCopy pw = new PDSimpleWorkingCopy(p);
			// Create a Schema.
			Schema s = new Schema(pw);
			s.addEntity(new Entity(pw, "clubhub_clubgroups", "CompositeType"));
			s.addEntity(new Entity(pw, "club", "CompositeType"));
			s.addEntity(new Entity(pw, "name", "String"));
			s.addEntity(new Entity(pw, "identifier", "String"));
			s.addEntity(new Entity(pw, "grade", "CompositeType"));
			s.addEntity(new Entity(pw, "team", "CompositeType"));
			s.addEntity(new Entity(pw, "gradeteam", "CompositeType"));
			s.addEntity(new Entity(pw, "player", "CompositeType"));
			s.addEntity(new Entity(pw, "teamplayer", "CompositeType"));
			s.addEntity(new Entity(pw, "id", "Integer"));
			s.addEntity(new Entity(pw, "gradeid", "Integer"));
			s.addEntity(new Entity(pw, "teamid", "Integer"));
			s.addEntity(new Entity(pw, "playerid", "Integer"));
			s.addEntity(new Entity(pw, "firstname", "String"));
			s.addEntity(new Entity(pw, "lastname", "String"));
			s.addEntity(new Entity(pw, "email", "String"));
			s.addEntity(new Entity(pw, "dobyear", "Integer"));
			s.addEntity(new Entity(pw, "dobmonth", "Integer"));
			s.addEntity(new Entity(pw, "dobday", "Integer"));
			s.addEntity(new Entity(pw, "gender", "String"));
			s.addRelation("clubhub_clubgroups", "club", false);
			s.addRelation("club", "name", false);
			s.addRelation("club", "identifier", false);
			s.addRelation("club", "grade", true);
			s.addRelation("club", "gradeteam", true);
			s.addRelation("club", "player", true);
			s.addRelation("club", "teamplayer", true);
			s.addRelation("grade", "id", false);
			s.addRelation("grade", "name", false);
			s.addRelation("team", "id", false);
			s.addRelation("team", "name", false);
			s.addRelation("gradeteam", "gradeid", false);
			s.addRelation("gradeteam", "teamid", false);
			s.addRelation("player", "id", false);
			s.addRelation("player", "firstname", false);
			s.addRelation("player", "lastname", false);
			s.addRelation("player", "email", false);
			s.addRelation("player", "dobyear", false);
			s.addRelation("player", "dobmonth", false);
			s.addRelation("player", "dobday", false);
			s.addRelation("player", "gender", false);
			s.addRelation("teamplayer", "teamid", false);
			s.addRelation("teamplayer", "playerid", false);
			
			parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(new XMLtoPD(p, s));
			
			System.out.println("Models created\n Press enter to begin loading");
			System.in.read();
			
			parser.parse("test.xml");
			
			//Should all be stored in PDStore now.
			
			//Now we need an ObjectSchema
			ObjectSchema os = new ObjectSchema(pw, PlayerExample.class);
			Match m = new Match(pw, os, s);
			m.addMapping("None", "Player", "PlayerExample");
			m.addMapping("None", "firstname", "name");
			m.addMapping("None", "dobMonth", "dateOfBirth");
			m.addMapping("None", "gender", "gender");
			
			System.out.println("Loading finished.\nPress enter to begin retrieval\n\n");
			System.in.read();
			
			PDIntegrationLoad pl = new PDIntegrationLoad(null);
			for (PlayerExample pe : pl.getObjectsSince(PlayerExample.class, m, null)) {
				System.out.println(pe.toString());
			}
			
			System.out.println("All done");
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PDStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
	}
	

}
