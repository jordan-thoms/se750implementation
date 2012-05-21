package texteditor;


import java.util.Comparator;

import texteditor.dal.PDOperation;


public class OperationComparatorByTime implements Comparator<PDOperation> {

	@Override
	public int compare(PDOperation arg0, PDOperation arg1) {
		if (arg0.getTimeStamp() > arg1.getTimeStamp()) {
			return 1;
		}
		if (arg0.getTimeStamp() < arg1.getTimeStamp()) {
			return -1;
		}
		return 0;
	}

}
