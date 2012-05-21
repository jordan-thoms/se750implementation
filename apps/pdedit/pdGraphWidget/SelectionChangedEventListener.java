package pdedit.pdGraphWidget;

/**
 * Listener interface for when the selection has changed.
 * @author Craig Sutherland
 *
 */
public interface SelectionChangedEventListener {
	/**
	 * The selection has been changed.
	 * @param event - the details of the event
	 */
	public void SelectionChanged(SelectionChangedEvent event);
}
