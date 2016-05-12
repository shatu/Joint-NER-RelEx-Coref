package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.sl.core.IInstance;

public class NerInstance implements IInstance {
	public Constituent mConst;
	public TextAnnotation ta;
	
	public NerInstance(Constituent cont) {
		this.mConst = cont;
		this.ta = cont.getTextAnnotation();
	}
}
