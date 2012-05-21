package pdstore;

import java.util.ArrayList;
import java.util.List;

import nz.ac.auckland.se.genoupe.tools.Debug;

import pdstore.*;
import pdstore.generic.GlobalTypeAdapter;
import pdstore.generic.PDChange;
import pdstore.generic.Pairable;
import pdstore.generic.TypeAdapter;

/**
 * Represents the write operations of a transaction.
 * 
 * It is a stateful object. Two important states are: 
 * - 1, transaction open, detectable by durableId = null;
 * - 2, transaction committed, detectable by durableId != null
 * 
 * State transition should only be from 1 to 2.
 * The transition happens in setDurableId();
 * This also rewrites the transactions in the change objects.
 * ensureDurableIdIsSet() is idempotent and makes sure the state is afterwards in 2.
 *
 * 
 * @author Gerald, Christof
 * 
 */
public class Transaction<TransactionType extends Comparable<TransactionType>, InstanceType, RoleType extends Pairable<RoleType>>
		extends ArrayList<PDChange<TransactionType, InstanceType, RoleType>> {
	
	/**
	 * The adaptor object that encapsulates all operations on generic arguments.
	 */
	@SuppressWarnings("unchecked")
	public final TypeAdapter<TransactionType, InstanceType, RoleType> typeAdapter = (TypeAdapter<TransactionType, InstanceType, RoleType>) GlobalTypeAdapter.typeAdapter;


	private static final long serialVersionUID = 1924378213047995899L;

	private TransactionType id;
	private TransactionType beginId;
	private TransactionType durableId = null;
	private IsolationLevel isolationLevel = IsolationLevel.SNAPSHOT;
	private PersistenceLevel persistenceLevel = PersistenceLevel.LOG_AND_INDEX;
	
	
	public Transaction(TransactionType id) {
		this.id = id;
		this.beginId = id;
	}

	public TransactionType getId() {
		return id;
	}

	public void setId(TransactionType id) {
		this.id = id;
	}

	public TransactionType getBeginId() {
		return beginId;
	}

	public void setBeginId(TransactionType openId) {
		this.beginId = openId;
	}

	public TransactionType getDurableId() {
		return durableId;
	}

	public void setDurableId(TransactionType durableId) {
		this.durableId = durableId;
		for (PDChange<TransactionType, InstanceType, RoleType> change : this) {
			change.setTransaction(durableId);
		}
	}
	
	/**
	 *  ensureDurableIdIsSet() is idempotent and makes sure the state is afterwards in 2.
	 */
	public void ensureDurableIdIsSet() {
		if(this.durableId!=null) return;
		TransactionType branchId = typeAdapter.getBranchID(getId());
		setDurableId(typeAdapter.getDurableID(branchId));
	}



	public IsolationLevel getIsolationLevel() {
		return isolationLevel;
	}

	public void setIsolationLevel(IsolationLevel isolationLevel) {
		this.isolationLevel = isolationLevel;
	}

	public PersistenceLevel getPersistenceLevel() {
		return persistenceLevel;
	}

	public void setPersistenceLevel(PersistenceLevel persistenceLevel) {
		this.persistenceLevel = persistenceLevel;
	}

}