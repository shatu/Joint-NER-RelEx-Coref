package edu.illinois.cs.cogcomp.cs546ccm2.reader.ace2005.annotationStructure;

import java.io.Serializable;

public class ACETimeExMention implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String id;
	
	public int extentStart;
	public int extentEnd;
	public String extent;

}
