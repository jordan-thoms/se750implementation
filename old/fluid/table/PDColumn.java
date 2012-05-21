package fluid.table;

import java.util.ArrayList;

import fluid.util.DataBox;

import pdstore.GUID;

public class PDColumn {

	private ArrayList <DataBox> cells = new ArrayList<DataBox>();
	private String header = "";
	private GUID headerID;
	
	public PDColumn(DataBox head){
		header = (String)head.getElement();
		headerID = head.getID();
	}
	
	public void addCell(DataBox box){
		cells.add(box);
	}
	
	public GUID getHeaderID(){
		return headerID;
	}
	
	public DataBox getCell(int i){
		try{
			return cells.get(i);
		}catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public DataBox getCell(GUID id){
		for (DataBox b : cells){
			if (b.getID().equals(id)){
				return b;
			}
		}
		return null;
	}
	
	public int getNumberOfCells(){
		return cells.size();
	}
	
	public void setHeader(String h){
		header = h;
	}
	
	public String getHeader(){
		return header;
	}
}
