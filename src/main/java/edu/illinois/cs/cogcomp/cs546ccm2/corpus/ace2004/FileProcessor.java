/**
 * 
 */
package edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2004;

import java.io.File;

import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;

/**
 * @author shashank
 *
 */
public class FileProcessor {

    private static final String TEST_DIR="data/ACE2004/data/English/nwire";
    private static final String TEST_FILE="NYT20001230.1309.0093.apf.xml";

    private static final String TEST_DIR_B="data/ACE2004/data/English/bnews";
    private static final String TEST_FILE_B="NBC20001004.1830.1520.apf.xml";
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		readNewswire();
    	readBroadcast();
	}
	
	@SuppressWarnings("unused")
	public static void readNewswire() {
        AceFileProcessor proc = new AceFileProcessor(new CcgTextAnnotationBuilder(new IllinoisTokenizer()));
        ACEDocument doc = proc.processAceEntry(new File(TEST_DIR), TEST_DIR + "/" + TEST_FILE);
	}
	
	@SuppressWarnings("unused")
	public static void readBroadcast() {
        AceFileProcessor proc = new AceFileProcessor(new CcgTextAnnotationBuilder(new IllinoisTokenizer()));
        ACEDocument doc = proc.processAceEntry(new File(TEST_DIR_B), TEST_DIR_B + "/" + TEST_FILE_B);
	}

}
