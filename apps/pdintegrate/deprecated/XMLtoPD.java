package pdintegrate.deprecated;

import java.util.EmptyStackException;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import pdintegrate.DAL.Entity;
import pdintegrate.DAL.Relation;
import pdintegrate.DAL.Schema;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.dal.PDSimpleWorkingCopy;
import pdstore.dal.PDWorkingCopy;

/**
 * For now, writes all data to s.
 * @param s
 */
public class XMLtoPD implements ContentHandler {

	private PDWorkingCopy pw;
	private PDStore s;
	private GUID g;
	private Stack<GUID> stack;
	private Schema schema;

	public XMLtoPD(PDStore s, Schema schema) {
		this.s = s;
		this.pw = new PDSimpleWorkingCopy(s);
		this.schema = schema;
		this.stack = new Stack<GUID>();
	}
	
	public GUID newPDInstance(GUID typeId, PDStore s) {
		GUID retval = new GUID();
		GUID g = s.begin();
		s.addLink(g, retval, PDStore.TYPE_TYPEID, typeId);
		s.commit(g);
		return retval;
	}
	
	public GUID getTypeId(GUID instance, PDStore s) {
		GUID g = s.begin();
		return (GUID)s.getInstance(g, instance, PDStore.TYPE_TYPEID);
	}


	@Override
	public void startElement(String uri, String localname, String qname,
			Attributes atts) throws SAXException {

		System.out.println("Starting " + qname);
		//Find this element in the schema definition
		Entity ntt = this.schema.getNamedEntity(qname);
		assert(ntt != null);

		//Create a new PDInstance which has as its type id the entity's id.
		GUID g = newPDInstance(ntt.getId(), s);
		assert(getTypeId(g, s).equals(ntt.getId()));

		//look at the previous pi's entity, look at its relations.
		try {
			GUID prev = this.stack.peek();
			
			System.out.println("The Previous is: " + prev.toString() + " : "+ getTypeId(prev, s));
			for (Object o : pw.getAllInstancesOfType(Entity.TYPE_ID)) {
				Entity e = (Entity) o;
				if (e.getId() == getTypeId(prev, s))
					System.out.println(o);
			}
			//compare them with this entity.
			Entity e = (Entity)pw.load(Entity.TYPE_ID, getTypeId(prev, s));
			for (Object o: e.getRelations()) {
				Relation r = (Relation)o;
				if (r.getEntity().equals(ntt)) {
					// If they match, link them!
					GUID linktransaction = s.begin();
					s.addLink(linktransaction, prev, r.getId(), g);
					System.out.println("Linking: " + prev.toString()+ " & " + g.toString());
					s.commit(linktransaction);
					break;
				}
			}
		} catch (EmptyStackException e) {
			//Do nothing
		}

		//At this point we're either linked with the previous, or the root itself.

		//Put us onto the stack.
		this.stack.push(g);

		for (int i = 0; i < atts.getLength(); i++) {
			//Need to link our attributes
			String name = atts.getQName(i);
			String value = atts.getValue(i);
			System.out.println(atts.getQName(i) + ":" + atts.getValue(i));
			for (Object o: ntt.getRelations()) {
				Relation r = (Relation)o;
				if (r.getEntity().getName().equals(name)) {
					// If they match, link them!
					GUID linktransaction = s.begin();
					s.addLink(linktransaction, g, r.getId(), value);
					System.out.println("Linking: " + g.toString()+ " & " + value.toString());
					s.commit(linktransaction);
					break;
				}
			}
		}

		//At this point, all the element is linked with its parent (if it has any)
		//Its attributes are linked to it
		//And it is the top of the stack
	}

	@Override
	public void endElement(String uri, String localname, String arg2)
	throws SAXException {
		System.out.println("Finishing " + localname);
		this.stack.pop();
		// I want to see any emptyStackExceptions thrown here,
		// because they are indicative of some trouble.

	}

	@Override
	public void characters(char[] ch, int start, int len) throws SAXException {

		char[] letters = new char[len];

		for (int i = 0; i< len; i++)
			letters[i] = ch[start + i];	

		// TODO: I don't know.  Will this ever have anything?
		try {
			System.out.println("Printing characters for " + this.stack.peek().toString() + this.stack.peek());
			System.out.println(letters);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startDocument() throws SAXException {
		// Start a transaction.
		if (s != null) {
			this.g = s.begin();
		}

	}


	@Override
	public void endDocument() throws SAXException {
		if (s != null) {
			this.s.commit(this.g);
			this.s.close();
		}
	}


	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
		// Not necessary

	}

	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
	throws SAXException {
		// Not necessary

	}

	@Override
	public void processingInstruction(String arg0, String arg1)
	throws SAXException {
		// Not necessary

	}

	@Override
	public void setDocumentLocator(Locator arg0) {
		// Not necessary

	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {
		// Not necessary

	}

	@Override
	public void startPrefixMapping(String arg0, String arg1)
	throws SAXException {
		// Not necessary

	}


}
