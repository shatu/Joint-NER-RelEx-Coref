package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import java.util.Map;

import edu.illinois.cs.cogcomp.sl.core.SLProblem;


public class LabeledNERData extends SLProblem{
	/**
	 * The label mapping
	 */
	public final Map<String, Integer> labelMapping;

	/**
	 * Number of total features
	 */
	public final int numFeatures;

	
	public LabeledNERData(Map<String, Integer> m, Integer numfeatures) {		
		labelMapping = m;
		numFeatures = numfeatures;
	}
}
