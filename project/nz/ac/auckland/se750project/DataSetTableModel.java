package nz.ac.auckland.se750project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import nz.ac.auckland.se750project.dal.PDDataRecord;
import nz.ac.auckland.se750project.dal.PDDataSet;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDWorkingCopy;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.notify.PDListener;

public class DataSetTableModel extends AbstractTableModel {
	private GUID dataSetGUID;
	private PDStore store;
	private PDDataSet dataSet;
	private PDWorkingCopy wc;
	private Collection<GUID> accessibleRoles;
	
	public DataSetTableModel(final GUID dataSet, PDStore store, PDWorkingCopy wc) {
		this.dataSetGUID = dataSet;
		this.dataSet = new PDDataSet(wc, dataSet);
		this.store = store;
		this.wc = wc;
		
		// Setup the listener
		store.getListenerDispatcher().add(new PDListener<GUID, Object, GUID>() {
			
			@Override
			public void transactionCommitted(
					List<PDChange<GUID, Object, GUID>> transaction,
					List<PDChange<GUID, Object, GUID>> matchedChanges,
					PDCoreI<GUID, Object, GUID> core) {
				fireTableDataChanged();
				System.out.println("Triggered");
			}
			
			@Override
			public Collection<PDChange<GUID, Object, GUID>> getMatchingTemplates() {
				Collection<PDChange<GUID, Object, GUID>> templates = new ArrayList<PDChange<GUID, Object, GUID>>();
				templates.add(new PDChange<GUID, Object, GUID>(null, null, null, null, null));
				return templates;
			}
		});
		
		addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent arg0) {
				System.out.println("ChangeEvent");
			}
		});
	}
	@Override
	public int getColumnCount() {
		GUID transaction = store.begin();
		GUID type = PDDataRecord.typeId;
		accessibleRoles = store.getAccessibleRoles(transaction, type);
		store.commit(transaction);
		return accessibleRoles.size();
	}

	@Override
	public int getRowCount() {
		return dataSet.getRecords().size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		PDDataRecord record = (PDDataRecord) dataSet.getRecords().toArray()[row];
		return wc.getInstance(record, (GUID) accessibleRoles.toArray()[column]);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Long value = Long.valueOf((String) aValue);
		PDDataRecord record = (PDDataRecord) dataSet.getRecords().toArray()[rowIndex];
		GUID editedRole = (GUID) accessibleRoles.toArray()[columnIndex];
		wc.setLink(record.getId(), editedRole, value);
		wc.commit();
	}

	@Override
	public String getColumnName(int column) {
		GUID transaction = store.begin();
		String str = store.getName(transaction, accessibleRoles.toArray()[column]);
		store.commit(transaction);
		return str;
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

}
