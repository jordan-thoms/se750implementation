package angil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import angil.dal.PDAction;
import angil.dal.PDPage;

public class LoadTestWorker implements Runnable {

	PDPage initialPDPage = null;
	// The seeds set for user decision delay
	private static final int USER_DECISION_DELAY = 0;
	// The flag that decide the end of recursion.
	boolean keepRecursion = true;

	@Override
	public void run() {
		goNext(initialPDPage);
	}

	/**
	 * Constructor, pass the PDPage instance to worker class.
	 * 
	 * @param initialPDPage
	 */
	public LoadTestWorker(PDPage initialPDPage) {
		this.initialPDPage = initialPDPage;
	}

	/**
	 * Send input request to server by HttpClient class
	 * 
	 * @param request
	 *            The request header e.g."GET /examples/homebanking/verify.jsp?username=auckland&password=a&login=login HTTP/1.1"
	 */
	public String sendRequestToServer(String request) {
		HttpClient hc = new HttpClient(request);
		String estimatedTime = hc.sendRequest();
		return estimatedTime;
	}

	/**
	 * Randomly generate the user delay time.Its initial value is 1. The delay
	 * may be set to 1000, means user delays for 0 to 1 second to make decision.
	 * The proper value of delay can be set to 3000 to 5000.
	 */
	public void randomDelay() {
		Random rd = new Random();
		int d = rd.nextInt(USER_DECISION_DELAY);
		try {
			Thread.sleep(d);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * User may delay before send request. This method is used to simulate user
	 * decision delay before sending request to server.
	 * 
	 * @param inputRequest
	 *            The input request line e.g."GET /examples/homebanking/verify.jsp?username=auckland&password=a&login=login HTTP/1.1"
	 * @return The request will be send to server.
	 */
	public String getUnchangedRequest(String inputRequest) {
		randomDelay();
		return inputRequest;
	}

	/**
	 * This method is designed to stochastically select the next action.
	 * @param page	The current PDPage instance
	 * @return	The selected action name
	 */
	public String stochasticallySelectNextAction(PDPage page) {
		Random stochasticValue = new Random();
		// Initially set the action to first action verify.
		String action = "verify";
		List<PDAction> actions = new ArrayList<PDAction>(page.getNextActions());
		double ranDouble = stochasticValue.nextDouble();
		for (PDAction a : actions) {
			double probability = a.getProbability();
			ranDouble -= probability;
			if (ranDouble < 0) {
				action = a.getName();
				break;
			}
		}
//		LogMaker.logToConsole("At page -> " + page.getName()+" Selecte action -> "+action);
		return action;
	}

	/**
	 * Select the next action with equal probability
	 * 
	 * @param page
	 *            The start page instance
	 * @return The selected action name
	 */
	public String equallySelectNextAction(PDPage page) {
		Random stochasticValue = new Random();
		// Initially set the action to first action verify.
		String action = "verify";
		ArrayList<String> nameList = new ArrayList<String>();
		List<PDAction> actions = new ArrayList<PDAction>(page.getNextActions());
		LogMaker.logToConsole("\nAt page --> " + page.getName());
		for (PDAction a : actions) {
			nameList.add(a.getName());
		}
		int index = stochasticValue.nextInt(nameList.size());
		action = nameList.get(index);
		LogMaker.logToConsole("Action Selected: --> " + action);
		return action;
	}

	/**
	 * This mehtod is the main logic of this class. It recursively walk through
	 * one possible user session. The session start from login page and end with
	 * logout action.
	 * 
	 * @param initialPDPage
	 *            The start page of one user session
	 */
	public void goNext(PDPage initialPDPage) {
		// Initial the action will be selected next.
		PDAction selectedAction = null;
		PDPage nextPage = null;
		String estimatedTime = "0";

		if (!keepRecursion)
			LogMaker.logToConsole("End of one user session");

		// Get the next actions from initial page
		List<PDAction> actions = new ArrayList<PDAction>(initialPDPage
				.getNextActions());

		// Use stochastic methods to select next action
		String selectedActionName = stochasticallySelectNextAction(initialPDPage);

		// Use equal probability methods to select next action
		// String selectedActionName =
		// equallySelectNextAction(initialPDPage);
		for (PDAction action : actions) {
			if (action.getName().equals(selectedActionName)) {
				selectedAction = action;
				nextPage = selectedAction.getNextPage();
				estimatedTime = sendRequestToServer(selectedAction
						.getRequestURL());

				LogMaker.logToFile(Thread.currentThread().getName()
						+ " {"+initialPDPage.getName() + "} -> ["
						+ selectedAction.getName() + "] -> {"
						+ nextPage.getName() + "} Spends " + estimatedTime
						+ " seconds");

				if (!selectedAction.getName().equals("logout"))
					goNext(nextPage);
				else {
					keepRecursion = false;
					LogMaker.logToConsole("End of "+ Thread.currentThread().getName()+ " session!");
				}
			}
		}
	}
}
