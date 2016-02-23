package edu.illinois.cs.cogcomp.cs546ccm2.reader.ace2005.annotationStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ACEEntity  implements Serializable {
	
	public String classEntity;//CLASS;
	public String id;//ID;
	public String type;//TYPE;
	public String subtype;//SUBTYPE;

	public List<ACEEntityMention> entityMentionList = new ArrayList<ACEEntityMention>();
}
