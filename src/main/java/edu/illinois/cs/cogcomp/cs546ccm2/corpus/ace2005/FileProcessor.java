/**
 * 
 */
package edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005;

import java.io.File;

import edu.illinois.cs.cogcomp.cs546ccm2.reader.ace2005.annotationStructure.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.ace2005.documentReader.AceFileProcessor;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;

/**
 * @author shashank
 *
 */
public class FileProcessor {

	private static final String TEST_DIR="data/ACE2005/nw";
    private static final String TEST_FILE="XIN_ENG_20030616.0274.apf.xml";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testProcessDocument();
	}
	
	@SuppressWarnings("unused")
	public static void testProcessDocument() {
        AceFileProcessor proc = new AceFileProcessor(new CcgTextAnnotationBuilder(new IllinoisTokenizer()));

        ACEDocument doc = proc.processAceEntry(new File(TEST_DIR), TEST_DIR + "/" + TEST_FILE);
    }

}
