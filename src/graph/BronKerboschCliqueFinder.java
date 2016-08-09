package graph;

import java.util.*;

import static utils.Helper.*;

/**
 * This class implements Bron–Kerbosch algorithm
 */
public class BronKerboschCliqueFinder {
	private final Graph _graph;
	private final BitSet _subGraph;
	private List<BitSet> _cliques;

	public BronKerboschCliqueFinder(final Graph graph) {
		this._graph = graph;
		this._subGraph = new BitSet(graph.V());
		this._subGraph.set(0, graph.V());
	}

	public BronKerboschCliqueFinder(final Graph graph, BitSet subgraph) {
		this._graph = graph;
		this._subGraph = (BitSet) subgraph.clone();
	}

	/**
	 * Finds all maximal cliques of the graph (subgraph). A clique is maximal if
	 * it is
	 * impossible to enlarge it by adding another vertex from the graph
	 * (subgraph). Note
	 * that a maximal clique is not necessarily the biggest clique in the graph.
	 *
	 * @return List of cliques (each of which is represented as a BitSet)
	 */
	public List<BitSet> getAllMaximalCliques() {
		_cliques = new ArrayList<BitSet>();
		BitSet R = new BitSet(_graph.V()); // empty
		BitSet P = _subGraph;
		BitSet X = new BitSet(_graph.V()); // empty
		BronKerbosch2(R, P, X);
		return _cliques;
	}

	final private void BronKerbosch2(BitSet R, BitSet P, BitSet X) {
		if (P.isEmpty() && X.isEmpty()) {
			// report R as a maximal clique
			_cliques.add(R);
		} else {
			int u = selectPivot(set_union(P, X));
			BitSet P_minus_NBu = set_difference(P, _graph.neighbours(u));
			for (int v = P_minus_NBu.nextSetBit(0); v >= 0; v = P_minus_NBu.nextSetBit(v + 1)) {
				BitSet R_or_v = (BitSet) R.clone();
				R_or_v.set(v);
				BronKerbosch2(R_or_v, set_intersection(P, _graph.neighbours(v)), set_intersection(X, _graph.neighbours(v)));
				P.clear(v);
				X.set(v);
			}
		}
	}

	private int selectPivot(BitSet set) {
		//TODO there is a optimization problem.
		return set.nextSetBit(0);
	}

	public Collection<BitSet> getBiggestMaximalCliques() {
		// first, find all cliques
		getAllMaximalCliques();

		int maximum = 0;
		Collection<BitSet> biggest_cliques = new ArrayList<BitSet>();
		for (BitSet clique : _cliques) {
			if (maximum < clique.cardinality()) {
				maximum = clique.cardinality();
			}
		}
		for (BitSet clique : _cliques) {
			if (maximum == clique.cardinality()) {
				biggest_cliques.add(clique);
			}
		}
		return biggest_cliques;
	}
}