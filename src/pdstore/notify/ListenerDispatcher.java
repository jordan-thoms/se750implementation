package pdstore.notify;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pdstore.*;
import pdstore.generic.PDChange;
import pdstore.generic.PDCoreI;
import pdstore.generic.Pairable;

/**
 * This dispatcher is itself a listener, but is used to add more specific
 * listeners to PDStore. ListenerDispatchers with special functionality are
 * provided by PDStore. Most everyday usages of PDStore's notification
 * mechanisms will employ objects of this class.
 * 
 * 
 * You can define a change template and use it as a trigger criterion for a
 * listener.
 * 
 * All you need is to create a listener class that implements
 * pdstore.generic.PDListener<GUID, Object, GUID> which has a
 * transactionCommitted method that is called on notification. See, for example,
 * test/pdstore.TestListener There is JavaDoc in the PDListener interface.
 * 
 * Ok, now you have a listener object myListener of your listener class. Let's
 * say you want to listen to any change that refers to a roleX. What you do is:
 * 
 * store.getListenerDispatcher().addListener(myListener, new PDChange<GUID,
 * Object, GUID>(null, null, null, roleX, null));
 * 
 * This looks more complicated than it is. The PDChange object is the change
 * template that tells the listener dispatcher when to call your myListener.
 * "null" is the wildcard, i.e. for the template change above all change
 * properties don't matter except the roleX.
 * 
 * For example, if you wanted to install an event listener myDeleteListener that
 * deletes a node in your tree for a link (instanceA, roleX, instanceB), then
 * you would install this listener like this:
 * 
 * store.getListenerDispatcher().addListener(myDeleteListener, new
 * PDChange<GUID, Object, GUID>(ChangeType.LINK_REMOVED, null, instanceA, roleX,
 * instanceB));
 * 
 * The implementation of activating specific listeners is efficient, i.e. it
 * uses an index.
 * 
 * 
 * @author clut002
 * @author gweb017
 * 
 * @param <TransactionType>
 * @param <InstanceType>
 * @param <RoleType>
 */
public class ListenerDispatcher<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
    extends PDListenerAdapter<TransactionType, InstanceType, RoleType> {

	public ListenerDispatcher() {
		super();
	}

	private Map<RoleType, Map<InstanceType, Map<InstanceType, Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>>>>> listenerIndex = 
			new HashMap<RoleType, Map<InstanceType, Map<InstanceType, Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>>>>>();

	/**
	 * 	 This method is used to add specific listeners to PDStore. 
	 *   
	 * 
	 * @param newListener
	 * @param changeTemplate For a detailed description of the changeTemplate see the class comment.
	 */
	public void addListener(
			PDListener<TransactionType, InstanceType, RoleType> newListener,
			PDChange<TransactionType, InstanceType, RoleType> changeTemplate) {

		newListener.getMatchingTemplates().add(changeTemplate);
		addOnlyToIndex(newListener, changeTemplate);
	}

	private void addOnlyToIndex(
			PDListener<TransactionType, InstanceType, RoleType> newListener,
			PDChange<TransactionType, InstanceType, RoleType> changeTemplate) {
		Map<InstanceType, Map<InstanceType, Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>>>> iicIndex;
		if (listenerIndex.containsKey(changeTemplate.getRole2())) {
			iicIndex = listenerIndex.get(changeTemplate.getRole2());
		} else {
			iicIndex = new HashMap<InstanceType, Map<InstanceType, Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>>>>();
			listenerIndex.put(changeTemplate.getRole2(), iicIndex);
		}

		Map<InstanceType, Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>>> icIndex;
		if (iicIndex.containsKey(changeTemplate.getInstance1())) {
			icIndex = iicIndex.get(changeTemplate.getInstance1());
		} else {
			icIndex = new HashMap<InstanceType, Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>>>();
			iicIndex.put(changeTemplate.getInstance1(), icIndex);
		}

		Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>> cIndex;
		if (icIndex.containsKey(changeTemplate.getInstance2())) {
			cIndex = icIndex.get(changeTemplate.getInstance2());
		} else {
			cIndex = new HashMap<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>>();
			icIndex.put(changeTemplate.getInstance2(), cIndex);
		}

		Set<PDListener<TransactionType, InstanceType, RoleType>> listeners;
		if (cIndex.containsKey(changeTemplate.getChangeType())) {
			listeners = cIndex.get(changeTemplate.getChangeType());
		} else {
			listeners = new HashSet<PDListener<TransactionType, InstanceType, RoleType>>();
			cIndex.put(changeTemplate.getChangeType(), listeners);
		}

		listeners.add(newListener);
	}

	public void removeListener(
			PDListener<TransactionType, InstanceType, RoleType> listener,
			PDChange<TransactionType, InstanceType, RoleType> changeTemplate) {

		Map<InstanceType, Map<InstanceType, Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>>>> iicIndex;
		if (listenerIndex.containsKey(changeTemplate.getRole2())) {
			iicIndex = listenerIndex.get(changeTemplate.getRole2());
		} else 
			return;

		Map<InstanceType, Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>>> icIndex;
		if (iicIndex.containsKey(changeTemplate.getInstance1())) {
			icIndex = iicIndex.get(changeTemplate.getInstance1());
		} else 
			return;
		
		Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>> cIndex;
		if (icIndex.containsKey(changeTemplate.getInstance2())) {
			cIndex = icIndex.get(changeTemplate.getInstance2());
		} else 
			return;

		Set<PDListener<TransactionType, InstanceType, RoleType>> listeners;
		if (cIndex.containsKey(changeTemplate.getChangeType())) {
			listeners = cIndex.get(changeTemplate.getChangeType());
		} else 
			return;

		listeners.remove(listener);
	}

	public Set<PDListener<TransactionType, InstanceType, RoleType>> getListeners(
			PDChange<TransactionType, InstanceType, RoleType> change) {

		Set<PDListener<TransactionType, InstanceType, RoleType>> listeners = new HashSet<PDListener<TransactionType, InstanceType, RoleType>>();
		addListenersInListenerIndex(change, listeners);
		return listeners;
	}

	void addListenersInListenerIndex(
			PDChange<TransactionType, InstanceType, RoleType> change,
			Set<PDListener<TransactionType, InstanceType, RoleType>> listeners) {

		if (listenerIndex.containsKey(change.getRole2()))
			addListenersInIICIndex(listenerIndex.get(change.getRole2()),
					change, listeners);

		if (listenerIndex.containsKey(null))
			addListenersInIICIndex(listenerIndex.get(null), change, listeners);
	}

	void addListenersInIICIndex(
			Map<InstanceType, Map<InstanceType, Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>>>> iicIndex,
			PDChange<TransactionType, InstanceType, RoleType> change,
			Set<PDListener<TransactionType, InstanceType, RoleType>> listeners) {

		if (iicIndex.containsKey(change.getInstance1()))
			addListenersInICIndex(iicIndex.get(change.getInstance1()), change,
					listeners);

		if (iicIndex.containsKey(null))
			addListenersInICIndex(iicIndex.get(null), change, listeners);
	}

	void addListenersInICIndex(
			Map<InstanceType, Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>>> icIndex,
			PDChange<TransactionType, InstanceType, RoleType> change,
			Set<PDListener<TransactionType, InstanceType, RoleType>> listeners) {

		if (icIndex.containsKey(change.getInstance2()))
			addListenersInCIndex(icIndex.get(change.getInstance2()), change,
					listeners);

		if (icIndex.containsKey(null))
			addListenersInCIndex(icIndex.get(null), change, listeners);
	}

	void addListenersInCIndex(
			Map<ChangeType, Set<PDListener<TransactionType, InstanceType, RoleType>>> cIndex,
			PDChange<TransactionType, InstanceType, RoleType> change,
			Set<PDListener<TransactionType, InstanceType, RoleType>> listeners) {

		if (cIndex.containsKey(change.getChangeType()))
			listeners.addAll(cIndex.get(change.getChangeType()));

		if (cIndex.containsKey(null))
			listeners.addAll(cIndex.get(null));
	}

	public void transactionCommitted(
			List<PDChange<TransactionType, InstanceType, RoleType>> transaction,
			List<PDChange<TransactionType, InstanceType, RoleType>> matchedChanges, PDCoreI<TransactionType, InstanceType, RoleType> core) {
		// TODO: make Hashmap <PDListener, List<PDChange>>
		HashSet<PDListener<TransactionType, InstanceType, RoleType>> listToCall = new HashSet<PDListener<TransactionType, InstanceType, RoleType>>();
		if(matchedChanges!=null)
		for (PDChange<TransactionType, InstanceType, RoleType> change : matchedChanges) {
			listToCall.addAll(getListeners(change));
		}
		for (PDListener<TransactionType, InstanceType, RoleType> listener : listToCall) {
			// TODO: replace matchedChanges with more specific results.
			listener.transactionCommitted(transaction, matchedChanges, core);
		}
	}

	public boolean add(PDListener<TransactionType, InstanceType, RoleType> listener) {
		for(PDChange<TransactionType, InstanceType, RoleType> changeTemplate :listener.getMatchingTemplates()){
			addOnlyToIndex(listener, changeTemplate);
		}
		return true;
	}

	public boolean remove(Object arg0) {
		return false;
	}

}
