package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifierOld;

import java.io.Serializable;
import java.util.*;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class NerFeatureGenerator extends AbstractFeatureGenerator implements Serializable {
	
	private static final long serialVersionUID = 760976071926109838L;
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
		features.addAll(FeatGenTools.getConjunctionsWithPairs(features, features));
		
		//FeatureTransformation for Multiclass setting here
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
	
	public List<Pair<String, Double>> getMentionUnigramBigramFeatures(NerInstance x) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> tokens = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		int start = x.mConst.getStartSpan();
		int end = x.mConst.getEndSpan();
		
		for(int i=start; i<end; ++i) {
			tokens.add(ta.getToken(i));
		}
		
		for(String tkn : tokens) {
			feats.add(new Pair<String, Double>("Unigram_" + tkn, 1.0));
		}
		
		for(int i=0; i<tokens.size()-1; ++i) {
			feats.add(new Pair<String, Double>("Bigram_" + tokens.get(i) + "_" + tokens.get(i+1), 1.0));
		}
		
		return feats;
	}
	
}

