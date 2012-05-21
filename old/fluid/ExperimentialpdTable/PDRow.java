package fluid.ExperimentialpdTable;

import java.util.ArrayList;

public class PDRow {
	
	ArrayList<PDCell> cells = new ArrayList<PDCell>();
	
	public void add(PDCell instances){
		cells.add(instances);
	}
	
	public PDCell getColumn(int i){
		return cells.get(i);
	}
}
