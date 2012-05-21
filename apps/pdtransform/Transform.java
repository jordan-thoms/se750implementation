package pdtransform;

import java.util.ArrayList;
import java.util.Collection;

import pdstore.GUID;
import pdstore.dal.PDInstance;
import pdstore.dal.PDRole;
import pdstore.dal.PDType;
import pdstore.dal.PDWorkingCopy;
import pdtransform.dal.PDGenerator;
import pdtransform.dal.PDGeneratorApplication;
import pdtransform.dal.PDMap;

/**
 * Transforms a input model using a generator into the output model
 * 
 * @author Philip Booth (pboo015)
 */

public class Transform {

	public Transform(PDWorkingCopy cache, PDGeneratorApplication ga) {
		// cache = ga.getPDCache();
		GUID input = ga.getInput();
		PDGenerator gen = ga.getGenerator();

		GUID output_template = gen.getOutputTemplate();

		PDType inputType = gen.getInputType();
		PDType outputType = gen.getOutputType();

		PDInstance startElement = cache.load(inputType.getId(), input);
		PDInstance element = cache.load(outputType.getId(), output_template);

		TreeWalker walker = new TreeWalker();
		walker.gaInputCursor.push(startElement);

		traverse_output(element, null, cache, ga, walker);

	}

	/**
	 * Main recursive method, traverses through the output template cloning the
	 * element then checking if it has an associated mapping, executing that
	 * mapping if it does and then moves to the children by calling this method
	 * on them. This approach results in a depth first recursion. <br>
	 * <br>
	 * 
	 * @param element
	 *            - PDInstance of the output template instance to be processed
	 * @param parentRole
	 *            - Relation the instance "element" is to its parent, this is
	 *            used in the cloning process
	 * @param cache
	 *            - PDCache instance associated with this Generator Application
	 * @param ga
	 *            - PDGeneratorApplication instance which PDTransform is being
	 *            run on
	 */
	private void traverse_output(PDInstance element, PDRole parentRole,
			PDWorkingCopy cache, PDGeneratorApplication ga, TreeWalker walker) {
		cloneElement(element, parentRole, cache, ga, walker);
		ArrayList<PDInstance> mappedChildren = checkMapping(element, ga, walker);

		ArrayList<PDRole> roles = walker.getRoles(element, cache);

		for (PDRole role : roles) {
			Collection<Object> children = cache.getInstances(element, role.getId());
			
			for (Object c : children) {
				if (c instanceof PDInstance) {
					PDInstance child = (PDInstance) c;
					if (!mappedChildren.contains(child)) {
						traverse_output(child, role, cache, ga, walker);
					}
				}

			}
		}

		walker.gaOutputCursor.pop();
	}

	/**
	 * Takes a PDInstance of the template output from the generator instance and
	 * clones it to the output branch of the generator application.
	 * 
	 * @param element
	 *            - PDInstance to be cloned from of the generator's output
	 *            template
	 * @param role
	 *            - The role this instance is to its parent
	 * @param cache
	 *            - PDCache instance associated with this Generator Application
	 * @param ga
	 *            - PDGeneratorApplication instance which PDTransform is being
	 *            run on
	 */
	private void cloneElement(PDInstance element, PDRole role,
			PDWorkingCopy cache, PDGeneratorApplication ga, TreeWalker walker) {
		if (walker.gaOutputCursor.isEmpty()) {
			PDInstance clone = cache.newInstance(element.getTypeId());
			clone.setName(element.getName());
			ga.setOutput(clone.getId());
			walker.gaOutputCursor.push(clone);
		} else {
			PDInstance clone = cache.newInstance(element.getTypeId());
			clone.setName(element.getName());
			cache.addLink(walker.gaOutputCursor.peek().getId(), role.getId(), clone.getId());
			walker.gaOutputCursor.push(clone);
		}
	}

	private void clonePrimitive(Object element, PDRole role,
			PDWorkingCopy cache, PDGeneratorApplication ga, TreeWalker walker) {
		cache.addLink(walker.gaOutputCursor.peek().getId(), role.getId(), element);
	}

	/**
	 * Takes a PDInstance which originates from the template output of the
	 * generator and checks if it is the ouput instance in any maps associated
	 * with the said generator. If a mapping if found then that mapping is
	 * executed.
	 * 
	 * @param element
	 *            - PDInstance to be checked
	 * @param ga
	 *            - PDGeneratorApplication instance which PDTransform is being
	 *            run on
	 * 
	 * @return arraylist of children that have already been processed
	 */
	private ArrayList<PDInstance> checkMapping(PDInstance element,
			PDGeneratorApplication ga, TreeWalker walker) {
		PDGenerator gen = ga.getGenerator();
		Collection<PDMap> maps = gen.getMaps();
		ArrayList<PDInstance> mappedChildren = new ArrayList<PDInstance>();

		for (PDMap map : maps) {
			GUID outputInstanceID = map.getOutputInstance();

			if (outputInstanceID.equals(element.getId())) {
				ArrayList<PDInstance> mapped = executeMapping(map, element, ga,
						walker);
				if (mapped != null)
					mappedChildren.addAll(mapped);
			}
		}
		return mappedChildren;
	}

	/**
	 * If a mapping was found in {@PDTransform.checkMapping
	 *  checkMapping} then this method is called and
	 * the mapping is executed.
	 * 
	 * @param map
	 *            - Map used to simulate for loop
	 * @param outputInstance
	 *            - PDInstance of the output instance in the map
	 * @param ga
	 *            - PDGeneratorApplication instance which PDTransform is being
	 *            run on
	 * 
	 * @return output template instances that have been processed
	 */
	private ArrayList<PDInstance> executeMapping(PDMap map,
			PDInstance outputInstance, PDGeneratorApplication ga,
			TreeWalker walker) {

		ArrayList<PDInstance> processed = new ArrayList<PDInstance>();

		PDRole inputRole = map.getInputRole();
		PDType inputType = map.getInputType();
		Collection<PDRole> outputRoles = map.getOutputRoles();

		PDWorkingCopy cache = ga.getPDWorkingCopy();

		ArrayList<PDInstance> inputMatches = new ArrayList<PDInstance>();

		if (walker.gaInputCursor.peek().getTypeId().equals(inputType.getId()))
			inputMatches.add(walker.gaInputCursor.peek());

		inputMatches.addAll(searchInput(inputType, ga, walker));

		ArrayList<PDInstance> verifiedMatches = new ArrayList<PDInstance>();

		for (Object ii : inputMatches) {
			PDInstance input_instance = (PDInstance) ii;

			/*
			 * If loop is entered then input_instances contains children of
			 * inputRole relation
			 */
			for (@SuppressWarnings("unused")
			Object element : cache.getInstances(input_instance, inputRole.getId())) {
				verifiedMatches.add(input_instance);
				break;
			}
		}

		for (PDInstance inputInstance : verifiedMatches) {
			walker.gaInputCursor.push(inputInstance);
			PDRole outputRole = null;

			ArrayList<Object> outputs = new ArrayList<Object>();
			if (outputRoles.size() > 1) {
				outputs = walker.getOrderedChildren(outputRoles,
						outputInstance, ga.getGenerator().getOrder());
			} else {
				for (PDRole OR : outputRoles) {
					Collection<Object> _outputs = cache.getInstances(outputInstance, OR.getId());
					for (Object o : _outputs) {
						outputs.add(o);
					}
					outputRole = OR;
				}
			}
			Collection<Object> inputs = cache.getInstances(inputInstance, inputRole.getId());

			for (Object i : inputs) {
				for (Object o : outputs) {
					PDInstance output = null;

					if (!o.equals(new String())) {
						output = (PDInstance) o;
					}

					for (PDRole r : outputRoles) {
						Collection<Object> oi = cache.getInstances(outputInstance, r.getId());
						if (oi.contains(output)) {
							outputRole = r;
							break;
						}

					}

					if (output != null) {
						walker.gaInputCursor.push((PDInstance) i);
						traverse_output(output, outputRole, cache, ga, walker);
						walker.gaInputCursor.pop();
					} else
						clonePrimitive(i, outputRole, cache, ga, walker);
					processed.add(output);
				}
			}
			walker.gaInputCursor.pop();
		}
		return processed;
	}

	/**
	 * This method is used to find all the instances in the generator
	 * application input that match the input type specified in the mapping. It
	 * uses the current position of the gaInputCursor and only searches down the
	 * tree from there.
	 * 
	 * @param inputType
	 *            - the PDType of the type of instances we are looking for
	 * @param ga
	 *            - PDGeneratorApplication instance which PDTransform is being
	 *            run on
	 * 
	 * @return An arrayList of PDInstances that match the type searched for
	 */
	private ArrayList<PDInstance> searchInput(PDType inputType,
			PDGeneratorApplication ga, TreeWalker walker) {
		PDWorkingCopy cache = ga.getPDWorkingCopy();

		ArrayList<Object> children = walker.getChildren(
				walker.gaInputCursor.peek(), cache);
		ArrayList<PDInstance> matches = new ArrayList<PDInstance>();

		for (Object c : children) {
			if (c instanceof PDInstance) {
				PDInstance child = (PDInstance) c;

				if (child.getTypeId().equals(inputType.getId())) {
					matches.add(child);
				}

				walker.gaInputCursor.push(child);
				ArrayList<PDInstance> childmatches = searchInput(inputType, ga,
						walker);
				for (PDInstance match : childmatches)
					matches.add(match);

				walker.gaInputCursor.pop();
			}
		}

		return matches;
	}

}
