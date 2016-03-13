package edu.illinois.cs.cogcomp.cs546ccm2.corpus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ACERelation  implements Serializable {

	private static final long serialVersionUID = 1L;
	public String id; //ID;
	public String type;//TYPE;
	public String subtype;//SUBTYPE;    //null for ACE2005
	public String modality;//MODALITY;  //null for ACE2005
	public String tense;//TENSE;        //null for ACE2005
	
	public List<ACERelationArgument> relationArgumentList = new ArrayList<ACERelationArgument>();
	public List<ACERelationMention> relationMentionList = new ArrayList<ACERelationMention>();

}
