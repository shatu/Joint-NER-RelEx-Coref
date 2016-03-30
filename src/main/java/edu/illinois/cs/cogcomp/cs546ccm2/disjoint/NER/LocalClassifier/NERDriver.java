package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLParameters;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.learner.Learner;
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory;

public class NERDriver {

	public static class AllTest{
		@CommandDescription(description="trainMultiClassModel trainingDataPath costMatrix ConfigFilePath modelPath")
		public static void trainMultiClassModel(String trainingDataPath, String costMatrixPath, String configFilePath, String modelPath)
				throws Exception {
			NERModel model = new NERModel();

			LabeledNERData sp = NERIOManager.readTrainingData(trainingDataPath);
			model.labelMapping = sp.labelMapping;
			model.numFeatures = sp.numFeatures;
			if(!costMatrixPath.equals("null"))
				model.cost_matrix = NERIOManager.getCostMatrix(sp.labelMapping,costMatrixPath);

			// initialize the inference solver
			model.infSolver = new NERInferenceSolver(model.cost_matrix);

			SLParameters para = new SLParameters();
			para.loadConfigFile(configFilePath);
			model.featureGenerator = new NERFeatureTransformer();

			Learner learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, para);
			model.wv = learner.train(sp);
			model.config =  new HashMap<String, String>();

			// save the model
			model.saveModel(modelPath);
		}
		@CommandDescription(description="testMultiClassModel modelPath testDataPath")
		public static void testMultiClassModel(String modelPath, String testDataPath) throws Exception{
			testMultiClassModel(modelPath, testDataPath, null);
		}

		@CommandDescription(description="testMultiClassModel modelPath testDataPath prediction")
		public static void testMultiClassModel(String modelPath, String testDataPath, String predictionFileName)
				throws Exception {
			NERModel model = (NERModel)SLModel.loadModel(modelPath);
			SLProblem sp = NERIOManager.readTestingData(testDataPath, model.labelMapping, model.numFeatures);

			BufferedWriter writer = null;
			if(predictionFileName!=null){
				writer = new BufferedWriter(new FileWriter(predictionFileName));
			}

			double pred_loss = 0.0;
			for (int i = 0; i < sp.size(); i++) {
				NERInstance ri = (NERInstance) sp.instanceList.get(i);
				NERLabel pred = (NERLabel) model.infSolver.getBestStructure(model.wv, ri);
				NERLabel gold = ((NERLabel)sp.goldStructureList.get(i));
				if(model.cost_matrix!=null)
					pred_loss += model.cost_matrix[gold.output][pred.output];
				else {
					if(pred.output!=gold.output)
					pred_loss += 1.0;
				}
				if(writer!=null)
					writer.write(pred.output+ "\n");
			}
			System.out.println("Loss = " + pred_loss/sp.size()+" "+pred_loss+"/"+sp.size());
			if(writer!=null)
				writer.close();
			return;
		}

	}
	public static void main(String[] args) throws Exception{
		InteractiveShell<AllTest> tester = new InteractiveShell<AllTest>(
				AllTest.class);

		if (args.length == 0)
			tester.showDocumentation();
		else
		{
			long start_time = System.currentTimeMillis();
			tester.runCommand(args);

			System.out.println("This experiment took "
					+ (System.currentTimeMillis() - start_time) / 1000.0
					+ " secs");
		}
	}
}
