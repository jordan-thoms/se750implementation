package gldebug;
import java.util.List;
import java.util.LinkedList;

class State
{
	public String name;
	public int numericName;
	public int enumName;
	public int budgieType;//budgie type
	public int length;
	public String data;
	public List<State> children;
	
	public State()
	{
		children = new LinkedList<State>();
	}

	public String toString()
	{
		return "Name: " + name;
	}
}