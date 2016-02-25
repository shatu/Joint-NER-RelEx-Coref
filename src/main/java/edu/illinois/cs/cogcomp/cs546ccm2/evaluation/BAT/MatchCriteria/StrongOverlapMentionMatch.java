package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria;

/**
 * Adopted by Shashank from BAT-Framework
 * A way to compare 2 Mentions -- overlapping mentions within Gold and Output are not removed
 * 
 *  Mention match within is Strong i.e. the Gold and Output mentions have to match exactly with each other.
 */

import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;

public class StrongOverlapMentionMatch implements MatchRelation<Mention> {
	@Override
	public boolean match(Mention t1, Mention t2) {
			return t1.equals(t2);
	}

	@Override
	public List<HashSet<Mention>> preProcessOutput(List<HashSet<Mention>> computedOutput) {
		return computedOutput;
	}

	@Override
	public List<HashSet<Mention>> preProcessGoldStandard(List<HashSet<Mention>> goldStandard) {
		return goldStandard;
	}

	@Override
	public String getName() {
		return "Strong Mention match (with possible Overlapping Mentions)";
	}
}
