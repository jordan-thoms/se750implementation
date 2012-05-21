package pdstore.ui.historyview;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * This class is used to draw starting point in the railway diagram
 */
public class JStartPointRender extends Canvas implements TableCellRenderer  
{
	/**
	 * Draw the start point at the beginning of graphics
	 * @param g The graphics to draw
	 */
	public void paint(Graphics g) {
		  
		  Dimension d = this.getSize();		  

	      int x = d.width/10;
	      int y = d.height/2;      		  
	      
	      int radius = (int) ((d.width < d.height) ? 0.24 * d.width : 0.24 * d.height);
	      g.setColor(Color.BLUE);
	      g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
	      g.setColor(Color.BLACK);
	      g.drawOval(x-radius-5, y-radius-5, 2*radius+10, 2*radius+10);
	
	}	  
		  
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			// TODO Auto-generated method stub
			return this;
		}
}
