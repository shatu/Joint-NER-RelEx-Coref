package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifierOld;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;

public class Folds {
	
//	public static Map<String, Integer> numFolds;
//	static {
//		numFolds = new HashMap<String, Integer>();
//		numFolds.put("AI2", 3);
//		numFolds.put("IL", 5);
//		numFolds.put("CC", 6);
//	}
	
	public static int getNumFolds(String dir) {
		File directory = null;
		int fold = 0;
		if(dir.equals("AI2")) {
			directory = new File(Params.ai2Dir);
		}
		if(dir.equals("IL")) {
			directory = new File(Params.ilDir);
		}
		if(dir.equals("CC")) {
			directory = new File(Params.ccDir);
		}
		for(File file : directory.listFiles()) {
			if(file.getName().contains("fold")) {
				fold++;
			}
		}
		return fold;
	}
	

	public static Pair<List<ACEDocument>, List<ACEDocument>> getDataSplitForAI2(int fold) 
			throws Exception {
		List<ACEDocument> train = new ArrayList<>();
		List<ACEDocument> test = new ArrayList<>();
//		Map<Integer, IntPair> folds = new HashMap<Integer, IntPair>();
//		folds.put(0, new IntPair(0, 134));
//		folds.put(1, new IntPair(134, 274));
//		folds.put(2, new IntPair(274, 395));
//		List<ACEDocument> probList = Reader.readAI2ACEDocumentsFromFile(Params.ai2MathFile);
//		IntPair foldSpan = folds.get(fold);
//		for(ACEDocument prob : probList) {
//			if(prob.getDocID() >= foldSpan.getFirst() && prob.getDocID() < foldSpan.getSecond()) {
//				test.add(prob);
//			} else {
//				train.add(prob);
//			}
//		}
		//List<ACEDocument> probs = Reader.readACEDocumentsFromJson(Params.ai2Dir);
		//TODO: Load documents using ACECorpus
		List<ACEDocument> probs = null;
		
		String str = FileUtils.readFileToString(new File(Params.ai2Dir+"fold"+fold+".txt"));
		Set<Integer> foldIndices = new HashSet<>();
		for(String index : str.split("\n")) {
			foldIndices.add(Integer.parseInt(index));
		}
		for(ACEDocument prob : probs) {
			if(foldIndices.contains(prob.getDocID())) {
				test.add(prob);
			} else {
				train.add(prob);
			}
		}
		return new Pair<List<ACEDocument>, List<ACEDocument>>(train, test);
	}
	
	public static Pair<List<ACEDocument>, List<ACEDocument>> getDataSplitForIL(int fold) 
			throws Exception {
		List<ACEDocument> train = new ArrayList<ACEDocument>();
		List<ACEDocument> test = new ArrayList<ACEDocument>();
//		List<ACEDocument> probs = Reader.readIllinoisACEDocumentsFromFile(
//				Params.illinoisMathFile);
		//List<ACEDocument> probs = Reader.readACEDocumentsFromJson(Params.ilDir);
		//TODO: Load documents using ACECorpus
		List<ACEDocument> probs = null;
		
		/* From folds file
		for(int i=0; i<5; ++i) {
			String str = FileUtils.readFileToString(new File(
					Params.illinoisFoldPrefix+i+".txt"));
			String strArr[] = str.split("\n");
			for(int j=0; j<strArr.length; ++j) {
				int index = Integer.parseInt(strArr[j].trim());
				if(fold == i) {
					test.add(probs.get(index));
				} else {
					train.add(probs.get(index));
				}
			}
			}*/
		String str = FileUtils.readFileToString(new File(Params.ilDir+"fold"+fold+".txt"));
		Set<Integer> foldIndices = new HashSet<>();
		for(String index : str.split("\n")) {
			foldIndices.add(Integer.parseInt(index));
		}
		for(ACEDocument prob : probs) {
			if(foldIndices.contains(prob.getDocID())) {
				test.add(prob);
			} else {
				train.add(prob);
			}
		}
		return new Pair<List<ACEDocument>, List<ACEDocument>>(train, test);
	}
	
	public static Pair<List<ACEDocument>, List<ACEDocument>> getDataSplitForCC(int fold) 
			throws Exception {
		List<ACEDocument> train = new ArrayList<ACEDocument>();
		List<ACEDocument> test = new ArrayList<ACEDocument>();
//		List<List<ACEDocument>> ACEDocumentList = Reader.readCCACEDocumentsFromDir("data/other/");
//		for(int i=0; i<6; ++i) {
//			if(i==fold) {
//				test.addAll(ACEDocumentList.get(i));
//			} else {
//				train.addAll(ACEDocumentList.get(i));
//			}
//		}
		
		//List<ACEDocument> probs = Reader.readACEDocumentsFromJson(Params.ccDir);
		//TODO: Load documents using ACECorpus
		List<ACEDocument> probs = null;
		String str = FileUtils.readFileToString(new File(Params.ccDir+"fold"+fold+".txt"));
		Set<Integer> foldIndices = new HashSet<>();
		for(String index : str.split("\n")) {
			foldIndices.add(Integer.parseInt(index));
		}
		for(ACEDocument prob : probs) {
			if(foldIndices.contains(prob.getDocID())) {
				test.add(prob);
			} else {
				train.add(prob);
			}
		}
		return new Pair<List<ACEDocument>, List<ACEDocument>>(train, test);
	}
	
	public static void main(String args[]) throws Exception {
		System.out.println(getDataSplitForAI2(0).getSecond().size());
		System.out.println(getDataSplitForAI2(1).getSecond().size());
		System.out.println(getDataSplitForAI2(2).getSecond().size());
	}
}
