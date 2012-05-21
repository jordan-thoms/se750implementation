package pdstore.ui.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.ui.WidgetI;

public class Circle implements WidgetI {

	final public static GUID widgetID = new GUID("6e4a108093ef11e1ac19842b2b9af4fd");

	public static void register(PDStore store, GUID transaction) {
		store.setName(transaction, widgetID, "Circle");
		store.setType(transaction, widgetID, PDStore.WIDGET_TYPEID);
		store.setLink(transaction, widgetID,
				PDStore.WIDGET_IMPLEMENTATION_ROLEID,
				"pdstore.ui.widgets.Circle");
	}

	final static double radius = 12;

	@Override
	public Dimension getPreferredSize(PDStore store, GUID transaction, GUID instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension getMinSize(PDStore store, GUID transaction, GUID instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension getMaxSize(PDStore store, GUID transaction, GUID instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw(PDStore store, GUID transaction, GUID instance,
			Graphics2D graphics) {
		// draw the circle
		graphics.setPaint(Color.BLACK);
		graphics.fill(new Ellipse2D.Double(-radius, -radius, 2 * radius,
				2 * radius));

		// draw the label
		String label = store.getLabel(transaction, instance);
		FontMetrics metrics = graphics.getFontMetrics();
		int height = metrics.getHeight();
		int width = metrics.stringWidth(label);
		graphics.drawString(label, -width / 2, (int) -radius - 5);
	}
}
