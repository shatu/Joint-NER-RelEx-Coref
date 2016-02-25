package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.MatchCriteria;

/**
 * Adopted by Shashank from BAT-Framework
 * A way to compare 2 Mentions -- overlapping mentions within Gold and Output are not removed
 * 
 *  Mention match within is Weak i.e. the Gold and Output mentions DON'T HAVE to match exactly (i.e. just an overlap will do) with each other.
 */

import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.DataStructures.Mention;

public class WeakOverlapMentionMatch implements MatchRelation<Mention>{
	@Override
	public boolean match(Mention t1, Mention t2) {
			return t1.overlaps(t2);
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
		return "Weak Mention match (with possible Overlapping Mentions)";
	}
}
