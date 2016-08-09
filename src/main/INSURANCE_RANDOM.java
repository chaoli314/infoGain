package main;

import graph.*;
import triangulation.*;

public class INSURANCE_RANDOM {

	public static void main(String[] args) {

		String filename = "bnlearn/child.net";

		bayesian_networks.BayesianNetwork bn = io.HUGIN_format.load(filename);

		Graph g = bn.getMoralGraph();
		int[] weights = bn.getWeights();

		for (int i = 0; i < Integer.MAX_VALUE; i++) {

			double dens = 0;

			edu.princeton.cs.algs4.Stopwatch s = new edu.princeton.cs.algs4.Stopwatch();
			
			for (int k = 0; k < 1000; k++) {

				Graph h = new Graph(g);
				
				for (int j = 0; j < i; ++j) {
					h.addEdge();
					h.addEdge();
					h.addEdge();
					h.addEdge();
					h.addEdge();
				}


				if (k<1)System.out.print(h.E() + "," + h.density() + ",");

				

				new TriangulationByDFS_DCM_2012().run(h, weights);
				
				dens = h.density() ;
			}
			
			if (dens > 0.9) break;
			
			System.out.println(s.elapsedTime()/1000.0);
		}

		
		
		
		
		
		for (int i = 0; i < Integer.MAX_VALUE; i++) {

			double dens = 0;

			edu.princeton.cs.algs4.Stopwatch s = new edu.princeton.cs.algs4.Stopwatch();
			
			for (int k = 0; k < 1000; k++) {

				Graph h = new Graph(g);
				
				for (int j = 0; j < i; ++j) {
					h.addEdge();
					h.addEdge();
					h.addEdge();
					h.addEdge();
					h.addEdge();
					}


				if (k<1)System.out.print(h.E() + "," + h.density() + ",");

				

				new TriangulationByDFS().run(h, weights);
				
				dens = h.density() ;
			}
			
			if (dens > 0.9) break;
			
			System.out.println(s.elapsedTime()/1000.0);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}

}
