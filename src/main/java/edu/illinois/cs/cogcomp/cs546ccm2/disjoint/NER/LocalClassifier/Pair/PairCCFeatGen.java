package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.Pair;

import java.io.Serializable;
import java.util.*;

import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.QuantitySchema;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.FeatGen;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.Tools;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class PairCCFeatGen extends AbstractFeatureGenerator 
implements Serializable{

	private static final long serialVersionUID = -5902462551801564955L;
	public Lexiconer lm = null;
	
	public PairCCFeatGen(Lexiconer lm) {
		this.lm = lm;
	}

	@Override
	public IFeatureVector getFeatureVector(IInstance prob, IStructure y) {
		PairX x = (PairX) prob;
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		// Feature functions here
//		features.addAll(globalFeatures(x));
		features.addAll(perQuantityFeatures(x));
		features.addAll(pairQuantityFeatures(x));
		features.addAll(FeatGen.getConjunctionsWithPairs(features, features));
		List<Pair<String, Double>> featuresWithPrefix = 
				new ArrayList<Pair<String, Double>>();
		String prefix = y.toString();
		for(Pair<String, Double> feature : features) {
			featuresWithPrefix.add(new Pair<String, Double>(
					prefix + "_" + feature.getFirst(), feature.getSecond()));
		}
		return FeatGen.getFeatureVectorFromListPair(featuresWithPrefix, lm);
	}
	
	// Whether question objects are found elsewhere, more or less is used
	public List<Pair<String, Double>> globalFeatures(PairX x) {
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		// Just take the first NP
		for(String token : x.schema.questionTokens) {
			if(token.equalsIgnoreCase("more") || token.equalsIgnoreCase("less") ||
					token.equalsIgnoreCase("than")) {
				features.add(new Pair<String, Double>("MoreOrLessOrThanPresentInQuestion", 1.0));
				break;
			}
		}
		for(String token : x.schema.questionTokens) {
			if(token.equalsIgnoreCase("each")) {
				features.add(new Pair<String, Double>("EachPresentInQuestion", 1.0));
				break;
			}
		}
		for(String token : x.schema.questionTokens) {
			if(token.equalsIgnoreCase("left")) {
				features.add(new Pair<String, Double>("LeftPresentInQuestion", 1.0));
				break;
			}
		}
		return features;
	}
		
	public List<Pair<String, Double>> perQuantityFeatures(PairX x) {
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		for(Pair<String, Double> feature : perQuantityFeatures(x, x.quantIndex1)) {
			features.add(new Pair<String, Double>(
					"1_"+feature.getFirst(), feature.getSecond()));
		}
		for(Pair<String, Double> feature : perQuantityFeatures(x, x.quantIndex2)) {
			features.add(new Pair<String, Double>(
					"2_"+feature.getFirst(), feature.getSecond()));
		}
		return features;
	}
	
	public List<Pair<String, Double>> pairQuantityFeatures(PairX x) {
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		Constituent verbPhrase1 = x.schema.quantSchemas.get(x.quantIndex1).verbPhrase;
		Constituent verbPhrase2 = x.schema.quantSchemas.get(x.quantIndex2).verbPhrase;
		String verb1 = x.schema.quantSchemas.get(x.quantIndex1).verb;
		String verb2 = x.schema.quantSchemas.get(x.quantIndex2).verb;
		String unit1 = x.schema.quantSchemas.get(x.quantIndex1).unit;
		String unit2 = x.schema.quantSchemas.get(x.quantIndex2).unit;
		Constituent rate1 = x.schema.quantSchemas.get(x.quantIndex1).rateUnit;
		Constituent rate2 = x.schema.quantSchemas.get(x.quantIndex2).rateUnit;
		if(verbPhrase1.getSpan().equals(verbPhrase2.getSpan())) {
			features.add(new Pair<String, Double>("SameVerbInstance", 1.0));
		}
		if(verb1.equals(verb2)) {
			features.add(new Pair<String, Double>("SameVerbForm", 1.0));
		}
		if(Tools.getNumTokenMatches(Arrays.asList(unit1.split(" ")), 
				Arrays.asList(unit2.split(" ")))>0) {
			features.add(new Pair<String, Double>("CommonTokenInUnit", 1.0));
		}
		if(rate2 != null && Tools.getNumTokenMatches(Arrays.asList(unit1.split(" ")), 
				Tools.getTokensList(rate2))>0) {
			features.add(new Pair<String, Double>("CommonTokenInRate", 1.0));
		}
		if(rate1 != null && Tools.getNumTokenMatches(Arrays.asList(unit2.split(" ")), 
				Tools.getTokensList(rate1))>0) {
			features.add(new Pair<String, Double>("CommonTokenInRate", 1.0));
		}
		return features;
	}
	
	public List<Pair<String, Double>> perQuantityFeatures(PairX x, int quantIndex) {
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		QuantitySchema qSchema = x.schema.quantSchemas.get(quantIndex);
		int tokenId = x.ta.getTokenIdFromCharacterOffset(x.quantities.get(quantIndex).start);
		features.add(new Pair<String,Double>("Verb_"+qSchema.verb, 1.0));
		if(qSchema.quantPhrase.getStartSpan() < qSchema.verbPhrase.getStartSpan()) {
			features.add(new Pair<String,Double>("QuantityToTheLeftOfVerb", 1.0));
		} else {
			features.add(new Pair<String,Double>("QuantityToTheRightOfVerb", 1.0));
		}
		// Neighboring adverbs or comparative adjectives
		for(int i=-3; i<=3; ++i) {
			if(tokenId+i<x.ta.size() && tokenId+i>=0 && (
					x.posTags.get(tokenId+i).getLabel().startsWith("RB") || 
					x.posTags.get(tokenId+i).getLabel().startsWith("JJR") ||
					x.posTags.get(tokenId+i).getLabel().startsWith("IN") ||
					x.posTags.get(tokenId+i).getLabel().startsWith("TO")
					)) {
				features.add(new Pair<String, Double>(
						"Neighbor_"+x.lemmas.get(tokenId+i), 1.0));
			}
		}
		// Rate present
		if(qSchema.rateUnit != null) {
			features.add(new Pair<String, Double>("RateUnitPresent", 1.0));
		}
		if(qSchema.rateUnit != null && Tools.getNumTokenMatches(x.schema.questionTokens, 
				Tools.getTokensList(qSchema.rateUnit))>0) {
			features.add(new Pair<String, Double>("RateFoundInQuestion", 1.0));
		}
		return features;
	}

}

