package fluid.ExperimentialpdTable;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * This is PD MultipleJTableWidget
 * @author Ted
 *
 */
public class PDMJTableWidget extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTable anchorTable;
	JScrollPane scrollPane;
	
	public PDMJTableWidget(TableModel model){
		anchorTable = new JTable(model);
		initWidget();
	}
	
	public PDMJTableWidget(Object [][] data, String [] col){
		super(new GridLayout(1,0));
		anchorTable = new JTable(data,col);
		initWidget();
	}
	
	private void initWidget(){
		anchorTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		anchorTable.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(anchorTable);
		this.add(scrollPane);
		initWidgetListeners();
	}
	
	private void initWidgetListeners(){
		anchorTable.getTableHeader().addMouseListener(new THeaderPopup(anchorTable));
		
		anchorTable.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				JTable t = (JTable) e.getSource();
				t.setColumnSelectionAllowed(false);
				t.setRowSelectionAllowed(true);
				System.out.println("Selected Row:"+t.getSelectedRow());
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
}
