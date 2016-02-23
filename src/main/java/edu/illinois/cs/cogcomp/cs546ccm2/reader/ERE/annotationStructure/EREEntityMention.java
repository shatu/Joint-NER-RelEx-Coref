package edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure;

import java.io.Serializable;

public class EREEntityMention implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String id;
	public String noun_type;
	public String source;
	
	public int offset;
	public int length;
	public String text;
	
}
