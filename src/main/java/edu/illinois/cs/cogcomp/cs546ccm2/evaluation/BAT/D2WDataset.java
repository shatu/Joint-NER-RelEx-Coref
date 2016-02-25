package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT;

import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;

public interface D2WDataset extends TopicDataset {
	public List<HashSet<Mention>> getMentionsInstanceList();
	
	public List<HashSet<Annotation>> getD2WGoldStandardList();
}
