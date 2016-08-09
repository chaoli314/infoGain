package factor_graph;

import java.util.*;

public class ExactInf extends InfAlg {

	/** All single variable marginals */
	private List<Factor> _beliefsV;
	/** All factor variable marginals */
	private List<Factor> _beliefsF;
	/** Logarithm of partition sum */
	private double _logZ;
	/** 同時確率分布 */
	private Factor _JPD;

	// Constructors ######################################################################
	public ExactInf(FactorGraph _fg) {
		super(_fg);
		construct();
	}

	/** Helper function for constructors */
	private void construct() {

		// clear variable beliefs and reserve space
		_beliefsV = new ArrayList<>();
		for (int i = 0; i < fg().nrVars(); i++)
			_beliefsV.add(new Factor(fg().var(i)));

		// clear factor beliefs and reserve space
		_beliefsF = new ArrayList<>();
		for (int I = 0; I < fg().nrFactors(); I++)
			_beliefsF.add(new Factor(fg().factor(I).vars()));
	}

	@Override
	public InfAlg construct(FactorGraph fg) {
		return new ExactInf(fg);
	}

	// ~ General InfAlg interface -----------------------------------------------------------

	@Override
	public InfAlg clone() {
		return new ExactInf(this.fg());
	}

	public Factor belief(Var v) {
		return beliefV(v.label());
	}

	public Factor belief(List<Var> ns) {
		if (ns.size() == 0) return new Factor();
		else if (ns.size() == 1) {
			return beliefV(ns.get(0).label());
		} else {
			int I = 0;
			for (I = 0; I < _beliefsF.size(); I++) {
				if (_beliefsF.get(I).vars().containsAll(ns)) break;
			}
			if (I == _beliefsF.size()) new RuntimeException("BELIEF_NOT_AVAILABLE");
			return _beliefsF.get(I).marginal(ns);
		}
	}

	/** efficient */
	public Factor beliefV(int i) {
		return _beliefsV.get(i);
	}

	/** efficient */
	public Factor beliefF(int I) {
		return _beliefsF.get(I);
	}

	public List<Factor> beliefs() {
		List<Factor> result = new ArrayList<>();
		result.addAll(_beliefsV);
		result.addAll(_beliefsF);
		return result;
	}

	public void init() {
		for (Factor factorV : _beliefsV)
			factorV.fill(1.0);
		for (Factor factorF : _beliefsF)
			factorF.fill(1.0);
	}

	public double run() {
		_JPD = new Factor();
		for (Factor cpt : fg().factors())
			_JPD = Factor.product(_JPD, cpt);

		double Z = _JPD.sum();
		_logZ = Math.log(Z);

		for (int vi = 0; vi < _beliefsV.size(); vi++) {
			Factor factorV = _JPD.marginal(_beliefsV.get(vi).vars());
			_beliefsV.set(vi, factorV);
			// System.out.println(factorV);
		}
		for (int fi = 0; fi < _beliefsF.size(); fi++) {
			Factor factorF = _JPD.marginal(_beliefsF.get(fi).vars());
			_beliefsF.set(fi, factorF);
		}
		return 0;
	}

	public Factor calcMarginal(List<Var> vs) {
		return _JPD.marginal(vs);
	}

	@Override
	public void init(List<Var> vs) {
		// TODO Auto-generated method stub

	}

}
