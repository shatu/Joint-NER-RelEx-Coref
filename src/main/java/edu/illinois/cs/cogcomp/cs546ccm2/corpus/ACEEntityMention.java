package edu.illinois.cs.cogcomp.cs546ccm2.corpus;

import java.io.Serializable;

public class ACEEntityMention implements Serializable {

	private static final long serialVersionUID = 1L;

	public String id;
	public String type;
	public String ldcType;
//	public String ldcATR;

	public int extentStart;
	public int extentEnd;
	public String extent;

	public int headStart;
	public int headEnd;
	public String head;

}
