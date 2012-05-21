package fluid;

import fluid.table.PDTableModel;
import aim.tablewidget.DataHandler;
import aim.tablewidget.DefaultTableModelHandler;
import aim.tablewidget.TableWidget;
import pdedit.dal.util.PDEditPortal;
import pdstore.GUID;
import pdstore.PDStore;

public class BioPDPortal implements PDEditPortal {
	
	private PDStore store;
	private TableWidget table;
	
	public BioPDPortal(){
		store = new PDStore("PDXplorer");
	}
	
	public BioPDPortal(String storeName){
		if (storeName != null){
			store = new PDStore(storeName);
		}else{
			store = new PDStore("PDXplorer");
		}
	}
	

	public GUID changedNode() {
		System.out.println("Node Changed: "+" Type: ");
		return null;
	}

	public PDStore getPDStore() {
		return store;
	}

	public void nodeChanged(GUID id, String type) {
		// TODO Auto-generated method stub

	}

	public void nodeSelected(GUID id, String type) {
		System.out.println("Node Selected: "+id+" Type: "+type);
		try{
			PDTableModel t = new PDTableModel(id, null, store);
			DataHandler d = new DefaultTableModelHandler(t);
			table.setDataHandler(d);
		}catch(Exception e){}
	}

	public GUID selectedNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAppName() {
		return "PDXplorer for Scienific Data";
	}

	public String getStoreName() {
		return "PDXplorer";
	}

	
	public void modelSelected(GUID id) {
		// TODO Auto-generated method stub
		
	}

	public void setTable(TableWidget tablewidget) {
		table = tablewidget;
	}

}
