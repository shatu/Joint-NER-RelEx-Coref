package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.CoRefChain;
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
	
	@SuppressWarnings("unchecked")
	@CommandDescription(description = "Params : SplitDirPath, train (true/false)")
	public static void doTrainTest(String splitDirPath, String isTrain) throws Exception {
		List<TextAnnotation> trainDocs;
		List<TextAnnotation> testDocs;
		
		File docsDir = new File(splitDirPath, "docs");
		File modelsDir = new File(splitDirPath, "CoRefModels");
		
		if(!modelsDir.exists()) {
			modelsDir.mkdir();
		}
		
		String modelPrefix = "GoldMentions";
		
		File trainDir = new File(docsDir, "Train");
		File trainFile = new File(trainDir, "ACE_Train.obj");
		ObjectInputStream istream = new ObjectInputStream(new FileInputStream(trainFile));
		trainDocs = (List<TextAnnotation>) istream.readObject();
		istream.close();
		
		File testDir = new File(docsDir, "Test");
		File testFile = new File(testDir, "ACE_Test.obj");
		istream = new ObjectInputStream(new FileInputStream(testFile));
		testDocs = (List<TextAnnotation>) istream.readObject();
		istream.close();

		SLProblem train = getTrainSP(trainDocs);
		SLProblem test = getTestSP(testDocs);
		
		if(isTrain.equalsIgnoreCase("true")) {
			trainModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".save", train);
		}
		
		testModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".save", train);
		testModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".save", test);
	}
	
	public static SLProblem getTrainSP(List<TextAnnotation> docList) throws Exception {
		SLProblem problem = new SLProblem();
		for (TextAnnotation ta : docList) {
			List<CoRefChain<Constituent>> chains = CoRefChain.getCoRefChainsFromCoRefView(ta, CCM2Constants.CoRefGoldExtent);
				
			for (int j=0; j<chains.size(); j++) {
				List<Pair<Constituent, Constituent>> posInstances = chains.get(j).getAllPairs();
				for (Pair<Constituent, Constituent> pair : posInstances) {
					CoRefInstance x = new CoRefInstance(pair.getFirst(), pair.getSecond());
					CoRefLabel y = new CoRefLabel("TRUE");
					problem.addExample(x, y);
				}
			}
				
			for (Pair<Constituent, Constituent> pair : getSequentialNegativeInstances(chains)) {
				CoRefInstance x = new CoRefInstance(pair.getFirst(), pair.getSecond());
				CoRefLabel y = new CoRefLabel("FALSE");
				problem.addExample(x, y);
			}
		}
		
		return problem;
	}

	public static SLProblem getTestSP(List<TextAnnotation> docList) throws Exception {
		SLProblem problem = new SLProblem();
		for (TextAnnotation ta : docList) {
			List<CoRefChain<Constituent>> chains = CoRefChain.getCoRefChainsFromCoRefView(ta, CCM2Constants.CoRefGoldExtent);
				
			for (int j=0; j<chains.size(); j++) {
				List<Pair<Constituent, Constituent>> posInstances = chains.get(j).getAllPairs();
				for (Pair<Constituent, Constituent> pair : posInstances) {
					CoRefInstance x = new CoRefInstance(pair.getFirst(), pair.getSecond());
					CoRefLabel y = new CoRefLabel("TRUE");
					problem.addExample(x, y);
				}
			}
				
			for (Pair<Constituent, Constituent> pair : getSequentialNegativeInstances(chains)) {
				CoRefInstance x = new CoRefInstance(pair.getFirst(), pair.getSecond());
				CoRefLabel y = new CoRefLabel("FALSE");
				problem.addExample(x, y);
			}
		}
		
		return problem;
	}
	
	public static List<Pair<Constituent, Constituent>> getSequentialNegativeInstances(List<CoRefChain<Constituent>> chains) {
		List<Pair<Constituent, Constituent>> negInstance = new ArrayList<>();
		for(int i=0; i<chains.size()-1; i++) {
			int j = i+1;
			negInstance.addAll(chains.get(i).getAllConjunctions(chains.get(j)));
		}
		
		return negInstance;
	}
	
	public static List<Pair<Constituent, Constituent>> getAllNegativeInstances(List<CoRefChain<Constituent>> chains) {
		List<Pair<Constituent, Constituent>> negInstance = new ArrayList<>();
		for(int i=0; i<chains.size()-1; i++) {
			for(int j=i+1; j<chains.size(); j++) {
				negInstance.addAll(chains.get(i).getAllConjunctions(chains.get(j)));
			}
		}
		
		return negInstance;
	}
	
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