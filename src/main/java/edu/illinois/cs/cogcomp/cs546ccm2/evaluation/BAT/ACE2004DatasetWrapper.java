package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEEntity;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEEntityMention;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2004.ACECorpus;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;

public class ACE2004DatasetWrapper extends ACEDatasetWrapper {
//	private List<String> textList;
//	private List<HashSet<Annotation>> docMentions;
	private List<HashSet<Annotation>> docEntities;
	
	private String NAME = "ACE2004Wrapper";
	private ACECorpus aceCorpus;
	private List<ACEDocument> docs;
	
	public ACE2004DatasetWrapper(String processedAceCorpusPath) {
		/*Initialize ACE Corpus*/
		aceCorpus = new ACECorpus();
		aceCorpus.initCorpus(processedAceCorpusPath);
		docs = new ArrayList<ACEDocument>();
	}
	
	public void loadAllDocs() {
		docs.addAll(aceCorpus.getAllDocs());
	}
	
	public void loadArabicTreebankDocs() {
		docs.addAll(aceCorpus.getarabic_treebankDocs());
	}
	
	public void loadBNewsDocs() {
		docs.addAll(aceCorpus.getbnewsDocs());
	}
	
	public void loadChineseTreebankDocs() {
		docs.addAll(aceCorpus.getchinese_treebankDocs());
	}
	
	public void loadFischerTranscriptsDocs() {
		docs.addAll(aceCorpus.getfisher_transcriptsDocs());
	}
	
	public void loadNWireDocs() {
		docs.addAll(aceCorpus.getnwireDocs());
	}
	
//	private void loadEntityMentionTags() {
//		for(ACEDocument doc: aceCorpus.getAllDocs()) {
//			docMentions.add(wrapEntityMentionTags(doc.getAllEntityMentions()));
//		}
//	}
	
//	private HashSet<Mention> wrapEntityMentionTags(List<ACEEntityMention> mentionlist) {
//		HashSet<Mention> mentionSet = new HashSet<>();
//		for(ACEEntityMention aceMention: mentionlist) {
//			Mention m = new Mention(aceMention.extentStart, aceMention.extentEnd - aceMention.extentStart + 1);
//			mentionSet.add(m);
//		}
//		
//		return mentionSet;
//	}
	
	public void loadNERTags() {
		for(ACEDocument doc: docs) {
			docEntities.add(wrapNERTags(doc.getAllEntities()));
		}
	}
	
	private HashSet<Annotation> wrapNERTags(List<ACEEntity> nerlist) {
		HashSet<Annotation> entitySet = new HashSet<>();
		for(ACEEntity aceEntity: nerlist) {
			//TODO: Check what needs to go in here for NER evaluation
			String concept = aceEntity.type; 
			for(ACEEntityMention aceMention: aceEntity.entityMentionList) {
				Annotation e = new Annotation(aceMention.extentStart, aceMention.extentEnd - aceMention.extentStart + 1, concept);
				entitySet.add(e);
			}
		}
		
		return entitySet;
	}
	
	//TODO: Understand the data format and complete this function
	@SuppressWarnings("unused")
	private void loadRelationTags() {
		throw new NotImplementedException();
	}

//	/**
//	 * TODO: Use ACECorpus object here to load text
//	 * This can wait for now
//	 */
//	public HashMap<String, String> loadBody() {
//		return null;
//	}

//	public void unifyMaps(HashMap<String, String> filenameToBody, HashMap<String, HashSet<Annotation>> filenameToAnnotations) {
//		annList = new Vector<HashSet<Annotation>>();
//		textList = new Vector<String>();
//		for (String filename : filenameToAnnotations.keySet()) {
//			textList.add(filenameToBody.get(filename));
//			annList.add(filenameToAnnotations.get(filename));
//		}
//	}

	public int getSize() {
		return docs.size();
	}

	public int getEntityMentionTagsCount() {
		return getNERTagsCount();
	}
	
	public int getNERTagsCount() {
		int count = 0;
		for (HashSet<Annotation> s : docEntities) {
			count += s.size();
		}
		return count;
	}
	
	//TODO: Understand the data format and complete this function
	public int getRelationTagsCount() {
		throw new NotImplementedException();
	}

	public String getName() {
		return NAME;
	}
	
	public List<HashSet<Annotation>> getEntityMentionTagsList() {
		return this.docEntities;
	}
	
	public List<HashSet<Annotation>> getNERTagsList() {
		return this.docEntities;
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
	public List<ACEDocument> getDocs() {
		return docs;
	}

}
