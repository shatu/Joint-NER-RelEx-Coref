package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria;

/**
 * Adopted by Shashank from BAT-Framework
 * 
 * A Naive way to compare 2 ScoredTags i.e. just match the concepts within (with no preprocessing of tags)
 */

import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.ScoredTag;

public class NaiveScoredTagMatch implements MatchRelation<ScoredTag>{

	@Override
	public boolean match(ScoredTag t1, ScoredTag t2) {
		try {
			return t1.getConcept().equals(t2.getConcept());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<HashSet<ScoredTag>> preProcessOutput(List<HashSet<ScoredTag>> computedOutput) {
		return computedOutput;
	}

	@Override
	public List<HashSet<ScoredTag>> preProcessGoldStandard(List<HashSet<ScoredTag>> goldStandard) {
		return preProcessOutput(goldStandard);
	}
	
	@Override
	public String getName() {
		return "Naive scored tag match";
	}

}
