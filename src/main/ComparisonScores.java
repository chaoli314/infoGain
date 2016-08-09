package main;

import graph.Graph;
import io.HUGIN_format;
import bayesian_networks.BayesianNetwork;
import triangulation.*;

public class ComparisonScores {

	public static void main(String[] args) {


		String filename = "bnlearn/hailfinder.net";
		
		BayesianNetwork bn = HUGIN_format.load(filename);
		
		Graph g = bn.getMoralGraph();
		int[] weights = bn.getWeights();
				
		new TriangulationByDFS_DCM_2015().run(g, weights);
		new DFS_treewidth().run(g, weights);
		new DFS_weightedTW().run(g, weights);
		new DFS_fillin().run(g, weights);

		
	}

}
