package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;

/**
 * 
 * Interface for all NER systems
 * 
 * @author shashank
 *
 */

public interface ANER {
	public void labelText(TextAnnotation ta) throws AnnotatorException;
	public String getName();
}
