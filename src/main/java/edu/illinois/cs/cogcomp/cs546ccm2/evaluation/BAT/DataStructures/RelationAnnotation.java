package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Adopted by Shashank from BAT-Framework
 * 
 * An relation annotation is for book-keeping relations
 *
 */

public class RelationAnnotation implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private Mention arg1;
	private Mention arg2;
	private String relationType;
	
	public RelationAnnotation(int positionL, int lengthL, int positionR, int lengthR, String relationType) {
		if (positionL < 0) throw new RuntimeException("Mention with negative position.");
		if (positionR < 0) throw new RuntimeException("Mention with negative position.");
		this.arg1 = new Mention(positionL, lengthL);
		this.arg2 = new Mention(positionR, lengthR);
		this.relationType = relationType;
	}

	public int getPositionL() {
		return arg1.getPosition();
	}
	
	public int getPositionR() {
		return arg2.getPosition();
	}

	public int getLengthL() {
		return arg1.getLength();
	}
	
	public int getLengthR() {
		return arg2.getLength();
	}
	
	public String getRelationType() {
		return relationType;
	}

	@Override 
	public boolean equals(Object a) {
		RelationAnnotation ann = (RelationAnnotation) a;
		return (this.arg1.equals(ann.arg1) && this.arg2.equals(ann.arg2) && this.getRelationType().equals(ann.getRelationType()));
	}
	
	public Mention getMentionLeft() {
		Mention emm = new Mention(arg1.getPosition(), arg1.getLength());
		return emm;
	}
	
	public Mention getMentionRight() {
		Mention emm = new Mention(arg2.getPosition(), arg2.getLength());
		return emm;
	}

	@Override 
	public int hashCode() {
		return (this.relationType.hashCode() ^ arg1.hashCode() ^ arg2.hashCode());
	}
//
//	//TODO: FIXME
//	public int compareTo(Object o) {
//		if (o instanceof RelationAnnotation) {
//			return this.getPosition() - ((RelationAnnotation)o).getPosition();
//		}
//		return super.compareTo(o);
//	}

	@Override 
	public Object clone() {
		return new RelationAnnotation(getPositionL(), getLengthL(), getPositionR(), getLengthR(), getRelationType());
	}

	//TODO
	public static HashSet<RelationAnnotation> deleteOverlappingAnnotations(HashSet<RelationAnnotation> s) {
		return s;
	}
	
//	
//	//TODO: FIXME
//	public boolean overlaps(RelationAnnotation t) {
//		return m.overlaps(t.m);
//	}
//	
//
//	//TODO: FIXME
//	public boolean overlaps(RelationAnnotation rel) {
//		return rel.mentionL.overlaps(rel.mentionL);
//	}
	
//	/*
//	 * Deletes overlapping mentions and keeps around only the longest spans
//	 * 
//	 * TODO: Should this function be moved to Utils
//	 * TODO: FIXME
//	 */
//	public static <T extends RelationAnnotation> HashSet<T> deleteOverlappingAnnotations(HashSet<T> anns) {
//		Vector<T> annsList = new Vector<T>(anns);
//		HashSet<T> res = new HashSet<T>();
//		Collections.sort(annsList);
//		for (int i=0; i<annsList.size(); i++){
//			T bestCandidate = annsList.get(i);
//			/* find conflicting annotations*/
//			int j=i+1;
//			while (j<annsList.size() && bestCandidate.overlaps(annsList.get(j))) {
//				//System.out.printf("Dataset is malformed: tag with position,length,wid [ %d, %d, %d] overlaps with tag [ %d, %d, %d]. Discarding tag with smallest length.%n", bestCandidate.position, bestCandidate.length, bestCandidate.getWikipediaArticle(), annsList.get(j).position, annsList.get(j).length, annsList.get(j).getWikipediaArticle());
//				if (bestCandidate.getLength() < annsList.get(j).getLength())
//					bestCandidate = annsList.get(j);
//				j++;
//			}
//			i=j-1;
//			res.add(bestCandidate);
//		}
//		return res;
//	}
}
