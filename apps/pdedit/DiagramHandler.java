package pdedit;

import java.util.ArrayList;
import java.util.HashMap;

import pdedit.dal.PDNode;
import pdedit.dal.util.ModelAccessor;
import pdedit.dal.util.PDEditPortal;
import pdedit.pdGraphWidget.DiagramEvent;
import pdedit.pdGraphWidget.DiagramEventListener;
import pdedit.pdGraphWidget.DiagramLink;
import pdedit.pdGraphWidget.DiagramNode;
import pdstore.GUID;
import pdstore.dal.PDSimpleWorkingCopy;

public class DiagramHandler implements DiagramEventListener {

	private ModelAccessor editor;
	private ArrayList<PDEditPortal> friends;

	public DiagramHandler(ModelAccessor editor, ArrayList<PDEditPortal> friends){
		this.editor = editor;
		this.friends = friends;
	}

	public void linkChanged(DiagramEvent d) {
		// TODO Auto-generated method stub

	}

	public void linkCreated(DiagramEvent d) {
		DiagramLink link = (DiagramLink)d.getSource();
		// check whether the link contains a primiative
		// change the node id of the primiative type to the role id
		GUID instance = editor.createRelation(link.getNode1().getId(), link.getRelation1(),
				link.getNode2().getId(), link.getRelation2());
		link.setId(instance);
		if (isPrimitive(link.getNode1())){
			link.getNode1().setRoleID(instance);
			HashMap<Object, Object> info = link.getNode1().getProperty().get(0).getInfo();
			createPDNode(link.getNode1(), info, instance);
		}
		
		if (isPrimitive(link.getNode2())){
			link.getNode1().setRoleID(instance);
			HashMap<Object, Object> info = link.getNode2().getProperty().get(0).getInfo();
			createPDNode(link.getNode2(), info, instance);
		}
		
	}
	
	private boolean isPrimitive(DiagramNode e){
		String type = (String)e.getProperty().get(0).getInfo().get("Type");
		if (type.equals("PDStore.ComplexType")){
			return false;
		}
		return true;
	}

	public void linkRemoved(DiagramEvent d) {
		DiagramLink link = (DiagramLink)d.getSource();
		GUID type1 = link.getNode1().getId();
		GUID type2 = link.getNode2().getId();
		editor.removeRole(link.getId(), type1, type2);
	}

	public void linkSelected(DiagramEvent d) {
		// TODO Auto-generated method stub

	}

	public void modelChanged(DiagramEvent d) {
		// TODO Auto-generated method stub

	}

	public void modelCreated(DiagramEvent d) {
		// TODO Auto-generated method stub

	}

	public void nodeChanged(DiagramEvent d) {
		System.out.println("node Changed");
		DiagramNode node = (DiagramNode)d.getSource();
		GUID id = node.getId();
		Object o = editor.getDnd().getNodesByGUID(id);
		if (o == null && node.getRoleID() != null){
			id = node.getRoleID();
			o = editor.getDnd().getNodesByGUID(id);
			System.out.println("node found");
		}
		PDNode n = null;
		if (o instanceof PDNode){
			n = (PDNode)o;
			if (n != null){
				System.out.println("node PDNode change");
				n.setHasX((double)node.getLocation().x);
				n.setHasY((double)node.getLocation().y);
				n.getPDWorkingCopy().commit();
				editor.renameType(id, node.getName());
			}
	}
	editor.commit();
	}

	public void nodeCreated(DiagramEvent d) {
		DiagramNode node = (DiagramNode)d.getSource();
		HashMap<Object, Object> info = node.getProperty().get(0).getInfo();
		GUID instance = new GUID();
		if (info.get("Type").equals("PDStore.ComplexType")){
			instance = editor.createType(node.getName());
			node.setId(instance);
			createPDNode(node, info, instance);
		}else{
			node.setId((GUID)info.get("GUID"));
		}
		
	}

	//toString() method means get name of node.
	private void createPDNode(DiagramNode node, HashMap<Object, Object> info,
			GUID instance) {
		PDSimpleWorkingCopy cache = new PDSimpleWorkingCopy(editor.getStore());
		PDNode n = new PDNode(cache, new GUID());
		n.addHasInstance(instance.toString());
		n.addHasX(node.getLocation().x*1.0);
		n.addHasY(node.getLocation().y*1.0);
		n.addHasType((String)info.get("Type"));
		cache.commit();
		editor.getDnd().addNode(instance,n);
		System.out.println("Node: "+instance.toString() +"\nType: "+(String)info.get("Type")+"\nDescription: "+instance.toString()+"\n Model Intialised: "+editor.accessorIntialisedModel()+"\n");
	}

	public void nodeRemoved(DiagramEvent d) {
		DiagramNode node = (DiagramNode)d.getSource();
		editor.removeType(node.getId());
	}

	public void nodeSelected(DiagramEvent d) {
		for (PDEditPortal p : friends){
			p.nodeSelected(((DiagramNode)d.getSource()).getId(),
					(String)((DiagramNode)d.getSource()).getProperty().get(0).getInfo().get("Type"));
		}
	}

}
