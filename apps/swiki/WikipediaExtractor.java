package swiki;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.apache.log4j.Logger;

public class WikipediaExtractor implements Runnable {
	private static final Logger LOG = Logger.getLogger(WikipediaExtractor.class);
	
	private NotificationListener listener;
	private long seq;
	
	String title;
	String content;
	
	public WikipediaExtractor(NotificationListener listener) {
		this.listener = listener;
		seq = 0;
	}
	
	
	public void run() {
		try {
			if (title == null || content == null) {
				throw new SwikiException("Title or content has not been set");
			}
			PageInfo i = new PageInfo(title);
			
			// Process content
			ContentProcessor.processContent(i, content);
			notify("PageInfo", "Processed PageInfo for " + title, i);
		} catch (Exception e) {
			LOG.error(e, e);
		}
		finally {
			notify("Task Complete", "Finished processing article" + title, null);
		}
	}
	
	private void notify(String type, String msg, Object obj) {
		Notification n = new Notification(type, this, seq, msg);
		listener.handleNotification(n, obj);
		seq++;
	}
}
