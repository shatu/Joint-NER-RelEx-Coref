package edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.AceFileProcessor;
import edu.illinois.cs.cogcomp.reader.util.EventConstants;
import gnu.trove.set.hash.TIntHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

/**
 * Read ACE2005 data, generate an NER corpus in column format using coarse or fine NER labels.
 * Augment with quantities and time expressions.
 *
 * @author shashank
 */
public class PrepareEntities {
    @SuppressWarnings("unused")
	private static final String NAME = PrepareEntities.class.getCanonicalName();

    protected enum EntityOutType { NER_FINE_BASIC, NER_COARSE_BASIC, NER_FINE_AUGMENTED, NER_COARSE_AUGMENTED };

    private static Logger logger = LoggerFactory.getLogger( PrepareEntities.class );


    private static final String OUT_TAG = "O";
    private static String docDirInput = "data/ACE2005/";
    private static String nerDirOutput = "target/test05";

    public static void main( String[] args ) throws IOException {

//        if (args.length != 2) {
//            System.err.println( "Usage: " + NAME + " aceInputDir nerOutputDir" );
//        }
//        String aceCorpusDir = args[0];
//        String nerOutputDir = args[1];
//
//        generateNerOutputFromAceDir(aceCorpusDir, nerOutputDir);
        generateNerOutputFromAceDir(docDirInput, nerDirOutput);
    }

    public static void generateNerLocal() {

        try {
            generateNerOutputFromAceDir( docDirInput, nerDirOutput );
        } catch (IOException e) {
            e.printStackTrace();
            System.exit( -1 );
        }
    }


    /**
     * generates two column format NER corpora from an ACE-annotated corpus: one using coarse-grained NE types,
     *   and one using fine-grained NE types.
     *
     * @param aceCorpusDir
     * @param nerOutputDir
     * @throws IOException
     */
    @SuppressWarnings("unused")
	public static void generateNerOutputFromAceDir( String aceCorpusDir, String nerOutputDir ) throws IOException {
        File inputFolder = new File (aceCorpusDir);
        File[] subFolderList = inputFolder.listFiles();
//        IOFileFilter fileFilter = TrueFileFilter.INSTANCE;
        AceFileProcessor processor = new AceFileProcessor( new CcgTextAnnotationBuilder( new IllinoisTokenizer() ) );
        Set<String> coarseTypes = new HashSet<>();
        Set<String> fineTypes = new HashSet<>();
        ArrayList<String> failList = new ArrayList<String>();
//        int correctFileCount = 0;

        for (int folderIndex = 0; folderIndex < subFolderList.length; ++folderIndex) {

//        	if(!subFolderList[folderIndex].getAbsolutePath().contains("un"))
//        		continue;
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File directory, String fileName) {
                    return fileName.endsWith(".apf.xml");
                }
            };
            File subFolderEntry = subFolderList[folderIndex];
            File labelFolder = new File(subFolderEntry.getAbsolutePath());
            File[] fileList = labelFolder.listFiles(filter);
            for (int fileID = 0; fileID < fileList.length; ++fileID) {

                String annotationFile = fileList[fileID].getAbsolutePath();
                String annotationOutFile = fileList[ fileID ].getName();
                System.err.println( "processing file '" + annotationFile + "'..." );
                ACEDocument doc = null;
                try {
                	doc = processor.processAceEntry(subFolderEntry, annotationFile);
                
                List<TextAnnotation> taList = AceFileProcessor.populateTextAnnotation(doc);

                updateTypes( taList, coarseTypes, fineTypes );

                String outFilePrefix = IOUtils.stripFileExtension( annotationOutFile );
                String fineNerOutDir = nerOutputDir + "/" + EventConstants.NER_ACE_FINE;
                String coarseNerOutDir = nerOutputDir + "/" + EventConstants.NER_ACE_COARSE;

                writeOutEntityColumnFormat(EntityOutType.NER_COARSE_AUGMENTED, coarseNerOutDir, outFilePrefix, taList);
                writeOutEntityColumnFormat(EntityOutType.NER_FINE_AUGMENTED, fineNerOutDir, outFilePrefix, taList);
//                correctFileCount++;
               } catch (Exception e) {
                	failList.add(annotationFile);
                	continue;
                }
                
            }

            printTypes( "Coarse Types: ", coarseTypes );
            printTypes( "Fine Types: ", fineTypes );
        }
//        for(int i=0; i<failList.size(); i++) {
//        	System.out.println(failList.get(i));
//        }
//        System.out.println(correctFileCount);
    }

    private static void printTypes(String s, Set<String> types) {
        System.out.println(s);

        for (String t : types)
            System.out.println(t);

        return;
    }

    /**
     * generate lists of the types for the different corpora
     *
     * @param taList
     * @param coarseTypes
     * @param fineTypes
     */
    private static void updateTypes(List<TextAnnotation> taList, Set<String> coarseTypes, Set<String> fineTypes) {

        for (TextAnnotation ta : taList) {
        	// View might not exist for very short texts
            if (ta.hasView(EventConstants.NER_ACE_COARSE ))
                for (Constituent c : ta.getView(EventConstants.NER_ACE_COARSE))
                    coarseTypes.add(c.getLabel());

            if (ta.hasView(EventConstants.NER_ACE_FINE))
                for (Constituent c : ta.getView(EventConstants.NER_ACE_FINE))
                    fineTypes.add(c.getLabel());

            if (ta.hasView(EventConstants.NER_ACE_QUANTITY))
                for (Constituent c : ta.getView(EventConstants.NER_ACE_QUANTITY)) {
                    coarseTypes.add(c.getLabel());
                    fineTypes.add(c.getLabel());
                }

        }

        fineTypes.add( EventConstants.TIME_ENTITY_TYPE );
        coarseTypes.add( EventConstants.TIME_ENTITY_TYPE );

        return;
    }

    /**
     * assumes no overlapping entities. Probably needs to be accounted for.
     *
     * @param entityOutType  which Views to use
     * @param nerOutputDir  output directory for new file. A new file will be created with a name prefixed
     *                      with the annotation file name.
     * @param annotationFile    file containing ACE annotation.
     * @param taList    a list of TextAnnotation objects corresponding to the ace annotations in annotationFile.
     */

    private static void writeOutEntityColumnFormat(EntityOutType entityOutType, String nerOutputDir, String annotationFile, List<TextAnnotation> taList) throws IOException {

        List< String > output = new LinkedList<>();

        for ( TextAnnotation ta : taList ) {
            output.addAll(generateEntityOutput(ta, getEntities(entityOutType, ta )) );

        }

        printOut( nerOutputDir, annotationFile, output );

        return;
    }

    private static List<Constituent> getEntities(EntityOutType entityOutType, TextAnnotation ta ) {
        List< String > viewNames = new LinkedList<>();

        if ( entityOutType.equals(EntityOutType.NER_COARSE_AUGMENTED ) || entityOutType.equals(EntityOutType.NER_FINE_AUGMENTED ) ) {
            viewNames.add( EventConstants.NER_ACE_QUANTITY );
            viewNames.add( EventConstants.NER_ACE_TIME );
        }

        if ( entityOutType.equals( EntityOutType.NER_COARSE_AUGMENTED ) || entityOutType.equals( EntityOutType.NER_COARSE_BASIC ) )
            viewNames.add( EventConstants.NER_ACE_COARSE );

        if ( entityOutType.equals( EntityOutType.NER_FINE_AUGMENTED ) || entityOutType.equals( EntityOutType.NER_FINE_BASIC  ) )
            viewNames.add( EventConstants.NER_ACE_FINE );

        List<Constituent> entities = new LinkedList< Constituent >();

        for ( String viewName : viewNames )
            if ( ta.hasView( viewName ) )
                entities.addAll( ta.getView( viewName ).getConstituents() );

        AceFileProcessor.removeOverlappingEntities( entities );

        return entities;
    }



    private static List<String> generateEntityOutput(TextAnnotation ta, List<Constituent> entities) {
        int currentIndex = 0;
        int inSentenceIndex = 0;
        List< String > output = new LinkedList<>();
        StringBuilder columnOutput = new StringBuilder();

        String[] toks = ta.getTokens();

        TIntHashSet sentenceEndIndexes = getSentenceEndIndexes( ta );

        for ( Constituent e : entities ) {
            int newIndex = e.getStartSpan();

            while( currentIndex < newIndex ) {
                if ( sentenceEndIndexes.contains( currentIndex ) ) {
                    output.add( columnOutput.toString() );
                    output.add( System.lineSeparator() );
                    columnOutput = new StringBuilder();
                    inSentenceIndex = 0;
                }

                printRow( columnOutput, OUT_TAG, ta, currentIndex, inSentenceIndex );
                currentIndex++;
                inSentenceIndex++;
            }

            //maybe new NE is right at the beginning of a sentence. Have to check again until we find a nicer formulation.
            if ( sentenceEndIndexes.contains( currentIndex ) ) {
                output.add( columnOutput.toString() );
                output.add( System.lineSeparator() );
                columnOutput = new StringBuilder();
                inSentenceIndex = 0;
            }

            int entitySizeInTokens = printNERows(columnOutput, ta, e, currentIndex, inSentenceIndex );
            currentIndex += entitySizeInTokens;
            inSentenceIndex += entitySizeInTokens;
        }

        while ( currentIndex < toks.length ) {
            if ( sentenceEndIndexes.contains( currentIndex ) ) {
                output.add(columnOutput.toString());
                output.add( System.lineSeparator() );
                columnOutput = new StringBuilder();
                inSentenceIndex = 0;
            }
            printRow( columnOutput, OUT_TAG, ta, currentIndex, inSentenceIndex );
            currentIndex++;
            inSentenceIndex++;
        }

        if ( !("".equals( columnOutput.toString() ) ) )
            output.add( columnOutput.toString() );

        return output;
    }

    /**
     * print output into a file in directory specified, with name based on annotationFile.
     * Should not create an empty file (i.e., if columnOutput is empty).
     *
     * @param nerOutputDir  directory to write output file
     * @param annotationFile    used as prefix for the name of the new file
     * @param columnOutput  a list of strings to be printed to the output file
     * @throws IOException
     */
    private static void printOut(String nerOutputDir, String annotationFile, List<String> columnOutput) throws IOException {

        String outFile = nerOutputDir + "/" + annotationFile + ".ner.column.txt" ;


        if ( !columnOutput.isEmpty() ) {

            if ( !IOUtils.exists( nerOutputDir ) )
                IOUtils.mkdir( nerOutputDir );

            LineIO.write(outFile, columnOutput);
        }
    }

    /**
     * Get the token indexes corresponding to the end of sentences in TextAnnotation ta.
     * These are expected to be one-past-the-end indexes.
     *
     * @param ta
     * @return
     */
    private static TIntHashSet getSentenceEndIndexes(TextAnnotation ta) {
        TIntHashSet sentenceEndIndexes = new TIntHashSet();

        View sentenceView = ta.getView( ViewNames.SENTENCE );

        for ( Constituent s : sentenceView.getConstituents() )
            sentenceEndIndexes.add( s.getEndSpan() );

        return sentenceEndIndexes;
    }


    private static int printNERows(StringBuilder columnOutput, TextAnnotation ta, Constituent e, int currentIndex, int inSentenceIndex) {

        int startTokIndex = e.getStartSpan();
        int endTokIndex = e.getEndSpan();
        int entitySizeInTokens = endTokIndex - startTokIndex;

        for ( int i = startTokIndex; i < endTokIndex; ++i ) {
            String label = e.getLabel();

            if ( i == startTokIndex )
                label = "B-" + label;
            else
                label = "I-" + label;

            printRow( columnOutput, label, ta, currentIndex, inSentenceIndex );
            currentIndex++;
            inSentenceIndex++;
        }


        return entitySizeInTokens;
    }

    /**
     * append a single row of column format NE corpus from TextAnnotation ta corresponding to token index
     *   tokenIndex to columnOutput
     * for now, assume only token info is present
     *
     * @param columnOutput
     * @param ta
     * @param tokenIndex
     */
    private static void printRow(StringBuilder columnOutput, String label, TextAnnotation ta, int tokenIndex, int inSentenceOffset ) {
        // B-LOC	0	0	O	O	WASHINGTON	x	x	0
        // O	0	1	O	O	--	x	x	0

        if ( tokenIndex < ta.size() ) {
            String token = ta.getToken(tokenIndex);
            columnOutput.append(label).append("\t").append("0").append( "\t" ).append(inSentenceOffset).append( "\t" );
            columnOutput.append(OUT_TAG).append( "\t" ).append(OUT_TAG).append( "\t" );
            columnOutput.append( token ).append("\t").append("x").append("\t").append("x");
            columnOutput.append("\t").append("0").append(System.lineSeparator());
        }
        else
            logger.warn( "out of range of token indexes: " + ta.getCorpusId() + " index " + tokenIndex );

    }

    @SuppressWarnings("unused")
	private static void printEmptyRow(StringBuilder columnOutput) {
        columnOutput.append( System.lineSeparator() );
    }
}
