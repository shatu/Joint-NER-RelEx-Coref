package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;


import edu.illinois.cs.cogcomp.sl.core.IStructure;

public class CorefLabel implements IStructure {
	
	public boolean label;
	
	public CorefLabel(boolean label) {
		this.label = label;
	}
	
	public CorefLabel(CorefLabel other) {
		this.label = other.label;
	}
	
	@Override
	public String toString() {
		return label+"";
	}
	
	public static float getLoss(CorefLabel gold, CorefLabel pred) {
		if(gold.toString().equals(pred.toString())) {
			return 0.0f;
		}
		return 1.0f;
	}

}
