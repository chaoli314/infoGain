/**
 * HUGIN_format
 */
package io;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import factor_graph.*;
import bayesian_networks.*;

/**
 * @author chao
 *
 */
public class HUGIN_format {
	public static BayesianNetwork load(String filename) {
		
		String netName = filename.substring(filename.lastIndexOf("/") + 1, filename.length() );
		Map<String, Integer> variableIndex = new TreeMap<>();
		List<List<String>> LsLsState = new ArrayList<>();
		List<Factor> factors = new ArrayList<>();
		List<List<Integer>> LsLsParent = new ArrayList<>();
		// IO operation.
		StringBuffer hugin = new StringBuffer();
		try {
			Scanner in = new Scanner(new FileReader(filename));
			while (in.hasNextLine())
				hugin.append(in.nextLine());
			in.close();
		} catch (IOException e) {
			System.err.println("Exception when reading Hugin Format file.");
			e.printStackTrace();
		}

		// Grab individual nodes	螟画焚縺ｮ諠�蝣ｱ
		Pattern p_node = Pattern.compile("node\\s+([^\\{\\s]+)\\s*\\{.*?states\\s*=\\s*\\((.+?)\\);.*?\\}");
		Pattern P_state = Pattern.compile("\"(.+?)\"");
		
		Matcher m_node = p_node.matcher(hugin);
		for (int index = 0; m_node.find(); index++) {
			String variableName = m_node.group(1);// variable name
			variableIndex.put(variableName, index);
			String states = m_node.group(2);// states of variable
			Matcher m_state = P_state.matcher(states);
			List<String> LsState = new ArrayList<>();
			while (m_state.find()) {
				LsState.add(m_state.group(1));
			}
			LsLsState.add(LsState);
		}

		// Grab individual potential	遒ｺ邇�陦ｨ
		Pattern p_potential = Pattern.compile("potential\\s*\\(\\s*([^\\|\\)\\s]+)(\\s*\\|\\s*([^\\)]*))?\\s*\\)\\s*\\{.*?data\\s*=\\s*(\\([^\\}]+\\))[^\\}]*\\}");
		
		
		//Pattern p_potential = Pattern.compile("potential\\s*\\(\\s*([^\\|\\)\\s]+)(\\s*\\|([^\\)]+))?\\s*\\)\\s*\\{[^\\}]*data\\s*=\\s*(\\(+[^\\}]+\\))[^\\}]*\\}");
		
		// .compile("potential\\s*\\(([^\\|\\)\\s]+)(\\s*\\|([^\\)]+))?\\s*\\)\\s*\\{[^\\}]*data\\s*=\\s*(\\(+[^\\}]*\\))+[^\\}]*\\}");
				
		// 蟄舌ヮ繝ｼ繝曳roup1	 隕ｪ繝弱�ｼ繝曳roup3 	遒ｺ邇㍑roup4縲�縲�縲�縲�縲�縲�縲�縲�縲�
		Pattern p_variableName = Pattern.compile("\\S+");
		Pattern p_probability = Pattern.compile("(?<=\\()[^\\(\\)]+(?=\\))");
		
		Matcher m_potential = p_potential.matcher(hugin);
		while (m_potential.find()) {
			List<String> varNames = new ArrayList<>();
			String child = m_potential.group(1);
			varNames.add(child);
			String parents = null;
			if ((parents = m_potential.group(3)) != null) {
				Matcher m_parents = p_variableName.matcher(parents);
				while (m_parents.find()) {
					varNames.add(m_parents.group());
				}
			}
			int numberOfVars = varNames.size();
			List<Var> varSet = new ArrayList<>();
			
			int label0 = variableIndex.get(varNames.get(0));
			int range0 = LsLsState.get(label0).size();
			varSet.add(new Var(label0, range0));
			
			List<Integer> LsParent = new ArrayList<>();
			for (int i = numberOfVars-1; i >0 ; i--) {
				int label = variableIndex.get(varNames.get(i));
				LsParent.add(label);
				int range = LsLsState.get(label).size();
				varSet.add(new Var(label, range));
			}
			int tableSize = utils.Helper.tableSize(varSet);
			double[] value = new double[tableSize];
			String data = m_potential.group(4);
			Matcher m_probability = p_probability.matcher(data);
			int index = 0;
			while (m_probability.find()) {
				Scanner in = new Scanner(m_probability.group());
				while (in.hasNextDouble()) {
					value[index++] = in.nextDouble();
				}
				in.close();
			}
			LsLsParent.add(LsParent);
			factors.add(new Factor(varSet, value));
		}
		return new BayesianNetwork(netName, variableIndex, LsLsState, factors,LsLsParent);
	}

	public static void write(BayesianNetwork bn, String filename) {
	}
}
