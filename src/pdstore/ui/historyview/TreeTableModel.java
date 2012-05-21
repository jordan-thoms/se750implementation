package pdstore.ui.historyview;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.generic.PDChange;

/*
 * TreeTableModel class
 */
public class TreeTableModel extends SwingWorker<Change,Change> implements TreeModel {
	
	DefaultListModel eventLogModel = new javax.swing.DefaultListModel();
    Store store = new Store();
    PDStore pdstore;
    Transaction currTr = new Transaction();
    int numBranches = 0;
    Hashtable<GUID, GUID> branches; //transactionID branchID
    
    /**
     * TreeTable Constructor 
     * @param pdstore
     * 		PDStore object
     */
    public TreeTableModel(PDStore pdstore){
    	
    	this.pdstore = pdstore;
    	branches = new Hashtable();
    	
    }
    /**
     * Empty Constructor.  Does nothing.  Should not be used.
     */
    public TreeTableModel(){
    	
    }
    
    /**
     * Not implemented TODO
     */
    @Override
	public void addTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub
	}
    
    /**
     * @param node object to be interrogated for children at index, index
     * @param index index of requested child
     * @return Child of Transactions which are Changes, or Child of Store which are transactions at index, Null for Change objects because they have no children
     */
	@Override
	public Object getChild(Object node, int index) {
		if (node.getClass() == Store.class){
			Store n = (Store) node;
			return n.transactions.get(index);
		}else if (node.getClass() == Transaction.class){
			Transaction t = (Transaction) node;
			return t.changeList.get(index);
		}else if (node.getClass() == Change.class){
			return null;
		}
		return null;
	}
	
	/**
	 * @return number of children a given object has
	 */
	@Override
	public int getChildCount(Object node) {
		if (node.getClass() == Store.class){
			Store n = (Store) node;
			return n.size();
		}else if (node.getClass() == Transaction.class){
			Transaction t = (Transaction) node;
			return t.size();
		}else if (node.getClass() == Change.class){
			return 0;
		}
		return 0;
	}
	
	/**
	 * Gets index of child of given parent
	 * @param parent
	 * @param child
	 * @return index of child
	 */
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent.getClass() == Store.class){
			Store n = (Store) parent;
			return n.transactions.indexOf(child);
		}else if (parent.getClass() == Transaction.class){
			Transaction t = (Transaction) parent;
			return t.changeList.indexOf(child);
		}
		return 0;
	}

	/**
	 * Returns the PDStore object which is the root of a TreeTable
	 * @return The root of the treetable which is a PDStore object
	 */
	@Override
	public Object getRoot() {
		return this.store;
	}

	/**
	 * Returns a boolean defining whether a Store, Transaction, or Child  object is a leaf.
	 * All children are necessarily leaves.  Stores and Transactions are leaves when they have no children.
	 * @param node the object to be determined whether it is a leaf or not
	 * @return True if the node is a leaf, false otherwise
	 */
	@Override
	public boolean isLeaf(Object node) {
		if (node.getClass() == Store.class){
			Store n = (Store) node;
			if(n.size()==0){
				return true;
			}
			else{
				return false;
			}
		}else if (node.getClass() == Transaction.class){
			Transaction t = (Transaction) node;
			if (t.size()==0){
				return true;
			}else{
				return false;
			}
		}else if (node.getClass() == Change.class){
			return true;
		}
		return true;
	}

	/**
	 * Does nothing TODO
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		
	}

	/**
	 * Does nothing TODO
	 */
	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		
	}

	/**
	 * Iterates through the changes in a PDStore and packages them in Change Objects.  These are passed to the process method with the publish command.
	 */
	protected Change doInBackground() throws Exception {
	    int changeCount = 0;
		Change ch = new Change();
    	PDChange<GUID, Object, GUID> gChange;
    	Iterator<PDChange<GUID, Object, GUID>> iter = pdstore.iterator();
    	
    	while(iter.hasNext()){
    		gChange = iter.next();
    		ch = new Change(gChange);
    		changeCount++;
    		publish(ch);
    	}
    	return ch;
	}
	
	/**
	 * Creates Transaction objects and puts change Objects in them.  Changes are placed in Transactions according to their Transaction IDs
	 */
	protected void process(List<Change> chList){
		int propertyChange =0;
		
		if (branches.size() == 0){
			branches.put(chList.get(0).transactionID, chList.get(0).transactionID.getBranchID());
			numBranches++;
		}
		
		for (int i=0; i<chList.size(); i++){
			if (currTr.size() == 0){
				currTr.addChange(chList.get(i));
			}
			else if(currTr.getTransactionID()==chList.get(i).transactionID){
				currTr.addChange(chList.get(i));
			}else{
				store.add(currTr);
				currTr = new Transaction();
				currTr.numBranches = numBranches;
				currTr.branchPosition = numBranches-1;
				currTr.addChange(chList.get(i));
				if (!branches.containsValue(chList.get(i).transactionID.getBranchID())){
					branches.put(chList.get(i).transactionID, chList.get(i).transactionID.getBranchID());
					numBranches++;
					currTr.numBranches = numBranches;
					currTr.branchPosition = numBranches-1;
				}
			}
			firePropertyChange("transactionChange", 0, 1);
		}
		
		
	}
	
	/**
	 * Get the total number of branches in the PDStore
	 */
	public int getTotalBranches(){
		return numBranches;
	}
	
	/**
	 * Finishes processing of Changes being published in background.
	 */
	protected void done(){
		store.add(currTr);
	}
}
