package fluid.util.spreadsheet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Iterator;

public class PDSSheetPaser {
	private Sheet poiSheet;
	private FormulaEvaluator evaluator;

	public PDSSheetPaser(Sheet s){
		poiSheet = s;
		evaluator = s.getWorkbook().getCreationHelper().createFormulaEvaluator();
	}
	
	/*public CellValue evaluateCell(Cell c){
		return evaluator.evaluate(c);
	}*/
	
	public String getSheetName(){
		return poiSheet.getSheetName();
	}
	
	public ArrayList<Row> getRows(int from, int to){
		ArrayList<Row> rows = new ArrayList<Row>();
		for (int i = from; i < to + 1 ; i++){
			rows.add(poiSheet.getRow(i));
		}
		return rows;
	}
	
	public ArrayList<ArrayList<Cell>> getColumns(int from, int to){
		ArrayList<ArrayList<Cell>> cols = new ArrayList<ArrayList<Cell>>();
		int size = getNumberOfRows();
		for (int i = from; i < to + 1 ; i++){
			ArrayList<Cell> c = new ArrayList<Cell>();
			for (int j = 0 ; j < size ; j++){
				c.add(poiSheet.getRow(j).getCell(i));
			}
			cols.add(c);
		}
		return cols;
	}
	
	public int getNumberOfRows(){
		int numberOfRows = poiSheet.getPhysicalNumberOfRows();
		int processed = 0;
		for (int i = 0; i < numberOfRows; i ++){
			if (poiSheet.getRow(i) != null){
				processed ++;
			}else{
				numberOfRows ++;
			}
			if (processed ==  poiSheet.getPhysicalNumberOfRows()){
				return numberOfRows;
			}
		}
		return -1;
	}

	// load first row or row that the headers are on
	//		this will be the number of cells in the table
	// Each type will be a column
	// load each cell into the right type
	// link the instance to an anchor ie times
	// run down the row till find a row with no cell

	public void loadRows(){
		Iterator<Row> list = poiSheet.rowIterator();
		while(list.hasNext()){
			Row r = list.next();
			System.out.println();
			for (int i = 0; i < 200; i++){
				Cell c = r.getCell(i);
				if (c != null){
					if (c.getCellType() == Cell.CELL_TYPE_NUMERIC || 
							c.getCellType() == Cell.CELL_TYPE_STRING){
						System.out.print(c.toString());
					}else if (c.getCellType() == Cell.CELL_TYPE_FORMULA){
						CellValue cc = evaluator.evaluate(c);
						if (cc.getCellType() == Cell.CELL_TYPE_NUMERIC){
							System.out.print(cc.getNumberValue());
						}else{
							System.out.print(cc.formatAsString());
						}
					}else if (c.getCellType() == Cell.CELL_TYPE_STRING){
						System.out.print(c.getStringCellValue());
					}else if (c.getCellType()== Cell.CELL_TYPE_BLANK){
						System.out.print("Blank");
					}else{
						System.out.print(c.toString());
					}
				}else{
					System.out.print("Null");
				}
				System.out.print(" | ");
			}
		}
		System.out.println();
	}

	public CellValue evaluateCell(Cell c) {
		if (evaluator.evaluateFormulaCell(c) == -1){
			return evaluator.evaluate(c);
		}else{
			return new CellValue(evaluator.evaluateFormulaCell(c));
		}
	}
}
