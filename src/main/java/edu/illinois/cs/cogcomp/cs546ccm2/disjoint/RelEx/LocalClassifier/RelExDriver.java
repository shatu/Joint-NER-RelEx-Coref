package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx.LocalClassifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.illinois.cs.cogcomp.annotation.AnnotatorService;
import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.QueryableList;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Queries;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
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

public class RelExDriver {
	
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
		File modelsDir = new File(CCM2Constants.ACE05RelExModelPath);
		
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

		SLProblem train = getTrainSP(trainReader, CCM2Constants.RelExGoldExtent, CCM2Constants.MDGoldExtent);
		SLProblem test = getTestSP(testReader, CCM2Constants.RelExGoldExtent, CCM2Constants.MDGoldExtent);
		
		if (isTrain.equalsIgnoreCase("true")) {
			trainModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".model", train);
		}
		
		testModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".model", train);
		testModel(modelsDir.getAbsolutePath() + "/" + modelPrefix + ".model", test);
	}
	
	public static SLProblem getTrainSP(ACEReader docList, String relationViewName, String mentionViewName) throws Exception {
		SLProblem problem = new SLProblem();
		AnnotatorService annotator = CuratorFactory.buildCuratorClient();
		
		for (TextAnnotation ta : docList) {
			annotator.addView(ta, ViewNames.POS);
			
			List<Constituent> docAnnots = ((PredicateArgumentView)ta.getView(relationViewName)).getPredicates();
			
			for (Constituent cons: docAnnots) {
				if (cons.getOutgoingRelations().size() > 0) {
					for (Relation rel : cons.getOutgoingRelations()) {
						RelInstance x = new RelInstance(cons, rel.getTarget());
						RelLabel y = new RelLabel(rel.getRelationName());
						problem.addExample(x, y);
					}
				}
			}
				
			List<Pair<Constituent, Constituent>> negInstances = sampleNegativeInstances(ta, relationViewName, mentionViewName);
				
			for (Pair<Constituent, Constituent> pair : negInstances) {
				RelInstance x = new RelInstance(pair.getFirst(), pair.getSecond());
				RelLabel y = new RelLabel("NO-REL");
				problem.addExample(x, y);
			}
		}
		
		return problem;
	}
	
	public static SLProblem getTestSP(ACEReader docList, String relationViewName, String mentionViewName) throws Exception {
		SLProblem problem = new SLProblem();
		AnnotatorService annotator = CuratorFactory.buildCuratorClient();
		
		for (TextAnnotation ta : docList) {
			annotator.addView(ta, ViewNames.POS);
			
			List<Constituent> docAnnots = ((PredicateArgumentView)ta.getView(relationViewName)).getPredicates();
			
			for (Constituent cons: docAnnots) {
				if (cons.getOutgoingRelations().size() > 0) {
					for (Relation rel : cons.getOutgoingRelations()) {
						RelInstance x = new RelInstance(cons, rel.getTarget());
						RelLabel y = new RelLabel(rel.getRelationName());
						problem.addExample(x, y);
					}
				}
			}
				
			List<Pair<Constituent, Constituent>> negInstances = sampleNegativeInstances(ta, relationViewName, mentionViewName);
				
			for (Pair<Constituent, Constituent> pair : negInstances) {
				RelInstance x = new RelInstance(pair.getFirst(), pair.getSecond());
				RelLabel y = new RelLabel("NO-REL");
				problem.addExample(x, y);
			}
		}
		
		return problem;
	}
	
	public static List<Pair<Constituent, Constituent>> sampleNegativeInstances (TextAnnotation ta, String relationViewName, String mentionViewName) {
        Random rand = new Random();

        PredicateArgumentView relationView = (PredicateArgumentView) ta.getView(relationViewName);

        int numOfRelation = relationView.getPredicates().size();
        int toSample = (int) (numOfRelation * CCM2Constants.RelExNegSamplingRatio);

        List<Pair<Constituent, Constituent>> sampledRelations = new ArrayList<>(toSample);
        SpanLabelView mentionView = (SpanLabelView) ta.getView(mentionViewName);

        int addedEntities = 0;
        int attempts = 2;
        int numEntities = mentionView.getNumberOfConstituents();

        IQueryable<Constituent> predicates = new QueryableList<>(relationView.getPredicates());

        while (addedEntities < toSample && attempts > 0) {
            int firstEntity = rand.nextInt(numEntities);
            int secondEntity = rand.nextInt(numEntities);

            attempts--;
            
            if (firstEntity == secondEntity) {
                continue;
            }

            Constituent firstItem = mentionView.getConstituents().get(firstEntity);
            Constituent secondItem = mentionView.getConstituents().get(secondEntity);

            boolean hasRelation = false;
            IQueryable<Constituent> matchPredicateResult = predicates.where(Queries.sameSpanAsConstituent(firstItem));
            
            if (matchPredicateResult.count() > 0) {
                for (Constituent pred : matchPredicateResult) {
                    if (pred.getStartSpan() == secondItem.getStartSpan() && pred.getEndSpan() == secondItem.getEndSpan()) {
                        hasRelation = true;
                        break;
                    }
                }
            }

            if (!hasRelation) {
                sampledRelations.add(new Pair<>(firstItem, secondItem));
                addedEntities++;
                attempts = 2;
            }
         }

        return sampledRelations;
	}
	
	public static void testModel (String modelPath, SLProblem sp) throws Exception {
		SLModel model = SLModel.loadModel(modelPath);
		int total = sp.instanceList.size();
		double correct = 0;
		
		for (int i = 0; i < sp.instanceList.size(); i++) {
			RelInstance prob = (RelInstance) sp.instanceList.get(i);
			RelLabel gold = (RelLabel) sp.goldStructureList.get(i);
			RelLabel pred = (RelLabel) model.infSolver.getBestStructure(model.wv, prob);
			if (RelLabel.getLoss(gold, pred) < 0.0001) {
				correct++;
			} 
			else {
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
		RelFeatureGenerator fg = new RelFeatureGenerator(lm);
		model.featureGenerator = fg;
		model.infSolver = new RelInferenceSolver(fg);
		SLParameters para = new SLParameters();
		para.loadConfigFile(Params.spConfigFile);
		Learner learner = LearnerFactory.getLearner(model.infSolver, fg, para);
		model.wv = learner.train(train);
		lm.setAllowNewFeatures(false);
		model.saveModel(modelPath);
	}
	
	public static Map<String, Double> getLabelsWithScores(RelInstance inst, SLModel model) {
		List<String> labels = CCM2Constants.RelationTypesFull;
		Map<String, Double> labelsWithScores = new HashMap<String, Double>();
		
		for (String label : labels) {
			labelsWithScores.put(label, 1.0 * model.wv.dotProduct(model.featureGenerator.getFeatureVector(inst, new RelLabel(label))));
		}
		
		return labelsWithScores;
	}

	public static void main(String[] args) throws Exception {
		InteractiveShell<RelExDriver> tester = new InteractiveShell<RelExDriver>(RelExDriver.class);
		if (args.length == 0) {
			tester.showDocumentation();
		} 
		else {
			tester.runCommand(args);
		}
	}
}