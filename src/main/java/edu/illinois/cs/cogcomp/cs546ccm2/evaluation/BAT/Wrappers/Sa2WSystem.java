package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers;

import java.util.HashSet;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.ScoredAnnotation;

/**
 * A system that tags parts of a natural language text to Wikipedia concepts.
 *
 */
public interface Sa2WSystem extends Sd2WSystem, A2WSystem, Sc2WSystem{
	
	/**
	 * @param text a text to tag.
	 * @return a set containing the tags found for the given text, with a score associated to it representing
	 * the relevance of the topic to the text.
	 * @throws AnnotationException 
	 */
	public HashSet<ScoredAnnotation> solveSa2W(String text);
	
	public HashSet<Annotation> solveA2W(String text, float threshold);
}
