package pdedit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.swing.Timer;

public class PDEditLogger {
	private static StringBuilder log;
	private static String lastEntre = "";
	private static long startTimer = 0;
	private static String header = "";
	private static int numberOfEntre = 0;
	private static Timer printIO;
	private static int delay = 5000;
	private static int postCount = 0;

	public static void setHeader(String message){
		header = message;
	}

	public static void startNewSession(){
		if (log == null){
			log = new StringBuilder();
		}
		if (printIO == null){
			printIO = new Timer(delay, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (numberOfEntre > 5 || postCount > 3){
						printOut();
						printIO.setDelay(delay);
						postCount = 0;
					}else{
						postCount ++;
						printIO.setDelay(printIO.getDelay()*2);
					}
				}
			});
		}
		printIO.start();
		startTimer = System.currentTimeMillis();
		Date n = new Date(startTimer);
		log.append("\nLog "+"["+n.toString()+"]:\n");
	}

	public static void addToLog(String message){
		if (log == null){
			log = new StringBuilder();
		}
		startTimer = System.currentTimeMillis();
		Date n = new Date(startTimer);
		log.append("["+n.toString()+"]| " + message);
		lastEntre = message;
		numberOfEntre++;
	}

	public static void addToLogWithoutTimestamp(String message){
		if (log == null){
			log = new StringBuilder();
		}
		log.append(message);
		lastEntre = message;
	}

	public static void appendDurationToLastMassage(){
		log.append("Done ["+(System.currentTimeMillis()-startTimer)/1000.0+"Sec]");
	}

	public static void appendDurationToLastMassage(long time){
		log.append("Done ["+(System.currentTimeMillis()-time)/1000.0+"Sec]");
	}

	public static void newLine(){
		log.append("\n");
	}

	public static String getLastEntre(){
		return lastEntre;
	}

	public static String getLog(){
		return log.toString();
	}

	public static void printOut(){
		File fileEntry = null;
		if (System.getProperty("os.name").contains("Mac")){
			fileEntry = new File("pdedit/log/messageLog.txt");
		}else{
			fileEntry = new File("pdedit\\log\\messageLog.txt");
		}
		boolean fileExist = fileEntry.exists();
		try { 
			FileWriter writer = null;
			if (fileExist){
				writer = new FileWriter(fileEntry, true);
			}else{
				writer = new FileWriter(fileEntry, false);
			}
			BufferedWriter out = new BufferedWriter(writer);
			if (!fileExist)
				out.write(header + "\n");
			out.write(log.toString()); 
			out.close(); 
			writer.close();
			fileEntry = null;
			log = new StringBuilder();
			numberOfEntre = 0;
		} catch (IOException e) { } 
	}
	
}
