package pdstore.generic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import pdstore.GUID;
import pdstore.dal.PDInstance;
import pdstore.dal.PDWorkingCopy;



public class  GenericLinkedList<TransactionType, InstanceType, RoleType extends Pairable<RoleType>, E extends InstanceType> implements Serializable, Iterable<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Class<E> javaType;
	private PDWorkingCopy workingCopy;
	private PDInstance parentInstance;
	private RoleType collectionRole;
	private RoleType nextRole;
	private GUID element;

	/**
	 * Constructor
	 * @param javaType, the class type of the instances in the linked list
	 * @param parentInstance, the PDinstance that stores the elements of the linked list
	 * @param collectionRole, the role type that exists between the PDInstance and each element in the list
	 * @param element, the GUID of the elements in the list
	 * @param nextRole, the role type that exists between elements in the list
	 */
	public GenericLinkedList(Class<E> javaType, PDInstance parentInstance, RoleType collectionRole, GUID element, RoleType nextRole){
		this.javaType = javaType;
		this.parentInstance = parentInstance;
		this.collectionRole = collectionRole;
		this.element = element;
		this.nextRole = nextRole;
		this.workingCopy = parentInstance.getPDWorkingCopy();
	}

	/**
	 * Method to add an element to the end of the list.
	 * @param element, the element to add to the list
	 */
	public  void add(E element){
		//TODO find a way to handle exceptions
		add(size(), element);	
	}

	/**
	 * Method to add an element at a given index in the list.
	 * @param index, the position to add the element at
	 * @param element, the element to add to the list
	 * @throws IndexOutOfBoundsException if the index is not
	 * contained in the current list
	 */
	public  void add(int index, E element){
		List<E> sorted = sortList();
		int size = sorted.size();

		if(contains(element) || element == null){
			throw new IndexOutOfBoundsException();
			//TODO: change exception type
		}

		if(index > size+1 || index < 0){
			throw new IndexOutOfBoundsException();
		}
		//link element to history
		workingCopy.addLink(parentInstance.getId(), (GUID)collectionRole, element);

		if (size == 0){
			//first instance in list, nothing else in list to link too
			;	//do nothing else
		}else if(index == size){
			//add to end of list
			workingCopy.addLink(((PDInstance)sorted.get(size -1)).getId(), (GUID)nextRole, element);
		}else if(index == 0 && size != 0){
			//add to front of the list
			workingCopy.addLink(((PDInstance)element).getId(), (GUID)nextRole, sorted.get(0));
		}else{
			//add to middle of the list
			E before = sorted.get(index - 1);
			E after = sorted.get(index);
			workingCopy.removeLink(((PDInstance)before).getId(), (GUID)nextRole, after);
			workingCopy.addLink(((PDInstance)before).getId(), (GUID)nextRole, element);
			workingCopy.addLink(((PDInstance)element).getId(), (GUID)nextRole, after);
		}
		workingCopy.commit();
	}

	/**
	 * Method to determine if a the generic linked list contains 
	 * a given element.
	 * @param element, the element to search for
	 * @return true if the list contains the element, false otherwise
	 */
	public  boolean contains(E element){
		List<E> sorted = sortList();
		for(E object : sorted){
			if(((PDInstance) object).getId() == ((PDInstance)element).getId()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Method to determine if a list contains a given element
	 * @param element, the element to search for
	 * @param list, the list to search the element for
	 * @return true if the given list contains the element,
	 * false otherwise
	 */
	public  boolean contains(List<E> list, E element){
		for(E object : list){
			if(((PDInstance) object).getId() == ((PDInstance)element).getId()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to get an element at a given index.
	 * @param index, the position of the element
	 * @return the element at the given index
	 */
	public  E get(int index){
		return sortList().get(index);
	}

	public int indexOf(E element) {
		List<E> sorted = sortList();
		for(int i = 0; i < sorted.size(); i++){
			if( ((PDInstance)sorted.get(i)).getId() == ((PDInstance)element).getId()){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Method to check if the list contains no elements
	 * @return true if the list is empty, false otherwise
	 */
	public boolean isEmpty(){
		return sortList().isEmpty();
	}

	/**
	 * Method to remove an element form the list at a given index
	 * @param index, the position of the element to remove
	 * @throws IndexOutOfBoundsException
	 */
	public synchronized void remove(int index){
		List<E> sorted = sortList();
		int size = sorted.size();

		if(index >= size || index < 0){
			throw new IndexOutOfBoundsException("invalid index, can not perform remove");
		}
		E element = sorted.get(index);

		// remove link to history
		workingCopy.removeLink(parentInstance.getId(), (GUID) collectionRole, element);

		if (index > 0) {
			// remove backwards pointing link
			workingCopy.removeLink(((PDInstance) element).getId(),
					(GUID) nextRole.getPartner(), sorted.get(index - 1));
		}
		if (index < size - 1) {
			// remove front pointing link
			workingCopy.removeLink(((PDInstance) element).getId(),
					(GUID) nextRole, sorted.get(index + 1));
		}
		if (index != 0 && index != size - 1) {
			// join up front and back elements
			workingCopy.addLink(((PDInstance) sorted.get(index - 1)).getId(),
					(GUID) nextRole, sorted.get(index + 1));
		}
		//TODO temporarily removed commit to get DiagramEditor to work
		// collaboratively between different computers.
		// workingCopy.commit();
	}

	/**
	 * Method to remove a given element from the list.
	 * @param element, the element to remove
	 * @throws IndexOutOfBoundsException
	 */
	public synchronized void remove(E element){
		List<E> sorted = sortList();
		for(int i = 0; i < sorted.size(); i++){
			if( ((PDInstance)sorted.get(i)).getId().equals(((PDInstance)element).getId())){
				remove(i);
				return;
			}
		}
		throw new IndexOutOfBoundsException("Element is not contained in the list, cannot remove");
	}

	/**
	 * Method to return the number of elements in the list
	 * @return an int representing the number of elements in the list
	 */
	public synchronized int size(){
		return sortList().size();
	}

	/**
	 * Method to sort the elements of the list into order based on their roles
	 * @return a list of elements
	 */
	@SuppressWarnings("unchecked")	
	private List<E> sortList(){
		Collection<E> c =  new HashSet<E>();
		workingCopy.getInstances(parentInstance, (GUID)collectionRole, javaType, element, c);
		List<E> sorted = new ArrayList<E>();
		if (c.size() != 0) {
			E element = c.iterator().next();
			sorted.add(element);

			PDWorkingCopy workingCopy = ((PDInstance) element)
			.getPDWorkingCopy();

			// Sort forwards from first InstanceType.
			E next;
			while ((next = (E) workingCopy.getInstance(
					(PDInstance) sorted.get(sorted.size() - 1),
					(GUID) this.nextRole)) != null) {
				sorted.add(next);
			}

			// Sort backwards from first InstanceType.
			RoleType previousRole = this.nextRole.getPartner();
			E previous;
			while ((previous = (E) workingCopy.getInstance(
					(PDInstance) sorted.get(0), (GUID) previousRole)) != null) {
				sorted.add(0, previous);
			}
		}
		return sorted;
	}

	/**
	 * Iterator method
	 * @return a new iterator for the list
	 */
	@Override
	public Iterator<E> iterator() {
		return new GenericLinkListIterator(sortList());
	}

	/**
	 * Iterator class for the linked list.
	 *
	 */
	class GenericLinkListIterator implements Iterator<E>{

		private Iterator<E> state;
		private boolean hasNext = false;
		private E nextInstance = null;

		/**
		 * Constructor for the iterator
		 * @param sorted, a list of ordered elements
		 */
		GenericLinkListIterator(List<E> sorted){
			this.state = sorted.iterator();
			calcNext();
		}

		@Override
		public boolean hasNext() {
			return this.hasNext;
		}

		@Override
		public E next() {
			E ret = nextInstance;
			calcNext();
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

		private void calcNext(){
			this.hasNext = false;
			while(!hasNext && state.hasNext()){
				this.nextInstance = state.next();
				this.hasNext = true;
			}
		}

	}
}
