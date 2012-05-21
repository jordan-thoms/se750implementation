package fluid.util.spreadsheet;

import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class CommandUtil {

	public static boolean isInDictionary(String target){
		return SpreadSheetDictionary.isInDictionary(target);
	}

	public static String beginsWith(String suff){
		return SpreadSheetDictionary.beginsWith(suff);
	}

	public static ElementContainer execute(PDSSheetPaser sheet, String command,ElementContainer result ){
		String[] orderOfOp = command.split(" ");
		for (String s : orderOfOp){
			if (!isInDictionary(s)){
				if (!isNumber(s)){
					return null;
				}
			}
		}

		return exe(sheet,orderOfOp,result);
	}

	private static ElementContainer exe(PDSSheetPaser sheet,String[] orderOfOP, ElementContainer results){
		//operation
		int [] range = covertToNumber(orderOfOP[2]);
		int from = range[0];
		int to = from;
		if (range.length > 1){
			to = range[1];
		}
		switch(SpreadSheetDictionary.stringToEnum(orderOfOP[0])){
		case Get:
			return executeGet(sheet, orderOfOP[1],from,to);
		case Select:
			if (results == null)
				return null;
			return select(results,orderOfOP[1],from,to);
		default:
			break;
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	private static ElementContainer select(ElementContainer results,String element, int from, int to ){
		System.out.println(element + " | " +from+","+to);
		switch(SpreadSheetDictionary.stringToEnum(element)){
		case Row:
			System.out.println("Row");
			System.out.println(results.getElementType());
			if (results.getElementType() == ElementContainer.ROWS){
				ArrayList <Row> rows = new ArrayList<Row>();
				for (Object r : results){
					if (((Row)r).getRowNum() >= from && ((Row)r).getRowNum() <= to){
						rows.add((Row)r);
					}
				}
				return ElementContainer.createRowContainer(rows);
			}else{
				ArrayList<ArrayList<Cell>> colls = new ArrayList<ArrayList<Cell>>();
				for (Object cols : results){
					ArrayList<Cell> cells = (ArrayList<Cell>)cols;
					ArrayList<Cell> temp = new ArrayList<Cell>();
					if (from > -1 && to < cells.size()){
						for (int i = from; i < to + 1; i++){
							temp.add(cells.get(i));
						}
						colls.add(temp);
					}
				}
				return  ElementContainer.createColumnContainer(colls);
			}
		case Column:
			if (results.getElementType() == ElementContainer.ROWS){

			}else{

			}
			return null;
		default:
			return null;
		}
	}

	private static ElementContainer executeGet(PDSSheetPaser sheet,String element, int from, int to){
		switch(SpreadSheetDictionary.stringToEnum(element)){
		case Row:
			return ElementContainer.createRowContainer(sheet.getRows(from, to));
		case Column:
			return ElementContainer.createColumnContainer(sheet.getColumns(from, to));
		default:
			return null;
		}
	}

	private static boolean isNumber(String s){
		String[] element = s.split("-");
		for (String i : element){
			try{
				Integer.parseInt(i);
			}catch(NumberFormatException n){
				return false;
			}
		}
		return true;
	}

	private static int [] covertToNumber(String s){
		String[] element = s.split("-");
		int [] ret = new int [element.length];
		int j = 0;
		for (String i : element){
			try{
				ret[j] = Integer.parseInt(i);
			}catch(NumberFormatException n){
				return null;
			}
			j++;
		}
		return ret;
	}
}
