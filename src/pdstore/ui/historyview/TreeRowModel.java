package pdstore.ui.historyview;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;

import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RowModel;

public class TreeRowModel implements RowModel{
	
	Outline outline;
	
	
	public TreeRowModel(Outline out){
		this.outline = out;
	}
	
	/**
	 * 
	 * @param row
	 * @param col
	 * @return Returns null because no cells are editable
	 */
	public TableCellEditor getCellEditor(int row, int col){
		TableCellEditor tmpEditor = null;
		return tmpEditor;
	}

	/**
	 * Returns the class of a column
	 */
	@Override
	public Class getColumnClass(int arg0) {
		if (arg0 == 0){
			return Canvas.class;
		}
		else{
			return String.class;
		}
		
	} 

	/**
	 * Returns the number of columns in the table.  Hardcoded to 8.
	 */
	@Override
	public int getColumnCount() {
		return 8;
	}

	/**
	 * Returns column names for column headings
	 */
	@Override
	public String getColumnName(int column) {
		switch (column){
		case 0:
	        return "Graph";
        case 1:
        	return "Change Type";
        case 2:
        	return "Transaction GUID";
        case 3:
        	return "Branch ID";
        case 4:
        	return "Instance 1";
        case 5:
        	return "Role 2";
        case 6:
        	return "Instance 2";
        case 7:
        	return "Timestamp";
        }
        return "";

	}

	/**
	 * @return Returns a string that is appropriate for a column for each row.
	 */
	@Override
	public Object getValueFor(Object node, int column) {
			
		if (node.getClass() == Store.class){
			Store n = (Store) node;
			switch (column){
			case 0:
				//outline.getColumn("Graph").setCellRenderer(n.cr);
				return null;
	        default:
	        	return "";
	        }
		}else if (node.getClass() == Transaction.class){
			Transaction t = (Transaction) node;
			switch (column){
			case 0:
				//outline.getColumn("Graph").setCellRenderer(t.cr);
				return null;
			case 1:
				return "";
	        case 2:
	        	return t.id.toString();
	        case 3:
	        	return t.id.getBranchID().toString();
	        case 7:
	        	return t.timestamp.toString();
	        default:
	        	return "";
	        }
		}else if (node.getClass() == Change.class){
			Change c = (Change) node;
			switch (column){
			case 0:
				//outline.getColumn("Graph").setCellRenderer(c.cr);
				return null;
	        case 1:
	        	return c.type.toString();
	        case 2:
	        	return c.transactionID.toString();
	        case 3:
	        	return c.transactionID.getBranchID();
	        case 4:
	        	return c.instance1.toString();
	        case 5:
	        	return c.role2.toString();
	        case 6:
	        	return c.instance2.toString();
	        case 7:
	        	return c.timestamp.toString();
	        }
		}
		return null;
	}

	/**
	 * Always returns false, because no cells are editable in VCSgui
	 */
	@Override
	public boolean isCellEditable(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Does nothing, because no cells in VCSgui are editable
	 */
	@Override
	public void setValueFor(Object arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

}
