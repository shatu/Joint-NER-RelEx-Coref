package edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2004;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;

/**
 * @author Shashank
 */
public class PrepareNERData {
	
	private List<ACEDocument> docs;
	private ACECorpus corpus;
	
	public static void main(String [] args) {
		String inDirPath = "data/ACE2004_processed";
		String outDirPath = "data/ACE2004_NER/docs";
		PrepareNERData data = new PrepareNERData(inDirPath);
		data.splitAndDump(0.8f, outDirPath);
	}
 
	public PrepareNERData(String inDirPath) {
		corpus = new ACECorpus();
		corpus.initCorpus(inDirPath);
		docs = corpus.getAllDocs();
	}

	public int size() {
		return docs.size();
	}

	/**
	 * A helper function that shuffles the order of the docs
	 * 
	 * @param rnd: A random number generator -- if you use the same random generator (with the same seed), you will get the same ordering.
	 */
	public void shuffle(Random rnd) {
		int numberOfDocs = size();
		for (int i = 0; i < numberOfDocs; i++) {
			int j = i + rnd.nextInt(numberOfDocs - i);

			ACEDocument tmp = docs.get(i);
			docs.set(i, docs.get(j));
			docs.set(j, tmp);
		}
	}

	public void splitAndDump(float trainPerc, String outPath){
		this.shuffle(new Random(0));
		List<ACEDocument> train = new ArrayList<ACEDocument>();
		List<ACEDocument> test = new ArrayList<ACEDocument>();
		
		int numberOfTrainInstance = (int) (this.size()*trainPerc);
		for (int i = 0; i < size(); i++) {
			if (i < numberOfTrainInstance) {
				train.add(docs.get(i));
			} else {
				test.add(docs.get(i));
			}
		}
		
		File outDir = new File(outPath);
		if (outDir.exists() == true) {
			throw new RuntimeException("NER docs Directory already exists .. exiting");
		}
		outDir.mkdirs();
		
		File trainDir = new File(outDir, "Train");
		trainDir.mkdir();
		
		File testDir = new File(outDir, "Test");
		testDir.mkdir();
		
		File trainFile = new File(trainDir, "ACE_Train.obj");
        FileOutputStream f;
		try {
			f = new FileOutputStream(trainFile);
		    ObjectOutputStream s = new ObjectOutputStream(f);
		    s.writeObject(docs);
		    s.close();
		    f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File testFile = new File(testDir, "ACE_Test.obj");
		try {
			f = new FileOutputStream(testFile);
		    ObjectOutputStream s = new ObjectOutputStream(f);
		    s.writeObject(docs);
		    s.close();
		    f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	/**
//	 * A helper function that helps you to perform cross validation. It splits
//	 * the data in to n_fold {@link Pair}s, and each pair contains the (Training
//	 * and Testing) split.
//	 * 
//	 * @param numOffolds
//	 *            The number of fold you wish to performance cross validations.
//	 *            It equals to the length of the returned list.
//	 * @param rnd
//	 *            A random number generator. If you use the same seed, you will
//	 *            generate the same split. It makes the comparisons between
//	 *            different algorithms easier.
//	 * @return
//	 */
//	public List<Pair<List<ACEDocument>, List<ACEDocument>>> splitDataToNFolds(
//			int numOffolds, Random rnd) {
//
//		List<Integer> indexList = new ArrayList<Integer>();
//		int bp_size = size();
//		for (int i = 0; i < bp_size; i++)
//			indexList.add(i);
//
//		Collections.shuffle(indexList, rnd);
//		List<Pair<PrepareNERData, PrepareNERData>> res = new ArrayList<Pair<PrepareNERData, PrepareNERData>>();
//
//		for (int f = 0; f < numOffolds; f++) {
//			PrepareNERData cvTrain = new PrepareNERData();
//			PrepareNERData cvTest = new PrepareNERData();
//
//			if (instanceWeightList != null) {
//				cvTrain.instanceWeightList = new ArrayList<Float>();
//				cvTest.instanceWeightList = new ArrayList<Float>();
//			}
//
//			for (int i = 0; i < bp_size; i++) {
//				int real_idx = indexList.get(i);
//				if ((i) % numOffolds == f) {
//					// test
//					cvTest.instanceList.add(instanceList.get(real_idx));
//					cvTest.goldStructureList.add(goldStructureList.get(real_idx));
//					if (instanceWeightList != null) {
//						cvTest.instanceWeightList.add(instanceWeightList.get(real_idx));
//					}
//				} else {
//					// train
//					cvTrain.instanceList.add(instanceList.get(real_idx));
//					cvTrain.goldStructureList.add(goldStructureList.get(real_idx));
//					if (instanceWeightList != null) {
//						cvTrain.instanceWeightList.add(instanceWeightList.get(real_idx));
//					}
//				}
//			}
//
//			res.add(new Pair<PrepareNERData, PrepareNERData>(cvTrain,
//					cvTest));
//		}
//		return res;
//	}
}
