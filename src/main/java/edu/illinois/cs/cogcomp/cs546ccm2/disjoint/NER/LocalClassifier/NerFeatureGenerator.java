package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

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
	
	/*
	 * TODO: Add Mention Length feature, 
	 * 			 Mention Brown Cluster feature,
	 * 			 Mention Head + Shape feature,
	 * 			 Dependency Parser based features
	 * 			 Use figer/analysis/feature for reference
	 */
	private IFeatureVector extractFeatures(NerInstance inst, NerLabel label) {
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		List<Pair<String, Double>> featuresWithPrefix = new ArrayList<Pair<String, Double>>();
		String prefix = label.toString();
		
		// Feature functions here
		features.addAll(getMentionUnigramFeatures(inst));
		features.addAll(getMentionBigramFeatures(inst));
		features.addAll(getContextBagUnigramFeatures(inst, 5));
		features.addAll(getContextBagBigramFeatures(inst, 5));
		features.addAll(getPOSContextBagUnigramFeatures(inst, 5));
//		features.addAll(getPOSContextBagBigramFeatures(inst, 5));
		features.addAll(getMentionShapeFeatures(inst));
		features.addAll(getMentionPOSFeatures(inst));
		
		features.add(new Pair<String, Double>("BIAS", 1.0));
		
		//FeatureTransformation for a Multi-class setting here
		for(Pair<String, Double> feature : features) {
			featuresWithPrefix.add(new Pair<String, Double>(prefix + "_" + feature.getFirst(), feature.getSecond()));
		}
		
		return FeatGenTools.getFeatureVectorFromListPair(featuresWithPrefix, lm);
	}
	
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
	
	public List<Pair<String, Double>> getMentionPOSFeatures(NerInstance x) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> tokens = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		List<Constituent> posTags = ta.getView(ViewNames.POS).getConstituents();
		
		int start = x.mConst.getStartSpan();
		int end = x.mConst.getEndSpan();
		
		for(int i=start; i< end; i++) {
			String tag = posTags.get(i).getLabel();
			tokens.add(tag);
		}
		
		for(String tkn : tokens) {
			feats.add(new Pair<String, Double>("MentionPOS_" + tkn, 1.0));
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
	
	public List<Pair<String, Double>> getMentionShapeFeatures(NerInstance x) {
		List<Pair<String, Double>> feats = new ArrayList<>();
		List<String> tokens = new ArrayList<>();
		
		TextAnnotation ta = x.ta;
		int start = x.mConst.getStartSpan();
		int end = x.mConst.getEndSpan();
		
		for(String s : ta.getTokensInSpan(start, end)) {
			tokens.add(s);
		}
		
		for(String tkn : tokens) {
			feats.add(new Pair<String, Double>("MentionShape_" + getWordShape(tkn), 1.0));
		}
		
		return feats;
	}
	
	public static String getWordShape(String token) {
		return token.replaceAll("\\p{Lower}+", "a")
				.replaceAll("\\p{Upper}+", "A").replaceAll("\\p{Punct}+", ".")
				.replaceAll("\\p{Digit}+", "0");
	}
	
	
	
}

