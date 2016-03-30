package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.AMentionDetector;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.GoldMD;

/**
 * @author Shashank
 */

public class CreateNERTrainTestData {
	
	AMentionDetector mDetector;
	String baseDir;
	
	public static void main(String[] args) throws Exception {
		String nerDir = "data/ACE2005_NER";
		// One can use a different mention detection system here
		GoldMD goldMD = new GoldMD();
		CreateNERTrainTestData processor = new CreateNERTrainTestData(nerDir, goldMD);
		processor.processAndDump();
	}
	
	public CreateNERTrainTestData(String baseDir, AMentionDetector mDetector) {
		this.baseDir = baseDir;
		this.mDetector = mDetector;
	}
	
	public void processAndDump() throws Exception {
		File docsDir = new File(baseDir, "docs");
		File trainTestDir = new File(baseDir, "extracted");
		
		if(!trainTestDir.exists()) {
			trainTestDir.mkdir();
		}
		
		File inTrainDir = new File(docsDir, "Train");
		File inTrainFile = new File(inTrainDir, "ACE_Train.obj");
		File outTrainFile = new File(trainTestDir, "Train.obj");
		createTrainingData(inTrainFile, outTrainFile);
		
		File inTestDir = new File(docsDir, "Test");
		File inTestFile = new File(inTestDir, "ACE_Test.obj");
		File outTestFile = new File(trainTestDir, "Test.obj");
		createTestingData(inTestFile, outTestFile);
		
	}
	
	/**
	 * Read train docs and prepare training data
	 * 
	 * @param fname: File pointer to the train docs dump
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void createTrainingData(File trainFile, File outFile) throws Exception {
		ObjectInputStream s = new ObjectInputStream(new FileInputStream(trainFile));
		List<ACEDocument> trainDocs = (List<ACEDocument>) s.readObject();
		s.close();
		
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outFile));
		
		for(ACEDocument doc : trainDocs) {
			for(AnnotatedText at : doc.taList) {
				TextAnnotation ta = at.getTa();
				ArrayList<ArrayList<String>> instances = featureExtractor(ta);
			}
		}
	}
	
	/**
	 * Read test docs and prepare testing data.
	 * 
	 * @param fname: File pointer to the test docs dump
	 * @throws Exception
	 */
	private void createTestingData(File testFile, File outFile) throws Exception {
		createTrainingData(testFile, outFile);
	}
	
	/*
	 * TODO: Implement this function and return the feature vector which needs to be dumped on the disk
	 *       for Training and Testing
	 */
	private ArrayList<ArrayList<String>> featureExtractor(TextAnnotation ta) {
		return null;
	}
	
	
	
}
