package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures;

/**
 * Adopted by Shashank from BAT-Framework
 */

public class MultipleAnnotation extends Mention {
	private static final long serialVersionUID = 1L;
	
	private String[] candidates;
	
	public MultipleAnnotation(int position, int length, String[] candidates) {
		super(position, length);
		this.candidates = candidates;
	}
	
	public String[] getCandidates() {
		return candidates;
	}

}
