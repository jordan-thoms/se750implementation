package pdstore.changelog;

import pdstore.ChangeType;
import pdstore.GUID;
import pdstore.PDStoreException;

import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import pdstore.generic.PDChange;

public class ChangeIterator implements
		Iterator<PDChange<GUID, Object, GUID>> {

	ChangeLogStore logFile;
	long filePosition;
	GUID currentTransaction;
	boolean transactionFinished = true;

	public ChangeIterator(ChangeLogStore logFile) {
		this.logFile = logFile;
		filePosition = logFile.getHeaderSize();
	}

	public boolean hasNext() {
		try {
			return filePosition < logFile.getFile().length();
		} catch (IOException e) {
			// policy: only PDStoreExceptions
			throw new PDStoreException("", e);
		}
	}

	public PDChange<GUID, Object, GUID> next() {
		PDChange<GUID, Object, GUID> change = null;
		synchronized (logFile) {
			try {
				logFile.getFile().seek(filePosition);
				if (transactionFinished)
					currentTransaction = (GUID) logFile
							.readInstance(PrimitiveType.GUID);

				change = readChange();
				
				filePosition = logFile.getFile().getFilePointer();
				return change;
			} catch (EOFException e) {
				if(change!=null) return change;
				throw new PDStoreException("next() called, but hasNext()==false.", e);
			} catch (IOException e) {
				// other IO exceptions
				// policy: only PDStoreExceptions
				throw new PDStoreException("", e);
			}
		}
	}

	private PDChange<GUID, Object, GUID> readChange()
			throws IOException {

		// read the header of this change
		byte header = logFile.getFile().readByte();
		
		// test bit 7 as marker for last change in transaction
		transactionFinished = ((header & 0x80) != 0);

		// test bit 6 as change type
		ChangeType changeType;
		if ((header & 0x40) != 0)
			changeType = ChangeType.LINK_ADDED;
		else
			changeType = ChangeType.LINK_REMOVED;
				
		// read bits 3-5 as instance1 primitive type
		PrimitiveType type1 = PrimitiveType.typeForCode((header>>>3) & 0x07);
		
		// read bits 0-2 as instance2 primitive type
		PrimitiveType type2 = PrimitiveType.typeForCode(header & 0x07);
		
		// read instance1, role2, instance 2
		Object instance1 = logFile.readInstance(type1);
		GUID role2 = (GUID) logFile.readInstance(PrimitiveType.GUID);
		Object instance2 = logFile.readInstance(type2);

		return new PDChange<GUID, Object, GUID>(changeType, currentTransaction, instance1, role2, instance2);
	}

	public void remove() {
		throw new UnsupportedOperationException(
				"Transactions may not be removed from the data file");
	}

}
