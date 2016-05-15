package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.RelationAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;

public class ACE2005DatasetWrapper extends ACEDatasetWrapper {
//	private List<String> textList;
	private List<HashSet<Mention>> docMentions;
	private List<HashSet<Annotation>> docEntities;
	private List<HashSet<RelationAnnotation>> docRelations;
	
	private List<TextAnnotation> docs;
	private ACEReader aceReader;
	
	private String NAME = "ACE2005Wrapper";
	
	public ACE2005DatasetWrapper(String aceCorpusPath) throws Exception {
		aceReader = new ACEReader(aceCorpusPath, false);
		
		docEntities = new ArrayList<>();
		docMentions = new ArrayList<>();
		docs = new ArrayList<>();
		docRelations = new ArrayList<>();
		
//		loadEntityMentionTags();
//		loadRelationTags();
	}
	
	public void loadAllDocs () {
		for (TextAnnotation ta : aceReader) {
			docs.add(ta);
		}
	}
	
	public void loadBCDocs () {
		throw new NotImplementedException();
	}
	
	public void loadBNDocs () {
		throw new NotImplementedException();
	}
	
	public void loadCTSDocs () {
		throw new NotImplementedException();
	}
	
	public void loadNWDocs () {
		throw new NotImplementedException();
	}
	
	public void loadUNDocs () {
		throw new NotImplementedException();
	}
	
	public void loadWLDocs () {
		throw new NotImplementedException();
	}
	
	@SuppressWarnings("unused")
	private void loadEntityMentionTags(String viewName) {
		loadMentionTags(viewName);
	}
	
//	private HashSet<Mention> wrapEntityMentionTags(List<ACEEntityMention> mentionlist) {
//		HashSet<Mention> mentionSet = new HashSet<>();
//		for(ACEEntityMention aceMention: mentionlist) {
//			Mention m = new Mention(aceMention.extentStart, aceMention.extentEnd - aceMention.extentStart + 1);
//			mentionSet.add(m);
//		}
//		
//		return mentionSet;
//	}
	
	public void loadNERTags(String viewName) {
		for(TextAnnotation doc: docs) {
			docEntities.add(wrapNERTags(doc, viewName));
		}
	}
	
	public void loadMentionTags(String viewName) {
		for(TextAnnotation doc: docs) {
			docMentions.add(wrapMentionTags(doc, viewName));
		}
	}
	
	private HashSet<Annotation> wrapNERTags(TextAnnotation ta, String viewName) {
		HashSet<Annotation> entitySet = new HashSet<>();
		
		for (Constituent cons: ta.getView(viewName).getConstituents()) {
			String concept = cons.getLabel(); 
			Annotation e = new Annotation(cons.getStartSpan(), cons.length(), concept);
			entitySet.add(e);
		}
		
		return entitySet;
	}
	
	private HashSet<Mention> wrapMentionTags(TextAnnotation ta, String viewName) {
		HashSet<Mention> mentionSet = new HashSet<>();
		
		for (Constituent cons: ta.getView(viewName).getConstituents()) {
			Mention e = new Mention(cons.getStartSpan(), cons.length());
			mentionSet.add(e);
		}
		
		return mentionSet;
	}
	
	public void loadRelationTags(String viewName) {
		for(TextAnnotation doc: docs) {
			docRelations.add(wrapRelationTags(doc, viewName));
		}
	}
	
	private HashSet<RelationAnnotation> wrapRelationTags(TextAnnotation ta, String viewName) {
		HashSet<RelationAnnotation> relationSet = new HashSet<>();
		
		List<Constituent> docAnnots = ((PredicateArgumentView)ta.getView(viewName)).getPredicates();
		
		for (Constituent cons: docAnnots) {
			if (cons.getOutgoingRelations().size() > 0) {
				for (Relation rel : cons.getOutgoingRelations()) {
					Constituent source = cons;
					Constituent target = rel.getTarget();
					String relName = rel.getRelationName();
					RelationAnnotation e = new RelationAnnotation(source.getStartSpan(), source.length(), 
							target.getStartSpan(), target.length(), 
							relName);
					
					relationSet.add(e);
				}
			}
		}
		
		return relationSet;
	}

	public int getSize () {
		return docs.size();
	}

	public int getEntityMentionTagsCount () {
		return getNERTagsCount();
	}
	
	public int getNERTagsCount () {
		int count = 0;
		for (HashSet<Annotation> s : docEntities) {
			count += s.size();
		}
		return count;
	}
	
	public int getRelationTagsCount () {
		int count = 0;
		
		for (HashSet<RelationAnnotation> s : docRelations) {
			count += s.size();
		}
		
		return count;
	}

	public String getName() {
		return NAME;
	}
	
	public List<HashSet<Mention>> getEntityMentionTagsList() {
		return this.docMentions;
	}
	
	public List<HashSet<Annotation>> getNERTagsList() {
		return this.docEntities;
	}
	
	public List<HashSet<RelationAnnotation>> getRelationTagsList() {
		return this.docRelations;
	}
	
	public List<String> getTextInstanceList() {
		throw new NotImplementedException();
	}

	public List<HashSet<Annotation>> getA2WGoldStandardList() {
		throw new NotImplementedException();
	}

	@Override
	public List<HashSet<Tag>> getC2WGoldStandardList() {
		throw new NotImplementedException();
	}

	@Override
	public List<HashSet<Mention>> getMentionsInstanceList() {
		throw new NotImplementedException();
	}

	@Override
	public List<HashSet<Annotation>> getD2WGoldStandardList() {
		throw new NotImplementedException();
	}

	@Override
	public int getTagsCount() {
		throw new NotImplementedException();
	}

	@Override
	public List<TextAnnotation> getDocs() {
		return docs;
	}

	public static void main(String args[]) throws Exception {
		String ace05InputDir = CCM2Constants.ACE05TestCorpusPath;
		ACE2005DatasetWrapper ace05 = new ACE2005DatasetWrapper(ace05InputDir);
		ace05.loadAllDocs();
//		ace05.loadNERTags(CCM2Constants.NERGoldExtent);
		
//		HashSet<String> NerTags = new HashSet<>();
//		
//		for(HashSet<Annotation> tags: ace05.getEntityMentionTagsList()) {
//			for (Annotation annot: tags) {
//				NerTags.add(annot.getConcept());
//			}
//		}
//		
//		for(String label: NerTags) {
//			System.out.println(label);
//		}
		
//		ace05.loadRelationTags(CCM2Constants.RelExGoldExtent);
//		
//		HashSet<String> RelationTags = new HashSet<>();
//		
//		for(HashSet<RelationAnnotation> tags: ace05.getRelationTagsList()) {
//			for (RelationAnnotation annot: tags) {
//				RelationTags.add(annot.getRelationType());
//			}
//		}
//		
//		for (String label: RelationTags) {
//			System.out.println(label);
//		}
		
	}
}
