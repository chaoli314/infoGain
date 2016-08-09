package main;

import triangulation.*;
import bayesian_networks.*;
import graph.*;
import io.*;

public class test1 {

	public static void main(String[] args) {

		String filename = "bnlearn/alarm.net";
      //String filename = 		args[0] ;

		BayesianNetwork bn = HUGIN_format.load(filename);
		
		Graph g = bn.getMoralGraph();
		int[] weights = bn.getWeights();
		
		new TriangulationByDFS().run(g, weights);
		
		
	}
}