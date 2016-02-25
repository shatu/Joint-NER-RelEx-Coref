package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures;

/**
 * Adopted by Shashank from BAT-Framework
 */

import java.io.Serializable;

public class ScoredAnnotation extends Annotation implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private float score;

	public ScoredAnnotation(int position, int length, String concept, float score) {
		super(position, length, concept);
		this.score = score;
	}
	
	public float getScore() {
		return score;
	}

	@Override 
	public Object clone() {
		ScoredAnnotation cloned;
		try {
			cloned = new ScoredAnnotation(this.getPosition(), this.getLength(), this.getConcept(), this.score);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return cloned;
	}

}
