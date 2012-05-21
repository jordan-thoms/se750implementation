/**
 * This class is created for instantiate the generated pdmodel. The pages and
 * actions along with their roles are stored into store Loadtest.pds.
 * 
 * @author gaozhan
 * 
 */
package angil;

import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;
import angil.dal.PDAction;
import angil.dal.PDPage;

public class InstantiateHomeBanking {

	public static void main(String[] args) {
		PDWorkingCopy copy = new PDSimpleWorkingCopy(new PDStore("Loadtest"));

		// set up pages
		PDPage loginPage = new PDPage(copy);
		loginPage.setName("login");

		PDPage menuPage = new PDPage(copy);
		menuPage.setName("menu");

		PDPage statusPage = new PDPage(copy);
		statusPage.setName("status");

		PDPage depotPage = new PDPage(copy);
		depotPage.setName("depot");

		PDPage transferPage = new PDPage(copy);
		transferPage.setName("transfer");

		// set up actions
		PDAction logoutAction = new PDAction(copy);
		logoutAction.setName("logout");
		logoutAction
				.setRequestURL("GET /examples/homebanking/delay.jsp?logout=Logout HTTP/1.1");
		logoutAction.setProbability(0.2);

		PDAction verifyAction = new PDAction(copy);
		verifyAction.setName("verify");
		verifyAction
				.setRequestURL("GET /examples/homebanking/verify.jsp?username=auckland&password=a&login=login HTTP/1.1");
		verifyAction.setProbability(1.0);

		PDAction confirmtransferAction = new PDAction(copy);
		confirmtransferAction.setName("confirmtransfer");
		confirmtransferAction
				.setRequestURL("GET /examples/homebanking/confirmtransfer.jsp?amount=0&transfer=Transfer HTTP/1.1");
		confirmtransferAction.setProbability(0.9);

		PDAction maketransferAction = new PDAction(copy);
		maketransferAction.setName("maketransfer");
		maketransferAction
				.setRequestURL("GET /examples/homebanking/confirmtransfer.jsp?amount=0&transfer=Transfer HTTP/1.1");
		maketransferAction.setProbability(0.2);

		PDAction showstatusAction = new PDAction(copy);
		showstatusAction.setName("showstatus");
		showstatusAction
				.setRequestURL("GET /examples/homebanking/delay.jsp?status=Status HTTP/1.1");
		showstatusAction.setProbability(0.4);

		PDAction investAction = new PDAction(copy);
		investAction.setName("invest");
		investAction
				.setRequestURL("GET /examples/homebanking/delay.jsp?depot=Depot HTTP/1.1");
		investAction.setProbability(0.2);

		PDAction sellbondAction = new PDAction(copy);
		sellbondAction.setName("sellbond");
		sellbondAction
				.setRequestURL("GET /examples/homebanking/bonddelay.jsp?amount=0&sellbond=Sell+Bond HTTP/1.1");
		sellbondAction.setProbability(0.3);

		PDAction buybondAction = new PDAction(copy);
		buybondAction.setName("buybond");
		buybondAction
				.setRequestURL("GET /examples/homebanking/bonddelay.jsp?amount=0&buybond=Buy+Bond HTTP/1.1");
		buybondAction.setProbability(0.3);

		PDAction canceltransferAction = new PDAction(copy);
		canceltransferAction.setName("canceltransfer");
		canceltransferAction
				.setRequestURL("GET /examples/homebanking/cancel.jsp?cancel=Cancel HTTP/1.1");
		canceltransferAction.setProbability(0.1);

		PDAction cancelstatusAction = new PDAction(copy);
		cancelstatusAction.setName("cancelstatus");
		cancelstatusAction
				.setRequestURL("GET /examples/homebanking/cancel.jsp?cancel=Cancel HTTP/1.1");
		cancelstatusAction.setProbability(1.0);

		PDAction canceldepotAction = new PDAction(copy);
		canceldepotAction.setName("canceldepot");
		canceldepotAction
				.setRequestURL("GET /examples/homebanking/cancel.jsp?cancel=Cancel HTTP/1.1");
		canceldepotAction.setProbability(0.4);

		// set up transitions
		loginPage.addNextAction(verifyAction);
		verifyAction.addNextPage(menuPage);
		menuPage.addNextAction(logoutAction);
		logoutAction.addNextPage(loginPage);
		menuPage.addNextAction(maketransferAction);
		maketransferAction.addNextPage(transferPage);
		transferPage.addNextAction(confirmtransferAction);
		confirmtransferAction.addNextPage(menuPage);
		transferPage.addNextAction(canceltransferAction);
		canceltransferAction.addNextPage(menuPage);
		menuPage.addNextAction(showstatusAction);
		showstatusAction.addNextPage(statusPage);
		statusPage.addNextAction(cancelstatusAction);
		cancelstatusAction.addNextPage(menuPage);
		menuPage.addNextAction(investAction);
		investAction.addNextPage(depotPage);
		depotPage.addNextAction(canceldepotAction);
		canceldepotAction.addNextPage(menuPage);
		depotPage.addNextAction(sellbondAction);
		sellbondAction.addNextPage(depotPage);
		depotPage.addNextAction(buybondAction);
		buybondAction.addNextPage(depotPage);
		copy.commit();
		LogMaker.logToConsole("End of instantiation");
	}
}
