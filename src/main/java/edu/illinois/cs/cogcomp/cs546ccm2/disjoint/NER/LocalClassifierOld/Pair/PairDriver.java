package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.Problem;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.Folds;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.Params;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.Tools;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLParameters;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.learner.Learner;
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

public class PairDriver {
	
	@CommandDescription(description = "Params : train (true/false), dataset(AI2/IL/CC)")
	public static void crossVal(String train, String dataset) 
			throws Exception {
		double acc1 = 0.0, acc2 = 0.0;
		int numFolds = Folds.getNumFolds(dataset);
		for(int i=0; i<numFolds; i++) {
			Pair<Double, Double> pair = doTrainTest(i, train, dataset);
			acc1 += pair.getFirst();
			acc2 += pair.getSecond();
		}
		System.out.println("CV : " + (acc1/numFolds) + " " + (acc2/numFolds));
	}

	@CommandDescription(description = "Params : testFold, train (true/false), dataset(AI2/IL/CC)")
	public static Pair<Double, Double> doTrainTest(int testFold, String isTrain, String dataset) 
			throws Exception {
		Pair<List<Problem>, List<Problem>> split = null;
		String prefix = null;
		if(dataset.equals("AI2")) {
			split = Folds.getDataSplitForAI2(testFold);
			prefix = Params.ai2pair;
		}
		if(dataset.equals("IL")) {
			split = Folds.getDataSplitForIL(testFold);
			prefix = Params.ilPair;
		}
		if(dataset.equals("CC")) {
			split = Folds.getDataSplitForCC(testFold);
			prefix = Params.ccPair;
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
				for(int j=i+1; j<prob.quantities.size(); ++j) {
					String label = prob.expr.findLabelofLCA(i, j);
					PairX x = new PairX(prob, i, j);
					PairY y = new PairY(label);
					if(!label.equals("NONE")) {
						problem.addExample(x, y);
					}
				}
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
			PairX prob = (PairX) sp.instanceList.get(i);
			PairY gold = (PairY) sp.goldStructureList.get(i);
			PairY pred = (PairY) model.infSolver.getBestStructure(model.wv, prob);
			total.add(prob.problemId);
			if(PairY.getLoss(gold, pred) < 0.0001) {
				acc += 1;
			} else {
				incorrect.add(prob.problemId);
				System.out.println(prob.problemId+" : "+prob.ta.getText());
				System.out.println();
				System.out.println("Schema : "+prob.schema);
				System.out.println();
				System.out.println("Quantities : "+prob.quantities);
				System.out.println("Quant of Interest: "+prob.quantIndex1+" "+prob.quantIndex2);
				System.out.println("Gold : "+gold);
				System.out.println("Pred : "+pred);
				System.out.println("Loss : "+PairY.getLoss(gold, pred));
				System.out.println("Labels : "+Arrays.asList(getLabelsWithScores(prob, model)));
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
			fg = new PairAI2FeatGen(lm);
		}
		if(dataset.equals("IL")) {
			fg = new PairILFeatGen(lm);
		}
		if(dataset.equals("CC")) {
			fg = new PairCCFeatGen(lm);
		}
		model.featureGenerator = fg;
		model.infSolver = new PairInfSolver(fg);
		SLParameters para = new SLParameters();
		para.loadConfigFile(Params.spConfigFile);
		Learner learner = LearnerFactory.getLearner(model.infSolver, fg, para);
		model.wv = learner.train(train);
		lm.setAllowNewFeatures(false);
		model.saveModel(modelPath);
	}
	
	public static Map<String, Double> getLabelsWithScores(PairX prob, SLModel model) {
		List<String> labels = Arrays.asList("ADD", "SUB", "MUL", "DIV", "SUB_REV", "DIV_REV");
		Map<String, Double> labelsWithScores = new HashMap<String, Double>();
		for(String label : labels) {
			labelsWithScores.put(prob.quantIndex1+"_"+prob.quantIndex2+"_"+label,
					1.0*model.wv.dotProduct(model.featureGenerator.getFeatureVector(
							prob,
							new PairY(label))));
		}
		return labelsWithScores;
	}

	public static void main(String[] args) throws Exception {
		InteractiveShell<PairDriver> tester = new InteractiveShell<PairDriver>(
				PairDriver.class);
		if (args.length == 0) {
			tester.showDocumentation();
		} else {
			tester.runCommand(args);
		}
		Tools.pipeline.closeCache();
	}
}