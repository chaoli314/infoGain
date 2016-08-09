/** An implemention of the greedy heuristic triangulation strategies */

package triangulation;

import static utils.Helper.*;
import graph.Graph;

import java.util.*;

/** @author chao */
public abstract class GreedyHeuristic {
	/** Triangulate using this strategy */
	public Graph run(Graph initialGraph, int[] weights) {
		Graph graph = new Graph(initialGraph);

		int depth = 1;

		BitSet remaining = new BitSet(graph.V());
		remaining.set(0, graph.V());
		// While all nodes haven't been eliminated
		while (!remaining.isEmpty()) {
			int min = Integer.MAX_VALUE;
			int nextNode = -1;
			for (int i = remaining.nextSetBit(0); i >= 0; i = remaining.nextSetBit(i + 1)) {
				int cost_of_i = ComputeCost(graph, remaining, i, weights, depth);
				if (cost_of_i < min) {
					min = cost_of_i;
					nextNode = i;
				}
			}
			// Eliminate next
			eliminateNode(graph, remaining, nextNode);
		}
		return graph;
	}

	/** MinFill
	 * MinWidth
	 * MinWeight
	 * WMinFill */
	public abstract int ComputeCost(Graph graph, BitSet remaining, int node, int[] weights, int depth);
}