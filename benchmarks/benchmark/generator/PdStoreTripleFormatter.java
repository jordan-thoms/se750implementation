package benchmark.generator;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;

import benchmark.model.BSBMResource;
import benchmark.model.Offer;
import benchmark.model.Person;
import benchmark.model.Producer;
import benchmark.model.Product;
import benchmark.model.ProductFeature;
import benchmark.model.ProductType;
import benchmark.model.RatingSite;
import benchmark.model.Review;
import benchmark.model.Vendor;
import benchmark.serializer.ObjectBundle;
import benchmark.vocabulary.BSBM_pdstore;
import benchmark.vocabulary.DC;
import benchmark.vocabulary.FOAF_pdstore;
import benchmark.vocabulary.ISO3166;
import benchmark.vocabulary.RDF;
import benchmark.vocabulary.RDFS;
import benchmark.vocabulary.REV;
import benchmark.vocabulary.XSD;
import pdstore.GUID;
import pdstore.PDStore;
import pdstore.generic.PDStoreI;
import java.io.*;
import pdstore.*;
import pdstore.generic.*;

public class PdStoreTripleFormatter {

	private int currentWriter = 0;
	private long nrTriples;
	// Pdstore changes

	private static Hashtable<String, GUID> hshPredicatetbl = new Hashtable<String, GUID>();
	static String path = "pddata/";
	static String filename = Generator.outputFileName; // "PDStorebenchmark_ScaleFactor_2785";
	static String extensionname = ".nt";
	static PDStoreI<GUID, Object, GUID> store;
	static GUID transactionID;
	static File file;
	static RandomAccessFile raf;
	static FileOutputStream foptxt_setup;
	static FileOutputStream foptxt_Initialize;
	// Hash table data export
	static File filetxt;
	static RandomAccessFile raftxt;
	static String pathtxt = path;
	static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	static Date date = new Date();
	// dateFormat.format(date)
	static String filenametxt = "PDStorebenchmark_hashtableData"
			+ Math.random();
	static String extensionnametxt = ".txt";

	// End
	// End

	public long gatherData(ObjectBundle bundle, FileWriter fileWriter) {

		// Pdstore changes start

		store = new PDStore(filename);
		transactionID = store.begin();

	/*	file = new File(path + filename + extensionname);
		if (!file.exists() && false) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			if (false)
				raf = new RandomAccessFile(file, "rw");

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		*/// Pdstpre end

		Iterator<BSBMResource> it = bundle.iterator();

		while (it.hasNext()) {
			BSBMResource obj = it.next();
			try {
				if (obj instanceof ProductType) {
					fileWriter.append(convertProductType((ProductType) obj));
				} else if (obj instanceof Offer) {
					fileWriter.append(convertOffer((Offer) obj));
				} else if (obj instanceof Product) {
					fileWriter.append(convertProduct((Product) obj));
				} else if (obj instanceof Person) {
					fileWriter.append(convertPerson((Person) obj));
				} else if (obj instanceof Producer) {
					fileWriter.append(convertProducer((Producer) obj));
				} else if (obj instanceof ProductFeature) {
					fileWriter
							.append(convertProductFeature((ProductFeature) obj));
				} else if (obj instanceof Vendor) {
					fileWriter.append(convertVendor((Vendor) obj));
				} else if (obj instanceof Review) {
					fileWriter.append(convertReview((Review) obj));
				}
			} catch (IOException e) {
				System.err.println("Could not write into File!");
				System.err.println(e.getMessage());
				System.exit(-1);
			}
		}

		transactionID = store.commit(transactionID);
		System.out.println("Creating data finished");
		return nrTriples;
	}

	/*
	 * Converts the ProductType Object into an N-Triples String representation.
	 */
	private String convertProductType(ProductType pType) {
		StringBuffer result = new StringBuffer();
		// First the uriref for the subject
		String subjectURIREF = pType.toString_pdstore();
		// rdf:type
		result.append(createTriple(subjectURIREF,
				createURIref(RDF.type_pdstore),
				createURIref(BSBM_pdstore.ProductType)));

		// rdfs:label
		result.append(createTriple(subjectURIREF,
				createURIref(RDFS.label_pdstore),
				createLiteral(pType.getLabel())));

		// rdfs:comment
		result.append(createTriple(subjectURIREF,
				createURIref(RDFS.comment_pdstore),
				createLiteral(pType.getComment())));

		// rdfs:subClassOf
		if (pType.getParent() != null) {
			String parentURIREF = createURIref(BSBM_pdstore.INST_NS,
					"ProductType" + pType.getParent().getNr());
			result.append(createTriple(subjectURIREF,
					createURIref(RDFS.subClassOf_pdstore), parentURIREF));
		}

		// dc:publisher
		result.append(createTriple(subjectURIREF,
				createURIref(DC.publisher_pdstore),
				createURIref(BSBM_pdstore.getStandardizationInstitution(1))));

		// dc:date
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(pType.getPublishDate());
		String dateString = DateGenerator.formatDate(date);
		result.append(createTriple(subjectURIREF,
				createURIref(DC.date_pdstore),
				createDataTypeLiteral(dateString, createURIref(XSD.Date))));

		return result.toString();
	}

	/*
	 * Converts the Offer Object into an N-Triples String representation.
	 */
	private String convertOffer(Offer offer) {
		StringBuffer result = new StringBuffer();
		// First the uriref for the subject
		String subjectURIREF = offer.toString_pdstore();

		// rdf:type
		result.append(createTriple(subjectURIREF,
				createURIref(RDF.type_pdstore),
				createURIref(BSBM_pdstore.Offer)));

		// bsbm:product
		int productNr = offer.getProduct();
		int producerNr = Generator.getProducerOfProduct(productNr);
		result.append(createTriple(subjectURIREF,
				createURIref(BSBM_pdstore.product),
				Product.getURIref_pdstore(productNr, producerNr)));

		// bsbm:vendor
		result.append(createTriple(subjectURIREF,
				createURIref(BSBM_pdstore.vendor),
				Vendor.getURIref_pdstore(offer.getVendor())));

		// bsbm:price
		result.append(createTriple(
				subjectURIREF,
				createURIref(BSBM_pdstore.price),
				createDataTypeLiteral(offer.getPriceString(),
						createURIref(BSBM_pdstore.USD))));

		// bsbm:validFrom
		GregorianCalendar validFrom = new GregorianCalendar();
		validFrom.setTimeInMillis(offer.getValidFrom());
		String validFromString = DateGenerator.formatDateTime(validFrom);
		result.append(createTriple(
				subjectURIREF,
				createURIref(BSBM_pdstore.validFrom),
				createDataTypeLiteral(validFromString,
						createURIref(XSD.DateTime))));

		// bsbm:validTo
		GregorianCalendar validTo = new GregorianCalendar();
		validTo.setTimeInMillis(offer.getValidTo());
		String validToString = DateGenerator.formatDateTime(validTo);
		result.append(createTriple(
				subjectURIREF,
				createURIref(BSBM_pdstore.validTo),
				createDataTypeLiteral(validToString, createURIref(XSD.DateTime))));

		// bsbm:deliveryDays
		result.append(createTriple(
				subjectURIREF,
				createURIref(BSBM_pdstore.deliveryDays),
				createDataTypeLiteral(offer.getDeliveryDays().toString(),
						createURIref(XSD.Integer))));

		// bsbm:offerWebpage
		result.append(createTriple(subjectURIREF,
				createURIref(BSBM_pdstore.offerWebpage),
				createURIref(offer.getOfferWebpage())));

		// dc:publisher
		result.append(createTriple(subjectURIREF,
				createURIref(DC.publisher_pdstore),
				Vendor.getURIref_pdstore(offer.getVendor())));

		// dc:date
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(offer.getPublishDate());
		String dateString = DateGenerator.formatDate(date);
		result.append(createTriple(subjectURIREF,
				createURIref(DC.date_pdstore),
				createDataTypeLiteral(dateString, createURIref(XSD.Date))));

		return result.toString();
	}

	/*
	 * Converts the Product Object into an N-Triples String representation.
	 */
	private String convertProduct(Product product) {
		StringBuffer result = new StringBuffer();
		// First the uriref for the subject
		String subjectURIREF = product.toString_pdstore();

		// rdf:type
		result.append(createTriple(subjectURIREF,
				createURIref(RDF.type_pdstore),
				createURIref(BSBM_pdstore.Product)));

		// rdfs:label
		result.append(createTriple(subjectURIREF,
				createURIref(RDFS.label_pdstore),
				createLiteral(product.getLabel())));

		// rdfs:comment
		result.append(createTriple(subjectURIREF,
				createURIref(RDFS.comment_pdstore),
				createLiteral(product.getComment())));

		boolean forwardChaining = false;
		// bsbm:productType
		if (forwardChaining) {
			ProductType pt = product.getProductType();
			while (pt != null) {
				result.append(createTriple(subjectURIREF,
						createURIref(RDF.type_pdstore), pt.toString_pdstore()));
				pt = pt.getParent();
			}
		} else {
			result.append(createTriple(subjectURIREF,
					createURIref(RDF.type_pdstore), product.getProductType()
							.toString_pdstore()));
		}

		// bsbm:producer
		result.append(createTriple(subjectURIREF,
				createURIref(BSBM_pdstore.Producer),
				Producer.getURIref_pdstore(product.getProducer())));

		// bsbm:productPropertyNumeric
		Integer[] ppn = product.getProductPropertyNumeric();
		for (int i = 0, j = 1; i < ppn.length; i++, j++) {
			Integer value = ppn[i];
			if (value != null)
				result.append(createTriple(
						subjectURIREF,
						createURIref(BSBM_pdstore.getProductPropertyNumeric(j)),
						createDataTypeLiteral(value.toString(),
								createURIref(XSD.Integer))));
		}

		// bsbm:productPropertyTextual
		String[] ppt = product.getProductPropertyTextual();
		for (int i = 0, j = 1; i < ppt.length; i++, j++) {
			String value = ppt[i];
			if (value != null)
				result.append(createTriple(
						subjectURIREF,
						createURIref(BSBM_pdstore.getProductPropertyTextual(j)),
						createDataTypeLiteral(value, createURIref(XSD.String))));
		}

		// bsbm:productFeature
		Iterator<Integer> pf = product.getFeatures().iterator();
		while (pf.hasNext()) {
			Integer value = pf.next();
			result.append(createTriple(subjectURIREF,
					createURIref(BSBM_pdstore.productFeature),
					ProductFeature.getURIref_pdstore(value)));
		}

		// dc:publisher
		result.append(createTriple(subjectURIREF,
				createURIref(DC.publisher_pdstore),
				Producer.getURIref_pdstore(product.getProducer())));

		// dc:date
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(product.getPublishDate());
		String dateString = DateGenerator.formatDate(date);
		result.append(createTriple(subjectURIREF,
				createURIref(DC.date_pdstore),
				createDataTypeLiteral(dateString, createURIref(XSD.Date))));

		return result.toString();
	}

	/*
	 * Converts the Person Object into an N-Triples String representation.
	 */
	private String convertPerson(Person person) {
		StringBuffer result = new StringBuffer();
		// First the uriref for the subject
		String subjectURIREF = person.toString_pdstore();

		// rdf:type
		result.append(createTriple(subjectURIREF,
				createURIref(RDF.type_pdstore),
				createURIref(FOAF_pdstore.Person)));

		// foaf:name
		result.append(createTriple(subjectURIREF,
				createURIref(FOAF_pdstore.name),
				createLiteral(person.getName())));

		// foaf:mbox_sha1sum
		result.append(createTriple(subjectURIREF,
				createURIref(FOAF_pdstore.mbox_sha1sum),
				createLiteral(person.getMbox_sha1sum())));

		// bsbm:country
		result.append(createTriple(subjectURIREF,
				createURIref(BSBM_pdstore.country),
				createURIref(ISO3166.find(person.getCountryCode()))));

		// dc:publisher
		result.append(createTriple(subjectURIREF,
				createURIref(DC.publisher_pdstore),
				RatingSite.getURIref_pdstore(person.getPublisher())));

		// dc:date
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(person.getPublishDate());
		String dateString = DateGenerator.formatDate(date);
		result.append(createTriple(subjectURIREF,
				createURIref(DC.date_pdstore),
				createDataTypeLiteral(dateString, createURIref(XSD.Date))));

		return result.toString();
	}

	/*
	 * Converts the Producer Object into an N-Triples String representation.
	 */
	private String convertProducer(Producer producer) {
		StringBuffer result = new StringBuffer();
		// First the uriref for the subject
		String subjectURIREF = producer.toString_pdstore();

		// rdf:type
		result.append(createTriple(subjectURIREF,
				createURIref(RDF.type_pdstore),
				createURIref(BSBM_pdstore.Producer)));

		// rdfs:label
		result.append(createTriple(subjectURIREF,
				createURIref(RDFS.label_pdstore),
				createLiteral(producer.getLabel())));

		// rdfs:comment
		result.append(createTriple(subjectURIREF,
				createURIref(RDFS.comment_pdstore),
				createLiteral(producer.getComment())));

		// foaf:homepage
		result.append(createTriple(subjectURIREF,
				createURIref(FOAF_pdstore.homepage),
				createURIref(producer.getHomepage())));

		// bsbm:country
		result.append(createTriple(subjectURIREF,
				createURIref(BSBM_pdstore.country),
				createURIref(producer.getCountryCode())));// ISO3166.find(producer.getCountryCode()))));

		// dc:publisher
		result.append(createTriple(subjectURIREF,
				createURIref(DC.publisher_pdstore), producer.toString_pdstore()));

		// dc:date
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(producer.getPublishDate());
		String dateString = DateGenerator.formatDate(date);
		result.append(createTriple(subjectURIREF,
				createURIref(DC.date_pdstore),
				createDataTypeLiteral(dateString, createURIref(XSD.Date))));

		return result.toString();
	}

	/*
	 * Converts the ProductFeature Object into an N-Triples String
	 * representation.
	 */
	private String convertProductFeature(ProductFeature pf) {
		StringBuffer result = new StringBuffer();
		// First the uriref for the subject
		String subjectURIREF = createURIref(BSBM_pdstore.INST_NS,
				"ProductFeature" + pf.getNr());

		// rdf:type
		result.append(createTriple(subjectURIREF,
				createURIref(RDF.type_pdstore),
				createURIref(BSBM_pdstore.ProductFeature)));

		// rdfs:label
		result.append(createTriple(subjectURIREF,
				createURIref(RDFS.label_pdstore), createLiteral(pf.getLabel())));

		// rdfs:comment
		result.append(createTriple(subjectURIREF,
				createURIref(RDFS.comment_pdstore),
				createLiteral(pf.getComment())));

		// dc:publisher
		result.append(createTriple(subjectURIREF,
				createURIref(DC.publisher_pdstore), createURIref(BSBM_pdstore
						.getStandardizationInstitution(pf.getPublisher()))));

		// dc:date
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(pf.getPublishDate());
		String dateString = DateGenerator.formatDate(date);
		result.append(createTriple(subjectURIREF,
				createURIref(DC.date_pdstore),
				createDataTypeLiteral(dateString, createURIref(XSD.Date))));

		return result.toString();
	}

	/*
	 * Converts the Vendor Object into an N-Triples String representation.
	 */
	private String convertVendor(Vendor vendor) {
		StringBuffer result = new StringBuffer();
		// First the uriref for the subject
		String subjectURIREF = vendor.toString_pdstore();

		// rdf:type
		result.append(createTriple(subjectURIREF,
				createURIref(RDF.type_pdstore),
				createURIref(BSBM_pdstore.Vendor)));

		// rdfs:label
		result.append(createTriple(subjectURIREF,
				createURIref(RDFS.label_pdstore),
				createLiteral(vendor.getLabel())));

		// rdfs:comment
		result.append(createTriple(subjectURIREF,
				createURIref(RDFS.comment_pdstore),
				createLiteral(vendor.getComment())));

		// foaf:homepage
		result.append(createTriple(subjectURIREF,
				createURIref(FOAF_pdstore.homepage),
				createURIref(vendor.getHomepage())));

		// bsbm:country
		result.append(createTriple(subjectURIREF,
				createURIref(BSBM_pdstore.country),
				createURIref(vendor.getCountryCode())));// ISO3166.find(vendor.getCountryCode()))));

		// dc:publisher
		result.append(createTriple(subjectURIREF,
				createURIref(DC.publisher_pdstore), vendor.toString_pdstore()));

		// dc:date
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(vendor.getPublishDate());
		String dateString = DateGenerator.formatDate(date);
		result.append(createTriple(subjectURIREF,
				createURIref(DC.date_pdstore),
				createDataTypeLiteral(dateString, createURIref(XSD.Date))));

		return result.toString();
	}

	/*
	 * Converts the Review Object into an N-Triples String representation.
	 */
	private String convertReview(Review review) {
		StringBuffer result = new StringBuffer();
		// First the uriref for the subject
		String subjectURIREF = review.toString_pdstore();

		// rdf:type
		result.append(createTriple(subjectURIREF,
				createURIref(RDF.type_pdstore),
				createURIref(REV.Review_pdstore)));

		// bsbm:reviewFor
		result.append(createTriple(
				subjectURIREF,
				createURIref(BSBM_pdstore.reviewFor),
				Product.getURIref_pdstore(review.getProduct(),
						review.getProducerOfProduct())));

		// rev:reviewer
		result.append(createTriple(
				subjectURIREF,
				createURIref(REV.reviewer_pdstore),
				Person.getURIref_pdstore(review.getPerson(),
						review.getPublisher())));

		// bsbm:reviewDate
		GregorianCalendar reviewDate = new GregorianCalendar();
		reviewDate.setTimeInMillis(review.getReviewDate());
		String reviewDateString = DateGenerator.formatDateTime(reviewDate);
		result.append(createTriple(
				subjectURIREF,
				createURIref(BSBM_pdstore.reviewDate),
				createDataTypeLiteral(reviewDateString,
						createURIref(XSD.DateTime))));

		// dc:title
		result.append(createTriple(subjectURIREF,
				createURIref(DC.title_pdstore),
				createLiteral(review.getTitle())));

		// rev:text
		result.append(createTriple(
				subjectURIREF,
				createURIref(REV.text_pdstore),
				createLanguageLiteral(review.getText(),
						ISO3166.language[review.getLanguage()])));

		// bsbm:ratingX
		Integer[] ratings = review.getRatings();
		for (int i = 0, j = 1; i < ratings.length; i++, j++) {
			Integer value = ratings[i];
			if (value != null)
				result.append(createTriple(
						subjectURIREF,
						createURIref(BSBM_pdstore.getRating(j)),
						createDataTypeLiteral(value.toString(),
								createURIref(XSD.Integer))));
		}

		// dc:publisher
		result.append(createTriple(subjectURIREF,
				createURIref(DC.publisher_pdstore),
				RatingSite.getURIref_pdstore(review.getPublisher())));

		// dc:date
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(review.getPublishDate());
		String dateString = DateGenerator.formatDate(date);
		result.append(createTriple(subjectURIREF,
				createURIref(DC.date_pdstore),
				createDataTypeLiteral(dateString, createURIref(XSD.Date))));

		return result.toString();
	}

	// Create Literal
	private String createLiteral(String value) {
		StringBuffer result = new StringBuffer();
		// result.append("\"");
		result.append(value);
		// result.append("\"");
		return result.toString();
	}

	// Create typed literal
	private String createDataTypeLiteral(String value, String datatypeURI) {
		StringBuffer result = new StringBuffer();
		// result.append("\"");
		result.append(value);
		// result.append("\"^^");
		// result.append(datatypeURI);
		return result.toString();
	}

	// Create language tagged literal
	private String createLanguageLiteral(String text, String languageCode) {
		StringBuffer result = new StringBuffer();
		// result.append("\"");
		result.append(text);
		// result.append("\"@");
		// result.append(languageCode);
		return result.toString();
	}

	// Creates a triple
	private String createTriple(String subject, String predicate, String object) {
		StringBuffer result = new StringBuffer();
		result.append(subject);
		result.append(" ");
		result.append(predicate);
		result.append(" ");
		result.append(object);
		result.append(" .\n");

		nrTriples++;

		// pdstore changes start
		checkPredicateExists(predicate);
		GUID gd = hshPredicatetbl.get(predicate);
		// transactionID = store.begin();
		store.addLink(transactionID, subject, gd, object);
		// predicateGuidMapper(predicate);
		// End pdstore
		// transactionID = store.commit(transactionID);
		return result.toString();

	}

	// Create URIREF from namespace and element
	private String createURIref(String namespace, String element) {
		StringBuffer result = new StringBuffer();
		result.append(element);
		return result.toString();
	}

	// Create URIREF from URI
	private String createURIref(String uri) {
		StringBuffer result = new StringBuffer();
		// result.append("<");
		result.append(uri);
		// result.append(">");
		return result.toString();
	}

	public Long triplesGenerated() {

		return nrTriples;
	}

	// Changes related to inserting data to pds file

	public void checkPredicateExists(String predicate) {

		if (!hshPredicatetbl.containsKey(predicate)) {
			hshPredicatetbl.put(predicate, new GUID());
			predicateGuidMapper(predicate);
		}

	}

	public void predicateGuidMapper(String predicate) {
		// Initialize operation -start
		filetxt = new File(pathtxt + filenametxt + "Initialize"
				+ extensionnametxt);
		if (!filetxt.exists()) {
			try {
				filetxt.createNewFile();
				foptxt_Initialize = new FileOutputStream(filetxt);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		String t = "private GUID " + predicate + ";";
		try {// private GUID type=new GUID("8f67c1508eb211e185a9441ea1dc5b4f");
			foptxt_Initialize.write("\n".getBytes());
			foptxt_Initialize.write(t.getBytes());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			raftxt = new RandomAccessFile(filetxt, "rw");

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		// End initialze

		// aDD data from hash table to a text file -- Set up operation
		filetxt = new File(pathtxt + filenametxt + "Setup" + extensionnametxt);
		if (!filetxt.exists()) {
			try {
				filetxt.createNewFile();
				foptxt_setup = new FileOutputStream(filetxt);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		t = predicate + "= new GUID(" + '"' + hshPredicatetbl.get(predicate)
				+ '"' + ");";
		try {// private GUID type=new GUID("8f67c1508eb211e185a9441ea1dc5b4f");
			foptxt_setup.write("\n".getBytes());
			foptxt_setup.write(t.getBytes());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			raftxt = new RandomAccessFile(filetxt, "rw");

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		// End add data

	}
}
