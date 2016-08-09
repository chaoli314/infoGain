package triangulation;

import java.util.*;

import edu.princeton.cs.algs4.Counter;
import static utils.Helper.*;
import graph.*;

public class TriangulationByDFS_PROPOSED {
	private Graph best_graph;
	private long best_tts = Long.MAX_VALUE; // upper bound;

	private Map<BitSet, Long> map = new HashMap<>(); // Create coalescing map

	Counter ExpansionCounter = new Counter("Node expansions");
	Counter CoalescingCounter = new Counter("Coalescings");
	Counter UpperboundDiscardedCounter = new Counter("Pruned by upper bound");
	Counter SolutionsCounter = new Counter("Solutions found");

	public Graph run(Graph graph, int[] weights) {
		final int V = graph.V(); // graph size

		// Create the initial step
		Graph s_graph = new Graph(graph);
		BitSet s_remaining = new BitSet(V);
		s_remaining.set(0, V);
		List<BitSet> s_cliques = new BronKerboschCliqueFinder(s_graph, s_remaining).getAllMaximalCliques();
		long s_tts = totalTableSize(s_cliques, weights);

		// Eliminate simplicial nodes
		eliminateSimplicial(s_graph, s_remaining);
		if (s_remaining.isEmpty()) return s_graph;

		// Create an initial best step (using min-fill)
		best_graph = new MinFill().run(new Graph(s_graph), weights);
		List<BitSet> best_cliques = findCliques(best_graph);
		best_tts = totalTableSize(best_cliques, weights); // upper bound;

		// dfs search
		edu.princeton.cs.algs4.Stopwatch s = new edu.princeton.cs.algs4.Stopwatch();

		expandNode(s_graph, s_remaining, s_cliques, s_tts, weights);
		System.out.println(s.elapsedTime() + "," + ExpansionCounter + "," + map.size() + ",");
		// ////////////////////////////////////////////////////////
		System.out.println(best_tts);
		// System.out.println(ExpansionCounter);
		// System.out.println("map size|memory usage " + map.size());
		// ////////////////////////////////////////////////////////
		return best_graph;
	}

	/** Performs depth-first search */
	private void expandNode(Graph graph, BitSet remaining, List<BitSet> cliques, long tts, int[] weights) {

		this.ExpansionCounter.increment();
		
		// pivot clique selection strategy		
		BitSet pivotClique = new BitSet(graph.V());
		int max = 0;
		for (BitSet clique : cliques) {
			int size = set_intersection(clique, remaining).cardinality();
			if (size > max) {
				max = size;
				pivotClique = clique;
			}
		}
		//pivotClique = cliques.get(0);
		
		for (int node = remaining.nextSetBit(0); node >= 0; node = remaining.nextSetBit(node + 1)) {
			if (pivotClique.get(node))
				continue;// pivot clique pruning.

			Graph m_graph = new Graph(graph); // copy
			List<BitSet> m_cliques = new ArrayList<>(cliques);
			long m_tts = tts;
			BitSet m_remaining = (BitSet) remaining.clone();

			// eliminateNode
			BitSet changed = new BitSet(m_graph.V());
			BitSet reducedfillFamily = new BitSet(m_graph.V());
			eliminateVertexNEW(m_graph, m_remaining, node, changed, reducedfillFamily);
			// remove

			/** Remove old cliques and update table size where changed are the nodes that have added/removed edgees */
			Iterator<BitSet> it = m_cliques.iterator();
			while (it.hasNext()) {
				BitSet clique = it.next();
				if (clique.equals(set_intersection(clique, reducedfillFamily))) {
					m_tts -= tableSize(clique, weights);
					it.remove();
				}
			}

			/** Find new cliques and update table size using Bron-Kerbosch */
			List<BitSet> newCliques = new BronKerboschCliqueFinder(m_graph, reducedfillFamily).getAllMaximalCliques();
			it = newCliques.iterator();
			while (it.hasNext()) {
				BitSet clique = it.next();
				if (isClique(clique, m_graph)) {
					m_tts += tableSize(clique, weights);
					m_cliques.add(clique);
				}
			}

			eliminateSimplicial(m_graph, m_remaining);
			if (m_remaining.isEmpty()) {
				if (m_tts < best_tts) {
					best_graph = m_graph;
					best_tts = m_tts;
					SolutionsCounter = new Counter("Solutions found");
				} else if (m_tts == best_tts) {
					SolutionsCounter.increment();
				}
			} else {
				if (m_tts >= best_tts) {
					UpperboundDiscardedCounter.increment();
					continue;
				}
				if (map.get(m_remaining) != null && map.get(m_remaining) <= m_tts) {
					CoalescingCounter.increment();
					continue;
				}
				map.put(m_remaining, m_tts);
				expandNode(m_graph, m_remaining, m_cliques, m_tts, weights);
			}
		}
	}

	private boolean isClique(BitSet clique, Graph m_graph) {

		BitSet nb = new BitSet();
		for (int node = clique.nextSetBit(0); node >= 0; node = clique.nextSetBit(node + 1))
			nb.or(m_graph.neighbours(node));
		nb.andNot(clique);

		for (int i = nb.nextSetBit(0); i >= 0; i = nb.nextSetBit(i + 1)) {
			BitSet copiedClique = (BitSet) clique.clone();
			copiedClique.andNot(m_graph.neighbours(i));
			if (copiedClique.isEmpty()) return false;
		}
		return true;
	}

	/** Eliminate node and update cliques and total table size.
	 * This method is based on the article "Honour Thy Neighbour — Clique Maintenance in Dynamic Graphs" */
	private void eliminateVertexOLD(Graph graph, BitSet missing, int node, BitSet fills, BitSet fillFamily) {
		BitSet neighbors = set_intersection(graph.neighbours(node), missing);
		for (int i = neighbors.nextSetBit(0); i >= 0; i = neighbors.nextSetBit(i + 1)) {
			for (int j = neighbors.nextSetBit(i + 1); j >= 0; j = neighbors.nextSetBit(j + 1)) {
				if (!graph.containsEdge(i, j)) {
					fills.set(i);
					fills.set(j);

					graph.addEdge(i, j);

				}
			}
		}
		for (int i = fills.nextSetBit(0); i >= 0; i = fills.nextSetBit(i + 1)) {
			fillFamily.or(graph.neighbours(i));
		}
		fillFamily.or(fills);
		missing.clear(node);
	}

	/** Eliminate node and update cliques and total table size.
	 * This method is based on the article "Honour Thy Neighbour — Clique Maintenance in Dynamic Graphs" */
	private void eliminateVertexNEW(Graph graph, BitSet missing, int node, BitSet fills, BitSet reducedfillFamily) {
		BitSet neighbors = set_intersection(graph.neighbours(node), missing);
		for (int i = neighbors.nextSetBit(0); i >= 0; i = neighbors.nextSetBit(i + 1)) {
			for (int j = neighbors.nextSetBit(i + 1); j >= 0; j = neighbors.nextSetBit(j + 1)) {
				if (!graph.containsEdge(i, j)) {
					fills.set(i);
					fills.set(j);
					graph.addEdge(i, j);

					reducedfillFamily.or(set_intersection(graph.neighbours(i), graph.neighbours(j)));

				}
			}
		}

		reducedfillFamily.or(fills);
		missing.clear(node);
	}
}
