package bayesian_networks;

import graph.Graph;
import factor_graph.*;
import static utils.Helper.*;

import java.util.*;

public class BayesianNetwork {

	private String name_;	//BNの名前
	private Map<String, Integer> variableIndex_;
	private List<List<String>> LsLsStates_;
	private List<Factor> _factors;
	private List<List<Integer>> parents_;
	private List<Var> vars_;

	public BayesianNetwork(String name, Map<String, Integer> variableIndex_, List<List<String>> states, List<Factor> factors, List<List<Integer>> parents) {
		this.name_ = name;
		this.variableIndex_ = variableIndex_;
		this.LsLsStates_ = states;
		this._factors = factors;
		this.parents_ = parents;
		
		vars_ = new ArrayList<>();
		int[] weights = this.getWeights();
		for(int i = 0; i<weights.length;i++){
			vars_.add(new Var(i,weights[i]));
		}
	}

	
    public Var var( int i )  { 
        return vars_.get(i);
    }
	
	
	
	
	public List<Factor> get_factors() {
		return _factors;
	}
	public int 	nrVars() {
		return variableIndex_.size();
	}
	
	public String getName() {
		return name_;
	}

	public int[] getWeights() {
		int N = variableIndex_.size();
		int[] weight = new int[N];
		for (int i = 0; i < N; ++i) {
			weight[i] = LsLsStates_.get(i).size();
		}
		return weight;
	}

	public Graph getMoralGraph() {
		int N = variableIndex_.size();
		Graph graph = new Graph(N);
		
		for (int i = 0; i < _factors.size(); ++i) {
			List<Var> i_vars = _factors.get(i).vars();
			for (int j = 0; j < i_vars.size() - 1; ++j) {
				for (int k = j + 1; k < i_vars.size(); ++k) {
					int v1 = i_vars.get(j).label();
					int v2 = i_vars.get(k).label();
					graph.addEdge(v1, v2);
				}
			}
		}
		return graph;
	}

    /** Returns number of factors*/
	public int nrFactors() {
		return _factors.size();
	}


	public void applyEvidence(int lebel, int state) {
		List<Var> evidenceVars = new ArrayList<>(); 
		evidenceVars.add(vars_.get(lebel) );
		double[] p = new double[tableSize(evidenceVars)];
		p[state] = 1.0;
		Factor evidenceFactor = new Factor (evidenceVars, p);
		for(int i = 0;i<nrFactors();i++){
			List<Var> varsi = _factors.get(i).vars();
			if(varsi.containsAll(evidenceVars)){
				_factors.set(i,Factor.product(_factors.get(i), evidenceFactor));
			}
		}
	}
}
