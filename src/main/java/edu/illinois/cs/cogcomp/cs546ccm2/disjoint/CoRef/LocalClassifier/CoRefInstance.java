package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.sl.core.IInstance;

public class CoRefInstance implements IInstance {
	public Constituent mSource;
	public Constituent mTarget;
	public TextAnnotation ta;
	
	public CoRefInstance(Constituent source, Constituent target) {
		this.mSource = source;
		this.mTarget = target;
		this.ta = source.getTextAnnotation();
	}

}
