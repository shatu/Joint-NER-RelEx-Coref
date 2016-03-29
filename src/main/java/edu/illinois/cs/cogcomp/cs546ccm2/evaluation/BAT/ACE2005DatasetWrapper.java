package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEEntity;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEEntityMention;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.Paragraph;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.ACECorpus;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;

public class ACE2005DatasetWrapper extends ACEDatasetWrapper {
//	private List<String> textList;
//	private List<HashSet<Annotation>> docMentions;
	private List<HashSet<Annotation>> docEntities;
	private List<ACEDocument> docs;
	private ACECorpus aceCorpus;
	
	
	private String NAME = "ACE2005Wrapper";
	
	public ACE2005DatasetWrapper(String processedAceCorpusPath) {
		/*Initialize ACE Corpus*/
		aceCorpus = new ACECorpus();
		aceCorpus.initCorpus(processedAceCorpusPath);
		
		docEntities = new ArrayList<>();
		docs = new ArrayList<>();
		
//		loadEntityMentionTags();
//		loadRelationTags();
	}
	
	public void loadAllDocs() {
		docs.addAll(aceCorpus.getAllDocs());
	}
	
	public void loadBCDocs() {
		docs.addAll(aceCorpus.getbcDocs());
	}
	
	public void loadBNDocs() {
		docs.addAll(aceCorpus.getbnDocs());
	}
	
	public void loadCTSDocs() {
		docs.addAll(aceCorpus.getctsDocs());
	}
	
	public void loadNWDocs() {
		docs.addAll(aceCorpus.getnwDocs());
	}
	
	public void loadUNDocs() {
		docs.addAll(aceCorpus.getunDocs());
	}
	
	public void loadWLDocs() {
		docs.addAll(aceCorpus.getwlDocs());
	}
	
//	private void loadEntityMentionTags() {
//		for(ACEDocument doc: docs) {
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
	
	public void loadNERTagsFromView() {
		for(ACEDocument doc: docs) {
			docEntities.add(wrapNERTagsFromView(doc));
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
	
	private HashSet<Annotation> wrapNERTagsFromView(ACEDocument doc) {
		HashSet<Annotation> entitySet = new HashSet<>();
		List<Pair<String, Paragraph>> paragraphs = doc.paragraphs;
		List<Paragraph> contentParas = new ArrayList<>();
		for(Pair<String, Paragraph> pair: paragraphs) {
			if(pair.getFirst().equals("text"))
				contentParas.add(pair.getSecond());
		}
		
		int i=0;
		for(AnnotatedText ta: doc.taList) {
			List<Constituent> docAnnots;
			docAnnots = ta.getTa().getView(CCM2Constants.NERGold).getConstituents();
				
			for(Constituent cons: docAnnots) {
				Annotation annot = new Annotation(cons.getStartCharOffset() + contentParas.get(i).offsetFilterTags,
						cons.getEndCharOffset() - cons.getStartCharOffset(), cons.getLabel());
				entitySet.add(annot);
			}
			i++;
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

	public static void main(String args[]) {
		String ace05InputDir = "data/ACE2005_processed/";
		ACE2005DatasetWrapper ace05 = new ACE2005DatasetWrapper(ace05InputDir);
		ace05.loadAllDocs();
		ace05.loadNERTags();
		
		HashSet<String> NerTags = new HashSet<>();
		
		for(HashSet<Annotation> tags: ace05.getEntityMentionTagsList()) {
			for (Annotation annot: tags) {
				NerTags.add(annot.getConcept());
			}
		}
		
		for(String label: NerTags) {
			System.out.println(label);
		}
		
	}
	
	
}
