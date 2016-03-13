package edu.illinois.cs.cogcomp.cs546ccm2.corpus;

import java.util.HashMap;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;

/**
 * 
 * Intended to provide a set of functions to deal with a corpus.
 * 
 * @author shashank
 *
 */

public abstract class ACorpus {
	
	protected static String NAME = "ACorpus";
	
	protected static int docCount = 0;
	
	protected static boolean isInit = false;
	
	protected static List<ACEDocument> docs;
	
	protected static HashMap<String, Integer> docIDtoDocMap;
	
	public static String getName() {
		return NAME;
	}
	
	public ACEDocument getDocFromID(String id) {
		return docs.get(docIDtoDocMap.get(id));
	}
	
	public List<ACEDocument> getAllDocs() {
		return docs;
	}
    
    public int getDocCount() {
    	return docCount;
    }
    
    public static boolean isCorpusReady() {
    	return isInit;
    }
    
	public abstract void initCorpus(String inDirPath);
       
    public abstract void prepareCorpus(String docDirInput, String docDirOutput);

}
