package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria;

/**
 * Adopted by Shashank from BAT-Framework
 * A way to compare 2 Concepts -- preprocessing is done to remove multiple instances of the same concept within Gold and Output
 */

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;

public class ConceptAnnotationMatch implements MatchRelation<Annotation> {

	@Override
	public boolean match(Annotation t1, Annotation t2) {
		try {
			return t1.getConcept().equals(t2.getConcept());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	@Override
	public List<HashSet<Annotation>> preProcessOutput(List<HashSet<Annotation>> computedOutput) {
		List<HashSet<Annotation>> noDoubleConcepts = new Vector<HashSet<Annotation>>();
		for (HashSet<Annotation> s : computedOutput){
			HashSet<String> alreadyInsertedConcepts = new HashSet<String>();
			HashSet<Annotation> noDoubleSet = new HashSet<Annotation>();
			noDoubleConcepts.add(noDoubleSet);
			for (Annotation a: s)
				try {
					if (!alreadyInsertedConcepts.contains(a.getConcept())){
						noDoubleSet.add(new Annotation(a.getPosition(), a.getLength(), a.getConcept()));
						alreadyInsertedConcepts.add(a.getConcept());
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
					
				}
			}

		return noDoubleConcepts;
	}

	@Override
	public List<HashSet<Annotation>> preProcessGoldStandard(List<HashSet<Annotation>> goldStandard) {
		return preProcessOutput(goldStandard);
	}


	@Override
	public String getName() {
		return "Concept annotation match";
	}

}
