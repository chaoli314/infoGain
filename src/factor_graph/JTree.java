package factor_graph;

import edu.princeton.cs.algs4.*;
import graph.BronKerboschCliqueFinder;
import graph.Graph;
import triangulation.MinFill;

import java.util.*;

import static utils.Helper.*;

public class JTree {

	// / Stores the messages
	private Factor[][] _mes;
	//  Stores the logarithm of the partition sum
	// private double _logZ;

	//  Outer region beliefs
	private List<Factor> beliefsOuter = new ArrayList<>();

	// / Inner region beliefs
	private List<Factor> beliefsInner;

	public JTree(FactorGraph fg) {
		// 変数の状態数を表す配列
		int[] weights =null;// fg.getWeights();

		/** moral graph*/
		Graph moralGraph = fg.MarkovGraph();

		/** TRIANGULATION*/
		Graph triangulatedGraph = new MinFill().run(moralGraph, weights);

		/** JuncGraph and JOIN TREE*/
		List<BitSet> cliques = new BronKerboschCliqueFinder(triangulatedGraph).getAllMaximalCliques();
		System.out.println("total table size is (計算量):" + totalTableSize(cliques, weights));

		// Construct a weighted graph (each edge is weighted with the cardinality
		// of the intersection of the nodes, where the nodes are the elements of cl).
		EdgeWeightedGraph JuncGraph = new EdgeWeightedGraph(cliques.size());
		// Start by connecting all clusters with cluster zero, and weight zero, in order to get a connected weighted graph
		// for( int i = 1; i < cl.size(); i++ )
		// JuncGraph.addEdge(new Edge(i, 0, 0));
		for (int i = 0; i < cliques.size(); i++) {
			for (int j = i + 1; j < cliques.size(); j++) {
				int w = (set_intersection(cliques.get(i), cliques.get(j))).cardinality();
				if (w > 0) JuncGraph.addEdge(new Edge(i, j, -w));
			}
		}

		KruskalMST mst = new KruskalMST(JuncGraph); // 最小生成树。由于权重取负数，所以是最大生成树。
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// initial beliefsOuter as 1
		for(int alpha = 0; alpha < cliques.size(); alpha++ ){

			List<Var> vars =null;// BitSet2ListVar(cliques.get(alpha), weights);
			
			Factor factor_alpha = new Factor(vars); 
			
			beliefsOuter.add(factor_alpha);
		}
		

		
		
		// For each factor, find an outer region that subsumes that factor. Then, multiply the outer region with that factor.
		for (int I = 0; I < fg.factors().size(); I++) {
			Factor factorI = fg.factors().get(I);
			for (int alpha = 0; alpha < beliefsOuter.size(); alpha++) {
				if ( (beliefsOuter.get(alpha).vars()).contains(factorI.vars())) {
				
					System.out.println(beliefsOuter.get(0));
				//	beliefsOuter.get(alpha)= factorI;
					// beliefsOuter.get(alpha) = Factor.product(beliefsOuter.get(alpha), factorI);
				}
			}
		}		
		
		
		
		
		
		
		int root = 0;



		
		/*
		
		
		
	    // CollectEvidence
	    //_logZ = 0.0;
	    for( int i = RTree.size()-1; i>= 0;i-- ) {

	        Factor new_msg;
	        
	        new_msg = 
	        		
	        		Qa[RTree[i].second].marginal( IR( i ), false );
	        
	        _logZ += log(new_msg.normalize());
	        Qa[RTree[i].first] *= new_msg / Qb[i];
	        Qb[i] = new_msg;
	    }
	    if( RTree.empty() )
	        _logZ += log(Qa[0].normalize() );
	    else
	        _logZ += log(Qa[RTree[0].first].normalize());

	    // DistributeEvidence
	    for( size_t i = 0; i < RTree.size(); i++ ) {
//	      Make outer region RTree[i].second consistent with outer region RTree[i].first
//	      IR(i) = seperator OR(RTree[i].first) && OR(RTree[i].second)
	        Factor new_Qb;
	        if( props.inference == Properties::InfType::SUMPROD )
	            new_Qb = Qa[RTree[i].first].marginal( IR( i ) );
	        else
	            new_Qb = Qa[RTree[i].first].maxMarginal( IR( i ) );

	        Qa[RTree[i].second] *= new_Qb / Qb[i];
	        Qb[i] = new_Qb;
	    }

	    // Normalize
	    for( size_t alpha = 0; alpha < nrORs(); alpha++ )
	        Qa[alpha].normalize();
		
		
		
		
		*/
		
		
		
		
		
		
		System.out.println("moralGraph:" + moralGraph);

		System.out.println("Triangulated graph:" + triangulatedGraph);

		System.out.println("join graph graph:" + JuncGraph);

		System.out.println("join tree:有向グラフ" + mst.edges());

	}



}
