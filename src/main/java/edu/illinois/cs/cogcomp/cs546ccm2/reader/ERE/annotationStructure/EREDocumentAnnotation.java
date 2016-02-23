package edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EREDocumentAnnotation implements Serializable {
	
	public String kit_id;
	public String doc_id;
	public String source_type;
	
	public List<EREEntity> entityList = new ArrayList<EREEntity>();
	public List<EREFiller> valueList = new ArrayList<EREFiller>();
	public List<ERERelation> relationList = new ArrayList<ERERelation>();
	public List<EREEvent> eventList = new ArrayList<EREEvent>();
}
