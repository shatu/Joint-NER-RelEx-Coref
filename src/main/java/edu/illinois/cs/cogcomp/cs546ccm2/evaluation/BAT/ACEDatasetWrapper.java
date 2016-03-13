package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT;

import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACorpus;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;

import java.util.HashSet;

public abstract class ACEDatasetWrapper implements A2WDataset {

	public ACorpus aceCorpus;
	
	protected String NAME;

	public abstract boolean isCorpusReady();
	
	public abstract int getEntityMentionTagsCount();
	
	public abstract int getNERTagsCount();
	
	public abstract int getRelationTagsCount();
	
	public abstract List<HashSet<Annotation>> getEntityMentionTagsList();
	
	public abstract List<HashSet<Annotation>> getNERTagsList();
	
	//TODO: Add similar functions for Relations (and CoRef?)

}
