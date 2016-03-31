package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifierOld;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.Paragraph;
import edu.illinois.cs.cogcomp.sl.core.IInstance;

public class NerInstance implements IInstance {
	public ACEDocument doc;
	public Paragraph para;
	public Constituent mConst;
	public TextAnnotation ta;
	
	public NerInstance(ACEDocument doc, Paragraph para, Constituent cont) {
		this.doc = doc;
		this.mConst = cont;
		this.para = para;
		this.ta = cont.getTextAnnotation();
	}

}
