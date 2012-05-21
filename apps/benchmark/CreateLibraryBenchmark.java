package benchmark;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import pdstore.GUID;
import pdstore.PDStore;
import pdstore.generic.PDStoreI;

/**
 * class CreateLibraryBenchmark generates changes randomly and writes them to a
 * PDStore file and N-Triples (.nt) file. The subjects are String type,
 * predicates are GUID, and objects are Object type. The triples are in such
 * formats:
 * 
 *  (person, borrow, book), each person borrows 2 books in average.
 *  
 *  (person, worksfor, library), 10% of the people work for one of the libraries
 *  
 *  (library,category, book) each book has a category. there are 2 libraries: general library and SE 
 *  library, each book is randomly assigned to one of them 
 *  
 *  (person hasAge age) everyone has age recorded
 *  
 *  e.g. if the number of books is 5000, the number of people 1000, we 
 *  can get about 8000 triples
 * 
 * the path and the file names of PDStore file and .nt file can be changed from
 * the fileds "path" and "filename" the number of the person and books can be
 * changed from the fileds "numberOfPerson" and "numberOfBooks"
 */
public class CreateLibraryBenchmark {

	static GUID timeTick1 = new GUID();
	final public static GUID MATH_ROLEID = new GUID(
			"e900078a6a1311e09e6f005056c00008");
	final public static GUID CS_ROLEID = new GUID(
			"e911178a6a1311e09e6f005056c00008");
	final public static GUID STATS_ROLEID = new GUID(
			"e922278a6a1311e09e6f005056c00008");
	final public static GUID SE_ROLEID = new GUID(
			"e933378a6a1311e09e6f005056c00008");
	final public static GUID CHEM_ROLEID = new GUID(
			"e944478a6a1311e09e6f005056c00008");
	final public static GUID BORROW_ROLEID = new GUID(
			"f8ffe78f621311e09e6f005056c00008");
	final public static GUID WORKSFOR_ROLEID = new GUID(
			"9999978f621311e09e6f005056c00008");
	final public static GUID HASAGE_ROLEID = new GUID(
			"8899978f621311e09e6f005056c00008");
	
	static String path = "pddata/";
	static String filename = "PDStoreLibrary";
	static String extensionname = ".nt";
	static int numberOfBooks = 5000;
	static int numberOfPerson = 1000;
	static RandomAccessFile raf;
	static PDStoreI<GUID, Object, GUID> store;
	static GUID transactionID;
	static File file;

	public static void main(String[] args) {
		store = new PDStore(filename);
		transactionID = store.begin();

		file = new File(path + filename + extensionname);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

		try {
			raf = new RandomAccessFile(file, "rw");

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		// create book category list
		List<GUID> bookCategoryList = new ArrayList<GUID>();
		bookCategoryList.add(MATH_ROLEID);
		bookCategoryList.add(CS_ROLEID);
		bookCategoryList.add(STATS_ROLEID);
		bookCategoryList.add(SE_ROLEID);
		bookCategoryList.add(CHEM_ROLEID);

		// create library list
		List<String> libraryList = new ArrayList<String>();
		libraryList.add("SE library");
		libraryList.add("General library");

		// create book list and store them in to files

		List<String> bookList = new ArrayList<String>();
		for (int i = 0; i < numberOfBooks; i++) {
			String bookname = generateBookname();

			String subject = libraryList.get(randomNumber(libraryList.size()));
			GUID category = bookCategoryList.get(randomNumber(bookCategoryList
					.size()));
			bookList.add(bookname);
			addDataToPdStore(subject, category, bookname);
			writeSingleChangeToFile(subject, category, bookname);
		}
		for (int i = 0; i < numberOfPerson; i++) {
			String personname = generateRandomeName();
			// a person may borrow several books
			while (randomNumber(3) != 0) {
				String bookname = bookList.get(randomNumber(bookList.size()));
				addDataToPdStore(personname, BORROW_ROLEID, bookname);
				writeSingleChangeToFile(personname, BORROW_ROLEID, bookname);
			}
			// some people might work for library
			if (randomNumber(10) == 0) {
				String library = libraryList.get(randomNumber(libraryList
						.size()));
				addDataToPdStore(personname, WORKSFOR_ROLEID, library);
				writeSingleChangeToFile(personname, WORKSFOR_ROLEID, library);
			}
			int age = randomNumber(30) + 20;
			addDataToPdStore(personname, HASAGE_ROLEID, age);
			writeSingleChangeToFile(personname, HASAGE_ROLEID, age);

		}
		transactionID = store.commit(transactionID);
		System.out.println("Creating data finished");

	}

	/**
	 * write data to pdstore
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	private static void addDataToPdStore(String subject, GUID predicate,
			Object object) {

		store.addLink(transactionID, subject, predicate, object);

	}

	/**
	 * write data .nt file
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	private static void writeSingleChangeToFile(String subject, GUID predicate,
			Object object) {

		try {

			raf.seek(file.length());
			if (object instanceof Integer || object instanceof Float
					|| object instanceof Long)
				raf.writeBytes("<:" + subject + ">" + " " + "<:"
						+ predicate.toString() + ">" + " " + object + " .\r\n");
			else
				raf.writeBytes("<:" + subject + ">" + " " + "<:"
						+ predicate.toString() + ">" + " " + "<:" + object
						+ ">" + " .\r\n");
			// raf.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	private static String generateRandomeName() {
		char[] Vowels = { 'a', 'e', 'i', 'o', 'u' };
		char[] consonants = { 'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm',
				'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y' };

		String name = "";

		name += String.valueOf(consonants[randomNumber(20)])
				+ String.valueOf(Vowels[randomNumber(5)]);
		while (randomNumber(2) != 0) {
			if (randomNumber(3) == 1)
				name += String.valueOf(consonants[randomNumber(20)])
						+ String.valueOf(Vowels[randomNumber(5)])
						+ String.valueOf(Vowels[randomNumber(5)]);
			else
				name += String.valueOf(consonants[randomNumber(20)])
						+ String.valueOf(Vowels[randomNumber(5)]);
		}

		return (name);
	}

	private static int randomNumber(int n) {
		int rn = (int) (Math.random() * n);
		return rn;

	}

	private static String generateBookname() {
		String bookname = "";
		bookname += generateRandomeName() + " ";
		bookname += generateRandomeName() + " ";
		while (randomNumber(2) != 0) {
			bookname += generateRandomeName() + " ";
		}
		bookname = bookname.substring(0, bookname.length() - 2);
		return bookname;
	}
}
