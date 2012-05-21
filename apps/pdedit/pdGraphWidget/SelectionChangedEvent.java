package pdedit.pdGraphWidget;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * The event for when the selection has been changed in the widget.
 * @author Craig Sutherland
 *
 */
public class SelectionChangedEvent extends EventObject {

	private static final long serialVersionUID = -1450551162783999394L;
	private ArrayList<DiagramElement> selectedElements;

	/**
	 * Constructs a new SelectionChangedEvent with the specified source type and id.
	 * @param source - the object where the event originated
	 * @param nodes - the elements that have been selected
	 */
	public SelectionChangedEvent(Object source, List<DiagramNode> nodes) {
		super(source);
		this.selectedElements = new ArrayList<DiagramElement>(nodes);
	}
	
	public SelectionChangedEvent(Object source, DiagramLink link) {
		super(source);
		this.selectedElements = new ArrayList<DiagramElement>();
		this.selectedElements.add(link);
	}
	
	/**
	 * Gets the elements that are selected.
	 * @return the selected elements
	 */
	public ArrayList<DiagramElement> getSelectedElements() {
		return selectedElements;
	}
}
