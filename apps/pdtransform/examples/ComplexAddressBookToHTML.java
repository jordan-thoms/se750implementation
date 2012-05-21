package pdtransform.examples;

import pdstore.*;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;
import pdtransform.dal.PDAddressBook;
import pdtransform.dal.PDContact;
import pdtransform.dal.PDGenerator;
import pdtransform.dal.PDGeneratorApplication;
import pdtransform.dal.PDHTMLTag;
import pdtransform.dal.PDLITag;
import pdtransform.dal.PDMap;
import pdtransform.dal.PDOrder;
import pdtransform.dal.PDOrderedPair;
import pdtransform.dal.PDPrintInstruction;
import pdtransform.dal.PDSerializer;
import pdtransform.dal.PDSerializerApplication;
import pdtransform.dal.PDText;
import pdtransform.dal.PDULTag;


public class ComplexAddressBookToHTML {

	/**
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String dbString = "PDTransform";
			String AppName = "ComplexAddressBookToHTML";
			
			// Create a new cache that is connected to the DB
			PDWorkingCopy cache = new PDSimpleWorkingCopy(new PDStore(dbString));
			
			PDGeneratorApplication ga = (PDGeneratorApplication) cache.newInstance(PDGeneratorApplication.typeId);
			cache.addLink(ga.getId(), PDStore.HAS_TYPE_ROLEID, PDGeneratorApplication.typeId);
			ga.setName(AppName);
	
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
			contact.setName("Philip");
			contact.setContactsName("Philip Booth");
			contact.setAddress("Philip's address");
			privateABook.addContact(contact);
			
			PDContact contact2 = (PDContact)cache.newInstance(PDContact.typeId);
			contact2.setName("Gyurme");
			contact2.setContactsName("Gyurme Dahdul");
			contact2.setAddress("Gyurme's address");
			privateABook.addContact(contact2);
			
			PDContact contact3 = (PDContact)cache.newInstance(PDContact.typeId);
			contact3.setName("Collegue");
			contact3.setContactsName("Collegue x");
			contact3.setAddress("x's address");
			businessABook.addContact(contact3);
			
			PDContact contact4 = (PDContact)cache.newInstance(PDContact.typeId);
			contact4.setName("Boss");
			contact4.setContactsName("Boss y");
			contact4.setAddress("y's address");
			businessABook.addContact(contact4);
			
			abook.addSubAddressBook(businessABook);
			abook.addSubAddressBook(privateABook);
			
			ga.setInput(abook.getId());

			PDGenerator gen = (PDGenerator)cache.newInstance(PDGenerator.typeId);
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
			
			/* Map Definitions */
			
			PDMap map = (PDMap)cache.newInstance(PDMap.typeId);
			cache.addLink(map.getId(), PDStore.HAS_TYPE_ROLEID, PDMap.typeId);
			map.setName("List Of Contacts");
			map.setOutputInstance(ul.getId());
			map.setInputRole(PDAddressBook.roleContactId);
			map.addOutputRole(PDULTag.roleChildId);
			map.setInputType(PDAddressBook.typeId);
			
			PDMap map2 = (PDMap)cache.newInstance(PDMap.typeId);
			cache.addLink(map2.getId(), PDStore.HAS_TYPE_ROLEID, PDMap.typeId);
			map2.setName("Separating Address Books");
			map2.setOutputInstance(html.getId());
			map2.setInputRole(PDAddressBook.roleSubAddressBookId);
			map2.addOutputRole(PDHTMLTag.roleChildId);
			map2.addOutputRole(PDHTMLTag.roleTextId);
			map2.setInputType(PDAddressBook.typeId);
			
			PDMap map3 = (PDMap)cache.newInstance(PDMap.typeId);
			cache.addLink(map3.getId(), PDStore.HAS_TYPE_ROLEID, PDMap.typeId);
			map3.setName("Contact Details");
			map3.setOutputInstance(text.getId());
			map3.setInputRole(PDContact.roleContactsNameId);
			map3.addOutputRole(PDText.roleContentId);
			map3.setInputType(PDContact.typeId);
			
			PDMap map4 = (PDMap)cache.newInstance(PDMap.typeId);
			cache.addLink(map4.getId(), PDStore.HAS_TYPE_ROLEID, PDMap.typeId);
			map4.setName("Address Book Names");
			map4.setOutputInstance(text2.getId());
			map4.setInputRole(PDAddressBook.roleLabelId);
			map4.addOutputRole(PDText.roleContentId);
			map4.setInputType(PDAddressBook.typeId);
			
			/* Implemented in map2 via multiple output roles 
			 *
			 * PDMap map5 = (PDMap)cache.newInstance(PDMap.typeId);
			 * map5.setName("address book text nodes");
			 *
			 * map5.setOutputInstance(html.getId());
			 * map5.setInputRole(PDAddressBook.roleSubAddressBookId);
			 * map5.addOutputRole(PDHTMLTag.roleTextId);
			 * map5.setInputType(PDAddressBook.typeId);
			 *
			 */
			
			/* Generator setup */
			
			gen.setInputType(PDAddressBook.typeId);
			gen.setOutputType(PDHTMLTag.typeId);
			gen.setOutputTemplate(html.getId());
			
			gen.addMap(map);
			gen.addMap(map2);
			gen.addMap(map3);
			gen.addMap(map4);
			//gen.addMap(map5);

			gen.setGeneratorApplications(ga);

			/* Order Setup */
			
			PDOrder order = (PDOrder)cache.newInstance(PDOrder.typeId);
			cache.addLink(order.getId(), PDStore.HAS_TYPE_ROLEID, PDOrder.typeId);
			
			PDOrderedPair orderedPair1 = (PDOrderedPair)cache.newInstance(PDOrderedPair.typeId);
			cache.addLink(orderedPair1.getId(), PDStore.HAS_TYPE_ROLEID, PDOrderedPair.typeId);
			orderedPair1.setPrev(PDText.typeId);
			orderedPair1.setNext(PDULTag.typeId);

//			PDOrderedPair orderedPair2 = (PDOrderedPair)cache.newInstance(PDOrderedPair.typeId);
//			orderedPair2.setPrev(PDText.typeId);
//			orderedPair2.setNext(PDULTag.typeId);
//			
			order.addOrderedPairs(orderedPair1);
			
			gen.setOrder(order);
			
			ga.setGenerator(gen);
			
			/* Setup Serializer */
			PDSerializer serializer = (PDSerializer)cache.newInstance(PDSerializer.typeId);
			cache.addLink(serializer.getId(), PDStore.HAS_TYPE_ROLEID, PDSerializer.typeId);

			PDPrintInstruction htmlInstruction = (PDPrintInstruction)cache.newInstance(PDPrintInstruction.typeId);
			cache.addLink(htmlInstruction.getId(), PDStore.HAS_TYPE_ROLEID, PDPrintInstruction.typeId);
			PDPrintInstruction ulInstruction = (PDPrintInstruction)cache.newInstance(PDPrintInstruction.typeId);
			cache.addLink(ulInstruction.getId(), PDStore.HAS_TYPE_ROLEID, PDPrintInstruction.typeId);
			PDPrintInstruction liInstruction = (PDPrintInstruction)cache.newInstance(PDPrintInstruction.typeId);
			cache.addLink(liInstruction.getId(), PDStore.HAS_TYPE_ROLEID, PDPrintInstruction.typeId);

			htmlInstruction.setPrintBefore("<html>");
			htmlInstruction.setPrintAfter("</html>");
			htmlInstruction.setType(PDHTMLTag.typeId);

			ulInstruction.setPrintBefore("<ul>");
			ulInstruction.setPrintAfter("</ul>");
			ulInstruction.setType(PDULTag.typeId);

			liInstruction.setPrintBefore("<li>");
			liInstruction.setPrintAfter("</li>");
			liInstruction.setType(PDLITag.typeId);

			serializer.addInstruction(htmlInstruction);
			serializer.addInstruction(ulInstruction);
			serializer.addInstruction(liInstruction);

//			PDSerializerApplication sa = (PDSerializerApplication)cache.newInstance(PDSerializerApplication.typeId);
//			cache.addLink(sa.getId(), PDStore.HAS_TYPE_ROLEID, PDSerializerApplication.typeId);
//			sa.setSerializer(serializer);
//			//sa.setInput(ga.getOutput());
//			
//			sa.setInputType(gen.getOutputType());
//			sa.setOrder(gen.getOrder());
//
//			//System.out.println(ga.getOutput());
//			sa.setName("ComplexAddressBookToHTML_serializer");
			
			cache.commit();
			System.out.println("Transaction committed");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
