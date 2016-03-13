package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD;

import java.io.IOException;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.handler.IllinoisNerHandler;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.ACECorpus;
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator;

public class IllinoisNERPlugin {

//	private IllinoisChunkerHandler chunker;
//	private IllinoisPOSHandler posTagger;
	private IllinoisNerHandler ner;
	private boolean isOntonotes = false;
	
	public static void main(String[] args) throws AnnotatorException, IOException {
		String inDirPath = "data/ACE2005_processed";
		IllinoisNERPlugin ner = new IllinoisNERPlugin(true);
		ACECorpus aceCorpus = new ACECorpus();
		aceCorpus.initCorpus(inDirPath);
		ACEDocument doc = aceCorpus.getDocFromID("AFP_ENG_20030304.0250");
		for(AnnotatedText ta: doc.taList) {
			ner.labelText(ta.getTa());
//			List<Constituent> annots = ta.getTa().getView(ViewNames.NER_CONLL).getConstituents();
			List<Constituent> annots = ta.getTa().getView(ViewNames.NER_ONTONOTES).getConstituents();
			for(Constituent annot: annots) {
					System.out.println(annot.toString() + "-->" + annot.getLabel());
			}
		}
	}
	
	public IllinoisNERPlugin() throws IOException {
		this(false);
	}
	
	public IllinoisNERPlugin(boolean useOntonotes) throws IOException {
		this.isOntonotes = useOntonotes;
//		this.posTagger = new IllinoisPOSHandler();
//		this.chunker = new IllinoisChunkerHandler();
		if(useOntonotes)
			this.ner = new IllinoisNerHandler(new PipelineConfigurator().getDefaultConfig(), ViewNames.NER_ONTONOTES);
		else
			this.ner = new IllinoisNerHandler(new PipelineConfigurator().getDefaultConfig(), ViewNames.NER_CONLL);
	}
	
	public void labelText(TextAnnotation ta) throws AnnotatorException {
//		posTagger.labelTextAnnotation(ta);
//		chunker.labelTextAnnotation(ta);
		ner.labelTextAnnotation(ta);
	}

}
