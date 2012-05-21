package pdstore.ui;

import java.awt.Graphics2D;

import nz.ac.auckland.se.genoupe.tools.Debug;
import pdstore.GUID;
import pdstore.PDStoreException;
import pdstore.generic.PDStoreI;

public class PDStore extends pdstore.PDStore {

	public PDStore(PDStoreI<GUID, Object, GUID> store) {
		super(store);
	}

	public PDStore(String fileName, String filePath, String extension)
			throws PDStoreException {
		super(fileName, filePath, extension);
	}

	public PDStore(String fileName) throws PDStoreException {
		super(fileName);
	}

	public PDStore() {
	}
	
	/**
	 * Returns an instance of the PDStore widget that is used to represent the given instance.
	 * @param transaction the transaction to query the widget on
	 * @param instance the instance to represent visually with a PDStore widget
	 * @return a WidgetI widget that can be used to represent the instance
	 */
	public WidgetI getWidget(GUID transaction, GUID instance) {
		GUID widget = (GUID) getInstance(transaction, instance,
				VISUALIZED_BY_ROLEID);
		String implementation = (String) getInstance(transaction, widget,
				WIDGET_IMPLEMENTATION_ROLEID);
		Debug.assertTrue(implementation != null, "Widget instance "
				+ getLabel(transaction, widget)
				+ "does not specify an implementation.");

		WidgetI pdWidget = null;
		Class<?> widgetClass;
		try {
			widgetClass = Class.forName(implementation);
			pdWidget = (WidgetI) widgetClass.newInstance();
		} catch (ClassNotFoundException e) {
			throw new PDStoreException("", e);
		} catch (InstantiationException e) {
			throw new PDStoreException("", e);
		} catch (IllegalAccessException e) {
			throw new PDStoreException("", e);
		}
		return pdWidget;
	}
	
	/**
	 * Draws the given instance on the given graphics context.
	 * The instance will be drawn centered at the origin of the given graphics context.
	 * @param transaction the transaction to query the widget and instance on
	 * @param instance the instance to represent visually with a PDStore widget
	 * @param graphics the graphics context to draw on
	 */
	public void drawInstance(GUID transaction, GUID instance, Graphics2D graphics) {
		WidgetI widget = getWidget(transaction, instance);
		widget.draw(this, transaction, instance, graphics);
	}

}
