package pdstore.ui.historyview;

import javax.swing.table.TableCellRenderer;

import org.netbeans.swing.outline.Outline;

import pdstore.GUID;

public class MultiOutline extends Outline {

	int Offset = 0;
	JStartPointRender spr = new JStartPointRender();
	JDotRender dr = new JDotRender();
	JLineRender lr = new JLineRender();
	Transaction t = new Transaction();
	TreeTableModel tmodel;

	public MultiOutline() {
		super();
	}
	
	/**
	 * @param row Row coordinate in TreeTable
	 * @param col Column coordinate in TreeTable
	 * @return TableCellRenderer Returns a custom cell renderer for the graph column which is always column 1.
	 */
	public TableCellRenderer getCellRenderer(int row, int col) {

		if (col == 1) {
			String ch = (String) getValueAt(row, 2);
			String tr = (String) getValueAt(row, 3);
			if (tr == "" && ch == "") { // Store
				return spr;
			} else if (ch == "" && tr != "") { // Transaction
				int bcThis = tmodel.store.getBranchCount(tr);
				int branchPositionThis = tmodel.store.getBranchPosition(tr);
				int i=2;
				String changeType = (String) getValueAt(row+1, 2);
				while (changeType != null && changeType != ""){
					changeType = (String) getValueAt(row+i, 2);
					i++;
				}
				int bcNext = tmodel.store.getBranchCount((String)getValueAt(row+i-1,3));
				int branchPositionNext = tmodel.store.getBranchPosition((String)getValueAt(row+i-1,3));
				int positions[] = {branchPositionThis, branchPositionNext};
				if (bcNext > bcThis){//New Branch
					return dr = new JDotRender(tmodel.getTotalBranches(), bcThis, positions , branchPositionThis);
				}else{ //No New Branch
					return dr = new JDotRender(tmodel.getTotalBranches(), bcThis, null, branchPositionThis);
				}
			} else{ //Change
				int bcThis = tmodel.store.getBranchCount(tr);
				int bcNext = 0;
				int i=2;
				String changeType = (String) getValueAt(row+1, 2);
				while (changeType != null && changeType != ""){
					changeType = (String) getValueAt(row+i, 2);
					i++;
				}
				if (changeType == null){
					bcNext = bcThis;
				}else{
					bcNext = tmodel.store.getBranchCount((String)getValueAt(row+i-1,3));
				}
				int branchPosition = tmodel.store.getBranchPosition(tr);
				return lr = new JLineRender(tmodel.getTotalBranches(), bcNext, branchPosition);

			}
		}

		return super.getCellRenderer(row, col);

	}

}
