package pdstore;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import nz.ac.auckland.se.genoupe.tools.Debug;

import lib.jsonrpc.JSONObj;
import lib.jsonrpc.JSONProperty;

import pdstore.changeindex.IndexEntry;
import pdstore.generic.Pairable;

import com.eaio.util.lang.Hex;
import com.eaio.uuid.UUIDGen;

/**
 * This class implements RFC4122 UUIDs, aka GUIDs. In particular, version 1
 * UUIDs which are timestamp-based are implemented. As a node we either use the
 * computer's MAC address (for GUIDs identifying PDStore instances), or a random
 * number (for transaction GUIDs, with the node identifying a branch). For more
 * information about the structure of the GUIDs refer to:
 * http://www.ietf.org/rfc/rfc4122.txt
 * 
 * @author Christof, Gerald
 * 
 */
@JSONObj
public class GUID implements Pairable<GUID>, Comparable<GUID>, Serializable {

	static Map<GUID, GUID> map = new HashMap<GUID, GUID>();
	
	private static final GUID maxTransactionGUID = new GUID(UUIDGen.encodeTime(Long.MAX_VALUE),
			UUIDGen.getClockSeqAndNode()).getFirst();


	public static GUID load(String string) {
		GUID probe = new GUID(string);
		return load(probe);
	}

	public static GUID load(GUID probe) {
		GUID result = map.get(probe);
		if (result != null) {
			return result;
		}
		map.put(probe, probe);
		return probe;
	}

	/**
	 * Random number generator used for random node IDs.
	 */
	static Random rnd = new Random();

	/**
	 * The time field of the UUID, containing the parts: time_low (8 bytes),
	 * time_mid (4 bytes), time_hi_and_version (4 bytes).
	 * 
	 * @serial
	 */
	@JSONProperty
	private long time;

	/**
	 * The clock sequence and node field of the GUID, containing the parts:
	 * clk_seq_hi_res (2 bytes), clk_seq_low (2 bytes), node (0-1), node (2-5).
	 * 
	 * @serial
	 */
	@JSONProperty
	private long clockSeqAndNode;

	/**
	 * The bit mask for the "first"-bit that distinguishes partner roles and
	 * temporary (begin) from durable (commit) transaction IDs.
	 */
	public long maskForFirst = 0x0010000000000000L;

	/**
	 * Constructs a GUID from a 32-character hexadecimal string (without
	 * internal delimiters such as "-").
	 * 
	 * @param hexString
	 *            32-character hexadecimal string
	 */
	public GUID(String hexString) {
		this((CharSequence) (hexString.substring(0, 8) + "-"
				+ hexString.substring(8, 12) + "-"
				+ hexString.substring(12, 16) + "-"
				+ hexString.substring(16, 20) + "-" + hexString.substring(20,
				32)));

		if (!hexString.matches("[A-F0-9a-f]{32}")) {
			throw new RuntimeException("GUID must be given as 32 hex digits.");
		}
	}

	/**
	 * This constructor is not inlined into the one above because constructor
	 * calls in constructors have to be the first statement in the body. Without
	 * this constructor, the CharSequence would have to be created twice.
	 * 
	 * @param s
	 */
	private GUID(CharSequence s) {
		this(Hex.parseLong(s.subSequence(0, 18)), Hex.parseLong(s.subSequence(
				19, 36)));
	}

	/**
	 * Creates a GUID using a timestamp and the computer's MAC address.
	 */
	public GUID() {
		this(UUIDGen.newTime(), UUIDGen.getClockSeqAndNode());
		Debug.assertTrue(getLongTimestamp() > 0,
				"GUID time is not positive value!");
	}

	/**
	 * Constructs a GUID directly from the binary data in the longs. Use with
	 * care.
	 * 
	 * @param time
	 * @param clockSeqAndNode
	 */
	public GUID(long time, long clockSeqAndNode) {
		this.setTime(time);
		this.setClockSeqAndNode(clockSeqAndNode);
	}

	/**
	 * Private copy constructor which is used for generating new GUIDs for a
	 * given branch furthyer below.
	 * 
	 * @param id
	 *            GUID to copy from
	 */
	private GUID(GUID id) {
		this(id.getTime(), id.getClockSeqAndNode());
	}

	/**
	 * Compares two GUIDs for equality.
	 * 
	 * @see java.lang.Object#equals(Object)
	 * @param obj
	 *            the Object to compare this GUID with, may be <code>null</code>
	 * @return <code>true</code> if the other Object is equal to this UUID,
	 *         <code>false</code> if not
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GUID)) {
			return false;
		}
		GUID t = (GUID) obj;
		return (getTime() == t.getTime()) && (getClockSeqAndNode() == t.getClockSeqAndNode());
	}

	/**
	 * This method extracts the timestamp of the GUID as a long.
	 * 
	 * @return the timestamp as a long.
	 */
	public long getLongTimestamp() {
		long timeFromGuid = getTime();
		long realTime = decodeGUIDTime(timeFromGuid);

		return realTime;
	}

	/**
	 * Gets the java.util.Date representation of this GUID's timestamp.
	 * 
	 * @return Date representation of this GUID's timestamp
	 */
	public Date getDate() {
		return new Date((getLongTimestamp() / 10000) - 12219336000000L);
	}

	/**
	 * Gets the GUID identifying the branch of this GUID. This works only if
	 * this GUID is transaction GUID, which has a branch ID encoded into the
	 * node field. The branch GUID is simply this transaction GUID with the
	 * timestamp part and the colck sequence part set to 0.
	 * 
	 * @return the branch GUID of this transaction GUID.
	 */
	public GUID getBranchID() {
		GUID branchId = new GUID(this);
		branchId.setTime(0);
		branchId.setClockSeqAndNode(branchId.getClockSeqAndNode() & 0x0000FFFFFFFFFFFFL);
		return branchId;
	}

	/**
	 * Generates a new branch GUID. Like for all branch GUIDs, all parts except
	 * the node ID part are 0. The node ID is a random number.
	 * 
	 * @return new branch GUID
	 */
	public static GUID newBranchId() {
		GUID branchId = new GUID();
		branchId.setTime(0);
		branchId.setClockSeqAndNode(rnd.nextLong());
		branchId.setClockSeqAndNode(branchId.getClockSeqAndNode() & 0x0000FFFFFFFFFFFFL);
		branchId.setClockSeqAndNode(branchId.getClockSeqAndNode() | 0x0000010000000000L);
		return branchId;
	}

	public static GUID maxTransactionId() {
		// make sure the maximum transaction is flagged as "durable".
		return maxTransactionGUID; 
	}

	/**
	 * Creates a new transaction GUID from the given branch GUID, by using a new
	 * timestamp with the node ID which identifies the branch.
	 * 
	 * @param branchId
	 *            the GUID for the branch
	 * @return a GUID identifying a new transaction on the branch
	 */
	public static GUID newTransactionId(GUID branchId) {
		GUID transactionId = new GUID();
		setBranchId(transactionId, branchId);
		return transactionId;
	}

	public static void setBranchId(GUID transactionId, GUID branchId) {
		transactionId.setClockSeqAndNode(transactionId.getClockSeqAndNode() & 0xFFFF000000000000L);
		transactionId.setClockSeqAndNode(transactionId.getClockSeqAndNode()
				| branchId.getClockSeqAndNode());
	}

	/**
	 * This method reverses the bit shuffling performed in UUIDGen.newTime().
	 * 
	 * @param timeFromGuid
	 * @return
	 */
	public static long decodeGUIDTime(long timeFromGuid) {
		long realTime = bitShufflingForGUIDDecoding(timeFromGuid);
		return realTime;
	}

	/**
	 * Extracts and concatenates all the bits that belong to the timestamp.
	 * 
	 * @param timeFromGuid
	 *            the original top 8 bytes of the GUID
	 * @return timestamp as long
	 */
	public static long bitShufflingForGUIDDecoding(long timeFromGuid) {

		// Leach, et al. Standards Track [Page 6]
		//
		//
		// RFC 4122 A UUID URN Namespace July 2005
		//
		// To minimize confusion about bit assignments within octets, the UUID
		// record definition is defined only in terms of fields that are
		// integral numbers of octets. The fields are presented with the most
		// significant one first.
		//
		// Field Data Type Octet Note
		// #
		//
		// time_low unsigned 32 0-3 The low field of the
		// bit integer timestamp
		//
		// time_mid unsigned 16 4-5 The middle field of the
		// bit integer timestamp
		//
		// time_hi_and_version unsigned 16 6-7 The high field of the
		// bit integer timestamp multiplexed
		// with the version number
		//
		//
		// clock_seq_hi_and_rese unsigned 8 8 The high field of the
		// rved bit integer clock sequence
		// multiplexed with the
		// variant
		//
		// clock_seq_low unsigned 8 9 The low field of the
		// bit integer clock sequence
		//
		// node unsigned 48 10-15 The spatially unique
		// bit integer node identifier
		//
		// In the absence of explicit application or presentation protocol
		// specification to the contrary, a UUID is encoded as a 128-bit object,
		// as follows:
		//
		// The fields are encoded as 16 octets, with the sizes and order of the
		// fields defined above, and with each field encoded with the Most
		// Significant Byte first (known as network byte order). Note that the
		// field names, particularly for multiplexed fields, follow historical
		// practice.
		//
		// 0 1 2 3
		// 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
		// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		// | time_low |
		// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		// | time_mid | time_hi_and_version |
		// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		// |clk_seq_hi_res | clk_seq_low | node (0-1) |
		// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		// | node (2-5) |
		// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

		// construct inverse:
		// time hi and version
		// UUIDGen: time |= 0x1000 | ((timeMillis >> 48) & 0x0FFF); // version 1
		// reversion: deleting
		long timeWithoutVersion = timeFromGuid & 0xFFFFFFFFFFFF0FFFL;
		// Old: long timeWithoutVersion = time & 0x0FFFFFFFFFFFFFFFL;
		// This was wrong, because the version bits are somewhere else (see RFC
		// extract above).

		long timeHigh = timeWithoutVersion << 48;

		// time mid
		// UUIDGen: time |= (timeMillis & 0xFFFF00000000L) >> 16;
		long timeMid = (timeWithoutVersion & 0xFFFF0000L) << 16;

		// time low
		// UUIDGen: time = timeMillis << 32;

		// This must be the >>> operator, because the
		// timelow overwrites the sign.
		long timeLow = (timeWithoutVersion >>> 32);

		return timeHigh | timeMid | timeLow;
	}

	/**
	 * Converts this GUID into a 32-byte array.
	 * 
	 * @return 32-byte array containing the GUID
	 */
	public byte[] toByteArray() {
		String s = toString();
		byte[] bytes = new byte[16];
		for (int i = 0; i < 32; i += 2) {
			bytes[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return bytes;
	}

	/**
	 * Gets the partner GUID of this GUID. This is not part of the UUID standard
	 * but a custom extension used for partner roles in PDStore, which is
	 * defined in the Pairable interface.
	 */
	public GUID getPartner() {
		GUID probe = new GUID(this);
		// implement xor on last bit
		long lastbit = probe.getClockSeqAndNode() & maskForFirst;
		if (lastbit != 0) {
			probe.setClockSeqAndNode(probe.getClockSeqAndNode() & (~maskForFirst));
		} else {
			probe.setClockSeqAndNode(probe.getClockSeqAndNode() | maskForFirst);
		}
		GUID partner = GUID.load(probe);
		return partner;
	}

	/**
	 * Gets the "first" GUID of the pair of GUIDs that consists of this GUID and
	 * its partner. It can be used to normalize links in PDStore by using the
	 * "first" of the two paired GUIDs that describe a role.
	 */
	public GUID getFirst() {
		if (isFirst())
			return this;
		else
			return getPartner();
	}

	/**
	 * Returns true iff this is the "first" GUID of the pair of GUIDs that
	 * consists of this GUID and its partner.
	 */
	public boolean isFirst() {
		long lastbit = getClockSeqAndNode() & maskForFirst;
		return lastbit == 0;
	}

	/**
	 * Returns true iff this GUID has an earlier timestamp than the given GUID
	 * id.
	 * 
	 * @param id
	 *            the given GUID
	 * @return true iff this GUID was created earlier
	 */
	public boolean earlier(GUID id) {
		return getLongTimestamp() < id.getLongTimestamp();
	}

	/**
	 * Returns true iff this GUID has a later timestamp than the given GUID id.
	 * 
	 * @param id
	 *            the given GUID
	 * @return true iff this GUID was created later
	 */
	public boolean later(GUID id) {
		return getLongTimestamp() > id.getLongTimestamp();
	}

	/**
	 * Compares this GUID with the given GUID by their timestamp.
	 * 
	 * @param id
	 *            the GUID to compare this GUID with
	 * @return 0 if the GUIDs have the same timestamp; -1 if this GUID is
	 *         earlier; 1 if this GUID is later
	 */
	public int compareTo(GUID id) {
		if (earlier(id))
			return -1;
		if (later(id))
			return 1;
		return 0;
	}

	/**
	 * Serialization routine.
	 * 
	 * @param out
	 *            the ObjectOutputStream
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeLong(getTime());
		out.writeLong(getClockSeqAndNode());
	}

	/**
	 * Deserialization routine.
	 * 
	 * @param in
	 *            the ObjectInputStream
	 * @throws IOException
	 */
	private void readObject(ObjectInputStream in) throws IOException {
		setTime(in.readLong());
		setClockSeqAndNode(in.readLong());
	}

	/**
	 * Returns this GUID as a String.
	 * 
	 * @return a String, never <code>null</code>
	 * @see java.lang.Object#toString()
	 * @see #toAppendable(Appendable)
	 */
	@Override
	public String toString() {
		return toAppendable(null).toString();
	}

	/**
	 * Returns this GUID as a String as well as a hash to facilitate manual
	 * comparison of GUIDs.
	 * 
	 * @return a String, never <code>null</code>
	 * @see java.lang.Object#toString()
	 * @see #toAppendable(Appendable)
	 */
	public String toStringWithHash() {
		return toAppendable(null).toString() + " (#" + hashCode() + ")";
	}

	/**
	 * Appends a String representation of this to the given {@link StringBuffer}
	 * or creates a new one if none is given.
	 * 
	 * @param in
	 *            the StringBuffer to append to, may be <code>null</code>
	 * @return a StringBuffer, never <code>null</code>
	 * @see #toAppendable(Appendable)
	 */
	public StringBuffer toStringBuffer(StringBuffer in) {
		StringBuffer out = in;
		if (out == null) {
			out = new StringBuffer(36);
		} else {
			out.ensureCapacity(out.length() + 36);
		}
		return (StringBuffer) toAppendable(out);
	}

	/**
	 * Appends a String representation of this object to the given
	 * {@link Appendable} object.
	 * <p>
	 * For reasons I'll probably never understand, Sun has decided to have a
	 * number of I/O classes implement Appendable which forced them to destroy
	 * an otherwise nice and simple interface with {@link IOException}s.
	 * <p>
	 * I decided to ignore any possible IOExceptions in this method.
	 * 
	 * @param a
	 *            the Appendable object, may be <code>null</code>
	 * @return an Appendable object, defaults to a {@link StringBuilder} if
	 *         <code>a</code> is <code>null</code>
	 */
	public Appendable toAppendable(Appendable a) {
		Appendable out = a;
		if (out == null) {
			out = new StringBuilder(36);
		}
		Hex.append(out, (int) (getTime() >> 32));// .append('-');
		Hex.append(out, (short) (getTime() >> 16));// .append('-');
		Hex.append(out, (short) getTime());// .append('-');
		Hex.append(out, (short) (getClockSeqAndNode() >> 48));// .append('-');
		Hex.append(out, getClockSeqAndNode(), 12);
		return out;
	}

	/**
	 * Returns a hash code of this UUID. The hash code is calculated by XOR'ing
	 * the upper 32 bits of the time and clockSeqAndNode fields and the lower 32
	 * bits of the time and clockSeqAndNode fields.
	 * 
	 * @return an <code>int</code> representing the hash code
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (int) ((getTime() >> 32) ^ getTime() ^ (getClockSeqAndNode() >> 32) ^ getClockSeqAndNode());
	}

	/**
	 * Clones this UUID.
	 * 
	 * @return a new UUID with identical values, never <code>null</code>
	 */
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			// One of Sun's most epic fails.
			return null;
		}
	}

	/**
	 * Returns the time field of the UUID (upper 64 bits).
	 * 
	 * @return the time field
	 */
	public final long getTime() {
		return time;
	}

	/**
	 * Returns the clock and node field of the UUID (lower 64 bits).
	 * 
	 * @return the clockSeqAndNode field
	 */
	public final long getClockSeqAndNode() {
		return clockSeqAndNode;
	}

	private void setTime(long time) {
		this.time = time;
	}

	private void setClockSeqAndNode(long clockSeqAndNode) {
		this.clockSeqAndNode = clockSeqAndNode;
	}
}
