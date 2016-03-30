package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;

/**
 * 
 * Interface for all Mention Detection systems
 * 
 * @author shashank
 *
 */

public interface AMentionDetector {
	public void labelText(TextAnnotation ta) throws AnnotatorException;
}
