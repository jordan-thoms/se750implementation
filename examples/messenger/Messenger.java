package messenger;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.UIManager;

import pdstore.*;
import pdstore.ui.treeview.PDTreeView;

public class Messenger {
	/* Stephen Hood 05/04/12 - class "BookExample" in the package "book" has been copied
	 * and modified to create this initial single-instance messaging framework -
	 * based around Conversations which contain Messages. */
	
	public static void main(String[] args) {
		// open existing PDStore database,
		// or create it if it doesn't exist
		// note: the default folder is "pddata"
		PDStore store = new PDStore("MyConversationDatabase");
		
		// this method creates our example model
		createConversationModel(store);

		// let's add some conversation data into the store
		addSomeData(store);

		// and we query & print out some data about the conversationBobJim
		// and the messageOne instances
		querySomeData(store, conversationBobJim);
		System.out.println();
		querySomeData(store, messageOne);
	}

	// the GUID that identifies our conversation model
	// (models are also complex instances)
	// (note: new GUIDs can be generated with pdstore.GUIDGen)
	static GUID model = new GUID("e82730207e3e11e1a4740018dea49e3e");

	// the GUIDs for the types and roles of our conversation model
	// (yes, types and roles are complex instances, too)
	static GUID conversationType = new GUID("e82e5c117e3e11e1a4740018dea49e3e");
	static GUID conversationTitleRole = new GUID("e82e5c127e3e11e1a4740018dea49e3e");
	static GUID conversationStartDateRole = new GUID("e82e5c137e3e11e1a4740018dea49e3e");
	static GUID messageRole = new GUID("e82e5c187e3e11e1a4740018dea49e3e");
	static GUID messageType = new GUID("e82e5c147e3e11e1a4740018dea49e3e");
	static GUID messageContentRole = new GUID(
			"e82e5c157e3e11e1a4740018dea49e3e");
	static GUID messageAuthorRole = new GUID(
			"e82e5c167e3e11e1a4740018dea49e3e");
	static GUID conversationLibraryType = new GUID("e82e5c107e3e11e1a4740018dea49e3e");
	static GUID conversationRole = new GUID("e82e5c177e3e11e1a4740018dea49e3e");

	static void createConversationModel(PDStore store) {

		// begin a new database transaction
		GUID transaction = store.begin();

		// create a new model, with a new GUID
		store.createModel(transaction, model, "MessengerModel");
		
		// create the new complex type ConversationLibrary
		store.createType(transaction, model, conversationLibraryType, "ConversationLibrary");
		
		// create the new complex type Conversation
		store.createType(transaction, model, conversationType, "Conversation");

		// create relation from Conversation to String with a role "title"
		// the role on the other end is unnamed, hence the 3rd parameter null
		store.createRelation(transaction, conversationType, null, "title",
				conversationTitleRole, PDStore.STRING_TYPEID);

		// create relation from Conversation to String with a role "start date"
		// the role on the other end is unnamed, hence the 3rd parameter null
		store.createRelation(transaction, conversationType, null, "start date", conversationStartDateRole,
				PDStore.STRING_TYPEID);

		// create the new complex type Message
		store.createType(transaction, model, messageType, "Message");

		// create relation from Message to String with a role "content"
		store.createRelation(transaction, messageType, null, "content",
				messageContentRole, PDStore.STRING_TYPEID);

		// create relation from Message to String with a role "author"
		store.createRelation(transaction, messageType, null, "author",
				messageAuthorRole, PDStore.STRING_TYPEID);

		// create relation from ConversationLibrary to Conversation 
		store.createRelation(transaction, conversationLibraryType, "contained_in", "has_conversations", conversationRole, conversationType);
		
		// create a relation from Conversation to Message
		// note this relation has names for both roles, so it can be navigated
		// in both directions;
		// i.e. you can get the messages in a conversation and the
		// conversation a message belongs to.
		store.createRelation(transaction, conversationType, "conversation", "messages",
				messageRole, messageType);

		// commit the transaction
		store.commit(transaction);
	}

	// new GUIDs to identify a Conversation and a Message in our database
	static GUID conversationLibrary = new GUID("e82e5c197e3e11e1a4740018dea49e3e");
	static GUID conversationBobJim = new GUID("e82e5c1a7e3e11e1a4740018dea49e3e");
	static GUID conversationJillEthel = new GUID("e82e5c1b7e3e11e1a4740018dea49e3e");
	static GUID messageOne = new GUID("e82e5c1c7e3e11e1a4740018dea49e3e");
	static GUID messageTwo = new GUID("e82e5c1d7e3e11e1a4740018dea49e3e");
	static GUID messageThree = new GUID("e82e5c1e7e3e11e1a4740018dea49e3e");
	static GUID messageFour = new GUID("e82e5c1f7e3e11e1a4740018dea49e3e");

	static void addSomeData(PDStore store) {

		// begin a new database transaction
		GUID transaction = store.begin();
		
		// let's put a conversation between Bob and Jim into our database
		store.setName(transaction, conversationBobJim, "Bob & Jim");

		// tell PDStore that conversationBobJim is of type Conversation
		store.setType(transaction, conversationBobJim, conversationType);

		// add links for our conversationBobJim instance
		store.addLink(transaction, conversationBobJim, conversationTitleRole, "Important things get discussed");
		store.addLink(transaction, conversationBobJim, conversationStartDateRole, "04/04/12");

		// and we add a Message to the database
		store.setName(transaction, messageOne, "From Bob");
		store.setType(transaction, messageOne, messageType);
		store.addLink(transaction, messageOne, messageContentRole, "Hi there Jim");
		store.addLink(transaction, messageOne, messageAuthorRole, "Bob");

		// we link the Message to conversationBobJim
		store.addLink(transaction, conversationBobJim, messageRole, messageOne);
		
		// Put a second Conversation into the database:
		store.setName(transaction, conversationJillEthel, "Jill & Ethel");
		store.setType(transaction, conversationJillEthel, conversationType);
		store.addLink(transaction, conversationJillEthel, conversationTitleRole, "Girl Power");
		store.addLink(transaction, conversationJillEthel, conversationStartDateRole, "04/04/12");
		
		// new Message for conversationBobJim...
		store.setName(transaction, messageTwo, "From Jim");
		store.setType(transaction, messageTwo, messageType);
		store.addLink(transaction, messageTwo, messageContentRole, "Hello yourself!");
		store.addLink(transaction, messageTwo, messageAuthorRole, "Jim");
		store.addLink(transaction, conversationBobJim, messageRole, messageTwo);
		
		// new Message for conversationJillEthel...
		store.setName(transaction, messageThree, "From Jill");
		store.setType(transaction, messageThree, messageType);
		store.addLink(transaction, messageThree, messageContentRole, "Hear me roar!");
		store.addLink(transaction, messageThree, messageAuthorRole, "Jill");
		store.addLink(transaction, conversationJillEthel, messageRole, messageThree);

		// new Message for conversationBobJim...
		store.setName(transaction, messageFour, "From Bob");
		store.setType(transaction, messageFour, messageType);
		store.addLink(transaction, messageFour, messageContentRole, "No need to be rude...");
		store.addLink(transaction, messageFour, messageAuthorRole, "Bob");
		store.addLink(transaction, conversationBobJim, messageRole, messageFour);
		
		// put the ConversationLibrary instance into the database
		store.setName(transaction, conversationLibrary, "My conversation library");
		store.setType(transaction, conversationLibrary, conversationLibraryType);
		
		// link the Conversations to the ConversationLibrary
		store.addLink(transaction, conversationLibrary, conversationRole, conversationBobJim);
		store.addLink(transaction, conversationLibrary, conversationRole, conversationJillEthel);

		// commit the transaction
		store.commit(transaction);
	}

	static void querySomeData(PDStore store, Object instance) {

		// begin a new database transaction
		GUID transaction = store.begin();

		// let's get the type for the given instance
		GUID type = store.getType(transaction, instance);

		// let's see what accessible roles the type of the instance has
		Collection<GUID> accessibleRoles = store.getAccessibleRoles(
				transaction, type);

		System.out.println("Instance "
				+ store.getNameOrValue(transaction, instance) + " has type "
				+ store.getNameOrValue(transaction, type)
				+ " and accessible roles with values:");
		for (GUID role : accessibleRoles) {
			Collection<Object> values = store.getInstances(transaction,
					instance, role);

			System.out.print(store.getNameOrValue(transaction, role) + " = ");
			for (Object v : values)
				System.out.print(store.getNameOrValue(transaction, v) + ", ");
			System.out.println();
		}

		// commit the transaction
		store.commit(transaction);
	}

}
