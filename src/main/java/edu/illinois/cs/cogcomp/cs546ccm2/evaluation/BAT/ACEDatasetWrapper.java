package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT;

import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACorpus;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;

import java.util.HashSet;

public abstract class ACEDatasetWrapper implements A2WDataset {

	protected ACorpus aceCorpus;
	
	protected String NAME;
	
	public abstract int getEntityMentionTagsCount();
	
	public abstract int getNERTagsCount();
	
	public abstract int getRelationTagsCount();
	
	public abstract List<HashSet<Annotation>> getEntityMentionTagsList();
	
	public abstract List<HashSet<Annotation>> getNERTagsList();
	
	public abstract List<ACEDocument> getDocs();
	
	//TODO: Add similar functions for Relations (and CoRef?)

}
