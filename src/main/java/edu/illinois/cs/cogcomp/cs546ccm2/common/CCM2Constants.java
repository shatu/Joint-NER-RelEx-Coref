package edu.illinois.cs.cogcomp.cs546ccm2.common;

import java.util.Arrays;
import java.util.List;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;

/**
 * @author shashank
 */
public class CCM2Constants {
    public static String MDGold = "MDGold";
    public static String IllinoisChunker = ViewNames.SHALLOW_PARSE;
    public static String StanfordMD = "StanfordMD";
    
    public static String NERGold = "NERGold";
    public static String StanfordNER = "StanfordNER";
    public static String IllinoisNEROntonotes = ViewNames.NER_ONTONOTES;
    public static String IllinoisNERConll = ViewNames.NER_CONLL;
    public static List<String> NerLabels = Arrays.asList("GPE", "PER", "FAC", "WEA", "VEH", "LOC", "ORG");
   
    public static String ACE_Gold = "ACE_Gold";
}
