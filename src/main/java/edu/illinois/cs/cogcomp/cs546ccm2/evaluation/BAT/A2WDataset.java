package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT;

import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;

import java.util.HashSet;

public interface A2WDataset extends C2WDataset, D2WDataset {

	/**
	 * Note: the order of the elements in this list must be the same of those returned by getTextIterator().
	 * @return a list of the annotations of the text included in the dataset.
	 */
	public List<HashSet<Annotation>> getA2WGoldStandardList();


}
