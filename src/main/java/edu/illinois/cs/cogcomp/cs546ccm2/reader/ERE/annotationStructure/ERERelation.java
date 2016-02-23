package edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ERERelation  implements Serializable {
	
	public String id; //ID;
	public String type; //ID;
	public String subtype; //ID;

	public List<ERERelationMention> relationMentionList = new ArrayList<ERERelationMention>();

}
