package fluid.util.spreadsheet;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ImportProg {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String name = "C:\\Users\\tyeu008\\Documents\\ARCWaterQualityData.xls";
		try {
			PDSWorkbook wb = new PDSWorkbook(name);
			PDSSheetPaser sheet = new PDSSheetPaser(wb.getSheet(0));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
