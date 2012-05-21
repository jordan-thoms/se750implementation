package fluid.table;

import java.util.ArrayList;

import fluid.util.DataBox;

import pdstore.GUID;

public class PDRow {
	private ArrayList <DataBox> cells = new ArrayList<DataBox>();
	private String header = "";
	
	public void addCell(DataBox box){
		cells.add(box);
	}
	
	public DataBox getCell(int i){
		return cells.get(i);
	}
	
	public DataBox getCell(GUID id){
		for (DataBox b : cells){
			if (b.getID().equals(id)){
				return b;
			}
		}
		return null;
	}
	
	public void setHeader(String h){
		header = h;
	}
	
	public String getHeader(){
		return header;
	}

}
