package triangulation;

import graph.Graph;

import java.util.BitSet;

import static utils.Helper.*;

public class MinFill extends GreedyHeuristic {
	/** Count number of fill-ins. */
	@Override
	public int ComputeCost(Graph graph, BitSet remaining, int node, int[] weights, int depth) {

		int cost = countFillins(graph, node, remaining);

		remaining.clear(node);
		
		--depth;
		
		if(depth > 0 && (!remaining.isEmpty())){
			int  min = Integer.MAX_VALUE;

			Graph newGraph = new Graph(graph);
			
			eliminateNode(newGraph, remaining, node);
			
			for (int i = remaining.nextSetBit(0); i >= 0; i = remaining.nextSetBit(i + 1)) {

				int c = ComputeCost(newGraph, remaining, i, weights, depth);
				if(c < min)
					min = c;
			}
			cost += min;
		}
		
		remaining.set(node);
		
		return cost;
		
		
	}

	int countFillins(Graph graph, int node, BitSet remaining) {

		int count = 0;

		BitSet nb = set_intersection(graph.neighbours(node), remaining);
		for (int i = nb.nextSetBit(0); i >= 0; i = nb.nextSetBit(i + 1)) {
			for (int j = nb.nextSetBit(i + 1); j >= 0; j = nb.nextSetBit(j + 1)) {
				if (!graph.containsEdge(i, j)) {
					++count;
				}
			}
		}

		return count;

	}

}
