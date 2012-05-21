package pdedit.pdGraphWidget;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

public class PopupListener extends MouseAdapter {
	JPopupMenu popup;
	GraphWidget widget;

	PopupListener(JPopupMenu popupMenu, GraphWidget w) {
		popup = popupMenu;
		widget = w;
	}

	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		Point mouse = e.getPoint();
		if (e.getComponent() instanceof JScrollPane){
			Point temp = ((JScrollPane)e.getComponent()).getViewport().getViewPosition();
			mouse.x += temp.x;
			mouse.y += temp.y;
		}
		if (e.isPopupTrigger()&& !widget.greyOut) {
			widget.popPoint.setLocation(mouse.getX(),mouse.getY());
			popup.show(e.getComponent(),
					e.getX(), e.getY());
		}
	}
}
