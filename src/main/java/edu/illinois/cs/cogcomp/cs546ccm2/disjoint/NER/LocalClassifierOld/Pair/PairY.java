package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.Pair;


import edu.illinois.cs.cogcomp.sl.core.IStructure;

public class PairY implements IStructure {
	
	public String label;
	
	public PairY(String label) {
		this.label = label;
	}
	
	public PairY(PairY other) {
		this.label = other.label;
	}
	
	@Override
	public String toString() {
		return label;
	}
	
	public static float getLoss(PairY gold, PairY pred) {
		if(gold.toString().equals(pred.toString())) {
			return 0.0f;
		}
		return 1.0f;
	}

}
