package diagrameditor.ops;

import pdintegrate.DAL.PlayerExample;
import pdstore.dal.PDInstance;

public class StretchShape implements EditOperation {

	@Override
	public void apply(PDInstance superParameter) throws RuntimeException {
       if(!(superParameter instanceof PlayerExample)) {
    	   throw new RuntimeException("Wrong SuperParameter");
       }
       PlayerExample p = (PlayerExample)  superParameter;
       
		
	}

}
