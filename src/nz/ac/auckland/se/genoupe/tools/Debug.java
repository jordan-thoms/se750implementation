package nz.ac.auckland.se.genoupe.tools;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pdstore.PDStoreException;

/**
 * This is the unified class for logging debug information. All debug output
 * should be given with the following command:
 * 
 * Debug.err.println("your debug output string");
 * 
 * There should be no self-written test for debug modes. This is handled by the
 * class in the following way:
 * 
 * To turn on debug output for a single class, add the following static
 * constructor to this class:
 * 
 * static { Debug.err.debugThisClass(); }
 * 
 * To turn on the debug output for the whole application call anywhere at the
 * start of the program Debug.err.setGlobalDebug(true);
 * 
 * 
 * 
 * @author gweb017
 * 
 */
public class Debug {

	public static Debug err = new Debug();

	/**
	 * Counter for the number of debug calls done.
	 * 
	 */
	private static int currentDebugging = 0;

	/**
	 * Defining the maximum number CALLS of debug.println() with a Class-based
	 * debug trigger. This has been defined to reduce extreme runtime overhead.
	 * maxDebugging = 0 means no limit. A negative value means, Class-based
	 * debug triggers are never tested and no warning is printed.
	 * 
	 */
	private static int maxDebugging = -10;

	public static int getMaxDebugging() {
		return maxDebugging;
	}

	public static void setMaxDebugging(int maxDebugging) {
		Debug.maxDebugging = maxDebugging;
	}

	private PrintStream output = System.err;

	private boolean globalDebug = false;

	/**
	 * Indicates if a Debug.printX() method is currently being executed, to
	 * prevent execution of other prints and a possible infinite recursion.
	 */
	static boolean currentlyPrinting = false;

	/**
	 * Topics that should be debugged. A topic is an Object that identifies an
	 * area of interest, e.g. a String "paint" for debugging painting code only.
	 */
	private Set<Object> debugTopics = new HashSet<Object>();

	public Set<Object> getDebugTopics() {
		return debugTopics;
	}

	public void setDebugTopics(Set<Object> debugTopics) {
		this.debugTopics = debugTopics;
	}

	public static void addDebugTopic(Object... debugTopics) {
		StackTraceElement elem = getCaller(2);
		String topicList = "";
		for (Object topic : debugTopics) {
			err.debugTopics.add(topic);
			topicList += topic + ", ";
		}
		err.outputDebugInfo(elem, "Adding debug topics: " + topicList);
	}

	public static void removeDebugTopic(Object... debugTopics) {
		for (Object topic : debugTopics)
			err.debugTopics.remove(topic);
	}

	// TODO this needs to be documented
	private Map<String, Object> debugTags = new HashMap<String, Object>();

	public Map<String, Object> getDebugTags() {
		return debugTags;
	}

	public void setDebugTags(Map<String, Object> debugTags) {
		this.debugTags = debugTags;
	}

	public Object addTag(String key, Object value) {
		StackTraceElement elem = getCaller();
		if (isDebugging(elem))
			return getDebugTags().put(key, value);
		return null;
	}

	public void setOutput(PrintStream output) {
		this.output = output;
	}

	public PrintStream getOutput() {
		return output;
	}

	// TODO make all methods static

	/**
	 * Prints the given message (by using toString() on the object) onto the
	 * debug stream if given topic or the current class is registered for
	 * debugging.
	 * 
	 * @param message
	 *            the debugging message to print
	 * @param debugTopics
	 *            the topics that this comment is related to
	 */
	public static void println(Object message, Object... debugTopics) {

		if (!isDebugging(3, debugTopics) || currentlyPrinting)
			return;

		// set flag to avoid infinite recursion through toString()
		currentlyPrinting = true;
		
		// TODO: this method looks now very clean, but two calls to
		// getCaller have to be performed; one was performed within
		// isDebugging()
		// consider optimizing this.
		StackTraceElement elem = getCaller(2);
		err.outputDebugInfo(elem, message.toString());
		
		currentlyPrinting = false;
	}

	/**
	 * Prints the given message (by using toString() on each message object and
	 * concatenating the results) onto the debug stream if given topic or the
	 * current class is registered for debugging.
	 * 
	 * toString() on the message objects is evaluated lazily, i.e. only if the
	 * message is printed. Therefore this version of println can save
	 * computation time if there are deactivated println's with expensive
	 * toString()'s.
	 * 
	 * @param message
	 *            the objects making up the debugging message
	 * @param debugTopics
	 *            the topics that this comment is related to
	 */
	public static void println(Object[] message, Object... debugTopics) {
		if (!isDebugging(3, debugTopics) || currentlyPrinting)
			return;

		// set flag to avoid infinite recursion through toString()
		currentlyPrinting = true;
		
		outputObjectArray(message);
		
		currentlyPrinting = false;
	}

	public static void printCallStack(int depth, Object... debugTopics) {
		// check if debugging is enabled
		if (!isDebugging(3, debugTopics) || currentlyPrinting)
			return;

		StackTraceElement elem = getCaller(2);
		err.outputDebugInfo(elem, "Call stack");
		for (int i = 0; i < depth; i++)
			err.output.println("  " + i + " " + getCaller(i + 2));
	}

	/**
	 * Indicates if debugging is active. This is the case if a) the global debug
	 * flag is set, or b) if the caller class is registered for debugging, or c)
	 * if one of the given topics is registered for debugging.
	 * 
	 * @param debugTopics
	 *            the debugging topics that should be tested
	 * @return true iff debugging is active for the given conditions.
	 */
	public static boolean isDebugging(Object... debugTopics) {
		return isDebugging(4, debugTopics);
	}

	public static boolean isDebugging(int depth, Object... debugTopics) {
		return err.isGlobalDebug() || isDebuggingTopic(debugTopics);
	}

	private static boolean isDebuggingTopic(Object... debugTopics) {
		boolean debuggingTopic = false;
		for (Object topic : debugTopics) {
			if (err.debugTopics.contains(topic)) {
				debuggingTopic = true;
				break;
			}
		}
		return debuggingTopic;
	}

	public StackTraceElement getCaller() {
		return getCaller(3);
	}

	/**
	 * Gets the StackTraceElement for the ith caller. The 0th caller is the
	 * getCaller() method itself.
	 * 
	 * @param i
	 *            the number of the element on the call stack, counting from the
	 *            top
	 * @return the StackTraceElement found on the call stack
	 */
	public static StackTraceElement getCaller(int i) {
		StackTraceElement elem;
		// get the caller information
		StackTraceElement[] a = Thread.currentThread().getStackTrace();
		if (a.length == 0) {
			// virtual machine does probably not support stack trace
			elem = new StackTraceElement("notSupported", "-", "-", 99999);
		} else
			elem = a[i+1];
		return elem;
	}

	/**
	 * This method encapsulates the style and target of the debug output. It is
	 * advisable that there is always output onto the PrintStream "output". In
	 * the current implementation that is the only thing it does.
	 * 
	 * @param elem
	 * @param comment
	 */
	protected void outputDebugInfo(StackTraceElement elem, String comment) {

		err.output.println(comment + " at " + elem.toString());
	}

	public void setGlobalDebug(boolean globalDebug) {
		this.globalDebug = globalDebug;
	}

	public boolean isGlobalDebug() {
		return globalDebug;
	}

	public static void assertTrue(boolean condition, Object... message) {
		if (!condition)
			throw new RuntimeException(objectArrayToString(message));
	}

	public static void warningAssertTrue(boolean condition, Object... message) {
		if (!condition) {
			outputObjectArray(message);
		}
	}

	private static void outputObjectArray(Object... message) {
		String messageString = objectArrayToString(message);

		StackTraceElement elem = getCaller(2);
		err.outputDebugInfo(elem, messageString);
	}

	private static String objectArrayToString(Object... message) {
		String messageString = "";
		for (Object messagePart : message)
			messageString += messagePart.toString();
		return messageString;
	}
}
