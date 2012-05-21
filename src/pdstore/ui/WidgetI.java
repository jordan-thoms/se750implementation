package pdstore.ui;

import java.awt.Dimension;
import java.awt.Graphics2D;

import pdstore.GUID;
import pdstore.PDStore;

public interface WidgetI {

	public Dimension getPreferredSize(PDStore store, GUID transaction, GUID instance);
	public Dimension getMinSize(PDStore store, GUID transaction, GUID instance);
	public Dimension getMaxSize(PDStore store, GUID transaction, GUID instance);
	
	public void draw(PDStore store, GUID transaction, GUID instance, Graphics2D graphics);
}
