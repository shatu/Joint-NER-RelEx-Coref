package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class FeatGenTools {
	
	public static IFeatureVector getFeatureVectorFromListString(List<String> features, Lexiconer lm) {
		FeatureVectorBuffer fvb = new FeatureVectorBuffer();
		for(String feature : features) {
			if(!lm.containFeature(feature) && lm.isAllowNewFeatures()) {
				lm.addFeature(feature);
			}
			
			if(lm.containFeature(feature)) {
				fvb.addFeature(lm.getFeatureId(feature), 1.0);
			}
		}
		
		return fvb.toFeatureVector();
	}
	
	public static IFeatureVector getFeatureVectorFromListPair(List<Pair<String, Double>> features, Lexiconer lm) {
		FeatureVectorBuffer fvb = new FeatureVectorBuffer();
		for(Pair<String, Double> feature : features) {
			if(!lm.containFeature(feature.getFirst()) && lm.isAllowNewFeatures()) {
				lm.addFeature(feature.getFirst());
			}
			
			if(lm.containFeature(feature.getFirst())) {
				fvb.addFeature(lm.getFeatureId(feature.getFirst()), feature.getSecond());
			}
		}
		
		return fvb.toFeatureVector();
	}
		
	public static List<String> getConjunctions(List<String> features) {
		List<String> conjunctions = new ArrayList<String>();
		for(String feature1 : features) {
			for(String feature2 : features) {
				conjunctions.add(feature1+"_"+feature2);
			}
		}
		return conjunctions;
	}
	
	public static List<String> getConjunctions(List<String> features1, List<String> features2) {
		List<String> conjunctions = new ArrayList<String>();
		for(String feature1 : features1) {
			for(String feature2 : features2) {
				conjunctions.add(feature1+"_"+feature2);
			}
		}
		return conjunctions;
	}
	
	public static List<Pair<String, Double>> getConjunctionsWithPairs(List<Pair<String, Double>> features1, List<Pair<String, Double>> features2) {
		List<Pair<String, Double>> conjunctions = new ArrayList<Pair<String, Double>>();
		for(Pair<String, Double> feature1 : features1) {
			for(Pair<String, Double> feature2 : features2) {
				conjunctions.add(new Pair<String, Double>(feature1.getFirst()+"_"+feature2.getFirst(), feature1.getSecond()*feature2.getSecond()));
			}
		}
		return conjunctions;
	}
	
}
