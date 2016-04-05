package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx.LocalClassifier;

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

public class RelFeatureGenerator extends AbstractFeatureGenerator implements Serializable {
	
	private static final long serialVersionUID = 7646962490009315689L;
	public Lexiconer lm = null;
	
	public RelFeatureGenerator(Lexiconer lm) {
		this.lm = lm;
	}

	@Override
	public IFeatureVector getFeatureVector(IInstance inst, IStructure label) {
		RelInstance x = (RelInstance) inst;
		RelLabel y = (RelLabel) label; 
		
		return extractFeatures(x, y);
	}
	
	private IFeatureVector extractFeatures(RelInstance inst, RelLabel label) {
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
		
		features.addAll(getEntityTypeFeatures(inst));
		
		features.add(new Pair<String, Double>("BIAS", 1.0));
		
		features.addAll(FeatGenTools.getConjunctionsWithPairs(getEntityTypeFeatures(inst), getEntityTypeFeatures(inst)));
		
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
	
	
	public List<Pair<String, Double>> getEntityTypeFeatures(RelInstance x) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		
		feats.add(new Pair<String, Double>("SourceType_" + x.mSource.getLabel(), 1.0));
		feats.add(new Pair<String, Double>("TargetType_" + x.mTarget.getLabel(), 1.0));
		
		return feats;
	}
	
	public List<Pair<String, Double>> getPOSContextBagUnigramFeatures(RelInstance x, int window) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> sourceTokens = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		List<Constituent> posTags = ta.getView(ViewNames.POS).getConstituents();
		
		int sourceStart = x.mSource.getStartSpan();
		int sourceEnd = x.mSource.getEndSpan();
		
		int sourceStartToken = 0;
		int sourceEndToken = ta.size();
		
		if(sourceStart - window >= 0)
			sourceStartToken = sourceStart - window;
		
		if(sourceEnd + window <= ta.size())
			sourceEndToken = sourceEnd + window;
		
		for(int i=sourceStartToken; i<sourceStart; ++i) {
			String tag = posTags.get(i).getLabel();
			sourceTokens.add(tag);
		}
		
		for(int i=sourceEnd; i<sourceEndToken; ++i) {
			String tag = posTags.get(i).getLabel();
			sourceTokens.add(tag);
		}
		
		for(String tkn : sourceTokens) {
			feats.add(new Pair<String, Double>("POSSourceContextUnigram_" + tkn, 1.0));
		}
		
		List<String> targetTokens = new ArrayList<>();
		
		int targetStart = x.mTarget.getStartSpan();
		int targetEnd = x.mTarget.getEndSpan();
		
		int targetStartToken = 0;
		int targetEndToken = ta.size();
		
		if(targetStart - window >= 0)
			targetStartToken = targetStart - window;
		
		if(targetEnd + window <= ta.size())
			targetEndToken = targetEnd + window;
		
		for(int i=targetStartToken; i<targetStart; ++i) {
			String tag = posTags.get(i).getLabel();
			targetTokens.add(tag);
		}
		
		for(int i=targetEnd; i<targetEndToken; ++i) {
			String tag = posTags.get(i).getLabel();
			targetTokens.add(tag);
		}
		
		for(String tkn : targetTokens) {
			feats.add(new Pair<String, Double>("POSTargetContextUnigram_" + tkn, 1.0));
		}
		
		return feats;
	}
	
	public List<Pair<String, Double>> getPOSContextBagBigramFeatures(RelInstance x, int window) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> sourceTokensLeft = new ArrayList<>();
		List<String> sourceTokensRight = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		List<Constituent> posTags = ta.getView(ViewNames.POS).getConstituents();
		
		int sourceStart = x.mSource.getStartSpan();
		int sourceEnd = x.mSource.getEndSpan();
		
		int sourceStartToken = 0;
		int sourceEndToken = ta.size();
		
		if(sourceStart - window >= 0)
			sourceStartToken = sourceStart - window;
		
		if(sourceEnd + window <= ta.size())
			sourceEndToken = sourceEnd + window;
		
		for(int i=sourceStartToken; i<sourceStart; ++i) {
			String tag = posTags.get(i).getLabel();
			sourceTokensLeft.add(tag);
		}
		
		for(int i=sourceEnd; i<sourceEndToken; ++i) {
			String tag = posTags.get(i).getLabel();
			sourceTokensRight.add(tag);
		}
		
		for(int i=0; i<sourceTokensLeft.size()-1; ++i) {
			feats.add(new Pair<String, Double>("POSSourceContextBigram_" + sourceTokensLeft.get(i) + "_" + sourceTokensLeft.get(i+1), 1.0));
		}
		
		for(int i=0; i<sourceTokensRight.size()-1; ++i) {
			feats.add(new Pair<String, Double>("POSSourceContextBigram_" + sourceTokensRight.get(i) + "_" + sourceTokensRight.get(i+1), 1.0));
		}
		
		List<String> targetTokensLeft = new ArrayList<>();
		List<String> targetTokensRight = new ArrayList<>();
		
		int targetStart = x.mTarget.getStartSpan();
		int targetEnd = x.mTarget.getEndSpan();
		
		int targetStartToken = 0;
		int targetEndToken = ta.size();
		
		if(targetStart - window >= 0)
			targetStartToken = targetStart - window;
		
		if(targetEnd + window <= ta.size())
			targetEndToken = targetEnd + window;
		
		for(int i=targetStartToken; i<targetStart; ++i) {
			String tag = posTags.get(i).getLabel();
			targetTokensLeft.add(tag);
		}
		
		for(int i=targetEnd; i<targetEndToken; ++i) {
			String tag = posTags.get(i).getLabel();
			targetTokensRight.add(tag);
		}
		
		for(int i=0; i<targetTokensLeft.size()-1; ++i) {
			feats.add(new Pair<String, Double>("POSTargetContextBigram_" + targetTokensLeft.get(i) + "_" + targetTokensLeft.get(i+1), 1.0));
		}
		
		for(int i=0; i<targetTokensRight.size()-1; ++i) {
			feats.add(new Pair<String, Double>("POSTargetContextBigram_" + targetTokensRight.get(i) + "_" + targetTokensRight.get(i+1), 1.0));
		}
		
		return feats;
	}
	
	public List<Pair<String, Double>> getContextBagUnigramFeatures(RelInstance x, int window) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> sourceTokens = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		
		int sourceStart = x.mSource.getStartSpan();
		int sourceEnd = x.mSource.getEndSpan();
		
		int sourceStartToken = 0;
		int sourceEndToken = ta.size();
		
		if(sourceStart - window >= 0)
			sourceStartToken = sourceStart - window;
		
		if(sourceEnd + window <= ta.size())
			sourceEndToken = sourceEnd + window;
		
		for(int i=sourceStartToken; i<sourceStart; ++i) {
			String tag = ta.getToken(i);
			sourceTokens.add(tag);
		}
		
		for(int i=sourceEnd; i<sourceEndToken; ++i) {
			String tag = ta.getToken(i);
			sourceTokens.add(tag);
		}
		
		for(String tkn : sourceTokens) {
			feats.add(new Pair<String, Double>("SourceContextUnigram_" + tkn, 1.0));
		}
		
		List<String> targetTokens = new ArrayList<>();
		
		int targetStart = x.mTarget.getStartSpan();
		int targetEnd = x.mTarget.getEndSpan();
		
		int targetStartToken = 0;
		int targetEndToken = ta.size();
		
		if(targetStart - window >= 0)
			targetStartToken = targetStart - window;
		
		if(targetEnd + window <= ta.size())
			targetEndToken = targetEnd + window;
		
		for(int i=targetStartToken; i<targetStart; ++i) {
			String tag = ta.getToken(i);
			targetTokens.add(tag);
		}
		
		for(int i=targetEnd; i<targetEndToken; ++i) {
			String tag = ta.getToken(i);
			targetTokens.add(tag);
		}
		
		for(String tkn : targetTokens) {
			feats.add(new Pair<String, Double>("TargetContextUnigram_" + tkn, 1.0));
		}
		
		return feats;
	}
	
	public List<Pair<String, Double>> getContextBagBigramFeatures(RelInstance x, int window) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> sourceTokensLeft = new ArrayList<>();
		List<String> sourceTokensRight = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		
		int sourceStart = x.mSource.getStartSpan();
		int sourceEnd = x.mSource.getEndSpan();
		
		int sourceStartToken = 0;
		int sourceEndToken = ta.size();
		
		if(sourceStart - window >= 0)
			sourceStartToken = sourceStart - window;
		
		if(sourceEnd + window <= ta.size())
			sourceEndToken = sourceEnd + window;
		
		for(int i=sourceStartToken; i<sourceStart; ++i) {
			String tag = ta.getToken(i);
			sourceTokensLeft.add(tag);
		}
		
		for(int i=sourceEnd; i<sourceEndToken; ++i) {
			String tag = ta.getToken(i);
			sourceTokensRight.add(tag);
		}
		
		for(int i=0; i<sourceTokensLeft.size()-1; ++i) {
			feats.add(new Pair<String, Double>("SourceContextBigram_" + sourceTokensLeft.get(i) + "_" + sourceTokensLeft.get(i+1), 1.0));
		}
		
		for(int i=0; i<sourceTokensRight.size()-1; ++i) {
			feats.add(new Pair<String, Double>("SourceContextBigram_" + sourceTokensRight.get(i) + "_" + sourceTokensRight.get(i+1), 1.0));
		}
		
		List<String> targetTokensLeft = new ArrayList<>();
		List<String> targetTokensRight = new ArrayList<>();
		
		int targetStart = x.mTarget.getStartSpan();
		int targetEnd = x.mTarget.getEndSpan();
		
		int targetStartToken = 0;
		int targetEndToken = ta.size();
		
		if(targetStart - window >= 0)
			targetStartToken = targetStart - window;
		
		if(targetEnd + window <= ta.size())
			targetEndToken = targetEnd + window;
		
		for(int i=targetStartToken; i<targetStart; ++i) {
			String tag = ta.getToken(i);
			targetTokensLeft.add(tag);
		}
		
		for(int i=targetEnd; i<targetEndToken; ++i) {
			String tag = ta.getToken(i);
			targetTokensRight.add(tag);
		}
		
		for(int i=0; i<targetTokensLeft.size()-1; ++i) {
			feats.add(new Pair<String, Double>("TargetContextBigram_" + targetTokensLeft.get(i) + "_" + targetTokensLeft.get(i+1), 1.0));
		}
		
		for(int i=0; i<targetTokensRight.size()-1; ++i) {
			feats.add(new Pair<String, Double>("TargetContextBigram_" + targetTokensRight.get(i) + "_" + targetTokensRight.get(i+1), 1.0));
		}
		
		return feats;
	}
	
	public List<Pair<String, Double>> getMentionUnigramFeatures(RelInstance x) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> sourceTokens = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		int start = x.mSource.getStartSpan();
		int end = x.mSource.getEndSpan();
		
		for(int i=start; i<end; ++i) {
			sourceTokens.add(ta.getToken(i));
		}
		
		for(String tkn : sourceTokens) {
			feats.add(new Pair<String, Double>("UnigramSource_" + tkn, 1.0));
		}
		
		List<String> targetTokens = new ArrayList<>();
		
		start = x.mTarget.getStartSpan();
		end = x.mTarget.getEndSpan();
		
		for(int i=start; i<end; ++i) {
			targetTokens.add(ta.getToken(i));
		}
		
		for(String tkn : targetTokens) {
			feats.add(new Pair<String, Double>("UnigramTarget_" + tkn, 1.0));
		}
		
		return feats;
	}
	
	public List<Pair<String, Double>> getMentionBigramFeatures(RelInstance x) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> sourceTokens = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		int start = x.mSource.getStartSpan();
		int end = x.mSource.getEndSpan();
		
		for(int i=start; i<end; ++i) {
			sourceTokens.add(ta.getToken(i));
		}
		
		for(int i=0; i<sourceTokens.size()-1; ++i) {
			feats.add(new Pair<String, Double>("BigramSource_" + sourceTokens.get(i) + "_" + sourceTokens.get(i+1), 1.0));
		}
		
		List<String> targetTokens = new ArrayList<>();
		
		start = x.mTarget.getStartSpan();
		end = x.mTarget.getEndSpan();
		
		for(int i=start; i<end; ++i) {
			targetTokens.add(ta.getToken(i));
		}
		
		for(int i=0; i<targetTokens.size()-1; ++i) {
			feats.add(new Pair<String, Double>("BigramTarget_" + targetTokens.get(i) + "_" + targetTokens.get(i+1), 1.0));
		}
		
		return feats;
	}
	
}

