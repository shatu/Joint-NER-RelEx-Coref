package edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EREEntity  implements Serializable {
	
	public String id;//ID;
	public String type;//TYPE;
	public String specificity;//;

	public List<EREEntityMention> entityMentionList = new ArrayList<EREEntityMention>();
}
