package nz.ac.auckland.se750project;

import nz.ac.auckland.se750project.dal.PDDataRecord;
import nz.ac.auckland.se750project.dal.PDDataSet;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;

public class SampleData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PDStore store = PDStore.connectToServer("localhost");
		PDWorkingCopy wc = new PDSimpleWorkingCopy(store);
		PDDataSet dataSet = new PDDataSet(wc);
		PDDataRecord record = new PDDataRecord(wc);
		dataSet.addRecord(record);
		record.setRow1(5l);
		record.setRow2(10l);
		record.setRow3(15l);
		wc.commit();
		System.out.println(dataSet.getId());
	}

}
