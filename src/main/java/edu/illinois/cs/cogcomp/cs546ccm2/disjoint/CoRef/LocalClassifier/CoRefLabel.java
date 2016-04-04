package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;

import edu.illinois.cs.cogcomp.sl.core.IStructure;

public class CoRefLabel implements IStructure {
	
	public String type;
	
	public CoRefLabel(String type) {
		this.type = type;
	}
	
	public CoRefLabel(CoRefLabel other) {
		this.type = other.type;
	}
	
	@Override
	public String toString() {
		return this.type;
	}
	
	public static float getLoss(CoRefLabel gold, CoRefLabel pred) {
		if(gold.toString().equals(pred.toString())) {
			return 0.0f;
		}
		else
			return 1.0f;
	}

}
