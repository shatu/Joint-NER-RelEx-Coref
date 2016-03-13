package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD;

import java.util.List;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.handler.IllinoisChunkerHandler;
import edu.illinois.cs.cogcomp.annotation.handler.IllinoisPOSHandler;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.ACECorpus;

public class IllinoisChunkerPlugin {

	private IllinoisChunkerHandler chunker;
	private IllinoisPOSHandler posTagger;
	
	public static void main(String[] args) throws AnnotatorException {
		String inDirPath = "data/ACE2005_processed";
		IllinoisChunkerPlugin shallowParser = new IllinoisChunkerPlugin();
		ACECorpus aceCorpus = new ACECorpus();
		aceCorpus.initCorpus(inDirPath);
		ACEDocument doc = aceCorpus.getDocFromID("AFP_ENG_20030304.0250");
		for(AnnotatedText ta: doc.taList) {
			shallowParser.labelText(ta.getTa());
			List<Constituent> annots = ta.getTa().getView(ViewNames.SHALLOW_PARSE).getConstituents();
			for(Constituent annot: annots) {
				if(annot.getLabel().equals("NP"))
					System.out.println(annot.toString() + "-->" + annot.getLabel());
			}
		}
	}
	
	public IllinoisChunkerPlugin() {
		posTagger = new IllinoisPOSHandler();
		chunker = new IllinoisChunkerHandler();
	}
	
	public void labelText(TextAnnotation ta) throws AnnotatorException {
		posTagger.labelTextAnnotation(ta);
		chunker.labelTextAnnotation(ta);
	}

}
