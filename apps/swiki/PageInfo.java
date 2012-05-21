package swiki;

import java.util.HashMap;
import java.util.Map;

public class PageInfo {
	private Map<String, Map<String, String>> types;
	
	String title;
	String redirect;
	
	public PageInfo(String title) {
		this.title = title;
		types = new HashMap<String, Map<String, String>>();
	}
	
	public void addType(String type, Map<String, String> attributes) {
		types.put(type, attributes);
	}
	
	public Map<String, Map<String, String>> getTypes() {
		return types;
	}
}