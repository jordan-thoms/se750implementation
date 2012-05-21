package fluid.table;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import fluid.util.DataBox;
import fluid.util.PDTableWindow;

import pdstore.GUID;
import pdstore.PDStore;

public class PDTableModel implements TableModel{

	private ArrayList<TableModelListener> listeners = new ArrayList<TableModelListener>();
	private ArrayList<PDColumn> columns = new ArrayList<PDColumn>();
	private int numberOfRows = -1;
	private PDStore storage;
	private PDTableWindow tablePreview = null;

	public PDTableModel(GUID type, Collection<Object> instancesOfType, PDStore store){
		System.out.println("PDTableModel: Intialising TableModel");
		storage = store;
		GUID transaction = storage.begin();
		String name =  storage.getName(transaction, type);
		System.out.println("PDTableModel: Getting Roles of " + name);
		Collection<Object> rolesOfType = storage.getInstances(transaction, type, PDStore.ACCESSIBLE_ROLES_ROLEID);
		for (Object o : rolesOfType){
			DataBox b = new DataBox(storage.getName(transaction, (GUID)o), (GUID)o);
			columns.add(new PDColumn(b));
			System.out.println(b.getElement());
		}
		
		if (instancesOfType == null){
			instancesOfType = storage.getAllInstancesOfType(transaction, type);
		}
		
		int instanceCount = 1;
		for (Object o : instancesOfType){
			GUID id = (GUID)o;
			System.out.println(id);
			for (Object role : rolesOfType){
				PDColumn col = getColumnFromID((GUID)role);
				Object obj = null;
				Collection<Object> temp = null;
				if (col != null){
					obj = storage.getInstance(transaction, id, (GUID)role);
					temp = storage.getInstances(transaction, id, (GUID)role);
					if (temp.size() > 1){
						obj = temp;
					}
				}
				if (obj instanceof GUID || obj instanceof Collection<?>){
					PDButton button = new PDButton();
					button.setRole((GUID)role);
					button.setName(role.toString());
					if (temp.size() > 1){
						button.setInstances(temp);
					}else{
						button.setInstance((GUID)obj);
					}
					button.setToolTipText(obj.toString());
					button.setPreferredSize(new Dimension(250, 25));
					button.setSize(new Dimension(250, 25));
					button.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							System.out.println("Open window");
							PDButton b = (PDButton)arg0.getSource();
							GUID instance = null;
							if (instance != b.getInstance()){
								instance = new GUID(b.getInstance());
							}
							Collection<Object> instances = b.getInstances();
							GUID roles = new GUID(b.getName());
							GUID transaction = storage.begin();
							//TODO 
							GUID instanceType = storage.getAccessorType(transaction, roles.getPartner());
							if (instances == null){
								instances  = new ArrayList<Object>();
								instances.add(instance);
							}
							/*ArrayList<Object> instancesOfType = new ArrayList<Object>();
							instancesOfType.add(instance);*/
							if (tablePreview == null){
								tablePreview = new PDTableWindow(storage, instanceType, instances);
							}else{
								tablePreview.setTable(storage, instanceType, instances);
							}
							tablePreview.setTableName(storage.getName(transaction, roles));
							tablePreview.setVisible(true);
						}
					});
					//System.out.println(createText(obj,role));
					button.setText("Open Instance "+instanceCount);
					button.setText(createText(obj,role));
					try{
						col.addCell(new DataBox(button,(GUID)obj));
					}catch(ClassCastException cce){
						col.addCell(new DataBox(button,(GUID)temp.iterator().next()));
					}
					instanceCount++;
				}else if (obj != null){
					col.addCell(new DataBox(obj,(GUID)role));
				}else{
					col.addCell(new DataBox("No Data",(GUID)role));
				}
			}
		}
		
	}

	private String createText(Object ins, Object role2){
		String ret = "[ ";
		GUID transaction = storage.begin();
		GUID instance = null;
		if (ins instanceof Collection<?>){
			instance = (GUID)((Collection<?>) ins).iterator().next();
		}else{
			instance = (GUID)ins;
		}
		GUID type = storage.getAccessorType(transaction, ((GUID)role2).getPartner());
		Collection<Object> rolesOfType = storage.getInstances(transaction, type, PDStore.ACCESSIBLE_ROLES_ROLEID);
		Object obj = null;
		Collection<Object> temp = null;
		int i = 0;
		for (Object role : rolesOfType){
			if (i > 0){
				ret += ", ";
			}
			//System.out.println("Hello: "+storage.getName(transaction, (GUID)role));
			obj = storage.getInstance(transaction, instance, (GUID)role);
			temp = storage.getInstances(transaction, instance, (GUID)role);
			if (temp.size() > 1 || obj instanceof GUID){
				ret += "{}";
			}else if (obj != null){
				ret += obj.toString();
			}else{
				ret += "No Data";
			}
			i++;
		}
		ret += "]";
		return ret;
	}

	private PDColumn getColumnFromID(GUID id){
		for (PDColumn col : columns){
			if (col.getHeaderID().equals(id)){
				return col;
			}
		}
		return null;
	}

	@Override
	public void addTableModelListener(TableModelListener arg0) {
		listeners.add(arg0);

	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public String getColumnName(int arg0) {
		return columns.get(arg0).getHeader();
	}

	@Override
	public int getRowCount() {
		if (numberOfRows < 0){
			for (PDColumn p : columns){
				if (p.getNumberOfCells() > numberOfRows){
					numberOfRows = p.getNumberOfCells();
				}
			}
		}
		return numberOfRows;
	}

	@Override
	public Object getValueAt(int row, int col) {
		try{
			return columns.get(col).getCell(row).getElement();
		}catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
