package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD;

import java.io.IOException;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.handler.IllinoisNerHandler;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.ACECorpus;
import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator;

public class IllinoisNERPlugin implements AMentionDetector {
	
	private String NAME;
	private IllinoisNerHandler ner;
	private boolean isOntonotes = false;
	
	public static void main(String[] args) throws AnnotatorException, IOException {
		String inDirPath = CCM2Constants.ACE05ProcessedPath;
		IllinoisNERPlugin ner = new IllinoisNERPlugin(false);
		ACECorpus aceCorpus = new ACECorpus();
		aceCorpus.initCorpus(inDirPath);
		ACEDocument doc = aceCorpus.getDocFromID("AFP_ENG_20030304.0250");
		for(AnnotatedText ta: doc.taList) {
			ner.labelText(ta.getTa());
//			List<Constituent> annots = ta.getTa().getView(ViewNames.NER_CONLL).getConstituents();
			List<Constituent> annots = ta.getTa().getView(ner.getName()).getConstituents();
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
		if(isOntonotes) {
			this.ner = new IllinoisNerHandler(new PipelineConfigurator().getDefaultConfig(), ViewNames.NER_ONTONOTES);
			this.NAME = CCM2Constants.IllinoisNEROntonotes;
		}
		else {
			this.ner = new IllinoisNerHandler(new PipelineConfigurator().getDefaultConfig(), ViewNames.NER_CONLL);
			this.NAME = CCM2Constants.IllinoisNERConll; 
		}
	}
	
	public void labelText(TextAnnotation ta) throws AnnotatorException {
		ner.labelTextAnnotation(ta);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
