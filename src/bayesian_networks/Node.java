package bayesian_networks;

import java.util.*;

/**
 * Created by Chao Li on 8/9/16.
 */
public class Node {
    public String getName() {
        return name_;
    }

    public void setName(String name) {
        this.name_ = name;
    }

    public int getNumberOfStates(){
        return states_.size();
    }

    public   int	getStateIndex(String label){
        return label2index_.get(label);
    }

    String	getStateLabel(int state){
        return states_.get(state);
    }



    public void addParent(Node newParent){
        parents_.add(newParent);
    }

    public java.util.List<Node> getParents() {
        return parents_;
    }

    private java.util.List<Node> parents_;
    private String name_;
    private java.util.List<String> states_;
    private Map<String, Integer> label2index_;
}
