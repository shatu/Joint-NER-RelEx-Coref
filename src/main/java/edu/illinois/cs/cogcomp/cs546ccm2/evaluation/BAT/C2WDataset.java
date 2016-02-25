package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT;

import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;

import java.util.HashSet;


public interface C2WDataset extends TopicDataset {
	
	/**Note: this value should not be used as a parameter for the taggers.
	 * @return the number of annotations in the whole dataset.
	 */
	public int getTagsCount();
	
	public List<HashSet<Tag>> getC2WGoldStandardList();
	
}
