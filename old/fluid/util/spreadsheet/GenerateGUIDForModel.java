package fluid.util.spreadsheet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateGUIDForModel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String name = "pdedit/data/modelsGuid.txt";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(name));
			BufferedWriter writer = new BufferedWriter(new FileWriter(name, true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
