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
	
	public static String MDGold = "MDGold";
    public static String IllinoisChunker = ViewNames.SHALLOW_PARSE;
    public static String StanfordMD = "StanfordMD";
    
    public static String NERGold = "NERGold";
    public static String StanfordNER = "StanfordNER";
    public static String IllinoisNEROntonotes = ViewNames.NER_ONTONOTES;
    public static String IllinoisNERConll = ViewNames.NER_CONLL;
    public static List<String> NerLabels = Arrays.asList("GPE", "PER", "FAC", "WEA", "VEH", "LOC", "ORG");
    public static List<String> NerLabelsFull = Arrays.asList("NO-ENT", "GPE", "PER", "FAC", "WEA", "VEH", "LOC", "ORG");
    public static double NerNegSamplingFrac = 0.5d;
    
    public static final String CoRefGold = "CoRefGold";
    public static List<String> CoRefLabels = Arrays.asList("TRUE", "FALSE");
    public static String CoRefRelation = "CoRefRelation";
    		
    public static String RelExGold = "RelExGold";
    public static List<String> RelationTypes = Arrays.asList("PER-SOC", "ART", "GEN-AFF", "PHYS", "ORG-AFF", "PART-WHOLE");
    public static List<String> RelationTypesFull = Arrays.asList("NO-REL", "PER-SOC", "ART", "GEN-AFF", "PHYS", "ORG-AFF", "PART-WHOLE");
    public static final double RelExNegSamplingFrac = 0.08d;
    
    public static String ACE_Gold = "ACE_Gold";
    
    public static String ACE05CorpusPath = "data/ACE2005";
    public static String ACE04CorpusPath = "data/ACE2004";
    
    public static String ACE05ProcessedPath = "data/ACE2005_Processed";
    public static String ACE04ProcessedPath = "data/ACE2004_Processed";

    public static String ACE05SplitPath = "data/ACE2005_Split";
    public static String ACE04SplitPath = "data/ACE2004_Split";

    public static String ACE05NerModelPath = "data/ACE2005_Split/NerModels";
    public static String ACE04NerModelPath = "data/ACE2004_Split/NerModels";
    
    public static String ACE05RelExModelPath = "data/ACE2005_Split/RelExModels";
    public static String ACE04RelExModelPath = "data/ACE2004_Split/RelExModels";
    
}
