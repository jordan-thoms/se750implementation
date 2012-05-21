package pdintegrate.DAL;

import pdintegrate.annotations.*;
import pdstore.GUID;

@PDType(Entity_GUID = "")
public class PlayerExample {
	
	@PDAttribute(Role_GUID = "")
	public String name;

	@PDAttribute(Role_GUID = "")
	public Integer dateOfBirth;
	
	@PDAttribute(Role_GUID = "")
	public String email;
	
	@PDAttribute(Role_GUID = "")
	public String gender;
	
	@Override
	public String toString() {
		return "Name: " + this.name + 
		"\nBirth: " + this.dateOfBirth + 
		"\nemail: " + this.email +
		"\ngender: " + this.gender;
	}
}
