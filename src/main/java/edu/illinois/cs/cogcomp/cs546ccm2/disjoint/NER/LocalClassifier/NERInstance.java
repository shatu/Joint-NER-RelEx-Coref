package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;

public class NERInstance implements IInstance {
	public final IFeatureVector baseFv;
	public final int baseNfeature;
	public final int numberOfClasses;
	
	public NERInstance(int total_n_fea,int total_number_class,IFeatureVector base_fv){
		this.baseFv = base_fv;
		this.baseNfeature = total_n_fea; 
		this.numberOfClasses = total_number_class;
	}
}
