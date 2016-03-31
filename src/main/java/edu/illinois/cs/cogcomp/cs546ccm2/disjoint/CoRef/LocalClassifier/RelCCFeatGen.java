package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;

import java.io.Serializable;
import java.util.*;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifierOld.FeatGen;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifierOld.QuantitySchema;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifierOld.Tools;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class RelCCFeatGen extends AbstractFeatureGenerator implements Serializable{

	private static final long serialVersionUID = -5902462551801564955L;
	public Lexiconer lm = null;
	public List<String> verbs;
	
	public RelCCFeatGen(Lexiconer lm) {
		this.lm = lm;
	}

	@Override
	public IFeatureVector getFeatureVector(IInstance prob, IStructure y) {
		RelX x = (RelX) prob;
		List<Pair<String, Double>> features = new ArrayList<Pair<String, Double>>();
		// Feature functions here
		features.addAll(unitFeatures(x));
		features.addAll(verbFeatures(x));
		features.addAll(miscFeatures(x));
//		features.addAll(FeatGen.getConjunctionsWithPairs(features, features));
		List<Pair<String, Double>> featuresWithPrefix = 
				new ArrayList<Pair<String, Double>>();
		String prefix = y.toString();
		for(Pair<String, Double> feature : features) {
			featuresWithPrefix.add(new Pair<String, Double>(
					prefix + "_" + feature.getFirst(), feature.getSecond()));
		}
		return FeatGen.getFeatureVectorFromListPair(featuresWithPrefix, lm);
	}
	
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
		bestMatchQuestionIndex = Tools.getNumTokenMatches(x.schema.questionTokens, 
				Arrays.asList(qSchema.unit.split(" ")));
		for(QuantitySchema qs : x.schema.quantSchemas) {
			int numMatches = Tools.getNumTokenMatches(
					x.schema.questionTokens, Arrays.asList(qs.unit.split(" ")));
			if(bestMatchQuestion < numMatches) {
				bestMatchQuestion = numMatches;
				numBestMatches = 1;
			} else if(bestMatchQuestion == numMatches) {
				numBestMatches++;
			}
		}
		if(bestMatchQuestionIndex == 0) {
			features.add(new Pair<String,Double>("UnitFoundInQuestion", 1.0));
		}
		if(bestMatchQuestion == bestMatchQuestionIndex) {
			features.add(new Pair<String,Double>("BetterQuantUnitMatchInQuestion", 1.0));
		}
		if(numBestMatches > 1) {
			features.add(new Pair<String,Double>("MultipleQuantUnitBestMatchInQuestion", 1.0));
		}
		if(qSchema.quantPhrase.getEndSpan() - qSchema.quantPhrase.getStartSpan() > 1) {
			features.add(new Pair<String,Double>("Unit present", 1.0));
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
				int match = Tools.getNumTokenMatches(Arrays.asList(unit1.split(" ")), 
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
			int match = Tools.getNumTokenMatches(Arrays.asList(unit1.split(" ")), 
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
//		if(Tools.safeEquals(Tools.getValue(x.quantities.get(x.quantIndex)), 1.0) || 
//				Tools.safeEquals(Tools.getValue(x.quantities.get(x.quantIndex)), 2.0)) {
//			features.add(new Pair<String,Double>("OneOrTwo", 1.0));
//		}
		return features;
	}
	
	
	
	
}

