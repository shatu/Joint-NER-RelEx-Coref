package edu.illinois.cs.cogcomp.cs546ccm2.testReader.ace2004;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2004.PrepareEntities;

/**
 * Tests the working of the derived ACE04 Reader (present in this project)
 * 
 * @author shashank
 */
public class PrepareEntitiesTest {

    private static final String TEST_DIR="data/ACE2004/data/English";
    private static final String TEST_OUT="target/test04";

    @Test
    public void testReadDocument() {
        try {
            PrepareEntities.generateNerOutputFromAceDir(TEST_DIR, TEST_OUT);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        
    }

}
