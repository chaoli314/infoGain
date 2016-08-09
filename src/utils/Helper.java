/**
 *
 */
package utils;

import graph.*;

import java.util.*;

import factor_graph.*;

/** @author Chao Li */
public class Helper {

	// table size ###########################################################
	/** Computes the table size of a clique. */
	final public static int tableSize(final Iterable<Var> varSet) {
		int count = 1;
		for (Var x : varSet) {
			count *= x.states();
		}
		return count;
	}

	/** Computes the table size of a clique. */
	final public static long tableSize(BitSet bs, int[] weights) {
		
		if( 1 == bs.cardinality()){
			return 0;
		}
		
		
		long count = 1;
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
			count *= weights[i];
		}
		return count;
	}

	/** Computes the total table size for a set cliques. */
	final public static long totalTableSize(Iterable<BitSet> cliques, int[] weights) {
		long count = 0;
		for (BitSet clique : cliques) {
			count += tableSize(clique, weights);
		}
		return count;
	}

	
	final public static long maxSize(Iterable<BitSet> cliques, int[] weights) {
		long maximum = 0;
		for (BitSet clique : cliques) {
			if (maximum < clique.cardinality()) {
				maximum = clique.cardinality();
			}
		}
		return maximum;
	}
	
	

	
	
	final public static long maxTableSize(Iterable<BitSet> cliques, int[] weights) {
		long maximum = 0;
		for (BitSet clique : cliques) {
			if (maximum < utils.Helper.tableSize(clique, weights)) {
				maximum = utils.Helper.tableSize(clique, weights);
			}
		}
		return maximum;
	}
	
	
	
	
	// set<Var>###########################################################
	final public static List<Var> set_union(final Collection<Var> A, final Collection<Var> B) {
		Set<Var> result = new TreeSet<>();
		result.addAll(A);
		result.addAll(B);
		return new ArrayList<>(result);
	}

	final public static List<Var> set_intersection(final Collection<Var> A, final Collection<Var> B) {
		Set<Var> result = new TreeSet<>();
		result.addAll(A);
		result.retainAll(B);
		return new ArrayList<>(result);
	}

	final public static List<Var> set_difference(final Collection<Var> A, final Collection<Var> B) {
		Set<Var> result = new TreeSet<>();
		result.addAll(A);
		result.removeAll(B);
		return new ArrayList<>(result);
	}

	// BitSet ###########################################################
	public final static BitSet set_union(final BitSet A, final BitSet B) {
		BitSet result = (BitSet) A.clone();
		result.or(B);
		return result;
	}

	final public static BitSet set_intersection(final BitSet A, final BitSet B) {
		BitSet result = (BitSet) A.clone();
		result.and(B);
		return result;
	}

	final public static BitSet set_difference(final BitSet A, final BitSet B) {
		BitSet result = (BitSet) A.clone();
		result.andNot(B);
		return result;
	}

	// Vertex Elimination and Simplicial test##########################

	/**
	 * Checks if a node is simplicial with respect to a set of eliminated nodes.
	 * Missing is the set of remaining nodes. We are only interested in these.
	 */
	public static boolean isSimplicial(Graph graph, int node, BitSet missing) {
		BitSet neighbors = set_intersection(graph.neighbours(node), missing);
		for (int node_i = neighbors.nextSetBit(0); node_i >= 0; node_i = neighbors.nextSetBit(node_i + 1)) {
			neighbors.clear(node_i);
			if (!(set_difference(neighbors, graph.neighbours(node_i)).isEmpty())) {
				return false;
			}
		}
		return true;

		// BitField<size> nb = graph.neighbours(node) & missing;
		// typename BitField<size>::BitIterator ni(nb);
		// while(ni++) if((nb.flip(*ni) &~ graph.neighbours(*ni)).any()) return
		// false;
		// return true;
	}

	/**
	 * Eliminate all simplicial node in the induced subgraph G[remaining].
	 * 
	 * @param G
	 *            the graph G.
	 * @param remaining
	 *            the remaining graph G[remaining].
	 */
	public static void eliminateSimplicial(Graph G, BitSet remaining) {
		boolean finish = false;
		while (!finish) {
			finish = true;
			for (int node = remaining.nextSetBit(0); node >= 0; node = remaining.nextSetBit(node + 1)) {
				if (isSimplicial(G, node, remaining)) {
					remaining.clear(node);
					finish = false;
					break; // no break is also correct.
				}
			}
		}
	}

	/** Adds fill-ins needed to eliminate node on a G[remaining] */
	public static void eliminateNode(Graph G, BitSet remaining, int node) {
		BitSet nb = set_intersection(G.neighbours(node), remaining);
		for (int ni = nb.nextSetBit(0); ni >= 0; ni = nb.nextSetBit(ni + 1)) {
			// make ni simplicial except edge ni<->ni
			G.neighbours(ni).or(nb); 
			G.neighbours(ni).clear(ni);
		}
		remaining.clear(node);
	}

	/** Find the maximal cliques of a triangulated graph */
	public static List<BitSet> findCliques(Graph graph) {
		// TODO: should be implemented more efficient than BK algorithm.
		List<BitSet> cliques = new BronKerboschCliqueFinder(graph).getAllMaximalCliques();
		return cliques;
	}
	
	public static boolean isClique(BitSet clique, Graph G) {
		if (clique.isEmpty())return false;
		BitSet nb = new BitSet(G.V());
		for (int node = clique.nextSetBit(0); node >= 0; node = clique.nextSetBit(node + 1))
			nb.or(G.neighbours(node));
		nb.andNot(clique);

		for (int i = nb.nextSetBit(0); i >= 0; i = nb.nextSetBit(i + 1)) {
			BitSet copiedClique = (BitSet) clique.clone();
			copiedClique.andNot(G.neighbours(i));
			if (copiedClique.isEmpty())
				return false;
		}
		return true;
	}

}
