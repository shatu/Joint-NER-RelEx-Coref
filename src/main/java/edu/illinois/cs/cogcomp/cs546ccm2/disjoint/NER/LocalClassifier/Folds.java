package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import reader.Reader;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;

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
	

	public static Pair<List<Problem>, List<Problem>> getDataSplitForAI2(int fold) 
			throws Exception {
		List<Problem> train = new ArrayList<>();
		List<Problem> test = new ArrayList<>();
//		Map<Integer, IntPair> folds = new HashMap<Integer, IntPair>();
//		folds.put(0, new IntPair(0, 134));
//		folds.put(1, new IntPair(134, 274));
//		folds.put(2, new IntPair(274, 395));
//		List<Problem> probList = Reader.readAI2ProblemsFromFile(Params.ai2MathFile);
//		IntPair foldSpan = folds.get(fold);
//		for(Problem prob : probList) {
//			if(prob.id >= foldSpan.getFirst() && prob.id < foldSpan.getSecond()) {
//				test.add(prob);
//			} else {
//				train.add(prob);
//			}
//		}
		List<Problem> probs = Reader.readProblemsFromJson(Params.ai2Dir);
		String str = FileUtils.readFileToString(new File(Params.ai2Dir+"fold"+fold+".txt"));
		Set<Integer> foldIndices = new HashSet<>();
		for(String index : str.split("\n")) {
			foldIndices.add(Integer.parseInt(index));
		}
		for(Problem prob : probs) {
			if(foldIndices.contains(prob.id)) {
				test.add(prob);
			} else {
				train.add(prob);
			}
		}
		return new Pair<List<Problem>, List<Problem>>(train, test);
	}
	
	public static Pair<List<Problem>, List<Problem>> getDataSplitForIL(int fold) 
			throws Exception {
		List<Problem> train = new ArrayList<Problem>();
		List<Problem> test = new ArrayList<Problem>();
//		List<Problem> probs = Reader.readIllinoisProblemsFromFile(
//				Params.illinoisMathFile);
		List<Problem> probs = Reader.readProblemsFromJson(Params.ilDir);
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
		for(Problem prob : probs) {
			if(foldIndices.contains(prob.id)) {
				test.add(prob);
			} else {
				train.add(prob);
			}
		}
		return new Pair<List<Problem>, List<Problem>>(train, test);
	}
	
	public static Pair<List<Problem>, List<Problem>> getDataSplitForCC(int fold) 
			throws Exception {
		List<Problem> train = new ArrayList<Problem>();
		List<Problem> test = new ArrayList<Problem>();
//		List<List<Problem>> problemList = Reader.readCCProblemsFromDir("data/other/");
//		for(int i=0; i<6; ++i) {
//			if(i==fold) {
//				test.addAll(problemList.get(i));
//			} else {
//				train.addAll(problemList.get(i));
//			}
//		}
		List<Problem> probs = Reader.readProblemsFromJson(Params.ccDir);
		String str = FileUtils.readFileToString(new File(Params.ccDir+"fold"+fold+".txt"));
		Set<Integer> foldIndices = new HashSet<>();
		for(String index : str.split("\n")) {
			foldIndices.add(Integer.parseInt(index));
		}
		for(Problem prob : probs) {
			if(foldIndices.contains(prob.id)) {
				test.add(prob);
			} else {
				train.add(prob);
			}
		}
		return new Pair<List<Problem>, List<Problem>>(train, test);
	}
	
	public static void main(String args[]) throws Exception {
		System.out.println(getDataSplitForAI2(0).getSecond().size());
		System.out.println(getDataSplitForAI2(1).getSecond().size());
		System.out.println(getDataSplitForAI2(2).getSecond().size());
		Tools.pipeline.closeCache();
	}
}
