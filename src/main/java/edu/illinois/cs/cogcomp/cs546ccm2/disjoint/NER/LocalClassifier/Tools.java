package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

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

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorService;
import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.annotation.handler.IllinoisChunkerHandler;
import edu.illinois.cs.cogcomp.annotation.handler.IllinoisLemmatizerHandler;
import edu.illinois.cs.cogcomp.annotation.handler.IllinoisNerHandler;
import edu.illinois.cs.cogcomp.annotation.handler.IllinoisPOSHandler;
import edu.illinois.cs.cogcomp.annotation.handler.StanfordDepHandler;
import edu.illinois.cs.cogcomp.annotation.handler.StanfordParseHandler;
import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.quant.driver.QuantSpan;
import edu.illinois.cs.cogcomp.quant.driver.SimpleQuantifier;
import edu.illinois.cs.cogcomp.quant.standardize.Quantity;
import edu.illinois.cs.cogcomp.quant.standardize.Ratio;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.ParserAnnotator;

public class Tools {
	
	public static SimpleQuantifier quantifier;
	public static AnnotatorService pipeline;
    private static final String LEMMA_CONFIG = "lemmaConfig";
    private static final String NER_CONLL_CONFIG = "nerConllConfig";
	static {
		try {
			ResourceManager rm = new ResourceManager(Params.pipelineConfig);
			String lemmaConfig = rm.getString( LEMMA_CONFIG );
			String nerConllConfig = rm.getString( NER_CONLL_CONFIG );
			
	        IllinoisTokenizer tokenizer = new IllinoisTokenizer();
	        TextAnnotationBuilder taBuilder = new TextAnnotationBuilder( tokenizer );
	        IllinoisPOSHandler pos = new IllinoisPOSHandler();
	        IllinoisChunkerHandler chunk = new IllinoisChunkerHandler();
	        IllinoisNerHandler nerConll = new IllinoisNerHandler( nerConllConfig, ViewNames.NER_CONLL );
	        IllinoisLemmatizerHandler lemma = new IllinoisLemmatizerHandler( lemmaConfig );

	        Properties stanfordProps = new Properties();
	        stanfordProps.put( "annotators", "pos, parse") ;
	        stanfordProps.put("parse.originalDependencies", true);

	        POSTaggerAnnotator posAnnotator = new POSTaggerAnnotator( "pos", stanfordProps );
	        ParserAnnotator parseAnnotator = new ParserAnnotator( "parse", stanfordProps );

	        StanfordParseHandler parser = new StanfordParseHandler( posAnnotator, parseAnnotator );
//	        StanfordDepHandler depParser = new StanfordDepHandler( posAnnotator, parseAnnotator );
	        StanfordDepHandler depParser = new StanfordDepHandler( posAnnotator, parseAnnotator );

	        Map< String, Annotator> extraViewGenerators = new HashMap<String, Annotator>();

	        extraViewGenerators.put( ViewNames.POS, pos );
	        extraViewGenerators.put( ViewNames.SHALLOW_PARSE, chunk );
	        extraViewGenerators.put( ViewNames.LEMMA, lemma );
	        extraViewGenerators.put( ViewNames.NER_CONLL, nerConll );
	        extraViewGenerators.put( ViewNames.PARSE_STANFORD, parser );
	        extraViewGenerators.put( ViewNames.DEPENDENCY_STANFORD, depParser );

	        Map< String, Boolean > requestedViews = new HashMap<String, Boolean>();
	        for ( String view : extraViewGenerators.keySet() )
	            requestedViews.put( view, false );

	        pipeline =  new AnnotatorService(taBuilder, extraViewGenerators, rm);
			quantifier = new SimpleQuantifier();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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

	public static Double getValue(QuantSpan qs) {
		if (qs.object instanceof Quantity) {
			return ((Quantity)qs.object).value;
		} else if (qs.object instanceof Ratio) {
			return ((Ratio)qs.object).numerator.value / 
					((Ratio)qs.object).denominator.value;
		}
		return null;
	}

	public static String getUnit(QuantSpan qs) {
		if (qs.object instanceof Quantity) {
			return ((Quantity)qs.object).units;
		} else if (qs.object instanceof Ratio) {
			return ((Ratio)qs.object).denominator.units;
		}
		return null;
	}
	
	public static String getBound(QuantSpan qs) {
		if (qs.object instanceof Quantity) {
			return ((Quantity)qs.object).bound;
		} else if (qs.object instanceof Ratio) {
			return ((Ratio)qs.object).denominator.bound;
		}
		return null;
	}

	public static int getTokenIndex(QuantSpan qs, TextAnnotation ta) {
		return ta.getTokenIdFromCharacterOffset(qs.start);
	}
	
	public static List<Double> uniqueNumbers(List<QuantSpan> quantSpans) {
		List<Double> uniqueNos = new ArrayList<>();
		for(int i=0; i<quantSpans.size(); i++) {
			QuantSpan qs = quantSpans.get(i);
			boolean allow = true;
			for(int j=0; j<i; j++) {
				if(Tools.safeEquals(Tools.getValue(qs), Tools.getValue(quantSpans.get(j)))) {
					allow = false;
					break;
				}
			}
			if(allow) uniqueNos.add(Tools.getValue(qs));
		}
		return uniqueNos;
	}
	
	public static List<QuantSpan> getRelevantQuantSpans(
			Double d, List<QuantSpan> quantSpans) {
		List<QuantSpan> relevantSpans = new ArrayList<QuantSpan>();
		for(QuantSpan qs : quantSpans) {
			if(Tools.safeEquals(d, Tools.getValue(qs))) {
				relevantSpans.add(qs);
			}
		}
		return relevantSpans;
	}
	
	public static boolean contains(List<Double> arr, Double key) {
		for(Double d : arr) {
			if(Tools.safeEquals(d, key)) {
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
				if(Tools.safeEquals(d1, d2)) {
					found = true;
				}
			}
			if(!found) return false;
		}
		return true;
	}
	
	public static boolean areAllTokensInSameSentence(
			TextAnnotation ta, List<Integer> tokenIds) {
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
	
	// Returns id of first question sentence, -1 if none found
	public static int getQuestionSentenceId(TextAnnotation ta) {
		int questionSentId = -1;
		for(int i=0; i<ta.getNumberOfSentences(); ++i) {
			Sentence sent = ta.getSentence(i);
			if(sent.getText().trim().endsWith("?")) {
				questionSentId = i;
				break;
			}
		}
		return questionSentId;
	}
	
	public static int getNumTokenMatches(
			List<String> tknList1, List<String> tknList2) {
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
	
	public static List<String> getTokensListWithCorefReplacements(
			Constituent cons, Schema schema) {
		List<String> tokens = new ArrayList<String>();
		TextAnnotation ta = cons.getTextAnnotation();
		int startIndex = cons.getStartSpan();
		int endIndex = cons.getEndSpan();
		for(int i=startIndex; i<endIndex; ++i) {
			if(schema.coref.containsKey(i)) {
				IntPair ip = schema.coref.get(i);
				for(int j=ip.getFirst(); j<ip.getSecond(); ++j) {
					tokens.add(ta.getToken(j));
				}
			} else {
				tokens.add(ta.getToken(i));
			}
		}
		return tokens;
	}
	
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
