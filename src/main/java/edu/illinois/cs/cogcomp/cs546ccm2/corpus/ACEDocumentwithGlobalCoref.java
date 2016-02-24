package edu.illinois.cs.cogcomp.cs546ccm2.corpus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.CoreferenceView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.util.EventConstants;

public class ACEDocumentwithGlobalCoref extends ACEDocument implements Serializable {

//	public ACEDocumentAnnotation aceAnnotation;
//	
//	public List<AnnotatedText> taList = new ArrayList<AnnotatedText>();
//	
//	public String orginalContent;
//	
//	public String contentRemovingTags;
//	
//	public List<String> originalLines;
//	
//	public List<Pair<String, Paragraph>> paragraphs;
	
	public AnnotatedText globalTA;
	
	public boolean isDebug = false;
	
	public void buildCorefIndex () {
		this.buildIndexFromAnnotationCoref();
		this.buildIndexFromRealCoref();
		this.buildIndexFromTAList();
	}
	
	// note that the input constituent should be from taList.
	public Constituent getGlobalCoref (Constituent constituent, boolean isFromGold) {
		int start = new Integer(constituent.getAttribute("charStart")).intValue();
		int end = new Integer(constituent.getAttribute("charEnd")).intValue();
		
		int corefStart = -1;
		int corefEnd = -1;
		
		if (isFromGold) {
			if (goldCorefStartMap.containsKey(start)) {
				corefStart = goldCorefStartMap.get(start).getFirst();
				corefEnd = goldCorefStartMap.get(start).getSecond();
			}
			if (goldCorefEndMap.containsKey(end)) {
				corefStart = goldCorefEndMap.get(end).getFirst();
				corefEnd = goldCorefEndMap.get(end).getSecond();
			}
		} else {
			if (predCorefStartMap.containsKey(start)) {
				corefStart = predCorefStartMap.get(start).getFirst();
				corefEnd = predCorefStartMap.get(start).getSecond();
			}
			if (predCorefEndMap.containsKey(end)) {
				corefStart = predCorefEndMap.get(end).getFirst();
				corefEnd = predCorefEndMap.get(end).getSecond();
			}
		}
		
		TextAnnotation ta = null;
		int startToken = -1;
		int endToken = -1;
		if (startMap.containsKey(corefStart)) {
			ta = taList.get(startMap.get(corefStart).getFirst()).getTa();
			startToken = startMap.get(corefStart).getSecond();
		}
		if (endMap.containsKey(corefEnd)) {
			ta = taList.get(endMap.get(corefEnd).getFirst()).getTa();
			endToken = endMap.get(corefEnd).getSecond() + 1;
		}
		if (startToken != -1 && endToken == -1) {
			endToken = startToken + 1;
		}
		if (endToken != -1 && startToken == -1) {
			startToken = endToken - 1;
		}
		Constituent cons = null;
		if (startToken != -1 && endToken != -1) {
			cons = new Constituent("coref", "coref", ta, startToken, endToken);
		}
		return cons;
	}
	
	// HashMap<startIndex, Pair<taID, tokenID>>
	public HashMap<Integer, Pair<Integer, Integer>> startMap = new HashMap <Integer, Pair<Integer, Integer>>();
	// HashMap<endIndex, Pair<taID, tokenID>>
	public HashMap<Integer, Pair<Integer, Integer>> endMap = new HashMap <Integer, Pair<Integer, Integer>>();

	private void buildIndexFromTAList () {
		for (int i = 0; i < taList.size(); ++i) {
			TextAnnotation ta = taList.get(i).getTa();
			List<Constituent> tokenCons = ta.getView(EventConstants.TOKEN_WITH_CHAR_OFFSET).getConstituents();
			for (int j = 0; j < tokenCons.size(); ++j) {
//				Constituent con = tokenCons.get(j);
//				String word = con.getLabel();
				int start = new Integer(tokenCons.get(j).getAttribute("charStart")).intValue();
				int end = new Integer(tokenCons.get(j).getAttribute("charEnd")).intValue();
				startMap.put(start, new Pair<Integer, Integer>(i, j));
				endMap.put(end, new Pair<Integer, Integer>(i, j));
			}
		}
	}
	
	// HashMap<startIndex, Pair<startChar, endChar>>
	public HashMap<Integer, Pair<Integer, Integer>> predCorefStartMap = new HashMap <Integer, Pair<Integer, Integer>>();
	// HashMap<endIndex, Pair<startChar, endChar>>
	public HashMap<Integer, Pair<Integer, Integer>> predCorefEndMap = new HashMap <Integer, Pair<Integer, Integer>>();

	private void buildIndexFromRealCoref () {
		CoreferenceView corefView = (CoreferenceView)globalTA.getTa().getView(ViewNames.COREF);
		for(Constituent source : corefView.getConstituents()) {
			Constituent target = corefView.getCanonicalEntity(source);
			
//			String mention = source.getSurfaceString();
			int start = source.getStartCharOffset();
			int end = source.getEndCharOffset();
//			String coreMention = target.getSurfaceString();
			int corefStart = target.getStartCharOffset();
			int corefEnd = target.getEndCharOffset();
			
			predCorefStartMap.put(start, new Pair<Integer, Integer>(corefStart, corefEnd));
			predCorefEndMap.put(end, new Pair<Integer, Integer>(corefStart, corefEnd));
			
			if (isDebug) {
				String checkCoreference = contentRemovingTags.substring(corefStart, corefEnd);
				String checkMention = contentRemovingTags.substring(start, end);
				
				System.out.println ("[Mention] "  + checkMention + " [Coref org] " + checkCoreference);
			}
		}
	}
	
	// HashMap<startIndex, Pair<startChar, endChar>>
	public HashMap<Integer, Pair<Integer, Integer>> goldCorefStartMap = new HashMap <Integer, Pair<Integer, Integer>>();
	// HashMap<endIndex, Pair<startChar, endChar>>
	public HashMap<Integer, Pair<Integer, Integer>> goldCorefEndMap = new HashMap <Integer, Pair<Integer, Integer>>();

	private void buildIndexFromAnnotationCoref () {
		 List<ACEEntity> entityList = aceAnnotation.entityList;
		 for (int i = 0; i < entityList.size(); ++i) {
			 List<ACEEntityMention> entityMentionList = entityList.get(i).entityMentionList;
			 
			 if (entityMentionList.size() > 0) {
				 int corefStart = entityMentionList.get(0).extentStart;
				 int corefEnd = entityMentionList.get(0).extentEnd;
				 for (int j = 0; j < entityMentionList.size(); ++j) {
					 ACEEntityMention entityMention = entityMentionList.get(j);

					 goldCorefStartMap.put(entityMention.extentStart, new Pair<Integer, Integer>(corefStart, corefEnd));
					 goldCorefEndMap.put(entityMention.extentEnd, new Pair<Integer, Integer>(corefStart, corefEnd));

				 }
			 }

		 }
	}
	
}
