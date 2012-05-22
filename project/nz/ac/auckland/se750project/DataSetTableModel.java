package nz.ac.auckland.se750project;

import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import nz.ac.auckland.se750project.dal.PDDataRecord;
import nz.ac.auckland.se750project.dal.PDDataSet;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDWorkingCopy;

public class DataSetTableModel extends AbstractTableModel {
	private GUID dataSetGUID;
	private PDStore store;
	private PDDataSet dataSet;
	private PDWorkingCopy wc;
	private Collection<GUID> accessibleRoles;
	
	public DataSetTableModel(GUID dataSet, PDStore store, PDWorkingCopy wc) {
		this.dataSetGUID = dataSet;
		this.dataSet = new PDDataSet(wc, dataSet);
		this.store = store;
		this.wc = wc;
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
	public String getColumnName(int column) {
		GUID transaction = store.begin();
		String str = store.getName(transaction, accessibleRoles.toArray()[column]);
		store.commit(transaction);
		return str;
	}
}
