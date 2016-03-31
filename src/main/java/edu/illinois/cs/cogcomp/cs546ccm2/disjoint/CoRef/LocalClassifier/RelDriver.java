package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifierOld.Folds;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifierOld.Params;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifierOld.Problem;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifierOld.Tools;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLParameters;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.learner.Learner;
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class RelDriver {
	
	@CommandDescription(description = "Params : train (true/false), dataset(AI2/IL)")
	public static void crossVal(String train, String dataset) 
			throws Exception {
		double acc1 = 0.0, acc2 = 0.0;
		int numFolds = Folds.getNumFolds(dataset);
		for(int i=0;i<numFolds; i++) {
			Pair<Double, Double> pair = doTrainTest(i, train, dataset);
			acc1 += pair.getFirst();
			acc2 += pair.getSecond();
		}
		System.out.println("CV : " + (acc1/numFolds) + " " + (acc2/numFolds));
	}

	@CommandDescription(description = "Params : testFold, train (true/false), dataset(AI2/IL)")
	public static Pair<Double, Double> doTrainTest(int testFold, String isTrain, String dataset) 
			throws Exception {
		Pair<List<Problem>, List<Problem>> split = null;
		String prefix = null;
		if(dataset.equals("AI2")) {
			split = Folds.getDataSplitForAI2(testFold);
			prefix = Params.ai2rel;
		}
		if(dataset.equals("IL")) {
			split = Folds.getDataSplitForIL(testFold);
			prefix = Params.ilRel;
		}
		List<Problem> trainProbs = split.getFirst();
		List<Problem> testProbs = split.getSecond();
		SLProblem train = getSP(trainProbs);
		SLProblem test = getSP(testProbs);
		if(isTrain.equalsIgnoreCase("true")) {
			trainModel(prefix+testFold+".save", train, testFold, dataset);
		}
		return testModel(prefix+testFold+".save", test);
	}
	
	public static SLProblem getSP(List<Problem> problemList) throws Exception{
		SLProblem problem = new SLProblem();
		for(Problem prob : problemList){
			for(int i=0; i<prob.quantities.size(); ++i) {
				RelX x = new RelX(prob, i);
				RelY y = new RelY(prob.expr.findRelevanceLabel(i));
				problem.addExample(x, y);
			}
		}
		return problem;
	}
	
	public static Pair<Double, Double> testModel(String modelPath, SLProblem sp)
			throws Exception {
		SLModel model = SLModel.loadModel(modelPath);
		Set<Integer> incorrect = new HashSet<>();
		Set<Integer> total = new HashSet<>();
		double acc = 0.0;
		for (int i = 0; i < sp.instanceList.size(); i++) {
			RelX prob = (RelX) sp.instanceList.get(i);
			RelY gold = (RelY) sp.goldStructureList.get(i);
			RelY pred = (RelY) model.infSolver.getBestStructure(model.wv, prob);
			total.add(prob.problemId);
			if(RelY.getLoss(gold, pred) < 0.0001) {
				acc += 1;
			} else {
				incorrect.add(prob.problemId);
				System.out.println(prob.problemId+" : "+prob.ta.getText());
				System.out.println();
				System.out.println("Schema : "+prob.schema);
				System.out.println();
				System.out.println("Quantities : "+prob.quantities);
				System.out.println("Quant of Interest: "+prob.quantIndex);
				System.out.println("Gold : "+gold);
				System.out.println("Pred : "+pred);
				System.out.println();
			}
		}
		System.out.println("Accuracy : = " + acc + " / " + sp.instanceList.size() 
				+ " = " + (acc/sp.instanceList.size()));
		System.out.println("Strict Accuracy : ="+ (1-1.0*incorrect.size()/total.size()));
		return new Pair<Double, Double>(acc/sp.instanceList.size(),
				1-1.0*incorrect.size()/total.size());
	}
	
	public static void trainModel(String modelPath, SLProblem train, int testFold, String dataset) 
			throws Exception {
		SLModel model = new SLModel();
		Lexiconer lm = new Lexiconer();
		lm.setAllowNewFeatures(true);
		model.lm = lm;
		AbstractFeatureGenerator fg = null;
		if(dataset.equals("AI2")) {
			fg = new RelAI2FeatGen(lm);
		}
		if(dataset.equals("IL")) {
			fg = new RelILFeatGen(lm);
		}
		model.featureGenerator = fg;
		model.infSolver = new RelInfSolver(fg);
		SLParameters para = new SLParameters();
		para.loadConfigFile(Params.spConfigFile);
		Learner learner = LearnerFactory.getLearner(model.infSolver, fg, para);
		model.wv = learner.train(train);
		lm.setAllowNewFeatures(false);
		model.saveModel(modelPath);
	}
	
	public static Map<String, Double> getLabelsWithScores(RelX prob, SLModel model) {
		List<Boolean> labels = Arrays.asList(true, false);
		Map<String, Double> labelsWithScores = new HashMap<String, Double>();
		for(Boolean label : labels) {
			labelsWithScores.put(prob.quantIndex+"_"+label,
					1.0*model.wv.dotProduct(model.featureGenerator.getFeatureVector(
							prob,
							new RelY(label))));
		}
		return labelsWithScores;
	}
	
	public static void main(String[] args) throws Exception {
		InteractiveShell<RelDriver> tester = new InteractiveShell<RelDriver>(
				RelDriver.class);
		if (args.length == 0) {
			tester.showDocumentation();
		} else {
			tester.runCommand(args);
		}
		Tools.pipeline.closeCache();
	}
}