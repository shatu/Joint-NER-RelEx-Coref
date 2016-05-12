package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.MD.SystemPlugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.Paragraph;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.IllinoisNER_MDPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.A2WDataset;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.ACEDatasetWrapper;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers.A2WSystem;

public class IllinoisNERWrapper implements A2WSystem {
	
	private String NAME = "Illinois-NER";
	private IllinoisNER_MDPlugin ner;
	private boolean isOntonotes = false;
	
	public IllinoisNERWrapper() throws IOException {
		this(false);
	}
	
	public IllinoisNERWrapper(boolean useOntonotes) throws IOException {
		ner = new IllinoisNER_MDPlugin(useOntonotes);
		isOntonotes = useOntonotes;
		if(isOntonotes)
			NAME += "_Ontonotes";
		else
			NAME += "_CoNLL";
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
		List<HashSet<Annotation>> res = new ArrayList<>();
		for(ACEDocument doc: ds.getDocs()) {
			HashSet<Annotation> outAnnots = new HashSet<>();
			List<Pair<String, Paragraph>> paragraphs = doc.paragraphs;
			List<Paragraph> contentParas = new ArrayList<>();
			for(Pair<String, Paragraph> pair: paragraphs) {
				if(pair.getFirst().equals("text"))
					contentParas.add(pair.getSecond());
			}
			int i=0;
			for(AnnotatedText ta: doc.taList) {
				ner.labelText(ta.getTa());
				List<Constituent> docAnnots;
				if(isOntonotes)
					docAnnots = ta.getTa().getView(ViewNames.NER_ONTONOTES).getConstituents();
				else
					docAnnots = ta.getTa().getView(ViewNames.NER_CONLL).getConstituents();
				
				for(Constituent cons: docAnnots) {
					Annotation annot = new Annotation(cons.getStartCharOffset() + contentParas.get(i).offsetFilterTags,
							cons.getEndCharOffset() - cons.getStartCharOffset(), cons.getLabel());
					outAnnots.add(annot);
				}
				i++;
			}
			res.add(outAnnots);
		}
		
		return res;
	}
}
