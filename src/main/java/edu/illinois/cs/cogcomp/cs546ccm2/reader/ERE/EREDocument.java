package edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.EREDocumentAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure.PostParagraphTAC15;

public class EREDocument implements Serializable {

	public EREDocumentAnnotation ereAnnotation;
	
	public List<AnnotatedText> taList = new ArrayList<AnnotatedText>();
	
	public String orginalContent;
	
	public String contentRemovingTags;
	
	public List<String> originalLines;
	
	public List<PostParagraphTAC15> paragraphs;
	
}
