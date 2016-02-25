package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers;

import java.util.HashSet;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;

/**
 * A system that tags parts of a natural language text to Wikipedia concepts.
 *
 */
public interface C2WSystem extends TopicSystem {
	
	/**
	 * @param text a text to tag.
	 * @return a set containing the annotation found for the given text.
	 * @throws AnnotationException 
	 */
	public HashSet<Tag> solveC2W(String text);
	
	

}
