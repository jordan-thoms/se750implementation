package fluid.util.spreadsheet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class PDSWorkbook {
	private Workbook workbook;
	private final int xls = 0;
	private final int xlsx = 1;
	private final int ext_error = -1;

	public PDSWorkbook(String name) throws FileNotFoundException,IOException{
		InputStream myxls;
		myxls = new FileInputStream(name);
		connectToWorkBook(myxls, inspectType(name));
	}
	
	public int getNumberOfSheets(){
		return workbook.getNumberOfSheets();
	}
	
	public Sheet getSheet(int index){
		return workbook.getSheetAt(index);
	}
	
	public Sheet getSheet(String name){
		return workbook.getSheet(name);
	}

	private int inspectType(String name){
		String ext = name.substring(name.lastIndexOf("."));
		if (ext.toLowerCase().endsWith("xls")){
			return xls;
		}else if (ext.toLowerCase().endsWith("xlsx")){
			return xlsx;
		}
		return -1;
	}

	private void connectToWorkBook(InputStream input, int type) throws IOException{
		switch (type) {
		case xls:
			workbook = new HSSFWorkbook(input);
			break;
		case xlsx:
			workbook = new XSSFWorkbook(input);
			break;
		case ext_error:
			throw new RuntimeException("Trying to open something that is not a workbook");
		default:
			break;
		}
	}
}
