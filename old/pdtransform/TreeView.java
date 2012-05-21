package pdtransform;

import java.util.ArrayList;

import pdstore.dal.PDInstance;
import pdstore.dal.PDRole;
import pdtransform.dal.PDMap;

public interface TreeView {

	public void addNode(PDInstance parent, Object child, PDRole relation);

	public void addMaps(ArrayList<PDMap> maps, PDInstance parent);

	public void addEmptyRole(PDRole role, PDInstance parent);

	public void showGUI();

	public Object getTree();

	public void expand();
}
