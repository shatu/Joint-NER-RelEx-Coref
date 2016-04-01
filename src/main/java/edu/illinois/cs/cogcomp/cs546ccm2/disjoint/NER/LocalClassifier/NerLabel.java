package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import edu.illinois.cs.cogcomp.sl.core.IStructure;

public class NerLabel implements IStructure {
	
	public String type;
	
	public NerLabel(String type) {
		this.type = type;
	}
	
	public NerLabel(NerLabel other) {
		this.type = other.type;
	}
	
	@Override
	public String toString() {
		return this.type;
	}
	
	public static float getLoss(NerLabel gold, NerLabel pred) {
		if(gold.toString().equals(pred.toString())) {
			return 0.0f;
		}
		else
			return 1.0f;
	}

}
