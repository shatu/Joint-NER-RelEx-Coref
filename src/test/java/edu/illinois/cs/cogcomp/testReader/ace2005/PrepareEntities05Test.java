package edu.illinois.cs.cogcomp.testReader.ace2005;

import edu.illinois.cs.cogcomp.corpus.ace2005.PrepareEntities;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Tests the working of the derived ACE05 Reader (present in this project)
 * 
 * @author shashank
 */
public class PrepareEntities05Test {

    private static final String TEST_DIR="data/ACE2005";
    private static final String TEST_OUT="target/test05";

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