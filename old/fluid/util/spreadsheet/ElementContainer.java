package fluid.util.spreadsheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ElementContainer implements Iterable<Object>, Iterator<Object> {

	private ArrayList<Object> list;

	public static final int ROWS = 0;
	public static final int COLUMNS = 1;
	private int element = -1;
	private int count = 0;

	private ElementContainer (int e){
		element = e;
	}
	
	public int getElementType(){
		return element;
	}
	
	public int getSize(){
		return list.size();
	}

	public void addItem(Object o){
		if (list == null){
			list = new ArrayList<Object>();
		}
		list.add(o);
	}

	@Override
	public boolean hasNext() {
		if (list != null && count < list.size()){
			return true;
		}
		count = 0;
		return false;
	}

	@Override
	public Object next() {
		if (count == list.size())
			throw new NoSuchElementException();

		count++;
		return list.get(count - 1);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}

	@Override
	public Iterator<Object> iterator() {
		return this;
	}

	public int getNumberOfCellsInItem(Object o) {
		if (o instanceof Row){
			Row r = (Row)o;
			int numberOfCells = r.getPhysicalNumberOfCells();
			int processed = 0;
			for (int i = 0; i < numberOfCells; i ++){
				if (r.getCell(i) != null){
					processed ++;
				}else{
					numberOfCells ++;
				}
				if (processed ==  r.getPhysicalNumberOfCells()){
					return numberOfCells;
				}
			}
		}
		return -1;
	}

	public static ElementContainer createRowContainer(ArrayList<Row> rows) {
		ElementContainer e = new ElementContainer(ROWS);
		for (Row r : rows){
			e.addItem(r);
		}
		return e;
	}
	
	public static ElementContainer createColumnContainer(ArrayList<ArrayList<Cell>> cells) {
		ElementContainer e = new ElementContainer(COLUMNS);
		for (ArrayList<Cell> r : cells){
			e.addItem(r);
		}
		return e;
	}

}
