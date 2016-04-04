package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;

/**
 * 
 * Interface for all CoRef systems
 * 
 * @author shashank
 *
 */

public interface ACoRef {
	public void labelText(TextAnnotation ta) throws AnnotatorException;
	public String getName();
}
