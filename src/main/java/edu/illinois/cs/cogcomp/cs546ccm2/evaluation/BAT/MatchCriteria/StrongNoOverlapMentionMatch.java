package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria;

/**
 * Adopted by Shashank from BAT-Framework
 * A way to compare 2 Mentions -- with preprocessing to remove overlapping mentions within Gold and Output.
 * 
 *  Mention match is Strong i.e. the Gold and Output mentions have to match exactly with each other.
 */

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;

public class StrongNoOverlapMentionMatch implements MatchRelation<Mention> {
	@Override
	public boolean match(Mention t1, Mention t2) {
		return t1.equals(t2);
	}

	@Override
	public List<HashSet<Mention>> preProcessOutput(List<HashSet<Mention>> computedOutput) {
		List<HashSet<Mention>> nonOverlappingOutput = new Vector<HashSet<Mention>>();
		for (HashSet<Mention> s: computedOutput)
			nonOverlappingOutput.add(Mention.deleteOverlappingAnnotations(s));
		return nonOverlappingOutput;
	}

	@Override
	public List<HashSet<Mention>> preProcessGoldStandard(List<HashSet<Mention>> goldStandard) {
		return preProcessOutput(goldStandard);
	}

	@Override
	public String getName() {
		return "Strong mention match (with no Overlapping Mentions)";
	}
}
