package edu.illinois.cs.cogcomp.cs546ccm2.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;


/**
 * author Shashank
 */

public class TestCorpusGenerator {
	
	public static void main(String[] args) throws Exception {
		File trainDocListFile = new File("data/trainFiles");
		BufferedReader br = new BufferedReader(new FileReader(trainDocListFile));
		String filename;
		HashSet<String> trainingFiles = new HashSet<String>();
		
		while ((filename = br.readLine())!=null) {
			trainingFiles.add(filename);
		}
		
		br.close();
		
		File outPath = new File("data/ACE2005_CS546/corpus/test/bn");
		
		String inDirPath = CCM2Constants.ACE05FullCorpusPath;
		ACEReader aceReader = new ACEReader(inDirPath, new String[]{"bn"}, false);
		
		String docID = "AFP_ENG_20030304.0250";
		for (TextAnnotation ta: aceReader) {
			if (ta.getId().contains(docID) == false)
				continue;
			System.out.println(ta.getId());
		}
	}
}
