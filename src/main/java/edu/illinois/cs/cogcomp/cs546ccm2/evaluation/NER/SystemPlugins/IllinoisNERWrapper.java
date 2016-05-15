package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.NER.SystemPlugins;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.IllinoisNERPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.A2WDataset;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.ACEDatasetWrapper;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers.A2WSystem;

public class IllinoisNERWrapper implements A2WSystem {
	String NAME;
	private IllinoisNERPlugin ner;
	
	public IllinoisNERWrapper() throws Exception {
		this(CCM2Constants.IllinoisNERConll);
	}
	
	public IllinoisNERWrapper(String viewName) throws Exception {
		ner = new IllinoisNERPlugin(viewName);
		this.NAME = viewName;
	}
	
	@Override
	public HashSet<Annotation> solveA2W(String text) {
		throw new NotImplementedException();
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
	
	public List<HashSet<Annotation>> getA2WOutputAnnotationList(A2WDataset ds) {
		throw new NotImplementedException();
	}

	@Override
	public String getName() {
		return this.NAME;
	}
	
	public List<HashSet<Annotation>> getNERTagList(ACEDatasetWrapper ds) throws AnnotatorException {
		ArrayList<HashSet<Annotation>> res = new ArrayList<>();
		
		for (TextAnnotation ta: ds.getDocs()) {
			ner.addView(ta);
			List<Constituent> annots = ta.getView(ner.getViewName()).getConstituents();
			
			HashSet<Annotation> outAnnots = new HashSet<>();
			
			for (Constituent cons: annots) {
				Annotation annot = new Annotation(cons.getStartSpan(), cons.length(), cons.getLabel());
				outAnnots.add(annot);
			}
			
			res.add(outAnnots);
		}
		
		return res;
	}
}
