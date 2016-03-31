package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import edu.illinois.cs.cogcomp.sl.core.IStructure;



public class NERLabel implements IStructure{
	public int output = -1;
	
	public NERLabel(int y){
		output = y;
		assert output > -1;
	}


	@Override
	public String toString() {		
		return "" + output;
	}

	@Override
	public boolean equals(Object aThat) {
		// check for self-comparison
		if (this == aThat)
			return true;

		if (!(aThat instanceof NERLabel))
			return false;

		// cast to native object is now safe
		NERLabel that = (NERLabel) aThat;
		if (this.output == that.output){
			return true;
		}
		return false;
	}


	@Override
	public int hashCode() {
		return output;
	}

	

}
