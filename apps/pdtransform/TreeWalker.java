package pdtransform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import pdstore.GUID;
import pdstore.dal.PDInstance;
import pdstore.dal.PDRole;
import pdstore.dal.PDType;
import pdstore.dal.PDWorkingCopy;
import pdtransform.dal.PDGeneratorApplication;
import pdtransform.dal.PDOrder;
import pdtransform.dal.PDOrderedPair;
import pdtransform.dal.PDPrintInstruction;
import pdtransform.dal.PDSerializer;
import pdtransform.dal.PDSerializerApplication;

/**
 * Methods used by transform.java and serialize.java to traverse through the models
 *  
 * @author Philip Booth (pboo015)
 */

public class TreeWalker {
	
	/**	A stack which keeps track of the current position in the generator application output branch */
	Stack<PDInstance> gaOutputCursor = new Stack<PDInstance>();	
	/**	A stack which keeps track of the current position in the generator application input branch */
	Stack<PDInstance> gaInputCursor = new Stack<PDInstance>();
	/**	(TESTING PURPOSES) Used for indenting the printed output */
	String indent = "";
	ArrayList<PDInstance> processed = new ArrayList<PDInstance>();
	
	public TreeWalker()
	{
		
	}
	
	/**
	 * When provided with a cursor it gets the PDInstance that the cursor is currently pointing to and returns
	 * the PDInstances of the children of that instance. 
	 * 
	 * @param cursor - Stack which points to the instance that we want to get the children of.
	 * @param cache - PDCache instance associated with this Generator Application
	 * 
	 * @return an ArrayList of PDInstances of the children
	 */
	public ArrayList<Object> getChildren(PDInstance element, PDWorkingCopy cache)
	{
		PDType elementType = (PDType)cache.load(PDType.typeId, element.getTypeId());
		Collection<PDRole> roles = elementType.getAccessibleRoles();

		ArrayList<Object> childrenArray = new ArrayList<Object>();

		if (roles != null)
		{
			for (PDRole role : roles)
			{
				if (role.getName() != null)
				{
					// Set<Object> children = element.getInstances(role.getId());
					Collection<Object> children = cache.getInstances(element, role.getId());
					for (Object child : children)
					{
						if (child != null)
						{
							childrenArray.add(child);
						}
					}
				}
			}
		}
		return childrenArray;
	}

	/**
	 * Used in conjunction with getChildren. Like getChildren it gets the PDInstance that the specified cursor
	 * is pointing to, however, instead of returning the children this method returns an arrayList of roles that
	 * match up to the arrayList of children from getChildren. Used for passing a role to cloneElement.
	 * 
	 * @param cursor - Stack which points to the instance that we want to get the roles of.
	 * @param cache - PDCache instance associated with this Generator Application
	 * 
	 * @return an ArrayList of PDRoles
	 */
	public ArrayList<PDRole> getRoles(PDInstance element, PDWorkingCopy cache)
	{ 
		PDType elementType = (PDType)cache.load(PDType.typeId, element.getTypeId());
		Collection<PDRole> roles = elementType.getAccessibleRoles();

		ArrayList<PDRole> roleArray = new ArrayList<PDRole>();

		if (roles != null)
		{
			for (PDRole role : roles)
			{
				if (role.getName() != null)
				{
					roleArray.add(role);
				}
			}
		}
		return roleArray;
	}

	/**
	 * From the provided starting element, traverses the generator application output tree and prints the node names.
	 * Currently just used for testing. 
	 * 
	 * @param element - starting element
	 * @param cache - PDCache instance associated with this Generator Application
	 * @param ga - PDGeneratorApplication instance which PDTransform is being run on 
	 */
	public void print_output(PDInstance element, PDWorkingCopy cache, PDGeneratorApplication ga)
	{
		System.out.println( indent + element.getName());
		gaOutputCursor.push(element);
		indent += "\t";
		
		ArrayList<Object> children = getChildren(element, cache);
		//System.out.println(children);
		for (Object child : children)
		{
			if (child instanceof PDInstance)
				print_output((PDInstance)child, cache, ga);	
			else
				System.out.println(indent + child);
				
		}

		gaOutputCursor.pop();
		indent = indent.substring(0, indent.length() - 1);
	}
		
	public void serialize(PDSerializerApplication sa, PDWorkingCopy cache)
	{
		GUID input = sa.getInput();
		PDType inputType = sa.getInputType();
		
		PDInstance startElement = cache.load(inputType.getId(), input);
		indent = "";
		process_serializer(startElement, sa, cache);
		
	}
	
	public void process_serializer(PDInstance element, PDSerializerApplication sa, PDWorkingCopy cache)
	{
		PDSerializer serializer = sa.getSerializer();
		Collection<PDPrintInstruction> instructions = serializer.getInstructions();
		
		PDPrintInstruction instruction = null;
		
		for (PDPrintInstruction i : instructions)
		{
			if (i.getType().getId().equals(element.getTypeId()))
			{
				instruction = i;
			}
		}
		
		if (instruction != null)
		{
			System.out.println(indent + instruction.getPrintBefore());
		}
		
		gaOutputCursor.push(element);
		indent += "\t";

		ArrayList<Object> children = new ArrayList<Object>();
		Set<PDRole> useableRoles = new HashSet<PDRole>();
		
		PDType elementType = (PDType)cache.load(PDType.typeId, element.getTypeId());
		Collection<PDRole> roles = elementType.getAccessibleRoles();
		PDOrder order = sa.getOrder();
		
		// Get rid of roles without a name
		for (PDRole role : roles)
		{
			if (role.getName() != null)
				useableRoles.add(role);
		}
		
		if (useableRoles.size() > 1)
			children = getOrderedChildren(useableRoles, element, order);
		else
			children = getChildren(element, cache);
		
		for (Object child : children)
		{
			if (child instanceof PDInstance)
				process_serializer((PDInstance)child, sa, cache);
			else
				System.out.println(indent.substring(0, indent.length() - 1) + child);
		}

		if (instruction != null)
		{
			System.out.println(indent.substring(0, indent.length() - 1) + instruction.getPrintAfter());
		}
		
		gaOutputCursor.pop();
		indent = indent.substring(0, indent.length() - 1);
	}
	
	public ArrayList<Object> getOrderedChildren(Collection<PDRole> roles, PDInstance element, PDOrder order)
	{
		ArrayList<Object> orderedChildren = new ArrayList<Object>();
		ArrayList<Object> unorderedChildren = new ArrayList<Object>();
		//PDOrder order = ga.getGenerator().getOrder();

		for (PDRole role : roles)
		{
			PDWorkingCopy cache = element.getPDWorkingCopy();
			unorderedChildren.addAll(cache.getInstances(element, role.getId()));
		}

		Collection<PDOrderedPair> orderedPairs = order.getOrderedPairss();
		ArrayList<Object> tempList = new ArrayList<Object>();

		for (PDOrderedPair orderedPair : orderedPairs)
		{
			tempList.clear();
			tempList.addAll(unorderedChildren);
			
			GUID prev = orderedPair.getPrev();
			GUID next = orderedPair.getNext();

			innerFor:
				for (Object c : unorderedChildren)
				{
					if (c instanceof PDInstance && tempList.contains(c))
					{
						PDInstance child = (PDInstance)c;
						if (prev.equals(child.getTypeId()))
						{
							for (Object c2 : unorderedChildren)
							{
								if (c2 instanceof PDInstance && tempList.contains(c2))
								{
									PDInstance child2 = (PDInstance)c2;
									if (next.equals(child2.getTypeId()))
									{
										// If prev is in ordered list
										if (orderedChildren.contains(child) && !orderedChildren.contains(child2))
										{
											orderedChildren.add(orderedChildren.indexOf(child) + 1, child2);
											tempList.remove(child2);
										}

										// If next is in ordered list
										else if (orderedChildren.contains(child2) && !orderedChildren.contains(child))
										{
											if (orderedChildren.indexOf(child2) == 0)
												orderedChildren.add(0, child);
											else
												orderedChildren.add(orderedChildren.indexOf(child2) - 1, child);
											tempList.remove(child);
										}
										// If neither in ordered list
										else if (!orderedChildren.contains(child) && !orderedChildren.contains(child2))
										{
											orderedChildren.add(child);
											orderedChildren.add(child2);
											tempList.remove(child);
											tempList.remove(child2);
										}
										continue innerFor;
									}
								}
							}
						}
					}
				}
		}
		
		for (Object i : unorderedChildren)
		{
			if (!orderedChildren.contains(i))
				orderedChildren.add(i);
		}
		
		return orderedChildren;
	}
}
