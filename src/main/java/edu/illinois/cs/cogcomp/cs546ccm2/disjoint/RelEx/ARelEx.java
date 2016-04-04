package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;

/**
 * 
 * Interface for all RelEx systems
 * 
 * @author shashank
 *
 */

public interface ARelEx {
	public void labelText(TextAnnotation ta) throws AnnotatorException;
	public String getName();
}
