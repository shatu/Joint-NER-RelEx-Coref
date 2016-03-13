package edu.illinois.cs.cogcomp.cs546ccm2.corpus;

import java.io.Serializable;

public class ACERelationArgumentMention implements Serializable {

	private static final long serialVersionUID = 1L;
	public String id;
	public String role; //Arg-1 or Arg-2
	
	public int start;
	public int end;
	
	public String argStr;
}
