package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Metrics;

/**
 * Adopted by Shashank from BAT-Framework
 */

import java.io.Serializable;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;

public class RelatednessMetricsRecord implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private Tag tag1;
	private Tag tag2;
	private float relatedness;

	public RelatednessMetricsRecord(String entity1, String entity2, float relatedness) {
		this.tag1 = new Tag(entity1);
		this.tag2 = new Tag(entity2);
		this.relatedness = relatedness;
	}

	public String getEntity1() {
		return tag1.getConcept();
	}

	public String getEntity2() {
		return tag2.getConcept();
	}

	public float getRelatedness() {
		return relatedness;
	}

	@Override
	public Object clone() {
		return new RelatednessMetricsRecord(this.getEntity1(), this.getEntity2(),
				this.getRelatedness());
	}
	
	@Override
	public String toString(){
		return "("+tag1.getConcept()+","+tag2.getConcept()+") -> "+relatedness;
	}
}
