package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.MatchCriteria;

/**
 * Adopted by Shashank from BAT-Framework
 */

import java.util.HashSet;
import java.util.List;

public interface MatchRelation<E> {

	public boolean match(E t1, E t2);

	public List<HashSet<E>> preProcessOutput(List<HashSet<E>> computedOutput);

	public List<HashSet<E>> preProcessGoldStandard(List<HashSet<E>> goldStandard);

	public String getName();

}
