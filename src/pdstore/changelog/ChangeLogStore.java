package pdstore.changelog;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

import java.util.List;

import nz.ac.auckland.se.genoupe.tools.Debug;

import pdstore.*;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.generic.PDStoreI;
import pdstore.notify.PDListener;

/**
 * 
 * This is a persistent PDCore that stores the changes in a binary file.
 * 
 * This only does two things: Firstly, offers iterators over the transactions
 * (and hence the changes) in this binary file, and secondly writes new complete
 * transactions to the end.
 * 
 * This class can only deal with one transaction at a time. Currently (June
 * 2010) this class does not ensure that it is not used concurrently. The client
 * programs have to synchronize themselves.
 * 
 * @author zden011
 * @author zden011
 * 
 */
public class ChangeLogStore implements PDCoreI<GUID, Object, GUID> {

	// TODO new file creation & name stuff would be nicer in pdstore.PDStore
	public static final GUID VERSION_GUID = GUID
			.load("160060ea5b7ade11a67eb494a0878937");

	private RandomAccessFile file;

	RandomAccessFile getFile() {
		return file;
	}

	public ChangeLogStore(String fileName) throws PDStoreException {
		try {
			File fileEntry = new File(fileName);

			// check if file exists, if not create it
			if (!fileEntry.exists()) {
				if (fileEntry.createNewFile()) {
					this.file = new RandomAccessFile(fileEntry, "rw");
					init(GUID.newBranchId());
				} else {
					throw new PDStoreException("File creation failed!");
				}
			} else {
				file = new RandomAccessFile(fileEntry, "rw");

				// check version GUID
				if (!getVersion().equals(VERSION_GUID))
					throw new PDStoreException("File " + fileName
							+ " is not a ChangeLogStore file.");
			}
			file.seek(0);
		} catch (IOException e) {
			throw new PDStoreException("Error opening file", e);
		}
	}

	long getHeaderSize() {
		return 48;
	}

	public GUID getVersion() throws IOException {
		file.seek(0);
		byte[] versionGUID = new byte[16];
		file.read(versionGUID);
		return GUID.load(toHexString(versionGUID));
	}

	public GUID getRepository() throws PDStoreException {
		try {
			file.seek(0);
			file.skipBytes(16);
			byte[] repositoryGUID = new byte[16];
			file.read(repositoryGUID);

			return GUID.load(toHexString(repositoryGUID));
		} catch (IOException e) {
			throw new PDStoreException("getRepository failed", e);
		}
	}

	/**
	 * Retrieves the Master Branch Transaction ID
	 * 
	 * @return GUID of the Master Branch Transaction
	 * @throws PDStoreException
	 */
	// TODO remove?
	private GUID getMasterBranchTransaction() throws PDStoreException {
		try {
			file.seek(0);
			file.skipBytes(48);
			byte[] MasterBranchGUID = new byte[16];
			file.read(MasterBranchGUID);
			file.seek(0);
			return GUID.load(toHexString(MasterBranchGUID));
		} catch (IOException e) {
			throw new PDStoreException("getMasterBranchTransaction failed", e);
		}
	}

	/**
	 * Writes the header of a fresh .pds repository file
	 * 
	 * @param repository
	 *            the GUID of the repository
	 * @throws IOException
	 */
	private void init(GUID repository) throws IOException {
		// the file has to be brand new
		Debug.assertTrue(file.length() == 0, "The file has to be brand new!");

		byte[] versionGUID;
		byte[] repositoryGUID;
		byte[] previousTransactionGUID;

		versionGUID = VERSION_GUID.toByteArray();

		if (repository != null)
			repositoryGUID = repository.toByteArray();
		else
			repositoryGUID = createNullByteArray(16);

		previousTransactionGUID = createNullByteArray(16);

		file.write(versionGUID);
		file.write(repositoryGUID);
		file.write(previousTransactionGUID);
	}

	/**
	 * Reads an encoded instance of the given primitive type from the changelog
	 * at the current file position. This method is also used in
	 * changelog.ChangeIterator.
	 * 
	 * @param type
	 *            the primitive type of the instance to read
	 * @return the Object representing the read instance
	 * @throws IOException
	 */
	Object readInstance(PrimitiveType type) throws IOException {
		switch (type) {
		case GUID: {
			byte[] bytes = new byte[16];
			file.read(bytes);
			return GUID.load(toHexString(bytes));
		}
		case INTEGER: {
			return file.readLong();
		}
		case DOUBLE: {
			return file.readDouble();
		}
		case STRING: {
			long length = file.readLong();
			byte[] bytes = new byte[(int) length];
			file.read(bytes);
			return new String(bytes);
		}
		case BLOB: {
			long length = file.readLong();
			long position = file.getFilePointer();
			file.skipBytes((int) length);
			return new Blob(file, position, length);
		}
		case BOOLEAN: {
			return file.readBoolean();
		}
		case TIMESTAMP: {
			return new Timestamp(file.readLong());
		}
		}
		return null;
	}

	/**
	 * Encodes and writes the given instance to the changelog at the current
	 * file position.
	 * 
	 * @param instance
	 *            the instance to write
	 * @throws IOException
	 */
	private void writeInstance(Object instance) throws IOException {
		switch (PrimitiveType.typeOf(instance)) {
		case GUID: {
			file.write(((GUID) instance).toByteArray());
			break;
		}
		case INTEGER: {
			if (instance instanceof Integer)
				instance = new Long((Integer) instance);
			file.writeLong((Long) instance);
			break;
		}
		case DOUBLE: {
			file.writeDouble((Double) (instance));
			break;
		}
		case STRING: {
			byte[] bytes = ((String) instance).getBytes();
			file.writeLong(bytes.length);
			file.write(bytes);
			break;
		}
		case BLOB: {
			// handle Blob as well as byte[]
			byte[] bytes;
			if (instance instanceof Blob)
				bytes = ((Blob) instance).getData();
			else
				bytes = (byte[]) instance;

			file.writeLong(bytes.length);
			file.write(bytes);
			break;
		}
		case BOOLEAN: {
			file.writeBoolean((Boolean) instance);
			break;
		}
		case TIMESTAMP: {
			file.writeLong(((Timestamp) instance).getTime());
			break;
		}
		default:
			throw new RuntimeException("Not yet implemented.");
		}
	}

	/**
	 * Writes the header for a new transaction at the end of the changelog file.
	 * 
	 * @param transaction
	 *            the ID of the transaction to write
	 * @throws IOException
	 */
	private void writeTransactionHeader(GUID transaction) throws IOException {
		file.seek(file.length());
		file.write(transaction.toByteArray());
	}

	/**
	 * Write the given change to the current changelog file position. Changes
	 * must only be written after a transaction header.
	 * 
	 * @param change
	 *            the change to write
	 * @param isLastChange
	 *            flag that must be false for all changes except the last one in
	 *            the transaction
	 * @throws IOException
	 */
	private void writeChange(PDChange<GUID, Object, GUID> change,
			boolean isLastChange) throws IOException {
		Object instance1 = change.getInstance1();
		GUID role2Id = change.getRole2();
		Object instance2 = change.getInstance2();

		Debug.assertTrue(instance1 != null,
				"Instance1 of a link cannot be null!");
		Debug.assertTrue(role2Id != null, "Role2 of a link cannot be null!");
		Debug.assertTrue(instance2 != null,
				"Instance2 of a link cannot be null!");

		int header = 0;

		// set bit 7 as marker for last change in transaction
		if (isLastChange)
			header = header | 0x80;

		// set bit 6 as change type
		if (change.getChangeType() == ChangeType.LINK_ADDED)
			header = header | 0x40;
		else if (change.getChangeType() == ChangeType.LINK_REMOVED)
			;

		// set bits 3-5 as instance1 primitive type
		int type1 = PrimitiveType.typeOf(instance1).ordinal();
		header = header | (type1 << 3);

		// set bits 0-2 as instance2 primitive type
		int type2 = PrimitiveType.typeOf(instance2).ordinal();
		header = header | type2;

		// write change header, instance1, role2, instance2
		file.writeByte(header);
		writeInstance(instance1);
		writeInstance(role2Id);
		writeInstance(instance2);
	}

	public boolean isStoredBefore(Object instance) {
		// TODO not yet implemented - values are always stored
		return false;
	}

	private byte[] createNullByteArray(int arrayLength) {
		byte[] b = new byte[arrayLength];
		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) 0;
		}
		return b;
	}

	/**
	 * Converts a byte array to a hex string.
	 * 
	 * @param res
	 *            the byte array to convert
	 * @return the hex string representation of the given byte array
	 */
	public static String toHexString(byte[] res) {
		String result = "";
		for (int i = 0; i < res.length; i++) {
			result += Integer.toString(((res[i] & 0xf0) >> 4) + 0x10, 16)
					.substring(1);
			result += Integer.toString((res[i] & 0x0f) + 0x10, 16).substring(1);
		}
		return result;
	}

	protected void finalize() throws Throwable {
		file.close();
	}

	/**
	 * Merges this ChangeLogStore with the given other ChangeLogStore, and
	 * returns the merge result as a ChangeLogStore with the given
	 * targetFileName. The merge semantics is set union of all the transactions.
	 * 
	 * @param store2
	 *            the ChangeLogStore to merge this store with
	 * @param targetFileName
	 *            the file name of the resulting ChangeLogStore
	 * @return the ChangeLogStore that contains all the transactions
	 */
	public ChangeLogStore merge(ChangeLogStore store2, String targetFileName) {
		return merge(store2, new ChangeLogStore(targetFileName));
	}

	private ChangeLogStore merge(ChangeLogStore store2, ChangeLogStore target) {
		Iterator<PDChange<GUID, Object, GUID>> changes1 = iterator();
		Iterator<PDChange<GUID, Object, GUID>> changes2 = store2.iterator();
		PDChange<GUID, Object, GUID> c1 = null;
		PDChange<GUID, Object, GUID> c2 = null;
		if (changes1.hasNext())
			c1 = changes1.next();
		if (changes2.hasNext())
			c2 = changes2.next();

		while (c1 != null || c2 != null) {
			if (c2 == null) {
				// Only changes1 left to merge.
				// Write all changes1 of current transaction.
				c1 = mergeTransaction(changes1, c1, target);

			} else if (c1 == null) {
				// Only changes2 left to merge.
				// Write all changes2 of current transaction.
				c2 = mergeTransaction(changes2, c2, target);

			} else if (c1.getTransaction().earlier(c2.getTransaction())) {
				// Current transaction in changes1 is earlier.
				// Write all changes1 of current transaction.
				c1 = mergeTransaction(changes1, c1, target);

			} else if (c2.getTransaction().earlier(c1.getTransaction())) {
				// Current transaction in changes2 is earlier.
				// Write all changes2 of current transaction.
				c2 = mergeTransaction(changes2, c2, target);

			} else if (!c1.getTransaction().equals(c2.getTransaction())) {
				// Current transaction in both changes1 and changes2 happens to
				// have the same timestamp, but not the same GUID (i.e.
				// different transactions).
				// Write both transactions in arbitrary order.
				c1 = mergeTransaction(changes1, c1, target);
				c2 = mergeTransaction(changes2, c2, target);

			} else {
				// Current transaction in both changes1 and changes2 is exactly
				// the same.
				// Merge it only once into the result.
				c1 = mergeTransaction(changes1, c1, target);
			}
		}
		return target;
	}

	private PDChange<GUID, Object, GUID> mergeTransaction(
			Iterator<PDChange<GUID, Object, GUID>> source,
			PDChange<GUID, Object, GUID> currentChange, ChangeLogStore target) {
		try {
			GUID transaction = currentChange.getTransaction();
			target.writeTransactionHeader(transaction);
			PDChange<GUID, Object, GUID> nextChange = null;
			if (source.hasNext())
				nextChange = source.next();

			while (nextChange != null
					&& nextChange.getTransaction().equals(transaction)) {

				target.writeChange(currentChange, false);

				// move one change forward
				currentChange = nextChange;
				if (source.hasNext())
					nextChange = source.next();
				else
					nextChange = null;

			}

			// write last change in transaction
			target.writeChange(currentChange, true);

			return nextChange;

		} catch (IOException e) {
			throw new PDStoreException("Merge failed", e);
		}
	}

	public static void callListeners(List<PDListener<GUID, Object, GUID>> list,
			PDCoreI<GUID, Object, GUID> core,
			List<PDChange<GUID, Object, GUID>> changes) {
		for (PDListener<GUID, Object, GUID> listener : list) {
			listener.transactionCommitted(changes, changes, core);
		}
	}

	public Iterator<PDChange<GUID, Object, GUID>> iterator() {
		return new ChangeIterator(this);
	}

	private List<PDListener<GUID, Object, GUID>> interceptorList = new ArrayList<PDListener<GUID, Object, GUID>>();

	private List<PDListener<GUID, Object, GUID>> listenerList = new ArrayList<PDListener<GUID, Object, GUID>>();

	public List<PDListener<GUID, Object, GUID>> getDetachedListenerList()
			throws PDStoreException {
		return listenerList;
	}

	public List<PDListener<GUID, Object, GUID>> getInterceptorList()
			throws PDStoreException {
		return interceptorList;
	}

	@Override
	public GUID addTransaction(Transaction<GUID, Object, GUID> transaction)
			throws PDStoreException {

		// identify last change that will be written to the log
		int lastChange = -1; // set flag
		int count = 0;
		for (PDChange<GUID, Object, GUID> change : transaction) {
			if (change.isInLog()) {
				lastChange = count;
			}
			++count;
		}
		if (lastChange == -1)
			return null;

		synchronized (this) {
			callListeners(interceptorList, this, transaction);

			GUID beginTransactionId = transaction.getBeginId();
			transaction.ensureDurableIdIsSet();

			GUID durableTransactionId = transaction.getDurableId();

	
			// Add a change memorizing the two Transaction GUIDs
			// TODO Gerald, please check that the role is used correctly
			transaction.add(new PDChange<GUID, Object, GUID>(
					ChangeType.LINK_ADDED, durableTransactionId,
					beginTransactionId, PDStore.DURABLE_TRANSACTION_ROLEID,
					durableTransactionId));

			try {
				writeTransactionHeader(durableTransactionId);

				for (int i = 0; i < lastChange; i++)
					writeChange(transaction.get(i), false);

				writeChange(transaction.get(lastChange), true);
			} catch (IOException e) {
				throw new PDStoreException("Commit failed", e);
			}

			// rewrite the transactionID of all changes to the durable ID

			return durableTransactionId;
		}
	}
}
