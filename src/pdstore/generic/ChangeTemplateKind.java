package pdstore.generic;

import pdstore.sparql.Variable;

/**
 * Encodes the different cases of unknowns in a change template.
 * I means instance1 (or instance2) are known.
 * R means the role2 is known.
 * X means the respective component is unknown.
 * 
 * @author clut002
 *
 */
public enum ChangeTemplateKind {
	// Note: the instances are ordered using left-to-right binary counting.
	XXX, IXX, XRX, IRX, XXI, IXI, XRI, IRI;

	/**
	 * Converts a code number to the enum instance that corresponds to the
	 * binary representation of the code number.
	 * 
	 * @param code
	 * @return
	 */
	static ChangeTemplateKind convert(int code) {
		return values()[code];
	}

	public static ChangeTemplateKind getKind(PDChange<?, ?, ?> change) {
		int code = 0;
		if (!isUnspecified(change.getInstance1()))
			code += 1;
		if (!isUnspecified(change.getRole2()))
			code += 2;
		if (!isUnspecified(change.getInstance2()))
			code += 4;
		return ChangeTemplateKind.convert(code);
	}

	static boolean isUnspecified(Object o) {
		return o == null || o instanceof Variable;
	}
}
