package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria;

/**
 * Adopted by Shashank from BAT-Framework
 * A way to compare 2 Tags -- overlapping mentions within Gold and Output are not removed
 * 
 * TODO: See if it makes sense to have Strong and Weak version of this to capture
 * the exact error in annotations given the mention matches (weak, strong)
 *
 */

import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;

public class StrongTagMatch implements MatchRelation<Tag> {

	@Override
	public boolean match(Tag t1, Tag t2) {
		try {
			return t1.getConcept().equals(t2.getConcept());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<HashSet<Tag>> preProcessOutput(List<HashSet<Tag>> computedOutput) {
		return computedOutput;
	}

	@Override
	public List<HashSet<Tag>> preProcessGoldStandard(List<HashSet<Tag>> goldStandard) {
		return preProcessOutput(goldStandard);
	}

	@Override
	public String getName() {
		return "Strong tag match";
	}

}
