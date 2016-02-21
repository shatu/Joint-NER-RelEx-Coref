package edu.illinois.cs.cogcomp.testReader.ace2004;

import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.AceFileProcessor;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.ReadACEAnnotation;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

/**
 * test whether this code can read ACE 2004 annotations.
 *
 * @author shashank
 */
public class Ace04FileProcessorTest {

    private static final String TEST_DIR="data/ACE2004/data/English/nwire";
    private static final String TEST_FILE="NYT20001230.1309.0093.apf.xml";

    private static final String TEST_DIR_B="data/ACE2004/data/English/bnews";
    private static final String TEST_FILE_B="NBC20001004.1830.1520.apf.xml";

    @Test
    public void test2004Newswire() {
        ReadACEAnnotation.is2004mode = true;

        AceFileProcessor proc = new AceFileProcessor( new CcgTextAnnotationBuilder( new IllinoisTokenizer() ) );

        ACEDocument doc = proc.processAceEntry(new File(TEST_DIR), TEST_DIR + "/" + TEST_FILE);

        assertNotNull( doc );

        assertNotNull( doc.aceAnnotation );

        assert( !doc.aceAnnotation.entityList.isEmpty() );
//        assert( !doc.aceAnnotation.eventList.isEmpty() );
        assert( !doc.aceAnnotation.relationList.isEmpty() );
//        assert( !doc.aceAnnotation.timeExList.isEmpty() );
//        assert( !doc.aceAnnotation.valueList.isEmpty() );

    }



    @Test
    public void test2004Broadcast() {
        ReadACEAnnotation.is2004mode = true;

        AceFileProcessor proc = new AceFileProcessor( new CcgTextAnnotationBuilder( new IllinoisTokenizer() ) );

        ACEDocument doc = proc.processAceEntry(new File(TEST_DIR_B), TEST_DIR_B + "/" + TEST_FILE_B);

        assertNotNull( doc );

        assertNotNull( doc.aceAnnotation );

        assert( !doc.aceAnnotation.entityList.isEmpty() );
//        assert( !doc.aceAnnotation.eventList.isEmpty() );
        assert( !doc.aceAnnotation.relationList.isEmpty() );
//        assert( !doc.aceAnnotation.timeExList.isEmpty() );
//        assert( !doc.aceAnnotation.valueList.isEmpty() );

    }

}