package edu.illinois.cs.cogcomp.cs546ccm2.corpus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ACEEntity  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String classEntity;//CLASS;
	public String id;//ID;
	public String type;//TYPE;
	public String subtype;//SUBTYPE; //null for ACE2005

	public List<ACEEntityMention> entityMentionList = new ArrayList<ACEEntityMention>();
}
