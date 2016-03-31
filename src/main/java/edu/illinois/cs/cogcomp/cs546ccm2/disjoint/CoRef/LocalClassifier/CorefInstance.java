package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.Paragraph;
import edu.illinois.cs.cogcomp.sl.core.IInstance;

public class CorefInstance implements IInstance {
	public ACEDocument doc;
	public Paragraph para;
	public Constituent mConst;
	public TextAnnotation ta;
	
	public CorefInstance(ACEDocument doc, Paragraph para, Constituent cont) {
		this.doc = doc;
		this.mConst = cont;
		this.para = para;
		this.ta = cont.getTextAnnotation();
	}

}
