package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import java.io.Serializable;
import java.util.*;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class NerFeatureGenerator extends AbstractFeatureGenerator implements Serializable {
	
	private static final long serialVersionUID = 7646962490009315689L;
	public Lexiconer lm = null;
	
	public NerFeatureGenerator(Lexiconer lm) {
		this.lm = lm;
	}

	@Override
	public IFeatureVector getFeatureVector(IInstance inst, IStructure label) {
		NerInstance x = (NerInstance) inst;
		NerLabel y = (NerLabel) label; 
		
		return extractFeatures(x, y);
	}
	
	private IFeatureVector extractFeatures(NerInstance inst, NerLabel label) {
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		List<Pair<String, Double>> featuresWithPrefix = new ArrayList<Pair<String, Double>>();
		String prefix = label.toString();
		
		// Feature functions here
//		features.addAll(globalFeatures(inst));
		
		features.addAll(getMentionUnigramFeatures(inst));
		features.addAll(getMentionBigramFeatures(inst));
		features.addAll(getContextBagUnigramFeatures(inst, 5));
		features.addAll(getContextBagBigramFeatures(inst, 5));
		features.addAll(getPOSContextBagUnigramFeatures(inst, 5));
		features.addAll(getPOSContextBagBigramFeatures(inst, 5));
		
		features.add(new Pair<String, Double>("BIAS", 1.0));
		
//		features.addAll(FeatGenTools.getConjunctionsWithPairs(features, features));
		
		//FeatureTransformation for a Multi-class setting here
		for(Pair<String, Double> feature : features) {
			featuresWithPrefix.add(new Pair<String, Double>(prefix + "_" + feature.getFirst(), feature.getSecond()));
		}
		
		return FeatGenTools.getFeatureVectorFromListPair(featuresWithPrefix, lm);
	}
	
//	public List<Pair<String, Double>> globalFeatures(NerInstance x) {
//		x.ta.
//		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
//		Sentence sent = x.ta.getSentence(questionSentId);
//		for(int i=sent.getStartSpan(); i<sent.getEndSpan(); ++i) {
//			if(x.ta.getToken(i).equals("more") || x.ta.getToken(i).equals("less") ||
//					x.ta.getToken(i).equals("than")) {
//				features.add(new Pair<String, Double>("MoreOrLessOrThanPresentInQuestion", 1.0));
//				break;
//			}
//		}
//		
//		for(String token : x.schema.questionTokens) {
//			if(token.equalsIgnoreCase("left")) {
//				features.add(new Pair<String, Double>("LeftPresentInQuestion", 1.0));
//				break;
//			}
//		}
//		return features;
//	}
	
//	public List<Pair<String, Double>> perQuantityFeatures(NerInstance x) {
//		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
//		for(Pair<String, Double> feature : perQuantityFeatures(x, x.quantIndex1)) {
//			features.add(new Pair<String, Double>("1_"+feature.getFirst(), feature.getSecond()));
//		}
//		
//		for(Pair<String, Double> feature : perQuantityFeatures(x, x.quantIndex2)) {
//			features.add(new Pair<String, Double>("2_"+feature.getFirst(), feature.getSecond()));
//		}
//		return features;
//	}
	
//	public List<Pair<String, Double>> pairQuantityFeatures(NerInstance x) {
//		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
//		Constituent verbPhrase1 = x.schema.quantSchemas.get(x.quantIndex1).verbPhrase;
//		Constituent verbPhrase2 = x.schema.quantSchemas.get(x.quantIndex2).verbPhrase;
//		String verb1 = x.schema.quantSchemas.get(x.quantIndex1).verb;
//		String verb2 = x.schema.quantSchemas.get(x.quantIndex2).verb;
//		if(verbPhrase1.getSpan().equals(verbPhrase2.getSpan())) {
//			features.add(new Pair<String, Double>("SameVerbInstance", 1.0));
//		}
//		if(verb1.equals(verb2)) {
//			features.add(new Pair<String, Double>("SameVerbForm", 1.0));
//		}
//		if(Utils.getValue(x.quantities.get(x.quantIndex1)) > Utils.getValue(
//				x.quantities.get(x.quantIndex2))) {
//			features.add(new Pair<String, Double>("Ascending", 1.0));
//		}
//		return features;
//	}
	
//	public List<Pair<String, Double>> perQuantityFeatures(NerInstance x, int quantIndex) {
//		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
//		QuantitySchema qSchema = x.schema.quantSchemas.get(quantIndex);
//		int tokenId = x.ta.getTokenIdFromCharacterOffset(x.quantities.get(quantIndex).start);
//		features.add(new Pair<String,Double>("Verb_"+qSchema.verb, 1.0));
//		if(qSchema.quantPhrase.getStartSpan() < qSchema.verbPhrase.getStartSpan()) {
//			features.add(new Pair<String,Double>("QuantityToTheLeftOfVerb", 1.0));
//		} else {
//			features.add(new Pair<String,Double>("QuantityToTheRightOfVerb", 1.0));
//		}
//		// Neighboring adverbs or comparative adjectives
//		for(int i=-3; i<=3; ++i) {
//			if(tokenId+i<x.ta.size() && tokenId+i>=0 && (
//					x.posTags.get(tokenId+i).getLabel().startsWith("RB") || 
//					x.posTags.get(tokenId+i).getLabel().startsWith("JJR"))) {
//				features.add(new Pair<String, Double>(
//						"Neighbor_"+x.lemmas.get(tokenId+i), 1.0));
//			}
//		}
//		return features;
//	}
	
	public List<Pair<String, Double>> getPOSContextBagUnigramFeatures(NerInstance x, int window) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> tokens = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		List<Constituent> posTags = ta.getView(ViewNames.POS).getConstituents();
		
		int start = x.mConst.getStartSpan();
		int end = x.mConst.getEndSpan();
		
		int startToken = 0;
		int endToken = ta.size();
		
		if(start - window >= 0)
			startToken = start - window;
		
		if(end + window <= ta.size())
			endToken = end + window;
		
		for(int i=startToken; i<start; ++i) {
			String tag = posTags.get(i).getLabel();
			tokens.add(tag);
		}
		
		for(int i=end; i<endToken; ++i) {
			String tag = posTags.get(i).getLabel();
			tokens.add(tag);
		}
		
		for(String tkn : tokens) {
			feats.add(new Pair<String, Double>("POSContextUnigram_" + tkn, 1.0));
		}
		
		return feats;
	}
	
	public List<Pair<String, Double>> getPOSContextBagBigramFeatures(NerInstance x, int window) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> tokensLeft = new ArrayList<>();
		List<String> tokensRight = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		List<Constituent> posTags = ta.getView(ViewNames.POS).getConstituents();
		
		int start = x.mConst.getStartSpan();
		int end = x.mConst.getEndSpan();
		
		int startToken = 0;
		int endToken = ta.size();
		
		if(start - window >= 0)
			startToken = start - window;
		
		if(end + window <= ta.size())
			endToken = end + window;
		
		for(int i=startToken; i<start; i++) {
			String tag = posTags.get(i).getLabel();
			tokensLeft.add(tag);
		}
		
		for(int i=end; i<endToken; i++) {
			String tag = posTags.get(i).getLabel();
			tokensRight.add(tag);
		}
		
		for(int i=0; i<tokensLeft.size()-1; ++i) {
			feats.add(new Pair<String, Double>("POSContextBigram_" + tokensLeft.get(i) + "_" + tokensLeft.get(i+1), 1.0));
		}
		
		for(int i=0; i<tokensRight.size()-1; ++i) {
			feats.add(new Pair<String, Double>("POSContextBigram_" + tokensRight.get(i) + "_" + tokensRight.get(i+1), 1.0));
		}
		
		return feats;
	}
	
	public List<Pair<String, Double>> getContextBagUnigramFeatures(NerInstance x, int window) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> tokens = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		int start = x.mConst.getStartSpan();
		int end = x.mConst.getEndSpan();
		
		int startToken = 0;
		int endToken = ta.size();
		
		if(start - window >= 0)
			startToken = start - window;
		
		if(end + window <= ta.size())
			endToken = end + window;
		
		for(String s : ta.getTokensInSpan(startToken, start)) {
			tokens.add(s);
		}
		
		for(String s : ta.getTokensInSpan(end, endToken)) {
			tokens.add(s);
		}
		
		for(String tkn : tokens) {
			feats.add(new Pair<String, Double>("ContextUnigram_" + tkn, 1.0));
		}
		
		return feats;
	}
	
	public List<Pair<String, Double>> getContextBagBigramFeatures(NerInstance x, int window) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> tokensLeft = new ArrayList<>();
		List<String> tokensRight = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		int start = x.mConst.getStartSpan();
		int end = x.mConst.getEndSpan();
		
		int startToken = 0;
		int endToken = ta.size();
		
		if(start - window >= 0)
			startToken = start - window;
		
		if(end + window <= ta.size())
			endToken = end + window;
		
		for(String s : ta.getTokensInSpan(startToken, start)) {
			tokensLeft.add(s);
		}
		
		for(String s : ta.getTokensInSpan(end, endToken)) {
			tokensRight.add(s);
		}
		
		for(int i=0; i<tokensLeft.size()-1; ++i) {
			feats.add(new Pair<String, Double>("ContextBigram_" + tokensLeft.get(i) + "_" + tokensLeft.get(i+1), 1.0));
		}
		
		for(int i=0; i<tokensRight.size()-1; ++i) {
			feats.add(new Pair<String, Double>("ContextBigram_" + tokensRight.get(i) + "_" + tokensRight.get(i+1), 1.0));
		}
		
		return feats;
	}
	
	public List<Pair<String, Double>> getMentionUnigramFeatures(NerInstance x) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> tokens = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		int start = x.mConst.getStartSpan();
		int end = x.mConst.getEndSpan();
		
		for(String s : ta.getTokensInSpan(start, end)) {
			tokens.add(s);
		}
		
		for(String tkn : tokens) {
			feats.add(new Pair<String, Double>("MentionUnigram_" + tkn, 1.0));
		}
		
		return feats;
	}
	
	public List<Pair<String, Double>> getMentionBigramFeatures(NerInstance x) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> tokens = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		int start = x.mConst.getStartSpan();
		int end = x.mConst.getEndSpan();
		
		for(String s : ta.getTokensInSpan(start, end)) {
			tokens.add(s);
		}
		
		for(int i=0; i<tokens.size()-1; ++i) {
			feats.add(new Pair<String, Double>("MentionBigram_" + tokens.get(i) + "_" + tokens.get(i+1), 1.0));
		}
		
		return feats;
	}
	
}

