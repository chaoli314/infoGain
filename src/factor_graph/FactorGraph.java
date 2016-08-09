package factor_graph;

import graph.Graph;

import java.util.*;
import java.util.Map.*;

/** Represents a factor graph.
 * @author Chao Li */
public class FactorGraph {
	/** Stores the variables */
	private List<Var> _vars;
	/** Stores the factors */
	private List<Factor> _factors;
	/** Stores backups of some factors */
	private Map<Integer, Factor> _backup;
	// Stores the neighborhood structure
	/** neighbors of the variable */
	private List<List<Integer>> _nbV;
	/** neighbors of the factor */
	private List<List<Integer>> _nbF;

	// Constructors ###########################################################
	public FactorGraph(List<Factor> factors, List<Var> vars) {
		// add factors
		_factors = new ArrayList<>();
		for (Factor f : factors)
			_factors.add(new Factor(f)); // call copy constructor
		// add variables
		_vars = new ArrayList<>(vars);
		for (Var v : vars)
			_vars.add(new Var(v)); // call copy constructor
		// back up
		_backup = new HashMap<>();
		// Graph skeleton ###########################################################
		Collections.sort(_vars); // Variables: 0, 1, 2, 3,...,n-1 # assume the _vars contains all variable.;
		for (int i = 0; i < _vars.size(); ++i) {
			Var v = _vars.get(i);
			for (int j = 0; j < _factors.size(); ++j) {
				Factor f = _factors.get(j);
				if (f.get_vs().contains(v)) {
					_nbV.get(i).add(j);
					_nbF.get(j).add(i);
				}
			}
		}
	}

	public FactorGraph(FactorGraph fg) {
		this(fg._factors, fg._vars); // copy factors and vars and generator others
	}

	/** (non-Javadoc) * @see java.lang.Object#clone() */
	@Override
	public Object clone() {
		return new FactorGraph(this);
	}

	// Accessors and mutators ###########################################################
	public Var var(int i) {
		return _vars.get(i);
	}

	public List<Var> vars() {
		return _vars;
	}

	public Factor factor(int I) {
		return _factors.get(I);
	}

	public List<Factor> factors() {
		return _factors;
	}

	public List<Integer> nbV(int i) {
		return _nbV.get(i);
	}

	public List<Integer> nbF(int I) {
		return _nbF.get(I);
	}

	public Integer nbV(int i, int _I) {
		return _nbV.get(i).get(_I);
	}

	public Integer nbF(int I, int _i) {
		return _nbF.get(I).get(_i);
	}

	// Queries ###########################################################
	// @{
	/** Returns number of variables */
	public int nrVars() {
		return _vars.size();
	}

	/** Returns number of factors */
	public int nrFactors() {
		return _factors.size();
	}

	/** * Returns the index of a particular variable
	 * \note Time complexity: O(nrVars()) */
	public int findVar(Var n) {
		return _vars.indexOf(n);
	}

	public List<Integer> findVars(List<Var> ns) {
		List<Integer> result = new ArrayList<>();
		for (Var n : ns)
			result.add(findVar(n));
		return result;
	}

	/** Returns index of the first factor that depends on the variables
	 * \note Time complexity: O(nrFactors()) */
	public int findFactor(List<Var> ns) {
		int I;
		for (I = 0; I < nrFactors(); ++I)
			if (factor(I).vars().equals(ns)) break;
		return I;
	}

	/** Return all variables that occur in a factor involving the \a i 'th variable, itself included */
	List<Var> Delta(int i) {
		// calculate Markov Blanket
		Set<Var> Del = new TreeSet<>();
		for (int I : nbV(i)) {
			for (int j : nbF(I)) {
				Del.add(var(j));
			}
		}
		return new ArrayList<>(Del);
	}

	/** Return all variables that occur in a factor involving some variable in \a vs, \a vs itself included */
	List<Var> Delta(List<Var> vs) {
		return null;
	}

	/** excluded */
	List<Var> delta(int i) {
		List<Var> del = Delta(i);
		del.remove(var(i));
		return del;
	}

	/** excluded */
	List<Var> delta(List<Var> vs) {
		List<Var> del = Delta(vs);
		del.removeAll(vs);
		return del;
	}

    /// Constructs the corresponding Markov graph 
    /** \note The Markov (Moral graph or Domain graph)graph has the variables as nodes and an edge between two variables if and only if the variables share a factor.     */
    public Graph MarkovGraph()  {
    	Graph G = new Graph ( nrVars() );
    	for( int i = 0; i < nrVars(); i++ )
            for(  int I : nbV(i) )
                for (  int j : nbF(I) )
                    if( i < j )
                        G.addEdge( i, j );
        return G;
    }
	// Backup/restore mechanism for factors ###########################################################
	
    /// Set the content of the \a I 'th factor and make a backup of its old content if \a backup == \c true
	public void setFactor( int I,  Factor newFactor ) {
        _factors.set(I, newFactor);
    }

    /// Set the contents of all factors as specified by \a facs and make a backup of the old contents if \a backup == \c true
    public void setFactors( Map<Integer, Factor> facs) {
        for( Entry<Integer, Factor> fac : facs.entrySet()) {
            setFactor( fac.getKey(), fac.getValue() );
        }
    }



    public void backupFactor( int I ){
    	_backup.put(I, factor(I));
    };



    public void restoreFactor( int I ){
    	Factor it = _backup.get( I );
        setFactor(I, it);
        _backup.remove(I);
    };
    
    /// Backup the factors specified by indices in \a facs
    /** \throw MULTIPLE_UNDO if a backup already exists
     */
    public void backupFactors(  Set<Integer> facs ) {
	}

    /// Restore all factors to the backup copies
    public void restoreFactors(){
        setFactors( _backup );
    };

    /// Makes a backup of all factors connected to a set of variables
    /** \throw MULTIPLE_UNDO if a backup already exists
     */
    public void backupFactors( List<Var> ns ) {
	}

    /// Restores all factors connected to a set of variables from their backups
    public void restoreFactors(  List<Var> ns ) {
	}
//@}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////////////////////////////////////////////////////////////////

	/**Clamp the \a i 'th variable to value \a x (i.e. multiply with a Kronecker delta \f$\delta_{x_i, x}\f$)*/
	public void clamp(int i, int x) {
		Factor mask  = new Factor(var(i));
		mask.set( x, 1 );
	    Map<Integer, Factor> newFacs = new TreeMap<>();
	    for(int I : nbV(i)){
	    	newFacs.put(I, Factor.product(factor(I), mask));
	    	backupFactor( I );
	    }
	    setFactors( newFacs );
	}
	
	// not yet METHODS ###########################################################

	// / Returns \c true if the factor graph is connected
	// boolean isConnected() { return _G.isConnected(); }

	// / Returns \c true if the factor graph is a tree (i.e., has no cycles and is connected)
	// boolean isTree() { return _G.isTree(); }

	// / Returns \c true if each factor depends on at most two variables
	// boolean isPairwise() ;

	// / Returns \c true if each variable has only two possible values
	// boolean isBinary() ;

	// / Constructs the corresponding Markov graph
	/** \note The Markov graph has the variables as nodes and an edge
	 * between two variables if and only if the variables share a factor. */
	// GraphAL MarkovGraph() ;

	// / Returns whether the \a I 'th factor is maximal
	/** \note A factor (domain) is \a maximal if and only if it is not a
	 * strict subset of another factor domain. */
	// boolean isMaximal( size_t I ) ;

	// / Returns the index of a maximal factor that contains the \a I 'th factor
	/** \note A factor (domain) is \a maximal if and only if it is not a
	 * strict subset of another factor domain. */
	// size_t maximalFactor( size_t I ) ;

	// / Returns the maximal factor domains in this factorgraph
	/** \note A factor domain is \a maximal if and only if it is not a
	 * strict subset of another factor domain. */
	// std::vector<VarSet> maximalFactorDomains() ;

	// / Evaluates the log score (i.e., minus the energy) of the joint configuration \a statevec
	// Real logScore( std::vector<size_t>& statevec ) ;

	/** /// Writes a factor graph to a GraphViz .dot file
	 * /// public void printDot( std::ostream& os ) ; */

}