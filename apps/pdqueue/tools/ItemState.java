package pdqueue.tools;

public enum ItemState {
	PENDING,
	PROCESSING,
	COMPLETED,
	ERROR1,
	ERROR2;
	
	public String getMessage() {
		switch (this) {
			case ERROR1: return "ERROR CODE 1: CONNECTION ERROR";
			case ERROR2: return "ERROR CODE 2: DATABASE ERROR";
			default : return "CURRENT ITEM STATUS: " + this.toString();
		}
	}
}