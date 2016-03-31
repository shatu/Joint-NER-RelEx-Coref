package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;


import edu.illinois.cs.cogcomp.sl.core.IStructure;

public class RelY implements IStructure {
	
	public boolean relevance;
	
	public RelY(boolean relevance) {
		this.relevance = relevance;
	}
	
	public RelY(RelY other) {
		this.relevance = other.relevance;
	}
	
	@Override
	public String toString() {
		return relevance+"";
	}
	
	public static float getLoss(RelY gold, RelY pred) {
		if(gold.toString().equals(pred.toString())) {
			return 0.0f;
		}
		return 1.0f;
	}

}
