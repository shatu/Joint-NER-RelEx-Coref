package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers;

import java.util.HashSet;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;

public interface D2WSystem extends TopicSystem {
	
	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions);
	
	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions, boolean allow_overlap);
}
