package edu.illinois.cs.cogcomp.cs546ccm2.reader.ace2005.annotationStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ACEValue implements Serializable {

	public String id;//ID;
	public String type;//TYPE;

	public List<ACEValueMention> valueMentionList = new ArrayList<ACEValueMention>();
}
