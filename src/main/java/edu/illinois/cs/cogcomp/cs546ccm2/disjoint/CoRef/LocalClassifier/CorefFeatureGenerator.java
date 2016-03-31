package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;

import java.io.Serializable;
import java.util.*;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.Utils;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class CorefFeatureGenerator extends AbstractFeatureGenerator implements Serializable {
	
	private static final long serialVersionUID = 7646962490009315689L;
	public Lexiconer lm = null;
	
	public CorefFeatureGenerator(Lexiconer lm) {
		this.lm = lm;
	}

	@Override
	public IFeatureVector getFeatureVector(IInstance inst, IStructure label) {
		CorefInstance x = (CorefInstance) inst;
		CorefLabel y = (CorefLabel) label; 
		
		return extractFeatures(x, y);
	}
	
	private IFeatureVector extractFeatures(CorefInstance inst, CorefLabel label) {
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		List<Pair<String, Double>> featuresWithPrefix = new ArrayList<Pair<String, Double>>();
		String prefix = label.toString();
		
		// Feature functions here
//		features.addAll(globalFeatures(inst));
		
		features.addAll(getMentionUnigramBigramFeatures(inst));
		
//		features.addAll(FeatGenTools.getConjunctionsWithPairs(features, features));
		
		//FeatureTransformation for a Multi-class setting here
		for(Pair<String, Double> feature : features) {
			featuresWithPrefix.add(new Pair<String, Double>(prefix + "_" + feature.getFirst(), feature.getSecond()));
		}
		
		return FeatGenTools.getFeatureVectorFromListPair(featuresWithPrefix, lm);
	}
	
//	public List<Pair<String, Double>> globalFeatures(CorefInstance x) {
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
	
//	public List<Pair<String, Double>> perQuantityFeatures(CorefInstance x) {
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
	
//	public List<Pair<String, Double>> pairQuantityFeatures(CorefInstance x) {
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
	
//	public List<Pair<String, Double>> perQuantityFeatures(CorefInstance x, int quantIndex) {
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
	
	public List<Pair<String, Double>> verbFeatures(RelX x) {
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		int tokenIndex = x.ta.getTokenIdFromCharacterOffset(
				x.quantities.get(x.quantIndex).start);
		if(tokenIndex > x.schema.quantSchemas.get(x.quantIndex).verbPhrase.getStartSpan()) {
			features.add(new Pair<String,Double>("VerbToTheLeft", 1.0));
		} else {
			features.add(new Pair<String,Double>("VerbToTheRight", 1.0));
		}
		return features;
	}

	public List<Pair<String, Double>> unitFeatures(RelX x) {
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		QuantitySchema qSchema = x.schema.quantSchemas.get(x.quantIndex);
		int bestMatchQuestion = 0, bestMatchQuestionIndex = 0, numBestMatches = 0;
		bestMatchQuestionIndex = Utils.getNumTokenMatches(
				x.schema.questionTokens, 
				Arrays.asList(qSchema.unit.split(" ")));
		for(QuantitySchema qs : x.schema.quantSchemas) {
			int numMatches = Utils.getNumTokenMatches(
					x.schema.questionTokens, Arrays.asList(qs.unit.split(" ")));
			if(bestMatchQuestion < numMatches) {
				bestMatchQuestion = numMatches;
				numBestMatches = 1;
			} else if(bestMatchQuestion == numMatches) {
				numBestMatches++;
			}
		}
		if(bestMatchQuestionIndex > 0) {
			features.add(new Pair<String,Double>("UnitFoundInQuestion", 1.0));
		}
		if(bestMatchQuestion == bestMatchQuestionIndex) {
			features.add(new Pair<String,Double>("BestQuantUnitMatchInQuestion", 1.0));
		}
		if(numBestMatches > 1) {
			features.add(new Pair<String,Double>("MultipleQuantUnitBestMatchInQuestion", 1.0));
		}
		for(int i=0; i<x.schema.quantSchemas.size(); ++i) {
			if(i!=x.quantIndex && qSchema.unit.trim().equals(
					x.schema.quantSchemas.get(i).unit.trim())) {
				features.add(new Pair<String,Double>("ExactMatchUnit", 1.0));
				break;
			}
		}
		for(int i=0; i<x.schema.quantSchemas.size(); ++i) {
			for(int j=i+1; j<x.schema.quantSchemas.size(); ++j) {
				if(i!=x.quantIndex && j!=x.quantIndex && 
						x.schema.quantSchemas.get(i).unit.trim().equals(
								x.schema.quantSchemas.get(j).unit.trim())) {
					features.add(new Pair<String,Double>("OtherPairExactMatchUnit", 1.0));
					break;
				}
			}
		}
		int bestMatch = 0;
		for(int i=0; i<x.schema.quantSchemas.size(); ++i) {
			for(int j=i+1; j<x.schema.quantSchemas.size(); ++j) {
				String unit1 = x.schema.quantSchemas.get(i).unit.trim();
				String unit2 = x.schema.quantSchemas.get(j).unit.trim();
				int match = Utils.getNumTokenMatches(Arrays.asList(unit1.split(" ")), 
						Arrays.asList(unit2.split(" ")));
				if(match > bestMatch) {
					bestMatch = match;
				}
			}
		}
		int bestMatchForIndex = 0;
		for(int i=0; i<x.schema.quantSchemas.size(); ++i) {
			if(i==x.quantIndex) continue;
			String unit1 = x.schema.quantSchemas.get(i).unit.trim();
			String unit2 = qSchema.unit.trim();
			int match = Utils.getNumTokenMatches(Arrays.asList(unit1.split(" ")), 
					Arrays.asList(unit2.split(" ")));
			if(match > bestMatchForIndex) {
				bestMatchForIndex = match;
			}
		}
		if(bestMatchForIndex == 0) {
			features.add(new Pair<String,Double>("NoMatchWithOtherQuantUnits", 1.0));
		}
		if(bestMatch == bestMatchForIndex) {
			features.add(new Pair<String,Double>("BestMatchAmongQuantUnit", 1.0));
		}
		return features;
	}
	
	public List<Pair<String, Double>> miscFeatures(RelX x) {
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		if(x.quantities.size() == 2) {
			features.add(new Pair<String,Double>("Two_quantities", 1.0));
		}
		return features;
	}
	
	public List<Pair<String, Double>> connectedNPFeatures(RelX x) {
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		QuantitySchema qSchema = x.schema.quantSchemas.get(x.quantIndex);
		int bestMatchQuestion = 0, bestMatchQuestionIndex = 0;
		List<String> allQuantTokens = new ArrayList<String>(
				Arrays.asList(qSchema.unit.split(" ")));
		for(Constituent c : qSchema.connectedNPs) {
			allQuantTokens.addAll(Utils.getTokensList(c));
		}
		bestMatchQuestionIndex = Utils.getNumTokenMatches(
				x.schema.questionTokens, allQuantTokens);
		for(QuantitySchema qs : x.schema.quantSchemas) {
			allQuantTokens = new ArrayList<String>(Arrays.asList(qs.unit.split(" ")));
			for(Constituent c : qs.connectedNPs) {
				allQuantTokens.addAll(Utils.getTokensList(c));
			}
			bestMatchQuestion = Utils.getNumTokenMatches(x.schema.questionTokens, allQuantTokens);
		}
		if(bestMatchQuestionIndex == 0) {
			features.add(new Pair<String,Double>("UnitNPFoundInQuestion", 1.0));
		}
		if(bestMatchQuestion == bestMatchQuestionIndex) {
			features.add(new Pair<String,Double>("BestQuantUnitNPMatchInQuestion", 1.0));
		}
		int bestMatch = 0;
		for(int i=0; i<x.schema.quantSchemas.size(); ++i) {
			for(int j=i+1; j<x.schema.quantSchemas.size(); ++j) {
				QuantitySchema qs1 = x.schema.quantSchemas.get(i);
				QuantitySchema qs2 = x.schema.quantSchemas.get(j);
				List<String> allQuantTokens1 = new ArrayList<String>(
						Arrays.asList(qs1.unit.split(" ")));
				for(Constituent c : qs1.connectedNPs) {
					allQuantTokens1.addAll(Utils.getTokensList(c));
				}
				List<String> allQuantTokens2 = new ArrayList<String>(
						Arrays.asList(qs2.unit.split(" ")));
				for(Constituent c : qs2.connectedNPs) {
					allQuantTokens2.addAll(Utils.getTokensList(c));
				}
				int match = Utils.getNumTokenMatches(allQuantTokens1, allQuantTokens2);
				if(match > bestMatch) {
					bestMatch = match;
				}
			}
		}
		int bestMatchForIndex = 0;
		for(int i=0; i<x.schema.quantSchemas.size(); ++i) {
			if(i==x.quantIndex) continue;
			QuantitySchema qs1 = x.schema.quantSchemas.get(i);
			QuantitySchema qs2 = qSchema;
			List<String> allQuantTokens1 = new ArrayList<String>(
					Arrays.asList(qs1.unit.split(" ")));
			for(Constituent c : qs1.connectedNPs) {
				allQuantTokens1.addAll(Utils.getTokensList(c));
			}
			List<String> allQuantTokens2 = new ArrayList<String>(
					Arrays.asList(qs2.unit.split(" ")));
			for(Constituent c : qs2.connectedNPs) {
				allQuantTokens2.addAll(Utils.getTokensList(c));
			}
			int match = Utils.getNumTokenMatches(allQuantTokens1, allQuantTokens2);
			if(match > bestMatchForIndex) {
				bestMatchForIndex = match;
			}
		}
		if(bestMatchForIndex == 0) {
			features.add(new Pair<String,Double>("NoMatchNPWithOtherQuantUnits", 1.0));
		}
		if(bestMatch == bestMatchForIndex) {
			features.add(new Pair<String,Double>("BestMatchNPAmongQuantUnit", 1.0));
		}
		return features;
	}
	
	public List<Pair<String, Double>> getMentionUnigramBigramFeatures(CorefInstance x) {
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

