package angil;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;
import angil.dal.PDAction;
import angil.dal.PDPage;

/**
 * This class handles multi-threading. Pass start page name and user session
 * start rate to worker class.
 * 
 * @author gaozhan
 * 
 */
public class LoadTester {

	private int sessionStartRate = 240;
	private String startPageName = "login";
	private boolean continueLoadTest = true;

	public static void main(String[] args) {
		LoadTester loadtester = new LoadTester("login", 120);
		loadtester.stochasticTest();
	}


	public boolean isContinueLoadTest() {
		return continueLoadTest;
	}

	public void setContinueLoadTest(boolean continueLoadTest) {
		this.continueLoadTest = continueLoadTest;
	}

	/**
	 * Constructor of LoadTester class. Has start page name and session start
	 * rate parameters as input.
	 * 
	 * @param startPageName
	 *            The page name of first starting page
	 * @param sessionStartRate
	 *            The session start rate - number of new start sessions per
	 *            minutes.
	 */
	public LoadTester(String startPageName, int sessionStartRate) {
		this.startPageName = startPageName;
		this.sessionStartRate = sessionStartRate;
	}

	/**
	 * Implement multi-threading in this method. Pass the first page name.
	 * Decide the session start rate.
	 * 
	 */
	public void stochasticTest() {
		PDWorkingCopy copy = new PDSimpleWorkingCopy(new PDStore("Loadtest"));
		GUID pageID = copy.getId(startPageName);
		PDPage page = PDPage.load(copy, pageID);
		PDAction.typeId.toString();

		int sleepTime = (int) ((60 * 1000) / sessionStartRate);
		int userID = 0;
		LogMaker.logToConsole("Set user delay -> " + sleepTime+" ms");
		LogMaker.logToConsole("Set performance log in format {page} -> [action] -> {page}");
		
		while (continueLoadTest) {
			try {
				(new Thread((new LoadTestWorker(page)), "user#" + userID))
						.start();
				Thread.sleep(sleepTime);
				userID++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
