/** * Copyright */
package factor_graph;

import java.util.*;

/** InfAlg is an abstract base class, defining the common interface of all inference algorithms
 * @author chao * */
public abstract class InfAlg {

	private FactorGraph _fg;

	// Constructors ######################################################################
	/** Construct from FactorGraph */
	public InfAlg(FactorGraph fg) {
		this._fg = fg;
	}

	/** Returns a pointer to a newly constructed inference algorithm */
	public abstract InfAlg construct(FactorGraph fg);

	/** Returns reference to underlying FactorGraph. */
	public FactorGraph fg() {
		return _fg;
	}

	
	// TODO copy constructor	
    /// Returns a pointer to a new, cloned copy of \c *this (i.e., virtual copy constructor)
    public abstract InfAlg clone();
    
	// ~ Inference interface #####################################################

	/** \note This method should be called at least once before run() is called. */
	public abstract void init();

	public abstract void init( List<Var> vs ) ;

	/** \note Before run() is called the first time, init() should have been called. */
	public abstract double run();

	public Factor belief(Var v) {
		List<Var> vs = new ArrayList<>();
		vs.add(v);
		return belief(vs);
	}

	public abstract Factor belief(List<Var> vs);

	/** beliefV is preferred to belief for performance reasons */
	public Factor beliefV(int i) {
		return belief(fg().var(i));
	}

	/** efficient */
	public Factor beliefF(int I) {
		return belief(fg().factor(I).vars());
	}

	/** Returns all beliefs (approximate marginal probability distributions) calculated by the algorithm. */
	public abstract List<Factor> beliefs();

	// Changing the factor graph *********************************************************************

	/** Clamp var(i) to value x (i.e. multiply with a Kronecker delta/クロネッカーのデルタ/克罗内克函数/证据变量) */
	public void clamp(int i, int x) {
		_fg.clamp(i, x);
	}

	// ~ Backup/restore mechanism for factors ###########################################
	public void backupFactor(int I) {
		_fg.backupFactor(I);
	}

	public void backupFactors(List<Var> vs) {
		_fg.backupFactors(vs);
	}

	public void restoreFactor(int I) {
		_fg.restoreFactor(I);
	}

	public void restoreFactors(List<Var> vs) {
		_fg.restoreFactors(vs);
	}
}