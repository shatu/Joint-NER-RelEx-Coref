package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures;

import java.io.Serializable;

/**
 * Adopted by Shashank from BAT-Framework
 *
 */
public class Tag implements Comparable<Tag>, Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private String concept;

	public String getConcept() {
		return this.concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public Tag(String concept) {
		this.concept = concept;
	}

	@Override public boolean equals(Object t) {
		Tag tag = (Tag) t;
		return this.concept.equals(tag.concept);
	}

	@Override public int hashCode() {
		return this.concept.hashCode();
	}

	@Override public Object clone() {
		Tag cloned = new Tag(this.concept);
		return cloned;
	}

	@Override
	public int compareTo(Tag arg0) {
		return this.concept.compareTo(arg0.concept);
	}
}
