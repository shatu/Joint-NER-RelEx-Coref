package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers;

import java.util.HashSet;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.ScoredAnnotation;

/**
 * A system that tags parts of a natural language text to Wikipedia concepts.
 *
 */
public interface Sd2WSystem extends D2WSystem{
	
	/**
	 * @param text a text to tag.
	 * @param list of mentions
	 * @return a set containing the tags found for the given mentions, with a score associated to it representing
	 * the relevance of the topic to the text.
	 * @throws AnnotationException 
	 */
	public HashSet<ScoredAnnotation> solveSd2W(String text, HashSet<Mention> mentions);
	
	public HashSet<ScoredAnnotation> solveSd2W(String text, HashSet<Mention> mentions, boolean allow_overlap);
	
	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions, float threshold);
	
	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions, float threshold, boolean allow_overlap);

}
