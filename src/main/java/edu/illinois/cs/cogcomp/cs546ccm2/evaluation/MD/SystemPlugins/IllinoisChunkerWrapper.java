package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.MD.SystemPlugins;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.IllinoisChunkerPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.A2WDataset;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.ACEDatasetWrapper;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers.A2WSystem;

public class IllinoisChunkerWrapper implements A2WSystem {
	
	private String NAME = "Illinois-Chunker";
	private IllinoisChunkerPlugin chunker;
	
	public IllinoisChunkerWrapper() {
		chunker = new IllinoisChunkerPlugin();
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
	
	public List<HashSet<Annotation>> getEntityMentionTagList(ACEDatasetWrapper ds) throws AnnotatorException {
		if(!ds.isCorpusReady()) {
			System.out.println("Corpus not loaded in memory.. first initialize the corpus .. exiting ....");
			System.exit(0);
		}
		
		List<HashSet<Annotation>> res = new ArrayList<>();
		for(ACEDocument doc: ds.aceCorpus.getAllDocs()) {
			HashSet<Annotation> outAnnots = new HashSet<>();
			for(AnnotatedText ta: doc.taList) {
				chunker.labelText(ta.getTa());
				List<Constituent> docAnnots = ta.getTa().getView(ViewNames.SHALLOW_PARSE).getConstituents();
				for(Constituent cons: docAnnots) {
					if(cons.getLabel().equals("NP")) {
						Annotation annot = new Annotation(cons.getStartCharOffset(), cons.getEndCharOffset() - cons.getStartCharOffset(), cons.getLabel());
						outAnnots.add(annot);
					}
				}
			}
			res.add(outAnnots);
		}
		
		return res;
	}
}
