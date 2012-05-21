package fluid.util;

import pdstore.GUID;

public class DataBox {

	private Object element;
	private GUID id;
	
	public DataBox(Object data, GUID d){
		element = data;
		id = d;
	}
	
	public Object getElement(){
		return element;
	}
	
	public GUID getID(){
		return id;
	}
	
	@Override
	public String toString(){
		return element.toString();
	}
	
	
	public static void GenerateGUID(int number){
		for (int i = 0 ; i < number; i++){
			System.out.println(new GUID());
		}
	}
	
	public static void main(String[] args) {
		GenerateGUID(10);
	}
		
}
