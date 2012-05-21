package pdqueue.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import pdqueue.concurrency.PDSConnection;
import pdqueue.dal.PDItem;
import pdqueue.dal.PDQueue;
import pdstore.GUID;
import pdstore.dal.PDInstance;

public class Filter {


	public static Collection<PDItem> filterAndSort(Collection<Object> target, ItemState status) {
		return sort(filter(target, status));
	}
	
	public static Collection<PDItem> filter(Collection<Object> target, ItemState status) {
	    Collection<PDItem> result = new ArrayList<PDItem>();
	    for (Iterator<Object> it = target.iterator(); it.hasNext(); ) {
	    	PDItem item = PDItem.load(PDSConnection.getNewCopy(), (GUID)it.next());
	    	if (item.getState().equals(status.toString()))
	    		result.add(item);
	    }
	    return result;
	}
	
	public static List<PDItem> sort(Collection<PDItem> target) {
		List<PDItem> list = new ArrayList(target);
		Collections.sort(list, new TimeComparator());
		return list;
	}
	
	public static class TimeComparator implements Comparator<PDItem> {
		public int compare(PDItem item1, PDItem item2) {
			return item1.getTime().compareTo(item2.getTime());
		}
	}
}