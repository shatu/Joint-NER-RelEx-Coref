package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria;

/**
 * Adopted by Shashank from BAT-Framework
 * A way to compare 2 Mentions -- overlapping mentions within Gold and Output are not removed
 * 
 *  Mention match within is Weak i.e. the Gold and Output mentions DON'T HAVE to match exactly (i.e. just an overlap will do) with each other.
 */

import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;

public class WeakOverlapMentionMatch implements MatchRelation<Annotation>{
	@Override
	public boolean match(Annotation t1, Annotation t2) {
			return t1.getMention().overlaps(t2.getMention());
	}

	@Override
	public List<HashSet<Annotation>> preProcessOutput(List<HashSet<Annotation>> computedOutput) {
		return computedOutput;
	}

	@Override
	public List<HashSet<Annotation>> preProcessGoldStandard(List<HashSet<Annotation>> goldStandard) {
		return goldStandard;
	}

	@Override
	public String getName() {
		return "Weak Mention match (with possible Overlapping Mentions)";
	}
}
