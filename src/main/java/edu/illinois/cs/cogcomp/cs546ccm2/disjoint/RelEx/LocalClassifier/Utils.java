package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx.LocalClassifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;

public class Utils {

	public static double sigmoid(double x) {
		return 1.0/(1+Math.pow(Math.E, -x))*2.0-1.0;
	}
	
	public static boolean doesIntersect(IntPair ip1, IntPair ip2) {
		if(ip1.getFirst() <= ip2.getFirst() && ip2.getFirst() < ip1.getSecond()) {
			return true;
		}
		if(ip2.getFirst() <= ip1.getFirst() && ip1.getFirst() < ip2.getSecond()) {
			return true;
		}
		return false;
	}
	
	// is ip2 subset of ip1
	public static boolean doesContain(IntPair big, IntPair small) {
		if(big.getFirst() <= small.getFirst() && small.getSecond() <= big.getSecond()) {
			return true;
		}
		return false;
	}
	

	public static boolean doesContainNotEqual(IntPair big, IntPair small) {
		if(big.getFirst() == small.getFirst() && big.getSecond() == small.getSecond()) {
			return false;
		}
		if(big.getFirst() <= small.getFirst() && small.getSecond() <= big.getSecond()) {
			return true;
		}
		return false;
	}
	
	public static boolean safeEquals(Double d1, Double d2) {
		if(d1 == null && d2 == null) return true;
		if(d1 == null || d2 == null) {
			return false;
		}
		if(d1 > d2 - 0.0001 && d1 < d2 + 0.0001) {
			return true;
		}
		return false;
	}
	
	public static boolean contains(List<Double> arr, Double key) {
		for(Double d : arr) {
			if(Utils.safeEquals(d, key)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean equals(List<Double> arr1, List<Double> arr2) {
		if(arr1 == null || arr2 == null) return false;
		if(arr1.size() != arr2.size()) return false;
		for(Double d1 : arr1) {
			boolean found = false;
			for(Double d2 : arr2) {
				if(Utils.safeEquals(d1, d2)) {
					found = true;
				}
			}
			if(!found) return false;
		}
		return true;
	}
	
	public static boolean areAllTokensInSameSentence(TextAnnotation ta, List<Integer> tokenIds) {
		Set<Integer> sentenceIds = new HashSet<>();
		for(Integer tokenId : tokenIds) {
			sentenceIds.add(ta.getSentenceFromToken(tokenId).getSentenceId());
		}
		if(sentenceIds.size() == 1) return true;
		return false;
	}
	
	public static Integer max(List<Integer> intList) {
		Integer max = Integer.MIN_VALUE;
		for(Integer i : intList) {
			if(max < i) {
				max = i;
			}
		}
		return max;
	}
	
	public static Integer min(List<Integer> intList) {
		Integer min = Integer.MAX_VALUE;
		for(Integer i : intList) {
			if(min > i) {
				min = i;
			}
		}
		return min;
	}
	
	public static Map<String, List<Double>> readVectors() throws IOException {
		System.out.println("Reading vectors from "+Params.vectorFile);
		Map<String, List<Double>> vectorMap = new HashMap<String, List<Double>>();
		BufferedReader br = new BufferedReader(new FileReader(new File(Params.vectorFile)));
		String str;
		while((str = br.readLine()) != null) {
			String strArr[] = str.split(" ");
			List<Double> v = new ArrayList<Double>();
			for(int i=1; i<strArr.length; ++i) {
				v.add(Double.parseDouble(strArr[i]));
			}
			vectorMap.put(strArr[0], v);
		}
		br.close();
		System.out.println("Read "+vectorMap.size()+" vectors");
		return vectorMap;
	}
	
//	// Returns id of first question sentence, -1 if none found
//	public static int getQuestionSentenceId(TextAnnotation ta) {
//		int questionSentId = -1;
//		for(int i=0; i<ta.getNumberOfSentences(); ++i) {
//			Sentence sent = ta.getSentence(i);
//			if(sent.getText().trim().endsWith("?")) {
//				questionSentId = i;
//				break;
//			}
//		}
//		return questionSentId;
//	}
	
	public static int getNumTokenMatches(List<String> tknList1, List<String> tknList2) {
		int sim = 0;
		Set<String> s1 = new HashSet<>();
		Set<String> s2 = new HashSet<>();
		Set<String> unImportant = new HashSet<String>(
				Arrays.asList("a","an","the"));
		for(String tkn1 : tknList1) {
			s1.add(tkn1);
			for(String tkn2 : tknList2) {
				s2.add(tkn2);
			}
		}
		for(String tkn1 : s1) {
			if(unImportant.contains(tkn1)) continue;
			for(String tkn2 : s2) {
				if(tkn1.toLowerCase().equals(tkn2.toLowerCase())) {
					sim += 1;
					break;
				}
			}
		}
		return sim;
	}
	
//	public static List<String> getTokensListWithCorefReplacements(Constituent cons, Schema schema) {
//		List<String> tokens = new ArrayList<String>();
//		TextAnnotation ta = cons.getTextAnnotation();
//		int startIndex = cons.getStartSpan();
//		int endIndex = cons.getEndSpan();
//		for(int i=startIndex; i<endIndex; ++i) {
//			if(schema.coref.containsKey(i)) {
//				IntPair ip = schema.coref.get(i);
//				for(int j=ip.getFirst(); j<ip.getSecond(); ++j) {
//					tokens.add(ta.getToken(j));
//				}
//			} else {
//				tokens.add(ta.getToken(i));
//			}
//		}
//		return tokens;
//	}
	
	public static List<String> getTokensList(Constituent cons) {
		List<String> tokens = new ArrayList<String>();
		TextAnnotation ta = cons.getTextAnnotation();
		int startIndex = cons.getStartSpan();
		int endIndex = cons.getEndSpan();
		for(int i=startIndex; i<endIndex; ++i) {
			tokens.add(ta.getToken(i));
		}
		return tokens;
	}
	
	public static void printCons(List<Constituent> constituents) {
		for(Constituent cons : constituents) {
			System.out.println(cons.getLabel()+" : "+cons.getSurfaceForm()+" : "+cons.getSpan());
		}
	}
}
