package swiki;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.management.Notification;
import javax.management.NotificationListener;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Wrapper class for batch extracting semantic information from Wikipedia dumps
 */
public class WikipediaBatchExtractor implements NotificationListener {
	private static final Logger LOG = Logger.getLogger(WikipediaBatchExtractor.class);
	private static final int TOTALPAGES = 9541307;
	private static final int TOTALTHREADS = 4;
	private static final int MAXQUEUESIZE = 5000;
	private static final long REPORTDELAY = 5000;

	ExecutorService exec;
	int noPagesRead;
	int noPagesProcessed;
	BlockingQueue<Object> queue;

	private WikipediaBatchExtractor() {
		exec = Executors.newFixedThreadPool(TOTALTHREADS);
		noPagesRead = 0;
		noPagesProcessed = 0;
		queue = new LinkedBlockingQueue<Object>();
	}

	private void process(String fileName) {
		// Start the store process
		SwikiStore store = new SwikiStore(queue);
		exec.submit(store);

		// Parse the file
		startReporter();
		Date startTime = markStart(fileName);
		parseXMLDump(fileName);
		waitComplete();
		markComplete(fileName, startTime);

		// Finalize data in the store
		store.commit();
	}

	private void startReporter() {
		exec.submit(new Runnable() {
			
			public void run() {
				while (true) {
					try {
						Thread.sleep(REPORTDELAY);
						float percent = ((float) noPagesProcessed) * 100 / TOTALPAGES;
						LOG.info(String.format("q=%d r=%d p=%d %%=%.2f", queue.size(), noPagesRead,
								noPagesProcessed, percent));
					} catch (InterruptedException e) {
						return;
					} catch (Exception e) {
						LOG.error(e, e);
					}
				}
			}
		});
	}

	private static Date markStart(String fileName) {
		Date startTime = new Date();
		LOG.info(String.format("Parse start (%s): %s", startTime, fileName));

		return startTime;
	}

	private void parseXMLDump(String fileName) {
		try {
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(fileName));

			XMLInputFactory fac = XMLInputFactory.newInstance();
			XMLStreamReader r = fac.createXMLStreamReader(input);

			WikipediaExtractor currentExtractor = null;

			while (r.hasNext()) {
				int event = r.next();

				String n;
				switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					n = r.getLocalName();
					if (n.equals("page")) {
						currentExtractor = new WikipediaExtractor(this);
					} else if (n.equals("title")) {
						String t = r.getElementText();
						currentExtractor.title = t;
						LOG.debug("Title => " + t);
					} else if (n.equals("text")) {
						String c = r.getElementText();
						currentExtractor.content = c;
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					n = r.getLocalName();
					if (n.equals("page")) {
						exec.submit(currentExtractor);
						noPagesRead++;
					}
					break;
				}

				if (queue.size() > MAXQUEUESIZE) {
					Thread.sleep(MAXQUEUESIZE / 10);
				}
			}
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	private void waitComplete() {
		// Loop until all tasks are completed
		try {
			int noRunningTasks = noPagesRead - noPagesProcessed;
			while (noRunningTasks > 0 || !queue.isEmpty()) {
				Thread.sleep(REPORTDELAY / 10);
				LOG.info("Objects left => " + queue.size());
				LOG.info("Tasks left => " + noRunningTasks);
				noRunningTasks = noPagesRead - noPagesProcessed;
			}
			exec.shutdown();
			exec.awaitTermination(500, TimeUnit.MILLISECONDS);
			exec.shutdownNow();
		} catch (InterruptedException e) {
			LOG.error(e, e);
		}
	}

	private static void markComplete(String fileName, Date startTime) {
		Date endTime = new Date();
		LOG.info(String.format("Parse complete (%s): %s", endTime, fileName));

		long timeInMilis = endTime.getTime() - startTime.getTime();
		int remaining = (int) (timeInMilis / 1000);
		int sec = remaining % 60;
		remaining = remaining / 60;
		int min = remaining % 60;
		remaining = remaining / 60;
		int hr = remaining % 24;
		remaining = remaining / 24;
		int day = remaining;
		LOG.info(String.format("Time taken: %dd %d:%d:%d", day, hr, min, sec));
	}

	
	public synchronized void handleNotification(Notification n, Object obj) {
		if (n.getType().equals("PageInfo")) {
			try {
				queue.put(obj);
			} catch (InterruptedException e) {
				LOG.error(e, e);
			}
		} else if (n.getType().equals("Task Complete")) {
			noPagesProcessed++;
		}
		LOG.debug(n.getMessage());
	}

	/**
	 * @param [FileName]
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure(); // Setup logging

		// Set filename and start
		String fn = args[0];
		WikipediaBatchExtractor extractor = new WikipediaBatchExtractor();
		extractor.process(fn);
	}
}
