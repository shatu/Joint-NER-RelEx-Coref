package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx.LocalClassifier;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.Paragraph;
import edu.illinois.cs.cogcomp.sl.core.IInstance;

public class RelInstance implements IInstance {
	public ACEDocument doc;
	public Paragraph para;
	public Constituent mSource;
	public Constituent mTarget;
	public TextAnnotation ta;
	
	public RelInstance(ACEDocument doc, Paragraph para, Constituent source, Constituent target) {
		this.doc = doc;
		this.mSource = source;
		this.mTarget = target;
		this.para = para;
		this.ta = source.getTextAnnotation();
	}

}
