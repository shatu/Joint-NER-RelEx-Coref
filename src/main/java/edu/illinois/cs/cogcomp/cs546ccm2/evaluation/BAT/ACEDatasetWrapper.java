package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT;

import java.util.List;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.RelationAnnotation;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;

import java.util.HashSet;

public abstract class ACEDatasetWrapper implements A2WDataset {

	protected ACEReader aceReader;
	
	protected String NAME;
	
	public abstract int getEntityMentionTagsCount();
	
	public abstract int getNERTagsCount();
	
	public abstract int getRelationTagsCount();
	
	public abstract List<HashSet<Mention>> getEntityMentionTagsList();
	
	public abstract List<HashSet<Annotation>> getNERTagsList();
	
	public abstract List<HashSet<RelationAnnotation>> getRelationTagsList();
	
	public abstract List<TextAnnotation> getDocs();
	
	//TODO: Add similar functions for Relations (and CoRef?)

}
