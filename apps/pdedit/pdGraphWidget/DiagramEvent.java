package pdedit.pdGraphWidget;

import java.util.EventObject;

public class DiagramEvent extends EventObject{

	private static final long serialVersionUID = 1L;
	
	private Object source;
	
	public Object getSource() {
		return source;
	}

	public DiagramEvent(Object source) {
		super(source);
		this.source = source;
	}
	
	public String toString(){
		String ret = "";
		if (source instanceof DiagramNode){
			DiagramNode node = (DiagramNode)source;
			ret += "DiagramElement: Node\nName: "+node.getName()+"\n";
			if (node.getProperty().size() > 1){
				ret += "Properties {\n";
			}else{
				ret += "Property {\n";
			}
			for (ElementPropertyInterface e : node.getProperty()){
				ret += "\t"+e.toString()+"\n";
			}
			ret += "}";
			System.out.println(ret);
		}else if (source instanceof DiagramLink){
			DiagramLink link = (DiagramLink)source;
			ret += "DiagramElement: Role\n";
			ret += "Relationships {\n";
			ret += "\t"+link.getNode1Name() +" "+link.getRelation1()+" "+link.getNode2Name()+"\n";
			ret += "\t"+link.getNode2Name() +" "+link.getRelation2()+" "+link.getNode1Name()+"\n";
			ret += "}";
			System.out.println(ret);
		}
		return ret;
	}
	

}
