package fluid.ExperimentialpdTable;

import java.util.ArrayList;
/**
 * PDCells only has one parent and can have multiple children like a tree.
 * @author Ted
 *
 */
public class PDCell {
	PDCell parent;
	ArrayList<PDCell> children;
	
	Object data;
	
	public PDCell(PDCell parent, Object inst){
		this.parent = parent;
		data = inst;
		children = new ArrayList<PDCell>();
	}
	
	public void addChildren(PDCell child){
		children.add(child);
	}
	
	public int getNumberOfChildren(){
		return children.size();
	}
}
