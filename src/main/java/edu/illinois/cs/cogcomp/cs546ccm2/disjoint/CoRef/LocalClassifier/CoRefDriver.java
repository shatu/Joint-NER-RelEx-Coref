package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.illinois.cs.cogcomp.annotation.AnnotatorService;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.CoreferenceView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.curator.CuratorFactory;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLParameters;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.learner.Learner;
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class CoRefDriver {
	
	//TODO: Adapt the commented code to support cross-validation
//	@CommandDescription(description = "Params : train (true/false), dataset(AI2/IL/CC)")
//	public static void crossVal(String train, String dataset) 
//			throws Exception {
//		double acc1 = 0.0, acc2 = 0.0;
//		int numFolds = Folds.getNumFolds(dataset);
//		for(int i=0; i<numFolds; i++) {
//			Pair<Double, Double> pair = doTrainTest(i, train, dataset);
//			acc1 += pair.getFirst();
//			acc2 += pair.getSecond();
//		}
//		System.out.println("CV : " + (acc1/numFolds) + " " + (acc2/numFolds));
//	}
//
//	@CommandDescription(description = "Params : testFold, train (true/false), dataset(AI2/IL/CC)")
//	public static Pair<Double, Double> doTrainTest(int testFold, String isTrain, String dataset) 
//			throws Exception {
//		Pair<List<Problem>, List<Problem>> split = null;
//		String prefix = null;
//		if(dataset.equals("AI2")) {
//			split = Folds.getDataSplitForAI2(testFold);
//			prefix = Params.ai2pair;
//		}
//		if(dataset.equals("IL")) {
//			split = Folds.getDataSplitForIL(testFold);
//			prefix = Params.ilPair;
//		}
//		if(dataset.equals("CC")) {
//			split = Folds.getDataSplitForCC(testFold);
//			prefix = Params.ccPair;
//		}
//		List<Problem> trainProbs = split.getFirst();
//		List<Problem> testProbs = split.getSecond();
//		SLProblem train = getSP(trainProbs);
//		SLProblem test = getSP(testProbs);
//		if(isTrain.equalsIgnoreCase("true")) {
//			trainModel(prefix+testFold+".save", train, testFold, dataset);
//		}
//		return testModel(prefix+testFold+".save", test);
//	}
	
	@CommandDescription(description = "Params : train (true/false)")
	public static void doTrainTest(String isTrain) throws Exception {
		File modelsDir = new File(CCM2Constants.ACE05CoRefModelPath);

		ACEReader trainReader;
		ACEReader testReader;
		
		if (!modelsDir.exists()) {
			modelsDir.mkdirs();
		}
		
		String modelPrefix = CCM2Constants.MDGoldExtent;
		
		String trainDir = CCM2Constants.ACE05TrainCorpusPath;
		String testDir = CCM2Constants.ACE05TestCorpusPath;
		
		trainReader = new ACEReader(trainDir, false);
		testReader = new ACEReader(testDir, false);

		SLProblem train = getTrainSP(trainReader, CCM2Constants.CoRefGoldExtent, CCM2Constants.MDGoldExtent);
		SLProblem test = getTestSP(testReader, CCM2Constants.CoRefGoldExtent, CCM2Constants.MDGoldExtent);
		
		if (isTrain.equalsIgnoreCase("true")) {
			trainModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".model", train);
		}
		
		testModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".model", train);
		testModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".model", test);
	}
	
	public static SLProblem getTrainSP (ACEReader docList, String corefViewName, String mentionViewName) throws Exception {
		SLProblem problem = new SLProblem();
		AnnotatorService annotator = CuratorFactory.buildCuratorClient();
		
		for (TextAnnotation ta : docList) {
			annotator.addView(ta, ViewNames.POS);
				
//			int posInstanceCount = 0;
			List<Pair<Constituent, Constituent>> posInstances = getPositiveInstances(ta, corefViewName);
			for (Pair<Constituent, Constituent> pair : posInstances) {
				CoRefInstance x = new CoRefInstance(pair.getFirst(), pair.getSecond());
				CoRefLabel y = new CoRefLabel("TRUE");
				problem.addExample(x, y);
//				posInstanceCount++;
			}
			
			List<Pair<Constituent, Constituent>> negInstances = getNegativeInstances(ta, corefViewName);
//			Collections.shuffle(negInstances);
			
//			int negFrac = (int) (posInstanceCount*CCM2Constants.CoRefNegSamplingRatio);
				
			for (Pair<Constituent, Constituent> pair: negInstances) {
				CoRefInstance x = new CoRefInstance(pair.getFirst(), pair.getSecond());
				CoRefLabel y = new CoRefLabel("FALSE");
				problem.addExample(x, y);
			}
		}
		
		return problem;
	}

	public static SLProblem getTestSP (ACEReader docList, String corefViewName, String mentionViewName) throws Exception {
		SLProblem problem = new SLProblem();
		AnnotatorService annotator = CuratorFactory.buildCuratorClient();
		for (TextAnnotation ta : docList) {
			annotator.addView(ta, ViewNames.POS);
			
			List<Pair<Constituent, Constituent>> posInstances = getPositiveInstances(ta, corefViewName);
				
//			int posInstanceCount = 0;
			for (Pair<Constituent, Constituent> pair : posInstances) {
				CoRefInstance x = new CoRefInstance(pair.getFirst(), pair.getSecond());
				CoRefLabel y = new CoRefLabel("TRUE");
				problem.addExample(x, y);
//				posInstanceCount++;
			}
				
			List<Pair<Constituent, Constituent>> negInstances = getNegativeInstances(ta, corefViewName);
//			Collections.shuffle(negInstances);
			
//			int negFrac = (int) (posInstanceCount*CCM2Constants.CoRefNegSamplingRatio);
				
			for (Pair<Constituent, Constituent> pair: negInstances) {
				CoRefInstance x = new CoRefInstance(pair.getFirst(), pair.getSecond());
				CoRefLabel y = new CoRefLabel("FALSE");
				problem.addExample(x, y);
			}
		}
		
		return problem;
	}
	
	public static List<Pair<Constituent, Constituent>> getPositiveInstances(TextAnnotation ta, String corefViewName) {
		 CoreferenceView corefView = (CoreferenceView) ta.getView(corefViewName);
		 List<Constituent> constituents = corefView.getConstituents();   // Sorting constituents in increasing order
		 constituents = sortIncreasing(constituents);

		 int numConstituents = constituents.size();
		 
		 List<Pair<Constituent, Constituent>> problemInstances = new ArrayList<>();
		 
		 for (int i=0; i<numConstituents; i++) {
			 Constituent currC = constituents.get(i);
			 String currID = currC.getLabel();
			 int past = i-1;
			 
			 while (past >=0) {
				 Constituent prevC = constituents.get(past);
				 String prevID = prevC.getLabel();
				 
				 if (currID.equals(prevID)) {
					 problemInstances.add(new Pair<>(currC, prevC));
				 }
				 
				 past--;   
			 }
		 }
		 
		 return problemInstances;
	}
	
	public static List<Pair<Constituent, Constituent>> getNegativeInstances(TextAnnotation ta, String corefViewName) {
		 CoreferenceView corefView = (CoreferenceView) ta.getView(corefViewName);
		 List<Constituent> constituents = corefView.getConstituents();   // Sorting constituents in increasing order
		 constituents = sortIncreasing(constituents);

		 int numConstituents = constituents.size();
		 
		 List<Pair<Constituent, Constituent>> problemInstances = new ArrayList<>();
		 
		 for (int i=0; i<numConstituents; i++) {
			 Constituent currC = constituents.get(i);
			 String currID = currC.getLabel();
			 int past = i-1, negadded = 0;
			 
			 while (past >=0) {
				 Constituent prevC = constituents.get(past);
				 String prevID = prevC.getLabel();
				 if (currID.equals(prevID) == false) {
					 if (negadded < 5) {
						 problemInstances.add(new Pair<>(currC, prevC));
						 negadded++;
					 }
				 }
				 
				 past--;   
			 }
		 }
		 
		 return problemInstances;
	}
	
    public static List<Constituent> sortIncreasing(List<Constituent> constituents){
        Collections.sort(constituents, new Comparator<Constituent>(){
            @Override
            public int compare(Constituent o1, Constituent o2) {
                if(o1.getStartSpan() >= o2.getStartSpan() )
                    return 1;
                else
                    return -1;
            }
        });
        
        return constituents;
    }
	
//	public static List<Pair<Constituent, Constituent>> getSequentialNegativeInstances(List<CoRefChain<Constituent>> chains) {
//		List<Pair<Constituent, Constituent>> negInstance = new ArrayList<>();
//		for(int i=0; i<chains.size()-1; i++) {
//			int j = i+1;
//			negInstance.addAll(chains.get(i).getAllConjunctions(chains.get(j)));
//		}
//		
//		return negInstance;
//	}
	
//	public static List<Pair<Constituent, Constituent>> getAllNegativeInstances(List<CoRefChain<Constituent>> chains) {
//		List<Pair<Constituent, Constituent>> negInstance = new ArrayList<>();
//		for(int i=0; i<chains.size()-1; i++) {
//			for(int j=i+1; j<chains.size(); j++) {
//				negInstance.addAll(chains.get(i).getAllConjunctions(chains.get(j)));
//			}
//		}
//		
//		return negInstance;
//	}
	
	public static void testModel(String modelPath, SLProblem sp) throws Exception {
		SLModel model = SLModel.loadModel(modelPath);
		int total = sp.instanceList.size();
		double correct = 0;
		for (int i = 0; i < sp.instanceList.size(); i++) {
			CoRefInstance prob = (CoRefInstance) sp.instanceList.get(i);
			CoRefLabel gold = (CoRefLabel) sp.goldStructureList.get(i);
			CoRefLabel pred = (CoRefLabel) model.infSolver.getBestStructure(model.wv, prob);
			if(CoRefLabel.getLoss(gold, pred) < 0.0001) {
				correct++;
			} else {
//				incorrect++;
//				System.out.println(prob.doc.getDocID());
//				System.out.println();
//				System.out.println("Gold : " + gold);
//				System.out.println("Pred : " + pred);
//				System.out.println("Loss : " + NerLabel.getLoss(gold, pred));
//				System.out.println("Labels : " + Arrays.asList(getLabelsWithScores(prob, model)));
//				System.out.println();
			}
		}
		
		System.out.println("Accuracy : = " + correct + " / " + total + " = " + (1.0*correct/total));
//		System.out.println("Strict Accuracy : =" + (1 - (1.0 * incorrect/total)));
	}
	
	public static void trainModel(String modelPath, SLProblem train) throws Exception {
//		System.out.println(train.size());
		SLModel model = new SLModel();
		Lexiconer lm = new Lexiconer();
		lm.setAllowNewFeatures(true);
		model.lm = lm;
		CoRefFeatureGenerator fg = new CoRefFeatureGenerator(lm);
		model.featureGenerator = fg;
		model.infSolver = new CoRefInferenceSolver(fg);
		SLParameters para = new SLParameters();
		para.loadConfigFile(Params.spConfigFile);
		Learner learner = LearnerFactory.getLearner(model.infSolver, fg, para);
		model.wv = learner.train(train);
		lm.setAllowNewFeatures(false);
		model.saveModel(modelPath);
	}
	
	public static Map<String, Double> getLabelsWithScores(CoRefInstance inst, SLModel model) {
		List<String> labels = CCM2Constants.CoRefLabels;
		Map<String, Double> labelsWithScores = new HashMap<String, Double>();
		for(String label : labels) {
			labelsWithScores.put(label, 1.0 * model.wv.dotProduct(model.featureGenerator.getFeatureVector(inst, new CoRefLabel(label))));
		}
		
		return labelsWithScores;
	}

	public static void main(String[] args) throws Exception {
		InteractiveShell<CoRefDriver> tester = new InteractiveShell<CoRefDriver>(CoRefDriver.class);
		if (args.length == 0) {
			tester.showDocumentation();
		} else {
			tester.runCommand(args);
		}
	}
}