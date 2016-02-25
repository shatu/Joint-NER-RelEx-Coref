package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT;

import java.util.List;


public interface TopicDataset {
	/**
	 * @return the size of the dataset (i.e. the number of annotated texts contained in the dataset)
	 */
	public int getSize();
	
	public String getName();
	
	/**Note: the order of the elements in this list must be the same of those returned by getAnnotationsIterator().
	 * @return an iterator over the texts of this dataset (w/o annotations).
	 */
	public List<String> getTextInstanceList();
}
