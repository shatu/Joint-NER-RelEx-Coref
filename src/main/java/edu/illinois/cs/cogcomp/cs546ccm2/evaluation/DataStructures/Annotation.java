package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.DataStructures;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

/**
 * Adopted by Shashank from BAT-Framework
 * 
 * An annotation is an association between a mention in a text and a concept.
 *
 */

public class Annotation extends Tag implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private Mention m;
	
	public Annotation(int position, int length, String concept) {
		super(concept);
		this.m = new Mention(position, length);
		if (position < 0) throw new RuntimeException("Annotation with negative position.");
	}

	public int getPosition() {
		return m.getPosition();
	}

	public int getLength() {
		return m.getLength();
	}

	@Override 
	public boolean equals(Object a) {
		Annotation ann = (Annotation) a;
		return (m.equals(ann.m) && (this.getConcept().equals(ann.getConcept())));
	}
	
	public Mention getMention() {
		Mention emm = new Mention(m.getPosition(), m.getLength());
		return emm;
	}

	@Override 
	public int hashCode() {
		return (this.getConcept().hashCode() ^ m.hashCode());
	}

	@Override
	public int compareTo(Tag o) {
		if (o instanceof Annotation) {
			return this.getPosition() - ((Annotation)o).getPosition();
		}
		return super.compareTo(o);
	}

	@Override public Object clone() {
		return new Annotation(this.getPosition(), this.getLength(), this.getConcept());
	}
	
	public boolean overlaps(Annotation t) {
		return m.overlaps(t.m);
	}
	

	public boolean overlaps(Mention men) {
		return m.overlaps(men);
	}
	
	/*
	 * Deletes overlapping mentions and keeps around only the longest spans
	 * 
	 * TODO: Should this function be moved to Utils
	 */
	public static <T extends Annotation> HashSet<T> deleteOverlappingAnnotations(HashSet<T> anns) {
		Vector<T> annsList = new Vector<T>(anns);
		HashSet<T> res = new HashSet<T>();
		Collections.sort(annsList);
		for (int i=0; i<annsList.size(); i++){
			T bestCandidate = annsList.get(i);
			/* find conflicting annotations*/
			int j=i+1;
			while (j<annsList.size() && bestCandidate.overlaps(annsList.get(j))) {
				//System.out.printf("Dataset is malformed: tag with position,length,wid [ %d, %d, %d] overlaps with tag [ %d, %d, %d]. Discarding tag with smallest length.%n", bestCandidate.position, bestCandidate.length, bestCandidate.getWikipediaArticle(), annsList.get(j).position, annsList.get(j).length, annsList.get(j).getWikipediaArticle());
				if (bestCandidate.getLength() < annsList.get(j).getLength())
					bestCandidate = annsList.get(j);
				j++;
			}
			i=j-1;
			res.add(bestCandidate);
		}
		return res;
	}
}
