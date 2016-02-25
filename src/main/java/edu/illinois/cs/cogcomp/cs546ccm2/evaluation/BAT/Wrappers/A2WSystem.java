package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers;

import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.A2WDataset;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;

/**
 * A system that tags parts of a natural language text to Wikipedia concepts.
 *
 */
public interface A2WSystem extends C2WSystem, D2WSystem{
	
	/**
	 * @param text a text to tag.
	 * @return a set containing the annotation found for the given text.
	 * @throws AnnotationException 
	 */
	public HashSet<Annotation> solveA2W(String text);
	
	public List<HashSet<Annotation>> getA2WOutputAnnotationList(A2WDataset ds);

}
