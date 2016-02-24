package edu.illinois.cs.cogcomp.cs546ccm2.corpus;

import java.io.Serializable;

public class ACEEventArgumentMention implements Serializable {

	public String id;
	public String role;
	
	public int start;
	public int end;
	
	public String argStr;
}
