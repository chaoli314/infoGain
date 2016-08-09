package factor_graph;

import static factor_graph.Index.*;
import static utils.Helper.*;

import java.util.*;

/** * Represents a (probability) factor.
 * @author chao */

public class Factor {
	/** Stores the variables on which the factor depends */
	private List<Var> _vs; // not the bottle neck. List is more flexible.
	/** Stores the factor values */
	private double[] _p; // ボトルネック.double array is most efficient in java。

	// ~ Constructors -----------------------------------------------------------
	/** trivial factor (no variables with value p=1) */
	public Factor() {
		this._vs = new ArrayList<>(); // empty
		this._p = new double[] { 1 };
	}

	/** Constructs factor depending on variables in vars with all values set to p */
	public Factor(List<Var> vars, double[] p) {
		this._vs = vars;
		this._p = p;
	}

	/** Constructs factor depending on the variable v with 0 */
	public Factor(Var v) {
		this._vs = new ArrayList<>();
		this._vs.add(v);
		this._p = new double[v.get_states()];
	}

	/** Constructs factor depending on variables in vars with all values set to 0 */
	public Factor(List<Var> vars) {
		this._vs = vars;
		this._p = new double[tableSize(this._vs)];
	}

	/** Copy Constructor */
	public Factor(Factor other) {
		this._vs = new ArrayList<>(other._vs);
		this._p = new double[other._p.length];
		System.arraycopy(other._p, 0, this._p, 0, other._p.length);
	}

	/** Sets all values to x */
	public Factor fill(double x) {
		Arrays.fill(_p, x);
		return this;
	}

	/** permute 改变变量顺序，方便调试，书本上的例子; res_vars and this factor should have same size */
	public Factor permute(List<Var> res_vars) {
		int[] convertLinearIndex = indexFor(this._vs, res_vars);
		double[] res_p = new double[_p.length];
		for (int i = 0; i < res_p.length; ++i) {
			res_p[i] = this._p[convertLinearIndex[i]];
		}
		return new Factor(res_vars, res_p);
	}

	// ~ Getters and Setters *********************************************************************
	/** Returns reference to variable set (i.e., the variables on which the factor depends) */
	public List<Var> vars() {
		return _vs;
	}

	/** Returns reference to value vector */
	public double[] p() {
		return _p;
	}

	/** table size */
	public int nrStates() {
		return _p.length;
	}

	/** Sets i 'th entry to val */
	public void set(int i, double val) {
		_p[i] = val;
	}

	/** Gets i 'th entry */
	public double get(int i) {
		return _p[i];
	}

	/** @return the _vs */
	public final List<Var> get_vs() {
		return _vs;
	}

	/** @param _vs the _vs to set */
	public final void set_vs(List<Var> _vs) {
		this._vs = _vs;
	}

	/** @return the _p */
	public final double[] get_p() {
		return _p;
	}

	/** @param _p the _p to set */
	public final void set_p(double[] _p) {
		this._p = _p;
	}

	// ~ Some simple statistics *********************************************************************
	/** Returns maximum of all values */
	public double max() {
		return org.apache.commons.math3.stat.StatUtils.max(this._p);
	}

	/** Returns minimum of all values */
	public double min() {
		return org.apache.commons.math3.stat.StatUtils.min(this._p);
	}

	/** Returns sum of all values */
	public double sum() {
		return org.apache.commons.math3.stat.StatUtils.sum(this._p);
	}

	/** Returns true if one or more entries are NaN */
	public boolean hasNaNs() {
		boolean foundnan = false;
		for (double p : _p) {
			if (Double.isNaN(p)) {
				foundnan = true;
				break;
			}
		}
		return foundnan;
	}

	/** Returns true if one or more values are negative */
	public boolean hasNegatives() {
		boolean hasNegatives = false;
		for (double p : this._p) {
			if (0 > p) {
				hasNegatives = true;
				break;
			}
		}
		return hasNegatives;
	}

	// ~ 指数运算 *********************************************************************
	/** Applies logarithm pointwise;uses log(0)==0; */
	public Factor takeLog() {
		for (int i = 0; i < _p.length; ++i)
			_p[i] = log0(_p[i]);
		return this;
	}

	/** Returns pointwise logarithm */
	public Factor log() {
		Factor x = new Factor(this);
		x.takeLog();
		return x;
	}

	/** Returns pointwise exponent */
	public Factor exp() {
		Factor x = new Factor(this);
		x.takeExp();
		return x;
	}

	/** Applies exponent pointwise */
	public Factor takeExp() {
		for (int i = 0; i < _p.length; ++i)
			_p[i] = Math.exp(_p[i]);
		return this;
	}

	// + - * / ; 加减乘除运算 *********************************************************************

	/** Returns point-wise inverse ( uses 1/0==0; not 1/0==Infinity.) */
	public Factor inverse() {
		Factor x = new Factor(this);
		for (int i = 0; i < x._p.length; ++i)
			x._p[i] = (x._p[i] != 0 ? (1 / (x._p[i])) : 0);
		return x;
	}

	/** Returns marginal on vars, obtained by summing out all variables except those in vars; 周辺化 */
	public Factor marginal(List<Var> vars) {
		List<Var> res_vars = set_intersection(this._vs, vars); // もし、vars有多余变量，无视他
		double[] res_p = new double[tableSize(res_vars)];
		int[] i_res = indexFor(res_vars, this._vs);
		for (int i = 0; i < this._p.length; ++i)
			res_p[i_res[i]] += this._p[i];
		return new Factor(res_vars, res_p);
	}

	/** Returns max-marginal on \a vars, obtained by maximizing all variables except those in \a vars, and normalizing the result if \a normed == \c
	 * true */
	public Factor maxMarginal(List<Var> vars) {
		List<Var> res_vs = set_intersection(this._vs, vars); // もし、vars有多余变量，无视他
		double[] res_p = new double[tableSize(res_vs)];
		int[] i_res = indexFor(res_vs, this._vs);
		for (int i = 0; i < this._p.length; ++i)
			if (_p[i] > res_p[i_res[i]]) res_p[i_res[i]] = _p[i];
		return new Factor(res_vs, res_p);
	}

	/** reuse marginal */
	public Factor summing_out(List<Var> vars) {
		List<Var> res_vars = set_difference(this._vs, vars);
		return this.marginal(res_vars);
	}

	/** Caution : mutate this factor */
	public Factor product(Factor that) {
		Factor other = product(this, that);
		this._vs = other._vs;
		this._p = other._p;
		return this;
	}

	public static Factor product(Factor A, Factor B) {
		List<Var> C_vs = set_union(A._vs, B._vs);
		int[] i_A_for_C = indexFor(A._vs, C_vs);
		int[] i_B_for_C = indexFor(B._vs, C_vs);
		int C_tableSize = tableSize(C_vs);
		double[] C_p = new double[C_tableSize];
		for (int i_C = 0; i_C < C_tableSize; i_C++) {
			C_p[i_C] = A._p[i_A_for_C[i_C]] * B._p[i_B_for_C[i_C]];
		}
		return new Factor(C_vs, C_p);
	}

	public static Factor sum(Factor A, Factor B) {
		List<Var> C_vs = set_union(A._vs, B._vs);
		int[] i_A_for_C = indexFor(A._vs, C_vs);
		int[] i_B_for_C = indexFor(B._vs, C_vs);
		int C_tableSize = tableSize(C_vs);
		double[] C_p = new double[C_tableSize];
		for (int i_C = 0; i_C < C_tableSize; i_C++) {
			C_p[i_C] = A._p[i_A_for_C[i_C]] + B._p[i_B_for_C[i_C]];
		}
		return new Factor(C_vs, C_p);
	}

	public static Factor difference(Factor A, Factor B) {
		List<Var> C_vs = set_union(A._vs, B._vs);
		int[] i_A_for_C = indexFor(A._vs, C_vs);
		int[] i_B_for_C = indexFor(B._vs, C_vs);
		int C_tableSize = tableSize(C_vs);
		double[] C_p = new double[C_tableSize];
		for (int i_C = 0; i_C < C_tableSize; i_C++) {
			C_p[i_C] = A._p[i_A_for_C[i_C]] - B._p[i_B_for_C[i_C]];
		}
		return new Factor(C_vs, C_p);
	}

	/** Specilized the divide by zero */
	public static Factor quotient(Factor A, Factor B) {
		List<Var> C_vs = set_union(A._vs, B._vs);
		int[] i_A_for_C = indexFor(A._vs, C_vs);
		int[] i_B_for_C = indexFor(B._vs, C_vs);
		int C_tableSize = tableSize(C_vs);
		double[] C_p = new double[C_tableSize];
		for (int i_C = 0; i_C < C_tableSize; i_C++) {
			if (B._p[i_B_for_C[i_C]] != 0) C_p[i_C] = A._p[i_A_for_C[i_C]] / B._p[i_B_for_C[i_C]];
		}
		return new Factor(C_vs, C_p);
	}

	// ~ Entropy 情報理論など *********************************************************************

	/** Returns logarithm of x, or 0 if x == 0;log(0) or log(x),x<0数学上无定义; make sure x is not negative */
	private static double log0(double x) {
		return (x != 0) ? Math.log(x) : 0;
	}

	/** 条件：正規化してから、ENTROPYを計算すること；Returns the Shannon entropy of this factor */
	public double entropy() {
		double result = 0;
		for (double p : _p)
			result -= p * log0(p);
		return result;
	}

	/** using chain rule: H(XY) = H(X)+H(Y|X) */
	public double conditional_entropy(Var x) {

		List<Var> X = new ArrayList<>();
		X.add(x);

		double H_XY = this.entropy();
		double H_X = this.marginal(X).entropy();
		return H_XY - H_X;
	}

	/** using chain rule: H(XY) = H(X)+H(Y|X) => (result = H_XY - H_X) */
	public double conditional_entropy(List<Var> X) {
		double H_XY = this.entropy();
		double H_X = this.marginal(X).entropy();
		return H_XY - H_X;
	}

	/** I(X;Y) = H(Y) + H(X) - H(XY) */
	public double MutualInfo(Var x, Var y) {

		List<Var> X = new ArrayList<>();
		X.add(x);

		List<Var> Y = new ArrayList<>();
		Y.add(y);

		List<Var> XY = new ArrayList<>();
		XY.add(x);
		XY.add(y);

		double H_X = this.marginal(X).entropy();
		double H_Y = this.marginal(Y).entropy();
		double H_XY = this.marginal(XY).entropy();

		return H_X + H_Y - H_XY;
	}

	/** I(X;Y) = H(Y) + H(X) - H(XY) */
	public double MutualInfo(List<Var> X, List<Var> Y) {

		List<Var> XY = new ArrayList<>();
		XY.addAll(X);
		XY.addAll(Y);

		double H_X = this.marginal(X).entropy();
		double H_Y = this.marginal(Y).entropy();
		double H_XY = this.marginal(XY).entropy();

		return H_X + H_Y - H_XY;
	}

	// ~ methods *********************************************************************

	/** Normalizes this factor */
	public double normalize() {
		double Z = sum(); // apache math lib
		for (int i = 0; i < _p.length; ++i) {
			_p[i] /= Z;
		}
		return Z;
	}

	/** Returns normalized copy of this */
	public Factor normalized() {
		Factor x = new Factor(this);
		x.normalize();
		return x;
	}

	/** Draws all values i.i.d. from a uniform distribution on [0,1) */
	public Factor randomize() {
		Random rand = new Random();
		for (int i = 0; i < _p.length; ++i)
			_p[i] = rand.nextDouble();
		return this;
	}

	/** Sets all values to 1/n, n is the table size */
	public Factor setUniform() {
		Arrays.fill(_p, 1 / _p.length);
		return this;
	}

	/** (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode() */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(_p);
		result = prime * result + ((_vs == null) ? 0 : _vs.hashCode());
		return result;
	}

	/** (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Factor other = (Factor) obj;
		if (!Arrays.equals(_p, other._p)) return false;
		if (_vs == null) {
			if (other._vs != null) return false;
		} else if (!_vs.equals(other._vs)) return false;
		return true;
	}

	/** Writes a factor to an output stream */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("variables: " + _vs + "\n");
		for (int linearState = 0; linearState < tableSize(_vs); linearState++) {
			s.append(("" + String.format("%2d", linearState) + "|states" + Arrays.toString(Index.calcState(_vs, linearState))) + "|prob: "
					+ String.format("%.5f", _p[linearState]) + "\n");
		}
		return s.toString();
		// return "Factor [_vs=" + _vs + ", _p=" + Arrays.toString(_p) + "]";
	}

	

	// ~ 実装していないメソッド *********************************************************************

	/** not yet */
	void slice() {
	}

	/** Returns distance between two factors f and g, according to the distance measure */
	// pre f.vars() == g.vars()
	public double dist(Factor f, Factor g, int shului) {
		return -1;
	}
}