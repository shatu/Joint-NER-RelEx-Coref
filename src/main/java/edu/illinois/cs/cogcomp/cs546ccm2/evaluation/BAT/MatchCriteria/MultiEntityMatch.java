package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria;

/**
 * Adopted by Shashank from BAT-Framework
 */

import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.MultipleAnnotation;

public class MultiEntityMatch implements MatchRelation<MultipleAnnotation> {

	@Override
	public boolean match(MultipleAnnotation t1, MultipleAnnotation t2) {
		if (t1.getPosition() != t2.getPosition() || t1.getLength() != t2.getLength()) {
			return false;
		}
		for (String c1 : t1.getCandidates()) {
			for (String c2 : t2.getCandidates()) {
				try {
					if (c1.equals(c2))
						return true;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return false;
	}

	@Override
	public List<HashSet<MultipleAnnotation>> preProcessOutput(List<HashSet<MultipleAnnotation>> computedOutput) {
		return computedOutput;
	}

	@Override
	public List<HashSet<MultipleAnnotation>> preProcessGoldStandard(List<HashSet<MultipleAnnotation>> goldStandard) {
		return preProcessOutput(goldStandard);
	}

	@Override
	public String getName() {
		return "Multi-Entity match";
	}
}
