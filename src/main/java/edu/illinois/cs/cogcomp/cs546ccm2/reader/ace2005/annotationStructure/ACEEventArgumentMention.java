package edu.illinois.cs.cogcomp.cs546ccm2.reader.ace2005.annotationStructure;

import java.io.Serializable;

public class ACEEventArgumentMention implements Serializable {

	public String id;
	public String role;
	
	public int start;
	public int end;
	
	public String argStr;
}
