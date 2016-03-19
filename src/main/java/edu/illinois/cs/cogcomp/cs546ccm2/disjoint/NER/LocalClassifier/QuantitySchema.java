package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.quant.driver.QuantSpan;

public class QuantitySchema {
	
	public QuantSpan qs;
	public Constituent quantPhrase;
	public String unit;
	public Constituent verbPhrase;
	public String verb;
	public String verbLemma;
	public List<Constituent> connectedNPs;
	public Constituent subject;
	public Constituent rateUnit;
	
	public QuantitySchema(QuantSpan qs) {
		this.qs= qs; 
		connectedNPs = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		return "Num: "+Tools.getValue(qs)+" Verb: "+verb+" Unit: "+unit +
				" NPs: "+Arrays.asList(connectedNPs) + " Rate : "+rateUnit;
	}
	
	public List<Constituent> getConnectedNPs(Problem prob) {
		List<Constituent> npList = new ArrayList<>();
		List<Constituent> npListQuantRemoved = new ArrayList<>();
		boolean onlyQuantityInSentence = true;
		int sentId = prob.ta.getSentenceFromToken(quantPhrase.getStartSpan()).getSentenceId();
		for(QuantSpan qs : prob.quantities) {
			int tokenId = prob.ta.getTokenIdFromCharacterOffset(qs.start);
			if(prob.ta.getSentenceFromToken(tokenId).getSentenceId() == sentId &&
					!(quantPhrase.getStartSpan()<=tokenId && quantPhrase.getEndSpan()>tokenId)) {
				onlyQuantityInSentence = false;
				break;
			}
		}
		// Find NPs from children of verb
		if(verbPhrase != null) {
			List<Relation> relations = verbPhrase.getOutgoingRelations();
			for(Relation relation : relations) {
				if(!relation.getRelationName().equals("nsubj")) continue;
				Constituent dst = relation.getTarget();
				for(Constituent cons : prob.chunks) {
					if(cons.getStartSpan() <= dst.getStartSpan() &&
							cons.getEndSpan() > dst.getStartSpan() &&
							cons.getLabel().equals("NP") &&
							!npList.contains(cons)) {
						npList.add(cons);
						subject = cons;
						break;
					}
				}
			}
		}
		// Find NPs from PP NP connection
		int quantPhraseId = getChunkIndex(prob, quantPhrase.getStartSpan());
		if(quantPhraseId+2 < prob.chunks.size() && 
				!prob.chunks.get(quantPhraseId+1).getSurfaceForm().trim().equals("of") &&
				prob.chunks.get(quantPhraseId+1).getLabel().equals("PP") &&
				prob.chunks.get(quantPhraseId+2).getLabel().equals("NP") &&
				!npList.contains(prob.chunks.get(quantPhraseId+2))) {
			npList.add(prob.chunks.get(quantPhraseId+2));
		}
		if(quantPhraseId-2 >= 0 && 
				prob.chunks.get(quantPhraseId-1).getLabel().equals("PP") &&
				prob.chunks.get(quantPhraseId-2).getLabel().equals("NP") &&
				!npList.contains(prob.chunks.get(quantPhraseId-2))) {
			npList.add(prob.chunks.get(quantPhraseId-2));
		}
		//Get preceding NP
		if(quantPhraseId-1 >= 0 && 
				prob.chunks.get(quantPhraseId-1).getLabel().equals("NP") &&
				!prob.posTags.get(prob.chunks.get(quantPhraseId-1).getEndSpan())
				.getLabel().equals("CC") &&
				!npList.contains(prob.chunks.get(quantPhraseId-1))) {
			npList.add(prob.chunks.get(quantPhraseId-1));
		}
		//Get succeeding NP
		if(quantPhraseId+1 <prob.chunks.size() && 
				prob.chunks.get(quantPhraseId+1).getLabel().equals("NP") &&
				!prob.posTags.get(prob.chunks.get(quantPhraseId).getEndSpan())
				.getLabel().equals("CC") &&
				!npList.contains(prob.chunks.get(quantPhraseId+1))) {
			npList.add(prob.chunks.get(quantPhraseId+1));
		}
//		 If only quantity in sentence, all NPs are connected
		if(onlyQuantityInSentence) {
			for(int i=0; i<prob.chunks.size(); ++i) {
				Constituent cons = prob.chunks.get(i);
				if(cons.getSentenceId() == sentId && 
						(i>quantPhraseId+2 || i<quantPhraseId-2) && 
						!npList.contains(cons) && 
						cons.getLabel().equals("NP")) {
					npList.add(cons);
				}
			}
		}
		// Remove quantity phrases from npList
		for(Constituent cons : npList) {
			boolean allow = true;
			for(QuantSpan qs : prob.quantities) {
				int index = prob.ta.getTokenIdFromCharacterOffset(qs.start);
				if(index >= cons.getStartSpan() && index < cons.getEndSpan()) {
					allow = false;
					break;
				}
			}
			if(allow) {
				npListQuantRemoved.add(cons);
			}
		}
		return npListQuantRemoved;
	}
	
	public Constituent getDependentVerb(Problem prob, QuantSpan qs) {
		Constituent result = getDependencyConstituentCoveringTokenId(
				prob, prob.ta.getTokenIdFromCharacterOffset(qs.start));
		if(result == null) {
			System.out.println("Text : "+prob.question+" Token : "+prob.ta.getTokenIdFromCharacterOffset(qs.start));
			Tools.printCons(prob.dependency);
		}
		while(result != null) {
			if(result.getIncomingRelations().size() == 0) break;
//			System.out.println(result.getIncomingRelations().get(0).getSource()+" --> "+result);
			result = result.getIncomingRelations().get(0).getSource();
			if(prob.posTags.get(result.getStartSpan()).getLabel().startsWith("VB")) {
				return result;
			}
		}
		return result;
	}
	
	public Pair<String, Constituent> getUnit(Problem prob, int quantIndex) {
		String unit = "";
		int tokenId = prob.ta.getTokenIdFromCharacterOffset(
				prob.quantities.get(quantIndex).start);
		int quantPhraseId = getChunkIndex(prob, tokenId);
		Constituent quantPhrase = prob.chunks.get(quantPhraseId); 
		// Detect cases like 4 red and 6 blue balls
		int numQuantInChunk = 0;
		for(QuantSpan qs : prob.quantities) {
			int index = prob.ta.getTokenIdFromCharacterOffset(qs.start);
			if(index >= quantPhrase.getStartSpan() && index < quantPhrase.getEndSpan()) {
				numQuantInChunk++;
			}
		}
		int start = quantPhrase.getStartSpan();
		int end = quantPhrase.getEndSpan();
		boolean addEndNoun = false;
		if(numQuantInChunk > 1) {
			for(int i=quantPhrase.getStartSpan(); i<quantPhrase.getEndSpan(); ++i) {
				if(prob.posTags.get(i).getLabel().equals("CC")) {
					if(tokenId < i) {
						end = i;
						addEndNoun = true;
					} else {
						start = i+1;
					}
					break;
				}
			}
		}
		for(int i=start; i<end; ++i) {
			if(i != tokenId) {
				if(prob.ta.getToken(i).equals("$")) {
					unit += "dollar ";
				} else {
					unit += prob.lemmas.get(i) + " ";
				}
			}
		}
		// Connecting disconnected units, as in, 5 red and 6 green apples 
		if(addEndNoun && quantPhrase.getEndSpan()<=prob.ta.size() && 
				prob.posTags.get(quantPhrase.getEndSpan()-1).getLabel().startsWith("N")) {
			unit += prob.lemmas.get(quantPhrase.getEndSpan()-1)+" ";
		}
		// Unit from neighboring phrases
		if(quantPhraseId+2 < prob.chunks.size() && 
				prob.chunks.get(quantPhraseId+1).getSurfaceForm().trim().equals("of") &&
				prob.chunks.get(quantPhraseId+2).getLabel().equals("NP")) {
			Constituent cons = prob.chunks.get(quantPhraseId+2);
			for(int j=cons.getStartSpan(); j<cons.getEndSpan(); ++j) {
				unit += prob.lemmas.get(j) + " ";
			}
			quantPhraseId += 2;
		}
		return new Pair<String, Constituent>(unit, quantPhrase);
	}
	
	public Constituent getDependencyConstituentCoveringTokenId(
			Problem prob, int tokenId) {
		for(int i=0; i<=2; ++i) {
			for(Constituent cons : prob.dependency) {
				if(tokenId+i >= cons.getStartSpan() && 
						tokenId+i < cons.getEndSpan()) {
					return cons;
				}
			}
		}
		return null;
	}
	
	public static int getChunkIndex(Problem prob, int tokenId) {
		for(int i=0; i<=2; ++i) {
			for(int j=0; j<prob.chunks.size(); ++j) {
				Constituent cons = prob.chunks.get(j);
				if(tokenId+i >= cons.getStartSpan() && 
						tokenId+i < cons.getEndSpan()) {
					return j;
				}
			}
		}
		return -1;
	}

	public Constituent getRateUnit(Problem prob) {
		for(Constituent cons : connectedNPs) {
			if(cons.getSurfaceForm().toLowerCase().contains("each")) {
				return cons;
			}
		}
		for(Constituent cons : connectedNPs) {
			if(cons.getSurfaceForm().toLowerCase().contains("every")) {
				return cons;
			}
		}
		if(quantPhrase.getSurfaceForm().contains("each") || 
				quantPhrase.getSurfaceForm().contains("every")) {
			return quantPhrase;
		}
		int chunkId = getChunkIndex(prob, quantPhrase.getStartSpan());
		if(chunkId+2<prob.chunks.size() && 
				prob.chunks.get(chunkId+1).getSurfaceForm().equals("per")) {
			return prob.chunks.get(chunkId+2);
		}
		return null;
	}
}
