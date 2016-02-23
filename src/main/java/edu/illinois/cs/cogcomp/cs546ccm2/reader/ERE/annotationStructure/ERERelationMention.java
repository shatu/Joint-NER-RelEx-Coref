package edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ERERelationMention implements Serializable {

	private static final long serialVersionUID = 1L;

	public String id;
	public String realis;

	public String triggerSource;
	public int triggerOffset;
	public int triggerLength;
	public String triggerStr;
	
	public List<ERERelationArgumentMention> relationArgumentMentionList = new ArrayList<ERERelationArgumentMention>();

}
