package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;

import java.io.Serializable;
import java.util.*;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class CoRefFeatureGenerator extends AbstractFeatureGenerator implements Serializable {
	
	private static final long serialVersionUID = 7646962490009315689L;
	public Lexiconer lm = null;
	
	public CoRefFeatureGenerator(Lexiconer lm) {
		this.lm = lm;
	}

	@Override
	public IFeatureVector getFeatureVector(IInstance inst, IStructure label) {
		CoRefInstance x = (CoRefInstance) inst;
		CoRefLabel y = (CoRefLabel) label; 
		
		return extractFeatures(x, y);
	}
	
	private IFeatureVector extractFeatures(CoRefInstance inst, CoRefLabel label) {
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		List<Pair<String, Double>> featuresWithPrefix = new ArrayList<Pair<String, Double>>();
		String prefix = label.toString();
		
		// Feature functions here
		features.addAll(getMentionUnigramFeatures(inst));
		features.addAll(getMentionBigramFeatures(inst));
		features.addAll(getContextBagUnigramFeatures(inst, 5));
		features.addAll(getContextBagBigramFeatures(inst, 5));
		features.addAll(getPOSContextBagUnigramFeatures(inst, 5));
		features.addAll(getPOSContextBagBigramFeatures(inst, 5));
		features.addAll(getSameSentenceFeature(inst));
		
		features.addAll(getEntityTypeFeature(inst));
		
		features.add(new Pair<String, Double>("BIAS", 1.0));
		
		//FeatureTransformation for a Multi-class setting here
		for(Pair<String, Double> feature : features) {
			featuresWithPrefix.add(new Pair<String, Double>(prefix + "_" + feature.getFirst(), feature.getSecond()));
		}
		
		return FeatGenTools.getFeatureVectorFromListPair(featuresWithPrefix, lm);
	}
	
	public List<Pair<String, Double>> getEntityTypeFeature(CoRefInstance x) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		
		String sourceType = x.mSource.getAttribute("EntityType");
		String targetType = x.mTarget.getAttribute("EntityType");
		
		Boolean isSame = false;
		
		if (sourceType.equalsIgnoreCase(targetType))
			isSame = true;
				
		if(isSame)
			feats.add(new Pair<String, Double>("SameEntityType", 1.0));
		
		return feats;
	}
	
	public List<Pair<String, Double>> getPOSContextBagUnigramFeatures(CoRefInstance x, int window) {
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
	
	public List<Pair<String, Double>> getPOSContextBagBigramFeatures(CoRefInstance x, int window) {
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
	
	public List<Pair<String, Double>> getContextBagUnigramFeatures(CoRefInstance x, int window) {
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
	
	public List<Pair<String, Double>> getContextBagBigramFeatures(CoRefInstance x, int window) {
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
	
	public List<Pair<String, Double>> getMentionUnigramFeatures(CoRefInstance x) {
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
	
	public List<Pair<String, Double>> getMentionBigramFeatures(CoRefInstance x) {
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
	
	public List<Pair<String, Double>> getSameSentenceFeature(CoRefInstance x) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		Constituent source = x.mSource; 
		Constituent target = x.mTarget;
		
		int sourceID = ta.getSentenceId(source);
		int targetID = ta.getSentenceId(target);
		
		Boolean isSame = false;
		
		if(sourceID == targetID)
			isSame = true;
				
		if(isSame)
			feats.add(new Pair<String, Double>("SameSentence", 1.0));
		
		return feats;
	}
	
}

