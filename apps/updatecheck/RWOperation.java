package updatecheck;

import java.util.ArrayList;

public class RWOperation {
	public String fqdn;
	public String methodName;
	public String methodDesc;
	public int lineNo;
	public ArrayList<Integer> arguments;

	public RWOperation(String fqdn, String methodName, String methodDesc, int lineNo, ArrayList<Integer> arguments){
		this.fqdn = fqdn;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
		this.lineNo = lineNo;
		this.arguments = arguments;
	}
	
	public String toString(){
		return "FQDN="+fqdn+" MethodName="+methodName+" MethodDesc="+methodDesc+" LineNo="+lineNo;
	}
	
	public String getMethodName(){
		return methodName;
	}
}
