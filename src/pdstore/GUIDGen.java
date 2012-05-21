package pdstore;

import java.util.ArrayList;
import java.util.List;

/**
 * Command line tool that generates new GUIDs
 * 
 * @author clut002
 * 
 */
public class GUIDGen {
	private static int DEFAULT = 50;
	
	public static void main(String[] args) {
				
		for (GUID g : generateGUIDs()){
			System.out.println("	public final static GUID DUMMY"
					+ g.toString() + " = new GUID(");
			System.out.println("	        \"" + g.toString()+"\");");
		}
		
		for (GUID g : generateGUIDs()){
			System.out.println(g.toString());
		}
	}
	
	public static List<GUID> generateGUIDs(){
		return generateGUIDs(DEFAULT);
	}
	
	
	public static List<GUID> generateGUIDs(int k){
		
		List<GUID> guids = new ArrayList<GUID>();
		for (int i = 0; i < k; i++){
			guids.add(new GUID());
    	}
    	return guids;
	}

}
