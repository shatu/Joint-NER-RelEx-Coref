package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures;

/**
 * Adopted by Shashank from BAT-Framework
 */

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;

public class Mention implements Serializable, Cloneable, Comparable<Mention> {
	private static final long serialVersionUID = 1L;
	private int position; // starting position of the annotation in the original text
	private int length; // length of the annotation in the original text

	public Mention(int position, int length) {
		this.position = position;
		this.length = length;
	}

	public int getPosition() {
		return position;
	}

	public int getLength() {
		return length;
	}

	@Override
	public boolean equals(Object m) {
		Mention men = (Mention) m;
		return (this.position == men.position && this.length == men.length);
	}

	@Override
	public int hashCode() {
		return new Integer(position).hashCode() ^ new Integer(length).hashCode();
	}

	@Override
	public Object clone() {
		return new Mention(this.getPosition(), this.getLength());
	}

	public boolean overlaps(Mention m) {
		int p1 = this.getPosition();
		int l1 = this.getLength();
		int e1 = p1 + l1 - 1;
		int p2 = m.getPosition();
		int l2 = m.getLength();
		int e2 = p2 + l2 - 1;
		return ((p1 <= p2 && p2 <= e1) || (p1 <= e2 && e2 <= e1) || (p2 <= p1 && p1 <= e2) || (p2 <= e1 && e1 <= e2));
	}

	@Override
	public int compareTo(Mention m) {
		return this.getPosition() - m.getPosition();
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", this.position, this.length);
	}
	
	public static <T extends Mention> HashSet<T> deleteOverlappingAnnotations(HashSet<T> anns) {
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
