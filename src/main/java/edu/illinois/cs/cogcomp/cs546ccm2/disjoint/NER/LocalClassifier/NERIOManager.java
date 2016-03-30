package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.SparseFeatureVector;

public class NERIOManager {

	public static float[][] getCostMatrix(Map<String, Integer> labelMapping, String fname) throws Exception{
		int numLabels = labelMapping.size();
		
		float[][] res = new float[numLabels][numLabels];
		for(int i=0;i  < numLabels ;i ++){
			for(int j=0; j < numLabels; j ++){
				if (i==j)
					res[i][j] = 0;
				else
					res[i][j] = 1.0f;
			}
		}
		ArrayList<String> lines = LineIO.read(fname);
		
		for(String line : lines){
			
			if (line.trim().charAt(0) == '#')
				continue;
			String[] tokens = line.split("\\s+");
			if (tokens.length != 3)
				throw new Exception("Format error in the cost matrix file.");
			if (!labelMapping.containsKey(tokens[0]))
				throw new Exception("Format error in the cost matrix file. Label (" + tokens[0] +") does not exist!"); 
			if (!labelMapping.containsKey(tokens[1]))
				throw new Exception("Format error in the cost matrix file. Label (" + tokens[1] +") does not exist!");
						
			int i = labelMapping.get(tokens[0]);
			int j = labelMapping.get(tokens[1]);
			float cost = -1;
			
			try{
				cost = Float.parseFloat(tokens[2]);				
			} catch(NumberFormatException e){
				throw new Exception("Format error in the cost matrix file. The cost should be a number!");
			}
			
			if (i ==j && cost !=0 )
				throw new Exception("The cost should be zero when pred == gold.");
			
			if (cost < 0)
				throw new Exception("The cost cannot be negative.");
			res[i][j] = cost;
		}		
		System.out.println("Done!");
		return res;
	}
	
	
	private static int checkNumOfFeaturesAndBuildClassMapping(String fileName,
			Map<String, Integer> labelMapping) throws Exception {
		int numFeatures = 0;
		
		ArrayList<String> lines = LineIO.read(fileName);

		for (String line : lines) {			
			String[] tokens = line.split("\\s+");
			String lab = tokens[0];

			// put the lab names into labels_mapping
			if (!labelMapping.containsKey(lab)) {
				int lab_size = labelMapping.size();
				labelMapping.put(lab, lab_size);
			}

			for (int i = 1; i < tokens.length; i++) {
				String[] featureTokens = tokens[i].split(":");
				
				if (featureTokens.length != 2){
					throw new Exception("Format error in the input file! in >" + line +"<");
				}
				
				int idx = Integer.parseInt(featureTokens[0]);

				if (idx <= 0) {
					throw new Exception(
							"The feature index must >= 1 !");
				}

				if (idx > numFeatures) {
					numFeatures = idx;
				}
			}
		}

		numFeatures ++; //allocate for zero 
		numFeatures ++; //allocate for the bias term		
		
		System.out.println("Label Mapping: "
				+ labelMapping.toString().replace("=", "==>"));
		System.out.println("number of features:" + numFeatures);

		return numFeatures;
	}

	/**
	 * Read training data
	 * 
	 * @param fname
	 *            The filename contains the training data
	 * @return A LabeledMulticlasssData
	 * @throws Exception
	 */
	public static LabeledNERData readTrainingData(String fname)
			throws Exception {
		Map<String, Integer> labelMapping = new HashMap<String, Integer>();
		int numFeatures = checkNumOfFeaturesAndBuildClassMapping(fname,
				labelMapping);
		int numClasses = labelMapping.size();

		LabeledNERData res = new LabeledNERData(labelMapping,
				numFeatures);
		readMultiClassDataAndAddBiasTerm(fname, labelMapping, numFeatures, numClasses, res);
		return res;
	}

	/**
	 * Read testing data.
	 * 
	 * @param fname
	 *            The filename contains the testing data
	 * @return A LabeledMulticlasssData
	 * @throws Exception
	 */
	public static LabeledNERData readTestingData(String fname,
			Map<String, Integer> labelsMapping, int numFeatures) throws Exception {
		int numClasses = labelsMapping.size();
		LabeledNERData res = new LabeledNERData(labelsMapping,
				numFeatures);
		readMultiClassDataAndAddBiasTerm(fname, labelsMapping, numFeatures, numClasses, res);
		return res;
	}

	private static void readMultiClassDataAndAddBiasTerm(String fname,
			Map<String, Integer> labelMapping, int numFeatures, int numClasses,
			LabeledNERData res) throws FileNotFoundException {
		ArrayList<String> lines = LineIO.read(fname);
		for (String line : lines) {
			String[] tokens = line.split("\\s+");

			int activeLen = 1;

			// ignore the features > n_features
			for (int i = 1; i < tokens.length; i++) {

				String[] featureTokens = tokens[i].split(":");
				int idx = Integer.parseInt(featureTokens[0]); 
				if (idx <= numFeatures) 
					activeLen++;
			}

			int[] idxList = new int[activeLen];
			float[] valueList = new float[activeLen];

			for (int i = 1; i < tokens.length; i++) {
				String[] feaureTokens = tokens[i].split(":");
				int idx = Integer.parseInt(feaureTokens[0]); 
				if (idx <= numFeatures) { 
					idxList[i - 1] = idx;
					valueList[i - 1] = Float.parseFloat(feaureTokens[1]);
				}
			}
			// append the bias term
			idxList[activeLen-1] = numFeatures-1;
			valueList[activeLen-1] = 1;

			IFeatureVector fv = new SparseFeatureVector(idxList, valueList);
			NERInstance mi = new NERInstance(numFeatures, numClasses,
					fv);
			res.instanceList.add(mi);

			String lab = tokens[0];
			if (labelMapping.containsKey(lab)) {
				res.goldStructureList.add(new NERLabel(labelMapping.get(lab)));
			} else {
				// only design for unknown classes in the test data
				res.goldStructureList.add(new NERLabel(-1));
			}
		}
	}
}
