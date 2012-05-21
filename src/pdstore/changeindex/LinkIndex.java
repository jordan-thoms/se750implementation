package pdstore.changeindex;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pdstore.generic.PDChange;
import pdstore.generic.Pairable;

public class LinkIndex<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		implements
		Map<PDChange<TransactionType, InstanceType, RoleType>, IndexEntry<TransactionType, InstanceType, RoleType>> {

	Map<Integer, IndexEntry<TransactionType, InstanceType, RoleType>> map = new HashMap<Integer, IndexEntry<TransactionType, InstanceType, RoleType>>();

	private IndexEntry<TransactionType, InstanceType, RoleType> emptyResult = new IndexEntry<TransactionType, InstanceType, RoleType>();

	/**
	 * Returns the IndexEntry associated with the given change, or null if there
	 * is no IndexEntry.
	 */
	@Override
	public IndexEntry<TransactionType, InstanceType, RoleType> get(Object key) {
		return map
				.get(linkHash((PDChange<TransactionType, InstanceType, RoleType>) key));
	}

	public boolean add(PDChange<TransactionType, InstanceType, RoleType> change) {
		IndexEntry<TransactionType, InstanceType, RoleType> entry = get(change);
		if (entry == null) {
			entry = new IndexEntry<TransactionType, InstanceType, RoleType>();
			put(change, entry);
		}
		return entry.add(change);
	}

	@Override
	public IndexEntry<TransactionType, InstanceType, RoleType> put(
			PDChange<TransactionType, InstanceType, RoleType> key,
			IndexEntry<TransactionType, InstanceType, RoleType> value) {
		return map
				.put(linkHash((PDChange<TransactionType, InstanceType, RoleType>) key),
						value);
	}

	public int linkHash(PDChange<TransactionType, InstanceType, RoleType> change) {
		// only hashing for instances
		return change.getInstance1().hashCode()
				* change.getInstance2().hashCode();
	}

	/**
	 * Returns the IndexEntry associated with the given change, or an empty
	 * IndexEntry if there is no IndexEntry.
	 */
	public IndexEntry<TransactionType, InstanceType, RoleType> getNonEmpty(
			Object key) {
		IndexEntry<TransactionType, InstanceType, RoleType> result = this
				.get(key);
		if (result == null)
			return emptyResult;
		return result;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<java.util.Map.Entry<PDChange<TransactionType, InstanceType, RoleType>, IndexEntry<TransactionType, InstanceType, RoleType>>> entrySet() {
		throw new UnsupportedOperationException("not intended to be used");
	}

	@Override
	public Set<PDChange<TransactionType, InstanceType, RoleType>> keySet() {
		throw new UnsupportedOperationException("not intended to be used");
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<IndexEntry<TransactionType, InstanceType, RoleType>> values() {
		return map.values();
	}


	@Override
	public void putAll(
			Map<? extends PDChange<TransactionType, InstanceType, RoleType>, ? extends IndexEntry<TransactionType, InstanceType, RoleType>> m) {
		throw new UnsupportedOperationException("not intended to be used");

	}

	@Override
	public IndexEntry<TransactionType, InstanceType, RoleType> remove(Object key) {
		throw new UnsupportedOperationException("not intended to be used");
	}

	@Override
	public boolean containsKey(Object key) {
		throw new UnsupportedOperationException("not intended to be used");
	}
}
