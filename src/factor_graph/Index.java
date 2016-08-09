//最終版
/*** Copy */
package factor_graph;

import java.util.*;

import static utils.Helper.*;

/** Index * @author Chao Li */
public class Index {
	/** indexFor */
	public final static int[] indexFor(List<Var> indexVars, List<Var> forVars) {
		int numberOfVars = forVars.size();

		// The current linear index corresponding to the state of indexVars
		int _index = 0;
		int tableSize = tableSize(forVars);
		int[] index = new int[tableSize];
		// For each variable in forVars, the amount of change in _index
		int[] _ranges = new int[numberOfVars];
		// For each variable in forVars, the amount of change in _index
		int[] _sum = new int[numberOfVars];
		// For each variable in forVars, the current state
		int[] _state = new int[numberOfVars]; // 初始状态全部为 零。
		// //////////////////////////////////////////////////////////////////////////////////////////////
		for (int j = 0; j < numberOfVars; ++j)
			_ranges[j] = forVars.get(j).states();

		int sum = 1;
		for (int i = 0; i < indexVars.size(); ++i) {
			int j = forVars.indexOf(indexVars.get(i));
			_sum[j] = sum;
			sum *= _ranges[j];
		}
		// Increments the current state of forVars.
		/** i_forVars is from 1, since the index[0] = 0, */
		for (int i_forVars = 1; i_forVars < tableSize; ++i_forVars) {
			for (int i = 0; i < numberOfVars; ++i) {
				_index += _sum[i];
				if (++_state[i] < _ranges[i]) break;
				_index -= _sum[i] * _ranges[i];
				_state[i] = 0;
			}
			index[i_forVars] = _index;
		}
		return index;
	}

	/** * calcState */
	public final static int[] calcState(List<Var> vs, int linearState) {
		int[] state = new int[vs.size()];
		for (int i = 0; i < state.length; ++i) {
			state[i] = linearState % vs.get(i).states();
			linearState /= vs.get(i).states();
		}
		return state;
	}

	/** * calcLinearState */
	public final static int calcLinearState(List<Var> vs, int[] state) {
		int prod = 1;
		int linearState = 0;
		for (int i = 0; i < state.length; ++i) {
			linearState += prod * state[i];
			prod *= vs.get(i).states();
		}
		return linearState;
	}
}