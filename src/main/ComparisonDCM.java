package main;

import static utils.Helper.*;

import java.util.*;

import graph.*;
import triangulation.TriangulationByDFS;
import triangulation.TriangulationByDFS_DCM_2015;

public class ComparisonDCM {

	public static void main(String[] args) {

		for (int fileNumber = 1; fileNumber <= 10; fileNumber++) {

			// String filename = "20_3/" + fileNumber + ".net";

			// bayesian_networks.BayesianNetwork bn = io.HUGIN_format.load(filename);

			// Graph g = bn.getMoralGraph();

			Graph g = new Graph(25, 30);

			long time1 = 0;

			long time2 = 0;

			long time3 = 0;

			for (int i = 0; i < 1000; i++) {// 1000 times

				Graph h1 = new Graph(g);
				List<BitSet> cliques1 = new BronKerboschCliqueFinder(h1).getAllMaximalCliques();
				BitSet remaining1 = new BitSet();
				remaining1.set(0, h1.V());

				Graph h2 = new Graph(g);
				List<BitSet> cliques2 = new BronKerboschCliqueFinder(h2).getAllMaximalCliques();
				BitSet remaining2 = new BitSet();
				remaining2.set(0, h2.V());

				Graph h3 = new Graph(g);
				List<BitSet> cliques3 = new BronKerboschCliqueFinder(h3).getAllMaximalCliques();
				BitSet remaining3 = new BitSet();
				remaining3.set(0, h3.V());

				
				// test
				int[] weights = new int[g.V()];
				Arrays.fill(weights, 3);

				System.out.print(utils.Helper.totalTableSize(cliques1, weights) + "\t"
						+ utils.Helper.totalTableSize(cliques2, weights) + "\t"
						+ utils.Helper.totalTableSize(cliques3, weights));
				System.out.println();
				

				List<Integer> randomOrder = new ArrayList<>();
				for (int j = 0; j < g.V(); j++)
					randomOrder.add(j);
				Collections.shuffle(randomOrder);

				////////////////// method 1
				long start1 = System.nanoTime();
				for (int vertex : randomOrder) {
					
				
					BitSet setU = new BitSet(h1.V());
					BitSet fa_U_G1 = new BitSet(h1.V());
					TriangulationByDFS.eliminateVertex_DCM_2010(h1, remaining1, vertex, setU, fa_U_G1);

					/**
					 * remove old cliques
					 */
					Iterator<BitSet> it = cliques1.iterator();
					while (it.hasNext()) {
						BitSet clique = it.next();
						if (setU.intersects(clique)) {
							it.remove();
						}
					}

					/**
					 * Find new cliques
					 */
					List<BitSet> newCliques = new BronKerboschCliqueFinder(h1, fa_U_G1).getAllMaximalCliques();
					it = newCliques.iterator();
					while (it.hasNext()) {
						BitSet clique = it.next();
						if (setU.intersects(clique)) {
							if (utils.Helper.isClique(clique, h2)) {
							cliques1.add(clique);
							}
						}
					}
				}
				time1 += (System.nanoTime() - start1);

				////////////////// method 2
				long start2 = System.nanoTime();
				for (int vertex : randomOrder) {

					// eliminateNode
					BitSet U = new BitSet(h2.V());
					BitSet fa_U_G1_W = new BitSet(h2.V());

					BitSet W = (BitSet) remaining2.clone();
					W.flip(0, h2.V());

					TriangulationByDFS.eliminateVertex_DCM_2010(h2, remaining2, vertex, U, fa_U_G1_W);

					fa_U_G1_W.andNot(W); // remove visited.

					// remove

					/**
					 * Remove old cliques and update table size where changed
					 * are the nodes that have added/removed edgees
					 */
					Iterator<BitSet> it = cliques2.iterator();
					while (it.hasNext()) {
						BitSet clique = it.next();

						if (clique.intersects(U)) {
							if (!W.intersects(clique)) {
								it.remove();
							}
						}

					}

					/**
					 * Find new cliques and update table size using
					 * Bron-Kerbosch
					 */
					List<BitSet> newCliques = new BronKerboschCliqueFinder(h2, fa_U_G1_W).getAllMaximalCliques();
					it = newCliques.iterator();
					while (it.hasNext()) {
						BitSet clique = it.next();

						if (clique.intersects(U)) {
							// if (isClique(clique, W, m_H)) {
							if (utils.Helper.isClique(clique, h2)) {
								cliques2.add(clique);
							}
						}

					}

				}
				time2 += (System.nanoTime() - start2);

				// method 3
				long start3 = System.nanoTime();
				for (int vertex3 : randomOrder) {

					/** let m = copy(n) */

					// eliminateNode
					BitSet U = new BitSet(h3.V());
					BitSet fa_F_G1 = new BitSet(h3.V());
					TriangulationByDFS_DCM_2015.eliminateVertex_DCM_2015(h3, remaining3, vertex3, U, fa_F_G1);

					// remove

					/**
					 * Remove old cliques and update table size where changed
					 * are the nodes that have added/removed edgees
					 */
					Iterator<BitSet> it = cliques3.iterator();
					while (it.hasNext()) {
						BitSet clique = it.next();
						if (clique.equals(set_intersection(clique, fa_F_G1))) {
							it.remove();
						}
					}

					/**
					 * Find new cliques and update table size using
					 * Bron-Kerbosch
					 */
					List<BitSet> newCliques = new BronKerboschCliqueFinder(h3, fa_F_G1).getAllMaximalCliques();
					it = newCliques.iterator();
					while (it.hasNext()) {
						BitSet clique = it.next();
						if (utils.Helper.isClique(clique, h3)) {
							cliques3.add(clique);
						}
					}
				}
				time3 += (System.nanoTime() - start3);

				
					// test
					System.out.print(utils.Helper.totalTableSize(cliques1, weights) + "\t"
							+ utils.Helper.totalTableSize(cliques2, weights) + "\t"
							+ utils.Helper.totalTableSize(cliques3, weights));
					System.out.println();

		
				
				
			}

			System.out.println(g.V() + "," + g.E() + "," + g.density() + "," + time1 / 1000000000.0 + ","
					+ time2 / 1000000000.0 + "," + time3 / 1000000000.0);

		}

	}
}