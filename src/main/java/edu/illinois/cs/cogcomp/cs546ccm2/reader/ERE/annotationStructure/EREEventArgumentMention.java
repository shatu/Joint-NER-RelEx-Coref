package edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure;

import java.io.Serializable;

public class EREEventArgumentMention implements Serializable {

	public boolean isFiller;
	
	public String entity_id;
	public String entity_mention_id;

	public String filler_id;

	public String role;
	public String realis;
	
	public String argStr;
}
