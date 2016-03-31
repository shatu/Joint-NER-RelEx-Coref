package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.Paragraph;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLParameters;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.learner.Learner;
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class CorefDriver {
	
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
	@CommandDescription(description = "Params : NerDirPath, train (true/false)")
	public static void doTrainTest(String nerDirPath, String isTrain) throws Exception {
		List<ACEDocument> trainDocs;
		List<ACEDocument> testDocs;
		
		File docsDir = new File(nerDirPath, "docs");
		File modelsDir = new File(nerDirPath, "models");
		
		if(!modelsDir.exists()) {
			modelsDir.mkdir();
		}
		
		String modelPrefix = "GoldMentions";
		
		File trainDir = new File(docsDir, "Train");
		File trainFile = new File(trainDir, "ACE_Train.obj");
		ObjectInputStream istream = new ObjectInputStream(new FileInputStream(trainFile));
		trainDocs = (List<ACEDocument>) istream.readObject();
		istream.close();
		
		File testDir = new File(docsDir, "Test");
		File testFile = new File(testDir, "ACE_Test.obj");
		istream = new ObjectInputStream(new FileInputStream(testFile));
		testDocs = (List<ACEDocument>) istream.readObject();
		istream.close();

		SLProblem train = getSP(trainDocs);
		SLProblem test = getSP(testDocs);
		
		if(isTrain.equalsIgnoreCase("true")) {
			trainModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".save", train);
		}
		
		testModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".save", test);
	}
	
	public static SLProblem getSP(List<ACEDocument> docList) throws Exception {
		SLProblem problem = new SLProblem();
		for(ACEDocument doc : docList) {
			List<Pair<String, Paragraph>> paragraphs = doc.paragraphs;
			List<Paragraph> contentParas = new ArrayList<>();
			for(Pair<String, Paragraph> pair: paragraphs) {
				if(pair.getFirst().equals("text"))
					contentParas.add(pair.getSecond());
			}
			
			int i=0;
			for(AnnotatedText at: doc.taList) {
				List<Constituent> docAnnots;
				TextAnnotation ta = at.getTa();
				docAnnots = ta.getView(CCM2Constants.NERGold).getConstituents();
					
				for(Constituent cons: docAnnots) {
					CorefInstance x = new CorefInstance(doc, contentParas.get(i), cons);
					CorefLabel y = new CorefLabel(cons.getLabel());
					problem.addExample(x, y);
				}
				i++;
			}
		}
		return problem;
	}
	
	public static void testModel(String modelPath, SLProblem sp) throws Exception {
		SLModel model = SLModel.loadModel(modelPath);
		int total = sp.instanceList.size();
		double correct = 0;
		for (int i = 0; i < sp.instanceList.size(); i++) {
			CorefInstance prob = (CorefInstance) sp.instanceList.get(i);
			CorefLabel gold = (CorefLabel) sp.goldStructureList.get(i);
			CorefLabel pred = (CorefLabel) model.infSolver.getBestStructure(model.wv, prob);
			if(CorefLabel.getLoss(gold, pred) < 0.0001) {
				correct++;
			} else {
//				incorrect++;
//				System.out.println(prob.doc.getDocID());
//				System.out.println();
//				System.out.println("Gold : " + gold);
//				System.out.println("Pred : " + pred);
//				System.out.println("Loss : " + CorefLabel.getLoss(gold, pred));
//				System.out.println("Labels : " + Arrays.asList(getLabelsWithScores(prob, model)));
//				System.out.println();
			}
		}
		
		System.out.println("Accuracy : = " + correct + " / " + total + " = " + (1.0*correct/total));
//		System.out.println("Strict Accuracy : =" + (1 - (1.0 * incorrect/total)));
	}
	
	public static void trainModel(String modelPath, SLProblem train) throws Exception {
		SLModel model = new SLModel();
		Lexiconer lm = new Lexiconer();
		lm.setAllowNewFeatures(true);
		model.lm = lm;
		CorefFeatureGenerator fg = new CorefFeatureGenerator(lm);
		model.featureGenerator = fg;
		model.infSolver = new CorefInferenceSolver(fg);
		SLParameters para = new SLParameters();
		para.loadConfigFile(Params.spConfigFile);
		Learner learner = LearnerFactory.getLearner(model.infSolver, fg, para);
		model.wv = learner.train(train);
		lm.setAllowNewFeatures(false);
		model.saveModel(modelPath);
	}
	
	public static Map<Boolean, Double> getLabelsWithScores(CorefInstance inst, SLModel model) {
		List<Boolean> labels = Arrays.asList(true, false);
		Map<Boolean, Double> labelsWithScores = new HashMap<Boolean, Double>();
		for(Boolean label : labels) {
			labelsWithScores.put(label, 1.0 * model.wv.dotProduct(model.featureGenerator.getFeatureVector(inst, new CorefLabel(label))));
		}
		
		return labelsWithScores;
	}

	public static void main(String[] args) throws Exception {
		InteractiveShell<CorefDriver> tester = new InteractiveShell<CorefDriver>(CorefDriver.class);
		if (args.length == 0) {
			tester.showDocumentation();
		} else {
			tester.runCommand(args);
		}
	}
}