package pdstore.notify;

import java.util.ArrayList;
import java.util.List;

import pdstore.GUID;

public class SingleClientListenerService<Message> extends
		PDStoreListenerService<Message> {
	
	ArrayList<Message> newMessages = new ArrayList<Message>();

	/**
	 * This method is able to return a list of new messages for the 
	 * caller, if there have been some, and otherwise it should block.
	 * In the future implementation, it should return all new messages
	 * not yet seen by the client. 
	 * For this class, only one client should use this message.
	 * 
	 * @return the list of new messages.
	 */
	public List<Message> newMessages() {
		turnoverLock();
		if(!newMessages.isEmpty()){
	     	ArrayList<Message> result = newMessages;
	    	newMessages = new ArrayList<Message>();
			turnoverUnLock();	 
	    	return result;
		} 
		turnoverUnLock();	 
		ArrayList<Message> message = new ArrayList<Message>();
		message.add(nextMessage());
		return message;
	}
	
	public void processMessages(Message message) {
		super.processMessages(message);
		newMessages.add(message);
	}

	
}
