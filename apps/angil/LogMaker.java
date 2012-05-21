package angil;

import java.io.File;
import java.io.RandomAccessFile;

public class LogMaker {
	/**
	 * Log string to console.
	 * 
	 * @param input
	 */
	public synchronized static void logToConsole(String input) {
		System.out.println(input);
	}

	public synchronized static void logToFile(String input) {
		try {
			File file = new File(".\\apps\\angil\\Performance.log");
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(input.getBytes());
			raf.write("\n".getBytes());
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
