//Final version/直さないで
/**
 * Copy Right
 */
package factor_graph;

/** Represents a discrete random variable.
 * @author Chao Li */
public class Var implements Comparable<Var> {
	/** Label of the variable (its unique ID) */
	private final int _label;
	/** Number of possible values */
	private final int _states;

	/** Constructs a variable with a given label and number of states */
	public Var(int label, int states) {
		this._label = label;
		this._states = states;
	}

	/** Copy constructor. */
	public Var(Var other) {
		this._label = other._label;
		this._states = other._states;
	}

	// Setter and Getter Methods //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/** * Returns the label */
	public final int get_label() {
		return _label;
	}

	/** * Returns the number of states */
	public final int get_states() {
		return _states;
	}

	/** * Returns the label */
	public final int label() {
		return _label;
	}

	/** * Returns the number of states */
	public final int states() {
		return _states;
	}

	// Methods //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/** (non-Javadoc) * @see java.lang.Object#hashCode() */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _label;
		result = prime * result + _states;
		return result;
	}

	/** (non-Javadoc) * @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Var other = (Var) obj;
		if (_label != other._label) return false;
		if (_states != other._states) return false;
		return true;
	}

	/** (non-Javadoc) * @see java.lang.Object#toString() */
	@Override
	public String toString() {
		return new String(Character.toChars(65 + _label)) + "#" + _label;
		// return "Var [_label=" + _label + ", _states=" + _states + "]";
	}

	/** only compares labels (non-Javadoc) * @see java.lang.Comparable#compareTo(java.lang.Object) */
	@Override
	public int compareTo(Var o) {
		return this._label - o._label;
	}

}
