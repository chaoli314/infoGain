package triangulation;

import java.util.*;

import edu.princeton.cs.algs4.Counter;
import static utils.Helper.*;
import graph.*;

public class TriangulationByDFS_DCM2015_PIVOTCLIQUE {
	private Graph best_H;
	private long best_tts = Long.MAX_VALUE; // upper bound;

	private Map<BitSet, Long> map = new HashMap<>(); // Create coalescing map

	Counter nodeCounter = new Counter("Node expansions");
	Counter coalescingCounter = new Counter("Coalescings");
	Counter boundCounter = new Counter("Pruned by upper bound");
	Counter SolutionsCounter = new Counter("Solutions found");

	public Graph run(Graph G, int[] weights) {
		final int V = G.V(); // graph size

		// Let filled-in graph S.H = G.
		Graph s_H = new Graph(G);
		BitSet s_remaining = new BitSet(V);
		s_remaining.set(0, V);
		List<BitSet> s_cliques = new BronKerboschCliqueFinder(s_H, s_remaining).getAllMaximalCliques();
		long s_tts = totalTableSize(s_cliques, weights);

		eliminateSimplicial(s_H, s_remaining); // Helper

		if (s_remaining.isEmpty())
			return s_H;

		// Let best = MinFill(s.H).
		best_H = new MinFill().run(new Graph(s_H), weights);
		List<BitSet> best_cliques = findCliques(best_H);
		best_tts = totalTableSize(best_cliques, weights); // upper bound;

		// dfs search

		edu.princeton.cs.algs4.Stopwatch s = new edu.princeton.cs.algs4.Stopwatch();

		expandNode(s_H, s_remaining, s_cliques, s_tts, weights);

/*		System.out.println(G.V() + "," + G.E() + "," + G.density()
		+ "," + best_H.max_clique_size()+ "," + best_H.max_clique_TableSize(weights)
		+ "," + s.elapsedTime() + "," + nodeCounter.tally() + "," + map.size() + "," + best_tts);
*/

		System.out.println(G.V() + "," + G.E() + "," + G.density()
		+ "," + s.elapsedTime() + "," + nodeCounter.tally() + "," + map.size()
		 + "," + best_tts		+ "," + best_H.max_clique_size()+ "," + best_H.max_clique_TableSize(weights)+ "," + (best_H.E()-G.E())	);
		
		// System.out.println(ExpansionCounter);
		// System.out.println(map.size());
		// System.out.println("map size|memory usage " + map.size());

		return best_H;
	}

	/** Performs depth-first search */
	private void expandNode(Graph graph, BitSet remaining, List<BitSet> cliques, long tts, int[] weights) {

		this.nodeCounter.increment();
		
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
			
			
			/** let m = copy(n) */
			Graph m_H = new Graph(graph);
			BitSet m_remaining = (BitSet) remaining.clone();
			List<BitSet> m_cliques = new ArrayList<>(cliques);
			long m_tts = tts;

			// eliminateNode
			BitSet U = new BitSet(m_H.V());
			BitSet fa_F_G1 = new BitSet(m_H.V());
			TriangulationByDFS_DCM_2015.eliminateVertex_DCM_2015(m_H, m_remaining, node, U, fa_F_G1);
			// remove

			/**
			 * Remove old cliques and update table size where changed are the
			 * nodes that have added/removed edgees
			 */
			Iterator<BitSet> it = m_cliques.iterator();
			while (it.hasNext()) {
				BitSet clique = it.next();
				if (clique.equals(set_intersection(clique, fa_F_G1))) {
					m_tts -= tableSize(clique, weights);
					it.remove();
				}
			}

			/** Find new cliques and update table size using Bron-Kerbosch */
			List<BitSet> newCliques = new BronKerboschCliqueFinder(m_H, fa_F_G1).getAllMaximalCliques();
			it = newCliques.iterator();
			while (it.hasNext()) {
				BitSet clique = it.next();
				if (utils.Helper.isClique(clique, m_H)) {
					m_tts += tableSize(clique, weights);
					m_cliques.add(clique);
				}
			}

			eliminateSimplicial(m_H, m_remaining); // Helper

			if (m_remaining.isEmpty()) {
				if (m_tts < best_tts) {
					best_H = m_H;
					best_tts = m_tts;
				}
			} else {
				if (m_tts >= best_tts) {
					boundCounter.increment();
					continue;
				}
				if (map.get(m_remaining) != null && map.get(m_remaining) <= m_tts) {
					coalescingCounter.increment();
					continue;
				}
				map.put(m_remaining, m_tts);
				expandNode(m_H, m_remaining, m_cliques, m_tts, weights);
			}
		}
	}
}
