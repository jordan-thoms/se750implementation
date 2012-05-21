package pdtransform;
import java.awt.datatransfer.*;
import javax.swing.tree.*;
import java.util.*;
/**
 * A wrapper for regular mutable tree nodes to make them compatible with the 
 *  drag and drop interfaces of the JTree. *
 */
public class TransferableNode implements Transferable {
	public static final DataFlavor NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Node");
	private DefaultMutableTreeNode node;
	private DataFlavor[] flavors = { NODE_FLAVOR };
 
	public TransferableNode(DefaultMutableTreeNode nd) {
		node = nd;
	}  
 
	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (flavor == NODE_FLAVOR) {
			return node;
		}
		else {
			throw new UnsupportedFlavorException(flavor);	
		}			
	}
 
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}
 
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return Arrays.asList(flavors).contains(flavor);
	}
}
