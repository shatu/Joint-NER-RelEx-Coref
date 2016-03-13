package edu.illinois.cs.cogcomp.cs546ccm2.corpus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ACEDocumentAnnotation implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public String id; //DOCID attribute of the Document node in the original ACE file 
	
	public List<ACEEntity> entityList = new ArrayList<ACEEntity>();
	public List<ACEValue> valueList = new ArrayList<ACEValue>();
	public List<ACETimeEx> timeExList = new ArrayList<ACETimeEx>();
	public List<ACERelation> relationList = new ArrayList<ACERelation>();
	public List<ACEEvent> eventList = new ArrayList<ACEEvent>();
}
