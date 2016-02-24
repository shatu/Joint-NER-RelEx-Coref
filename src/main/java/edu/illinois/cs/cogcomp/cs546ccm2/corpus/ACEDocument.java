package edu.illinois.cs.cogcomp.cs546ccm2.corpus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;

/**
 * TODO: replace AnnotatedText with TextAnnotation
 *
 */
public class ACEDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	public ACEDocumentAnnotation aceAnnotation;
	
	public List<AnnotatedText> taList = new ArrayList<AnnotatedText>();
	
	public String orginalContent;
	
	public String contentRemovingTags;
	
	public List<String> originalLines;
	
	public List<Pair<String, Paragraph>> paragraphs;
	
}
