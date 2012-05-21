package fluid.pdTimeline;

import java.util.EventObject;

public class TimelineEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Object node;
	public Object getNode() {
		return node;
	}
	public TimelineEvent(Object source) {
		super(source);
		node = source;
	}

}
