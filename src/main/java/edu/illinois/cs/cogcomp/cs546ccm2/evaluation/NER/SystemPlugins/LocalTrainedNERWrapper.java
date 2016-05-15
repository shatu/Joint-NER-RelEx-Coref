package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.NER.SystemPlugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalTrainedNER;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.A2WDataset;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.ACEDatasetWrapper;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers.A2WSystem;

public class LocalTrainedNERWrapper implements A2WSystem {
	
	private String NAME;
	private LocalTrainedNER ner;
	
	public LocalTrainedNERWrapper(String nerView, String mdView) throws IOException, ClassNotFoundException {
		ner = new LocalTrainedNER(nerView, new String[]{ViewNames.POS, mdView});
		NAME = nerView;
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
	
	public List<HashSet<Annotation>> getNERTagList(ACEDatasetWrapper ds) throws Exception {
		
		ArrayList<HashSet<Annotation>> res = new ArrayList<>();
		
		for (TextAnnotation ta: ds.getDocs()) {
			ner.addView(ta);
			List<Constituent> annots = ta.getView(ner.getViewName()).getConstituents();
			
			HashSet<Annotation> outAnnots = new HashSet<>();
			
			for (Constituent cons: annots) {
				if (cons.getLabel().equalsIgnoreCase("NO-ENT"))
					continue;
				
				Annotation annot = new Annotation(cons.getStartSpan(), cons.length(), cons.getLabel());
				outAnnots.add(annot);
			}
			
			res.add(outAnnots);
		}
		
		return res;
	}
}
