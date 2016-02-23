package edu.illinois.cs.cogcomp.cs546ccm2.testReader.ace2005;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.AceAnnotationStructure.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.AceDocumentReader.Ace2005.AceFileProcessor;
import edu.illinois.cs.cogcomp.cs546ccm2.util.EventConstants;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;

/**
 * 
 * Tests the working of the original reader in processing a single ACE05 file
 * 
 * @author shashank
 */
public class AceFileProcessorTest {
    private static final String TEST_DIR="data/ACE2005/nw";
    private static final String TEST_FILE="XIN_ENG_20030616.0274.apf.xml";
//    private static final String TEST_FILE="MARKETVIEW_20050222.0729.apf.xml";
    
    @Test
    public void testProcessDocument() {
        AceFileProcessor proc = new AceFileProcessor( new CcgTextAnnotationBuilder( new IllinoisTokenizer() ) );

        ACEDocument doc = proc.processAceEntry(new File(TEST_DIR), TEST_DIR + "/" + TEST_FILE);

        List<TextAnnotation> taList = AceFileProcessor.populateTextAnnotation(doc);

        for (TextAnnotation ta : taList) {
            assertTrue( ta.hasView(EventConstants.NER_ACE_COARSE) );
        }
    }

}
