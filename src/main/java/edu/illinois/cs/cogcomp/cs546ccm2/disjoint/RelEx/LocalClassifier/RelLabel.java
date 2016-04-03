package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx.LocalClassifier;

import edu.illinois.cs.cogcomp.sl.core.IStructure;

public class RelLabel implements IStructure {
	
	public String type;
	
	public RelLabel(String type) {
		this.type = type;
	}
	
	public RelLabel(RelLabel other) {
		this.type = other.type;
	}
	
	@Override
	public String toString() {
		return this.type;
	}
	
	public static float getLoss(RelLabel gold, RelLabel pred) {
		if(gold.toString().equals(pred.toString())) {
			return 0.0f;
		}
		else
			return 1.0f;
	}

}
