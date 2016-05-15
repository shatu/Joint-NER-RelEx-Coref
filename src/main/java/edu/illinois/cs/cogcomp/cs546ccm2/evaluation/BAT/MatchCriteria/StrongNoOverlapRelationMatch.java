package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria;

/**
 * Adopted by Shashank from BAT-Framework
 * 
 * A way to compare 2 Annotations -- with preprocessing to remove overlapping mentions within Gold and Output.
 * 
 *  Mention match within is Strong i.e. the Gold and Output mentions have to match exactly with each other.
 */

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.RelationAnnotation;

public class StrongNoOverlapRelationMatch implements MatchRelation<RelationAnnotation> {
	@Override
	public boolean match(RelationAnnotation a1, RelationAnnotation a2) {
		try {
			return a1.equals(a2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<HashSet<RelationAnnotation>> preProcessOutput(List<HashSet<RelationAnnotation>> computedOutput) {
		List<HashSet<RelationAnnotation>> nonOverlappingOutput = new Vector<HashSet<RelationAnnotation>>();
		for (HashSet<RelationAnnotation> s: computedOutput)
			nonOverlappingOutput.add(RelationAnnotation.deleteOverlappingAnnotations(s));
		return nonOverlappingOutput;
	}

	@Override
	public List<HashSet<RelationAnnotation>> preProcessGoldStandard(List<HashSet<RelationAnnotation>> goldStandard) {
		return preProcessOutput(goldStandard);
	}

	@Override
	public String getName() {
		return "Strong relation annotation match (with no Overlapping Mentions)";
	}

}
