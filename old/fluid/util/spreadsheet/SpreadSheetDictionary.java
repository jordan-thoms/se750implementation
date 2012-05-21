package fluid.util.spreadsheet;

public enum SpreadSheetDictionary {
	Get ("Get"),
	Set ("Set"),
	Row ("Row"),
	Rows ("Rows"),
	Column ("Column"),
	Columns ("Columns"),
	Header ("Header"),
	Headers ("Headers"),
	Filter ("Filter"),
	Select ("Select"),
	Results ("Results");

	private final String tag;
	SpreadSheetDictionary(String name) {
		tag = name;
	}

	public static boolean isInDictionary(String target){
		for (SpreadSheetDictionary i :SpreadSheetDictionary.values()){
			if (target.toLowerCase().equals(i.getValue().toLowerCase())){
				return true;
			}
		}
		return false;
	}
	
	public static String beginsWith(String suff){
		for (SpreadSheetDictionary i :SpreadSheetDictionary.values()){
			if (i.getValue().toLowerCase().startsWith(suff.toLowerCase())){
				return i.getValue();
			}
		}
		return null;
	}
	
	public static SpreadSheetDictionary stringToEnum(String suff){
		for (SpreadSheetDictionary i :SpreadSheetDictionary.values()){
			if (i.getValue().equalsIgnoreCase(suff)){
				return i;
			}
		}
		return null;
	}

	public String getValue(){
		return tag;
	}
}
