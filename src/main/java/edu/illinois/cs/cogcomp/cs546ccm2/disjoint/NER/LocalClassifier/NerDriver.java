package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.annotation.AnnotatorService;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalTrainedNER;
import edu.illinois.cs.cogcomp.curator.CuratorFactory;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLParameters;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.learner.Learner;
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class NerDriver {
	
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
		File modelsDir = new File(CCM2Constants.ACE05NerModelPath);
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

		SLProblem train = getTrainSP(trainReader, CCM2Constants.MDGoldExtent);
		SLProblem test = getTestSP(testReader, CCM2Constants.MDGoldExtent);
		
		if (isTrain.equalsIgnoreCase("true")) {
			trainModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".model", train);
		}
		
		testModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".model", train);
		testModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".model", test);
	}
	
	public static SLProblem getTrainSP(ACEReader docList, String mdView) throws Exception {
		SLProblem problem = new SLProblem();
		AnnotatorService annotator = CuratorFactory.buildCuratorClient();
		
		for (TextAnnotation ta : docList) {
			annotator.addView(ta, ViewNames.POS);
			
			List<Constituent> posInstances = getPositiveInstances(ta, mdView); 
			
			int posInstanceCount = 0;
			for (Constituent cons: posInstances) {
				NerInstance x = new NerInstance(cons);
				NerLabel y = new NerLabel(cons.getLabel());
				problem.addExample(x, y);
				posInstanceCount++;
			}
				
			List<Constituent> negInstances = getAllNegativeInstances(ta, mdView); 
			Collections.shuffle(negInstances);
				
			int negFrac = (int) (posInstanceCount*CCM2Constants.NerNegSamplingRatio);
				
			for (Constituent cons: negInstances.subList(0, Math.min(negFrac, negInstances.size()))) {
				NerInstance x = new NerInstance(cons);
				NerLabel y = new NerLabel("NO-ENT");
				problem.addExample(x, y);
			}
		}
		
		return problem;
	}
	
	public static SLProblem getTestSP(ACEReader docList, String mdView) throws Exception {
		SLProblem problem = new SLProblem();
		AnnotatorService annotator = CuratorFactory.buildCuratorClient();
		
		for (TextAnnotation ta : docList) {
			annotator.addView(ta, ViewNames.POS);
			
			List<Constituent> posInstances = getPositiveInstances(ta, mdView); 
			
			int posInstanceCount = 0;
			for (Constituent cons: posInstances) {
				NerInstance x = new NerInstance(cons);
				NerLabel y = new NerLabel(cons.getLabel());
				problem.addExample(x, y);
				posInstanceCount++;
			}
				
			List<Constituent> negInstances = getAllNegativeInstances(ta, mdView); 
			Collections.shuffle(negInstances);
				
			int negFrac = (int) (posInstanceCount*CCM2Constants.NerNegSamplingRatio);
				
			for (Constituent cons: negInstances.subList(0, Math.min(negFrac, negInstances.size()))) {
				NerInstance x = new NerInstance(cons);
				NerLabel y = new NerLabel("NO-ENT");
				problem.addExample(x, y);
			}
		}
		
		return problem;
	}
	
	public static List<Constituent> getPositiveInstances(TextAnnotation ta, String mdView) throws Exception {
		LocalTrainedNER.addRequiredViews(ta, mdView);
		return ta.getView(mdView).getConstituents();
	}
	
	private static List<Constituent> getAllNegativeInstances(TextAnnotation ta, String mdView) throws Exception {
		List<Constituent> negInstances = new ArrayList<>();
		LocalTrainedNER.addRequiredViews(ta, mdView);
		List<Constituent> posInstances = ta.getView(mdView).getConstituents();
		
		int pointer = 0;
		for(Constituent cons : posInstances) {
			int i = cons.getStartSpan();
			for(int j=pointer; j<i; j++) {
				Constituent neg = new Constituent("NO-ENT", "Neg-Mentions", ta, j, j+1);
				negInstances.add(neg);
			}
			
			pointer = cons.getEndSpan();
		}
		
		return negInstances;
	}
	
	//TODO: Add support to extract negative instances from an arbitrary mention detection module
	@SuppressWarnings("unused")
	private static List<Constituent> getAllNegativeInstances(TextAnnotation ta, String goldViewName, String noisyViewName) {
		throw new NotImplementedException("Use the other overloaded function");
	}
	
	public static void testModel(String modelPath, SLProblem sp) throws Exception {
		SLModel model = SLModel.loadModel(modelPath);
		int total = sp.instanceList.size();
		double correct = 0;
		for (int i = 0; i < sp.instanceList.size(); i++) {
			NerInstance prob = (NerInstance) sp.instanceList.get(i);
			NerLabel gold = (NerLabel) sp.goldStructureList.get(i);
			NerLabel pred = (NerLabel) model.infSolver.getBestStructure(model.wv, prob);
			if(NerLabel.getLoss(gold, pred) < 0.0001) {
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
		NerFeatureGenerator fg = new NerFeatureGenerator(lm);
		model.featureGenerator = fg;
		model.infSolver = new NerInferenceSolver(fg);
		SLParameters para = new SLParameters();
		para.loadConfigFile(Params.spConfigFile);
		Learner learner = LearnerFactory.getLearner(model.infSolver, fg, para);
		model.wv = learner.train(train);
		lm.setAllowNewFeatures(false);
		model.saveModel(modelPath);
	}
	
	public static Map<String, Double> getLabelsWithScores(NerInstance inst, SLModel model) {
		List<String> labels = CCM2Constants.NerLabelsFull;
		Map<String, Double> labelsWithScores = new HashMap<String, Double>();
		for(String label : labels) {
			labelsWithScores.put(label, 1.0 * model.wv.dotProduct(model.featureGenerator.getFeatureVector(inst, new NerLabel(label))));
		}
		
		return labelsWithScores;
	}

	public static void main(String[] args) throws Exception {
		InteractiveShell<NerDriver> tester = new InteractiveShell<NerDriver>(NerDriver.class);
		if (args.length == 0) {
			tester.showDocumentation();
		} else {
			tester.runCommand(args);
		}
	}
}