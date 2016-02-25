package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.MD.SystemPlugins;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.A2WDataset;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers.A2WSystem;

public class MockAnnotator implements A2WSystem {
	
	@Override
	//TODO: Should implement this function and call relevant system's annotator from within
	public HashSet<Annotation> solveA2W(String text) {
		return null;
	}

	@Override
	public HashSet<Tag> solveC2W(String text) {
		throw new NotImplementedException();
	}
	
	@Override
	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions) {
		throw new NotImplementedException();
	}
	
	// This function doesn't allow overlapping mentions to be returned. Mentions need to match exactly.
	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions, boolean allow_overlap) {
		throw new NotImplementedException();
	}

	@Override
	public String getName() {
		return "MockAnnotator";
	}
	
	/**
	 * TODO: Implement this method if you want to return all the annotations as once given the dataset
	 */
	public List<HashSet<Annotation>> getA2WOutputAnnotationList(A2WDataset ds) {
		return null;
	}
	
}
