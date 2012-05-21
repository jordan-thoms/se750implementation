package pdtransform.examples;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;
import pdstore.dal.PDInstance;
import pdtransform.dal.PDAddressBook;
import pdtransform.dal.PDContact;
import pdtransform.dal.PDGenerator;
import pdtransform.dal.PDGeneratorApplication;
import pdtransform.dal.PDHTMLTag;
import pdtransform.dal.PDLITag;
import pdtransform.dal.PDText;
import pdtransform.dal.PDULTag;


public class SimpleList {

	public static void main(String[] args) {
		try {
			// Create a new cache that is connected to the DB
			PDWorkingCopy cache = new PDSimpleWorkingCopy(new PDStore("AVM"));

			PDGeneratorApplication ga = (PDGeneratorApplication) cache.newInstance(PDGeneratorApplication.typeId);
			cache.addLink(ga.getId(), PDStore.HAS_TYPE_ROLEID, PDGeneratorApplication.typeId);
			ga.setName("SimpleList");
			
			/* GenApp Input */
			
			PDAddressBook abook = (PDAddressBook) cache.newInstance(PDAddressBook.typeId);
			cache.addLink(abook.getId(), PDStore.HAS_TYPE_ROLEID, PDAddressBook.typeId);
			PDAddressBook businessABook = (PDAddressBook) cache.newInstance(PDAddressBook.typeId);
			cache.addLink(businessABook.getId(), PDStore.HAS_TYPE_ROLEID, PDAddressBook.typeId);
			PDAddressBook privateABook = (PDAddressBook) cache.newInstance(PDAddressBook.typeId);
			cache.addLink(privateABook.getId(), PDStore.HAS_TYPE_ROLEID, PDAddressBook.typeId);
			
			abook.setName("MyABook");
			businessABook.setName("BusinessContacts");
			businessABook.setLabel("Business Contacts");
			privateABook.setName("PrivateContacts");
			privateABook.setLabel("Private Contacts");
			
			PDContact contact = (PDContact)cache.newInstance(PDContact.typeId);	
			cache.addLink(contact.getId(), PDStore.HAS_TYPE_ROLEID, PDContact.typeId);
			contact.setName("Philip");
			contact.setContactsName("Philip Booth");
			contact.setAddress("Philip's address");
			privateABook.addContact(contact);
			
			PDContact contact2 = (PDContact)cache.newInstance(PDContact.typeId);
			cache.addLink(contact2.getId(), PDStore.HAS_TYPE_ROLEID, PDContact.typeId);
			contact2.setName("Gyurme");
			contact2.setContactsName("Gyurme Dahdul");
			contact2.setAddress("Gyurme's address");
			privateABook.addContact(contact2);
			
			PDContact contact3 = (PDContact)cache.newInstance(PDContact.typeId);
			cache.addLink(contact3.getId(), PDStore.HAS_TYPE_ROLEID, PDContact.typeId);
			contact3.setName("Collegue");
			contact3.setContactsName("Collegue x");
			contact3.setAddress("x's address");
			businessABook.addContact(contact3);
			
			PDContact contact4 = (PDContact)cache.newInstance(PDContact.typeId);
			cache.addLink(contact4.getId(), PDStore.HAS_TYPE_ROLEID, PDContact.typeId);
			contact4.setName("Boss");
			contact4.setContactsName("Boss y");
			contact4.setAddress("y's address");
			businessABook.addContact(contact4);
			
			abook.addSubAddressBook(businessABook);
			abook.addSubAddressBook(privateABook);
			
			ga.setInput(abook.getId());

			PDGenerator gen = (PDGenerator)cache.newInstance(PDGenerator.typeId);
			cache.addLink(gen.getId(), PDStore.HAS_TYPE_ROLEID, PDGenerator.typeId);
			gen.setName("ABookToHTML");

			/* Output Template */
			
			PDHTMLTag html = (PDHTMLTag)cache.newInstance(PDHTMLTag.typeId);
			cache.addLink(html.getId(), PDStore.HAS_TYPE_ROLEID, PDHTMLTag.typeId);
			html.setName("HTML Tag");
			PDLITag li = (PDLITag)cache.newInstance(PDLITag.typeId);
			cache.addLink(li.getId(), PDStore.HAS_TYPE_ROLEID, PDLITag.typeId);
			li.setName("LI Tag");
			PDULTag ul = (PDULTag)cache.newInstance(PDULTag.typeId);
			cache.addLink(ul.getId(), PDStore.HAS_TYPE_ROLEID, PDULTag.typeId);
			ul.setName("UL Tag");
			PDText text = (PDText)cache.newInstance(PDText.typeId);
			cache.addLink(text.getId(), PDStore.HAS_TYPE_ROLEID, PDText.typeId);
			text.setName("Text Node");
			PDText text2 = (PDText)cache.newInstance(PDText.typeId);
			cache.addLink(text2.getId(), PDStore.HAS_TYPE_ROLEID, PDText.typeId);
			text2.setName("Text Node");
			
			html.addChild(ul);
			ul.addChild(li);
			li.addText(text);
			text.addContent(new String());
			
			html.addText(text2);
			text2.addContent(new String());
			
			/* Generator setup */
			
			gen.setInputType(PDAddressBook.typeId);
			gen.setOutputType(PDHTMLTag.typeId);
			gen.setOutputTemplate(html.getId());

			gen.setGeneratorApplications(ga);

			/* Order Setup */
			
			ga.setGenerator(gen);
			
			cache.commit();
			System.out.println("Transaction committed");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
