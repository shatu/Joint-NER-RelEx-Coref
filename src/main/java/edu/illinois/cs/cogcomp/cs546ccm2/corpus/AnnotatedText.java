package edu.illinois.cs.cogcomp.cs546ccm2.corpus;


import java.io.Serializable;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;

public class AnnotatedText implements Serializable {
	
	private TextAnnotation ta;
	
	public AnnotatedText() {
		ta = null;
	}
	public AnnotatedText(TextAnnotation ta) {
		this.ta = ta;
	}
	
	public void setTa(TextAnnotation ta) {
		this.ta = ta;
	}

	public TextAnnotation getTa() {
		return ta;
	}
}
