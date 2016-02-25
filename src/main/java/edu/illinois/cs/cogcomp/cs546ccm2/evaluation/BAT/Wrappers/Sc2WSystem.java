package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers;

import java.util.HashSet;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.ScoredTag;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;

/**
 * A system that tags parts of a natural language text to Wikipedia concepts, assigning a score to the concepts
 * representing the likelihood that the tag is correct.
 *
 */
public interface Sc2WSystem extends C2WSystem  {

	public HashSet<ScoredTag> solveSc2W(String text);
	
	public HashSet<Tag> solveC2W(String text, float threshold);
	
}
