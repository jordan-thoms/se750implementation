package fluid.dummy;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import fluid.BioPDPortal;
import fluid.util.DataBox;
import fluid.util.spreadsheet.PDSSheetPaser;
import fluid.util.spreadsheet.PDSWorkbook;

import pdedit.PDEdit;
import pdstore.GUID;
import pdstore.PDStore;

public class DummyRiverData {
	static String modelName = "RiverModel";
	static String typeName = "River";
	static String rHasName = "name";
	static String rHasId = "riverId";
	static String rsites = "sites";
	static String typeNameSite = "Sites";
	static String typeNameSample = "Samples";
	static String rsamples = "samples";
	public final static Object [][] data = {
		{"Cascades Confluence" , 44603},
		{"Opanuku at Candia Rd" , 7904},
		{"Greenmount Drive Pakuranga" , 8215},
		{"Guys Rd Pakuranga" , 8216},
		{"Botany Rd",8217}
	};

	static DummyRiverData runner;
	static PDEdit editor;
	PDStore store;

	public static void main(String[] args) {
		runner = new DummyRiverData();
		//runner.createDummy("PDXplorer(1)");
		runner.createDummy("PDXplorer(2)");

	}

	private void createDummy(String name) {
		fileClean(name);
		editor = new PDEdit(new BioPDPortal(name));
		editor.setEnablePDEdit(false);
		editor.showPDModelWindow();
		continueWindow().setVisible(true);
	}

	private void fileClean(String name) {
		name = "pddata/"+name+".pds";
		File file = new File(name);
		if (file.exists()){
			int count = 0;
			while (file.exists()&& count < 10){
				boolean b = file.delete();
				if (b)
					System.out.println("Delete Successful");
				else
					System.out.println("Delete Not Successful");
				try {
					Thread.sleep(1000+count*200);
				} catch (InterruptedException e) {
				}
				count++;
			}
		}else{
			System.out.println(name + " does not exist");
		}

	}

	public JFrame continueWindow(){
		final JFrame frame = new JFrame("Dummy");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		JPanel panel = new JPanel();
		JButton button = new JButton("Continue");
		button.setPreferredSize(new Dimension(200,50));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.closePDModelWindow();
				frame.dispose();
				inputData();
				addSites();
				//testGetData();
				System.out.println("--------------DONE------------------");
				System.exit(0);
			}
		});
		JButton close = new JButton("Close");
		close.setPreferredSize(new Dimension(200,50));
		close.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editor.closePDModelWindow();
				frame.dispose();
				System.exit(0);
			}
		});
		panel.add(button);
		panel.add(close);
		frame.getContentPane().add(panel);
		frame.pack();
		return frame;
	}

	private void inputData() {
		store = editor.getStore();

		HashMap<String, GUID> roles = getRoles();
		GUID transaction = store.begin();
		for (int i = 0; i < data.length; i ++){
			System.out.println((String)data[i][0] +" , "+(Integer)data[i][1]);
			GUID riverInstance = new GUID();
			store.addLink(transaction, riverInstance, roles.get(rHasName), (String)data[i][0]);
			store.addLink(transaction, riverInstance, roles.get(rHasId), (Integer)data[i][1]);
		}
		store.commit(transaction);
	}

	private HashMap<String, GUID> getRoles(){
		HashMap<String, GUID> rolesRet = new HashMap<String, GUID>();
		GUID transaction = store.begin();
		Collection<Object> types = store.getInstances(transaction, 
				store.getId(transaction, modelName), 
				PDStore.MODELTYPE_ROLEID);
		for (Object o : types){
			Collection<Object> roles = store.getInstances(transaction, o, PDStore.ACCESSIBLE_ROLES_ROLEID);
			for (Object ob : roles){
				rolesRet.put(store.getName(transaction, (GUID)ob), (GUID)ob);
			}

		}
		return rolesRet;
	}

	private void addSites(){
		GUID transaction = store.begin();
		GUID sitesR = store.getId(transaction, rsites);
		GUID sitesT = store.getId(transaction, typeNameSite);
		GUID siteId = new GUID();
		GUID siteName = new GUID();
		//Create roles to type sites
		store.createRelation(transaction, sitesT, "", "siteId",siteId, PDStore.INTEGER_TYPEID);
		store.createRelation(transaction, sitesT, "", "siteName", siteName, PDStore.STRING_TYPEID);
		store.commit(transaction);

		//link to type instance
		Collection<Object> rivers = store.getAllInstancesOfType(transaction, store.getId(transaction, typeName));
		for (Object r : rivers){
			for (int i = 0; i < 3; i ++){
				transaction = store.begin();
				GUID newSite = new GUID();
				store.addLink(transaction, r, sitesR, newSite);
				store.addLink(transaction, newSite, siteId, i+1);
				store.addLink(transaction, newSite, siteName, "Site "+i);
				String riverName = (String)store.getInstance(transaction, r, store.getId(transaction, rHasName));
				store.commit(transaction);
				System.out.println(riverName);
				transaction = store.begin();
				System.out.println(newSite+"="+store.getInstance(transaction, r, sitesR));
				if (riverName.equals("Cascades Confluence")){
					if (i == 0){
						processSpreadSheet("spread\\Book1CC.xlsx",(GUID)r,sitesT,newSite);
					}else if (i == 1){
						processSpreadSheet("spread\\Book2CC.xlsx",(GUID)r,sitesT,newSite);
					}else if (i == 2){
						processSpreadSheet("spread\\Book3CC.xlsx",(GUID)r,sitesT,newSite);
					}
				}else if(riverName.equals("Greenmount Drive Pakuranga")){
					if (i == 0){
						processSpreadSheet("spread\\Book1GD.xlsx",(GUID)r,sitesT,newSite);
					}else if (i == 1){
						processSpreadSheet("spread\\Book2GD.xlsx",(GUID)r,sitesT,newSite);
					}else if (i == 2){
						processSpreadSheet("spread\\Book3GD.xlsx",(GUID)r,sitesT,newSite);
					}
				}else if(riverName.equals("Opanuku at Candia Rd")){
					if (i == 0){
						processSpreadSheet("spread\\Book1O.xlsx",(GUID)r,sitesT,newSite);
					}else if (i == 1){
						processSpreadSheet("spread\\Book2O.xlsx",(GUID)r,sitesT,newSite);
					}else if (i == 2){
						processSpreadSheet("spread\\Book3O.xlsx",(GUID)r,sitesT,newSite);
					}
				}else if(riverName.equals("Guys Rd Pakuranga")){
					if (i == 0){
						processSpreadSheet("spread\\Book1GR.xlsx",(GUID)r,sitesT,newSite);
					}else if (i == 1){
						processSpreadSheet("spread\\Book2GR.xlsx",(GUID)r,sitesT,newSite);
					}else if (i == 2){
						processSpreadSheet("spread\\Book3GR.xlsx",(GUID)r,sitesT,newSite);
					}
				}else if(riverName.equals("Botany Rd")){
					if (i == 0){
						processSpreadSheet("spread\\Book1BR.xlsx",(GUID)r,sitesT,newSite);
					}else if (i == 1){
						processSpreadSheet("spread\\Book2BR.xlsx",(GUID)r,sitesT,newSite);
					}else if (i == 2){
						processSpreadSheet("spread\\Book3BR.xlsx",(GUID)r,sitesT,newSite);
					}
				}
				

			}
		}
	}

	private void processSpreadSheet(String fullPathFileName,GUID riverInst, GUID siteT, GUID siteInst){
		GUID transaction = store.begin();
		try {
			GUID modelId = store.getId(transaction, modelName);
			// link to instance
			PDSWorkbook wb = new PDSWorkbook(fullPathFileName);
			PDSSheetPaser sheet = new PDSSheetPaser(wb.getSheet(0));
			Iterator<Cell> cells = sheet.getRows(2, 2).get(0).cellIterator();
			ArrayList<String> links = new ArrayList<String>();


			// link header
			while (cells.hasNext()){
				Cell c = cells.next();
				String value = sheet.evaluateCell(c).getStringValue();
				System.out.println(value);
				links.add(value);
			}
			ArrayList<GUID> test = new ArrayList<GUID>(links.size());
			ArrayList<Row> rows = sheet.getRows(3, sheet.getNumberOfRows());
			//Scan thru once for type
			//test = scanForTypes(test, rows);
			//Create Relations to types
			String typeName = "Measurements";
			GUID typeId = store.getId(transaction, typeName);
			//If Null create the new type
			if (typeId == null){
				typeId = new GUID();
				store.createType(transaction, modelId, typeId, typeName);
			}
			//if Null create new Role
			String newRole = "measurements";
			GUID roledID = store.getId(transaction, newRole);
			if (roledID == null){
				roledID = new GUID();
				String name = store.getName(transaction, siteT);
				store.createRelation(transaction, siteT, name, 
						newRole, roledID, typeId);

			}
			HashMap<String, GUID> roles = new HashMap<String, GUID>();
			for (int i = 0; i < links.size(); i++){
				GUID r = store.getId(transaction, links.get(i));
				if (r != null){
					roles.put(links.get(i), r);
				}else{
					roles.put(links.get(i), new GUID());
					store.createRelation(transaction, typeId, "", links.get(i), roles.get(links.get(i)), PDStore.STRING_TYPEID);
				}
			}
			store.commit(transaction);
			//Add instances
			for (Row r : rows){
				GUID t = null;
				if (r == null){
					continue;
				}
				GUID newRowInst = new GUID();
				Iterator<Cell> testCells = r.cellIterator();
				t = store.begin();
				store.addLink(t, siteInst, roledID, newRowInst);
				store.commit(t);
				while (testCells.hasNext()){
					t = store.begin();
					Cell c = testCells.next();
					String link = links.get(c.getColumnIndex());
					GUID role2 = roles.get(link);
					Object instance = castCellToValue(c);
					if (instance != null){
						store.addLink(t, newRowInst, role2, c.toString());
						store.commit(t);
					}

				}

			}


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Object castCellToValue(Cell c){
		Object ret = null;
		//System.out.println(c.toString());
		try{
			ret = Integer.parseInt(c.toString());
		}catch (Exception e) {
			//System.out.println("Not Integer");
		}
		try{
			ret = Double.parseDouble(c.toString());
		}catch (Exception e) {
			//System.out.println("Not Double");
		}
		try{
			if(c.toString().contains("-")){
				DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
				Date d = df.parse(c.toString());   
				ret = d;
				//System.out.println("Today = " + df.format(d));
			}
			//System.out.println(c.toString());
		}catch (Exception e) {
			//System.out.println("Not Date");
		}
		if (ret == null){
			ret = c.toString();
		}
		//System.out.println();
		return ret;
	}
}
