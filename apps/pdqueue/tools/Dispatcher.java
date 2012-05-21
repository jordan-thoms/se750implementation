package pdqueue.tools;

public interface Dispatcher<ItemID> {

	public void setConnection();
	public ItemID next();
}
