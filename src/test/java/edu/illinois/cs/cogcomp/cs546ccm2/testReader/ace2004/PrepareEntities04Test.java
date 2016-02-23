package edu.illinois.cs.cogcomp.cs546ccm2.testReader.ace2004;

import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2004.PrepareEntities;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.ReadACEAnnotation;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Tests the working of the derived ACE04 Reader (present in this project)
 * 
 * @author shashank
 */
public class PrepareEntities04Test {

    private static final String TEST_DIR="data/ACE2004/data/English";
    private static final String TEST_OUT="target/test04";

    @Test
    public void testReadDocument() {
    	ReadACEAnnotation.is2004mode = true;
        try {
            PrepareEntities.generateNerOutputFromAceDir(TEST_DIR, TEST_OUT);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
    }

}
