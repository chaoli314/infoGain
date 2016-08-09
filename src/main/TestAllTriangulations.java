package main;

import graph.*;
import io.HUGIN_format;
import bayesian_networks.BayesianNetwork;
import triangulation.*;

public class TestAllTriangulations {

	public static void main(String[] args) {

		String filename = "bnlearn/child.net";
		
		BayesianNetwork bn = HUGIN_format.load(filename);
		
		Graph g = bn.getMoralGraph();
		int[] weights = bn.getWeights();
		
	//	System.out.println(g.maxDegree());
		
		new TriangulationByDFS_DCM2015_PIVOTCLIQUE().run(g, weights);

		new TriangulationByDFS_DCM_2015().run(g, weights);

		
		//new TriangulationByDFS().run(g, weights);

/*	org.apache.commons.math3.stat.descriptive.SummaryStatistics s = new org.apache.commons.math3.stat.descriptive.SummaryStatistics();
		for(int w : weights){
			s.addValue(w);
		}
		System.out.printf("%.0f, ",s.getMax());
		System.out.printf("%.0f, ",s.getMin());
		System.out.printf("%.2f, ",s.getMean());
		System.out.printf("%.2f\n",s.getStandardDeviation());
*/		
		
		//g.addEdge(0.2);
		
		
		//new TriangulationByDFS_DCM_2015().run(g, weights);

		
		
		//Stopwatch s = new Stopwatch();
		
		//Graph  gg =new TriangulationByDFS().run(g, weights);
		
		//Graph  gg =new TriangulationByDFS_DCM().run(g, weights);
		
		//Graph  gg =new TriangulationByDFS_PIVOTCLIQUE().run(g, weights);

		//Graph  gg = new TriangulationByDFS_PROPOSED().run(g, weights);
		
		//new TriangulationByDFS_DCM_2012().run(g, weights);
		//new TriangulationByDFS_PIVOTCLIQUE().run(g, weights);
		//new TriangulationByDFS().run(g, weights);
		
		
		
		//new TriangulationByDFS().run(g, weights);
		
		new TriangulationByDFS_DCM_2012().run(g, weights);
		new TriangulationByDFS_DCM_2015().run(g, weights);
		new TriangulationByDFS_PIVOTCLIQUE().run(g, weights);
		new TriangulationByDFS_DCM2015_PIVOTCLIQUE().run(g, weights);
		
		//System.out.println(s.elapsedTime());		
	}
}
