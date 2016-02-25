package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures;

/**
 * Adopted by Shashank from BAT-Framework
 */

import java.io.Serializable;

public class ScoredTag extends Tag implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private float score;

	public ScoredTag(String concept, float score) {
		super(concept);
		this.score = score;
	}
	
	public float getScore() {
		return score;
	}

	@Override public Object clone() {
		return new ScoredTag(getConcept(), score);
	}

}
