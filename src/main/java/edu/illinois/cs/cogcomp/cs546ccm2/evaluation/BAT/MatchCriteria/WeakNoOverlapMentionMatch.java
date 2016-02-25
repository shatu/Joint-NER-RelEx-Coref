package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria;

/**
 * Adopted by Shashank from BAT-Framework
 * A way to compare 2 Mentions -- preprocessing is done to remove overlapping mentions within Gold and Output
 * 
 *  Mention match within is Weak i.e. the Gold and Output mentions DON'T HAVE to match exactly (i.e. just an overlap will do) with each other.
 */

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;

public class WeakNoOverlapMentionMatch implements MatchRelation<Annotation> {
	@Override
	public boolean match(Annotation t1, Annotation t2) {
			return t1.overlaps(t2);
	}

	@Override
	public List<HashSet<Annotation>> preProcessOutput(List<HashSet<Annotation>> computedOutput) {
		List<HashSet<Annotation>> nonOverlappingOutput = new Vector<HashSet<Annotation>>();
		for (HashSet<Annotation> s: computedOutput)
			nonOverlappingOutput.add(Annotation.deleteOverlappingAnnotations(s));
		return nonOverlappingOutput;
	}

	@Override
	public List<HashSet<Annotation>> preProcessGoldStandard(List<HashSet<Annotation>> goldStandard) {
		return preProcessOutput(goldStandard);
	}

	@Override
	public String getName() {
		return "Weak Mention match (with no Overlapping Mentions)";
	}
}
