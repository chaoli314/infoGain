/**
 * Representation of an undirected graph
 */
package graph;

import static utils.Helper.totalTableSize;

import java.util.*;

/** Representation of an undirected graph
 * 
 * @author Chao Li */
public class Graph {
	// Constructors ######################################################
	public Graph(int V) {
		if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
		_matrix = new BitSet[V];
		for (int x = 0; x < V; x++)
			_matrix[x] = new BitSet();
	}

	/** random graph with V vertices and E edges */
	public Graph(int V, int E) {
		this(V);
		if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
		if ( (2 * E) > V * (V - 1)) throw new IllegalArgumentException("Too many edges");
		
		// make connected graph
		
		List<Integer> randomOrder = new ArrayList<>();
		for (int j = 0; j < V; j++)
			randomOrder.add(j);
		Collections.shuffle(randomOrder);
		
		for (int i =0 ; i < V-1; i++){		
				addEdge(randomOrder.get(i), randomOrder.get(i+1));
		}
		
		// might be inefficient
		while (this.E() != E) {
			int v = (int) (V * Math.random());
			int w = (int) (V * Math.random());
			if (v != w) addEdge(v, w);
		}
	}

	/** Copy constructor. */
	public Graph(Graph G) {
		this._matrix = new BitSet[G.V()];
		for (int i = 0; i < this._matrix.length; ++i) {
			this._matrix[i] = (BitSet) G._matrix[i].clone();
		}
	}

	// Setter and Getter Methods ##############################

	public int V() {
		return _matrix.length;
	}

	public int E() {
		return countEdges();
	}

	public double density() {
		return (2 * E()) * 1000 / (V() * (V() - 1)) / 1000.0;
	}

	public long max_clique_size() {
		
		List<BitSet> cliques = new BronKerboschCliqueFinder(this).getAllMaximalCliques();
			
		long maximum = 0;
		for (BitSet clique : cliques) {
			if (maximum < clique.cardinality()) {
				maximum = clique.cardinality();
			}
		}
		
		return maximum;
	}
	
	public long max_clique_TableSize(int[] weights) {
		
		List<BitSet> cliques = new BronKerboschCliqueFinder(this).getAllMaximalCliques();
			
		long maximum = 0;
		for (BitSet clique : cliques) {
			if (maximum < utils.Helper.tableSize(clique, weights)) {
				maximum = utils.Helper.tableSize(clique, weights);
			}
		}
		return maximum;
	}

	//	best_tts = totalTableSize(best_cliques, weights); // upper bound;
	//final public static long tableSize(BitSet bs, int[] weights) {
		
	public double clusteringCoefficient(int v){
		
		if(degree(v) ==1){
			return 0;
		}
		
		int count= 0;
		
		BitSet nb =  neighbours(v);
		
		for (int i = nb.nextSetBit(0); i >= 0; i = nb.nextSetBit(i + 1)) {
			for (int j = nb.nextSetBit(i + 1); j >= 0; j = nb.nextSetBit(j + 1)) {
				if (this.containsEdge(i, j)) {
					++count;
				}
			}
		}
		
		return  2.0 * count /( degree(v) * (degree(v)-1));
		
	}
	
	public double clusteringCoefficient(){
		
		double C =0;
		
		for(int i = 0; i< V();i++){
			C += clusteringCoefficient(i);
		}
		
		return C/V();
	}
	
	public int degree(int v){
		return neighbours(v).cardinality();
	}
	
	
	public int maxDegree(){
		
		int max =0;
		for(int v = 0; v< V();++v){
			if(degree(v)>max){
				max = degree(v);
			}
		}
	return max;
	
	}
	
	/** Get neighbour BitSet */
	public BitSet neighbours(int x) {
		return _matrix[x];
	}

	/** Method for hacking the raw matrix (use with caution) */
	public BitSet[] HackMatrix() {
		return _matrix;
	}

	/** Counts the number of edges in this graph. */
	private int countEdges() {
		int count = 0;
		for (BitSet bs : _matrix) {
			count += bs.cardinality();
		}
		return count / 2;
	}

	// ADD and REMOVE ##############################

	/** Add a new node, and get the number of the node */
	public int addNode() {
		int node = V();
		BitSet[] new_matrix = new BitSet[V() + 1];
		System.arraycopy(_matrix, 0, new_matrix, 0, _matrix.length);
		new_matrix[node] = new BitSet();
		_matrix = new_matrix;
		return node;
	}

	/** Add a new Random edge */
	public void addEdge() {
		if ((2 * E()) == (V() * (V() - 1))) throw new IllegalArgumentException("Too many edges");

		int v = 0, w = 0;
		do {
			v = (int) (V() * Math.random());
			w = (int) (V() * Math.random());
		} while (v == w || containsEdge(v, w));
		addEdge(v, w);
	}
	
	public void addEdge(double density){
		while(this.density() < density){
			this.addEdge();
		}
	}

	public void addEdge(int v, int w) {
		_matrix[v].set(w);
		_matrix[w].set(v);
	}

	public void removeEdge(int v, int w) {
		_matrix[v].clear(w);
		_matrix[w].clear(v);
	}

	/** does the graph contain the edge v-w? */
	public boolean containsEdge(int x, int y) {
		return _matrix[x].get(y);
	}

	/** Checks if the edge (x,y) exists */
	public boolean adjacent(int x, int y) {
		return _matrix[x].get(y);
	}



	/** Set an edge */
	public void setEdge(int x, int y) {
		assert (x != y);
		_matrix[x].set(y);
		_matrix[y].set(x);
	}

	/** Set an edge to true or false */
	public void setEdge(int x, int y, boolean value) {
		assert (x != y || !value);
		_matrix[x].set(y, value);
		_matrix[y].set(x, value);
	}

	/** Clear all edges of the graph */
	public void clear() {
		for (int i = 0; i < V(); i++)
			_matrix[i].clear();
	}

	// Integer iterator for neighbors
	// ==================================================================
	/** return list of neighbors of v */
	public Iterable<Integer> adj(int v) {
		return new AdjIterator(v);
	}

	/*** support iteration over graph vertices */
	private class AdjIterator implements Iterator<Integer>, Iterable<Integer> {
		int v, w = 0;

		AdjIterator(int v) {
			this.v = v;
		}

		public Iterator<Integer> iterator() {
			return this;
		}

		public boolean hasNext() {
			while (w < V()) {
				if (_matrix[v].get(w)) return true;
				w++;
			}
			return false;
		}

		public Integer next() {
			if (hasNext()) {
				return w++;
			} else {
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	// Methods
	// **************************************************************

	/** Perform a consistency check */
	public boolean isConsistent() {
		for (int i = 0; i < V(); ++i) {
			for (int j = 0; j < V(); j++) {
				// Matrix must be symmetrical
				if (!_matrix[i].get(j) == _matrix[j].get(i)) return false;
				// Diagonal must be set to 0
				if (i == j && _matrix[i].get(j)) return false;
			}
		}
		return true;
	}

	/** (non-Javadoc) * @see java.lang.Object#hashCode() */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(_matrix);
		return result;
	}

	/** Compare two graphs (non-Javadoc) * @see
	 * java.lang.Object#equals(java.lang.Object) */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Graph other = (Graph) obj;
		if (!Arrays.equals(_matrix, other._matrix)) return false;
		return true;
	}

	/** (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString() */
	@Override
	public String toString() {
		return "Graph [_matrix=" + Arrays.toString(_matrix) + "]";
	}

	/** store */
	private BitSet[] _matrix;

}