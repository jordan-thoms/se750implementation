package pdstore.ui.historyview.tests;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class JPanelRender extends Canvas implements TableCellRenderer 
{

	  public void paint(Graphics g) {
	  
	    Dimension d = this.getSize();

	  drawThickLine
	    (g, getSize().width/2, 0, getSize().width/2, getSize().height, 1,
	     new Color(0).green);
	      
	  }
	  
	  public void drawThickLine(
			  Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c) {
			  // The thick line is in fact a filled polygon
			  g.setColor(c);
			  
			  int dX = x2 - x1;
			  int dY = y2 - y1;
			  // line length
			  double lineLength = Math.sqrt(dX * dX + dY * dY);

			  double scale = (double)(thickness) / (2 * lineLength);

			  // The x,y increments from an endpoint needed to create a rectangle...
			  double ddx = -scale * (double)dY;
			  double ddy = scale * (double)dX;
			  ddx += (ddx > 0) ? 0.5 : -0.5;
			  ddy += (ddy > 0) ? 0.5 : -0.5;
			  int dx = (int)ddx;
			  int dy = (int)ddy;

			  // Now we can compute the corner points...
			  int xPoints[] = new int[4];
			  int yPoints[] = new int[4];

			  xPoints[0] = x1 + dx; yPoints[0] = y1 + dy;
			  xPoints[1] = x1 - dx; yPoints[1] = y1 - dy;
			  xPoints[2] = x2 - dx; yPoints[2] = y2 - dy;
			  xPoints[3] = x2 + dx; yPoints[3] = y2 + dy;

			  g.fillPolygon(xPoints, yPoints, 4);
			  }
			

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		return this;
	}

	}