package fluid.ExperimentialpdTable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class THeaderPopup extends MouseAdapter implements ActionListener {

	JTable parent;
	JPopupMenu menu;

	public THeaderPopup(JTable parent){
		this.parent = parent;
		menu = new JPopupMenu();
		JMenuItem item = new JMenuItem("Extract Table"); 
		item.addActionListener(this); 
		menu.add(item); 
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		highlightCol(e);
		if (e.isPopupTrigger()) { 
			menu.show(e.getComponent(), e.getX(), e.getY()); 
		} 
	}

	@Override
	public void mousePressed(MouseEvent e) {
		highlightCol(e);
		if (e.isPopupTrigger()) { 
			menu.show(e.getComponent(), e.getX(), e.getY()); 
		} 
	}
	private void highlightCol(MouseEvent e) {
		parent.setColumnSelectionAllowed(true);
		parent.setRowSelectionAllowed(false);
		JTableHeader jth = (JTableHeader) e.getSource();
		int colpressed = -1;
		for (int i = 0; i < jth.getColumnModel().getColumnCount(); i++){
			if (jth.getHeaderRect(i).contains(e.getPoint())){
				colpressed = i;
			}
		}
		if (colpressed > -1 && colpressed < jth.getColumnModel().getColumnCount()){
			parent.setColumnSelectionInterval(colpressed, colpressed);
		}
		System.out.println("Column "+colpressed+" Pressed");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Extract Table");

	}
}
