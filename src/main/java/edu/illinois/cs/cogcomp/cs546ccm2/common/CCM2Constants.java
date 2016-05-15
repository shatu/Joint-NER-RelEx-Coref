package edu.illinois.cs.cogcomp.cs546ccm2.common;

import java.util.Arrays;
import java.util.List;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;

/**
 * @author shashank
 */
public class CCM2Constants {
	
	//TODO
	public static String MentionTypeArg = "MentionTypeArg";
    
    public static String NERGoldExtent = ViewNames.NER_ACE_COARSE_EXTENT;
    public static String NERGoldHead = ViewNames.NER_ACE_COARSE_HEAD;
    public static String StanfordNERView = "StanfordNER";
    public static String IllinoisNEROntonotes = ViewNames.NER_ONTONOTES;
    public static String IllinoisNERConll = ViewNames.NER_CONLL;
    public static String LocalTrainedNER_GoldMDView = "LocalTrainedNER_GoldMD";
    public static List<String> NerLabels = Arrays.asList("GPE", "PER", "FAC", "WEA", "VEH", "LOC", "ORG");
    public static List<String> NerLabelsFull = Arrays.asList("NO-ENT", "GPE", "PER", "FAC", "WEA", "VEH", "LOC", "ORG");
    public static double NerNegSamplingRatio = 2d;
    
	public static final String MDGoldExtent = ViewNames.NER_ACE_COARSE_EXTENT;
	public static final String MDGoldHead = ViewNames.NER_ACE_COARSE_HEAD;
    public static String IllinoisChunkerMD = ViewNames.SHALLOW_PARSE;
    public static String StanfordMDView = StanfordNERView;
    public static String IllinoisNEROntonotesMD = ViewNames.NER_ONTONOTES;
    public static String IllinoisNERConllMD = ViewNames.NER_CONLL;
    public static final String RetrainedChunkerMDViewName = "RetrainedChunkerMD";
    
    public static final String CoRefGoldExtent = ViewNames.COREF_EXTENT;
    public static final String CoRefGoldHead = ViewNames.COREF_HEAD;
    public static String LocalTrainedCoref_GoldMDView = "LocalTrainedCoref_GoldMD";
    public static List<String> CoRefLabels = Arrays.asList("TRUE", "FALSE");
    public static String CoRefRelation = "CoRefRelation";
    public static double CoRefNegSamplingRatio = 2d;
    		
    public static String RelExGoldExtent = ViewNames.RELATION_ACE_COARSE_EXTENT;
    public static String RelExGoldHead = ViewNames.RELATION_ACE_COARSE_HEAD;
    public static String LocalTrainedRelEx_GoldMDView = "LocalTrainedRelEx_GoldMD";
    public static List<String> RelationTypes = Arrays.asList("PER-SOC", "ART", "GEN-AFF", "PHYS", "ORG-AFF", "PART-WHOLE", "METONYMY");
    public static List<String> RelationTypesFull = Arrays.asList("NO-REL", "PER-SOC", "ART", "GEN-AFF", "PHYS", "ORG-AFF", "PART-WHOLE", "METONYMY");
    public static final double RelExNegSamplingRatio = 4.0d;
    
    public static String ACE_Gold = "ACE_Gold";
    
    public static String ACE05FullCorpusPath = "data/ACE2005_CS546/corpus/full";
    public static String ACE04FullCorpusPath = "data/ACE2004_CS546/corpus/full";

    public static String ACE05TrainCorpusPath = "data/ACE2005_CS546/corpus/train";
    public static String ACE04TrainCorpusPath = "data/ACE2004_CS546/corpus/train";
    
    public static String ACE05TestCorpusPath = "data/ACE2005_CS546/corpus/test";
    public static String ACE04TestCorpusPath = "data/ACE2004_CS546/corpus/test";
    
//    public static String ACE05SplitPath = "data/ACE2005_CS546/Split";
//    public static String ACE04SplitPath = "data/ACE2004_CS546/Split";

    public static String ACE05NerModelPath = "data/ACE2005_CS546/models/NerModels";
    public static String ACE04NerModelPath = "data/ACE2004_CS546/models/NerModels";
    
    public static String ACE05RelExModelPath = "data/ACE2005_CS546/models/RelExModels";
    public static String ACE04RelExModelPath = "data/ACE2004_CS546/models/RelExModels";
    
    public static String ACE05CoRefModelPath = "data/ACE2005_CS546/models/CoRefModels";
    public static String ACE04CoRefModelPath = "data/ACE2004_CS546/models/CoRefModels";
}
