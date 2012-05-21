package diagrameditor.ops;

import pdstore.dal.PDInstance;

public interface EditOperation {
	
	void apply(PDInstance superParameter) throws RuntimeException;
	
}
