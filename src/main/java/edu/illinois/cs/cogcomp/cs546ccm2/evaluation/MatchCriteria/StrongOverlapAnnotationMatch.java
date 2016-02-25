package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.MatchCriteria;

/**
 * Adopted by Shashank from BAT-Framework
 * A way to compare 2 Annotations -- overlapping mentions within Gold and Output are not removed
 * 
 *  Mention match within is Strong i.e. the Gold and Output mentions have to match exactly with each other.
 */

import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.DataStructures.Annotation;

public class StrongOverlapAnnotationMatch implements MatchRelation<Annotation> {

	@Override
	public boolean match(Annotation a1, Annotation a2) {
		try {
			return a1.getLength() == a2.getLength() &&
					a1.getPosition() == a2.getPosition() &&
					a1.getConcept().equals(a2.getConcept());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<HashSet<Annotation>> preProcessOutput(List<HashSet<Annotation>> computedOutput) {
		return computedOutput;
	}

	@Override
	public List<HashSet<Annotation>> preProcessGoldStandard(List<HashSet<Annotation>> goldStandard) {
		return preProcessOutput(goldStandard);
	}

	@Override
	public String getName() {
		return "Strong annotation match (with possible Overlapping Mentions)";
	}

}
