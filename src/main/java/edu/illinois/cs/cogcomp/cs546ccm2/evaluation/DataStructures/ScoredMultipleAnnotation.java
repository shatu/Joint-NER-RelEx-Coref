package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.DataStructures;

/**
 * Adopted by Shashank from BAT-Framework
 */

import java.io.Serializable;

public class ScoredMultipleAnnotation extends MultipleAnnotation implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private float[] scores;

	//concepts and their scores should match the index in the arrays
	public ScoredMultipleAnnotation(int position, int length, String[] concepts, float[] scores) {
		super(position, length, concepts);
		this.scores = scores;
	}
	
	public float[] getScores() {
		return scores;
	}
	

	@Override public Object clone() {
		ScoredMultipleAnnotation cloned;
		try {
			cloned = new ScoredMultipleAnnotation(this.getPosition(), this.getLength(), this.getCandidates(), this.scores);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return cloned;
	}

}
