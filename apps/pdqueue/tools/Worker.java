package pdqueue.tools;

public interface Worker<ItemID>{
	
	public void setConnection();
	public void dequeue(ItemID id);
}
