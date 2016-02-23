package edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EREEvent  implements Serializable {
	
	public String id; //ID;

	public List<EREEventMention> eventMentionList = new ArrayList<EREEventMention>();

}
