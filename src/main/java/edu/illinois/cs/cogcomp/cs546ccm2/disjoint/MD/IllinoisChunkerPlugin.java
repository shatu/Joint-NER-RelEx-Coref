package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.handler.IllinoisChunkerHandler;
import edu.illinois.cs.cogcomp.annotation.handler.IllinoisPOSHandler;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.Paragraph;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.ACECorpus;

public class IllinoisChunkerPlugin implements AMentionDetector {

	private String NAME = CCM2Constants.IllinoisChunker;
	private IllinoisChunkerHandler chunker;
	private IllinoisPOSHandler posTagger;
	
	public static void main(String[] args) throws AnnotatorException {
		String inDirPath = CCM2Constants.ACE05ProcessedPath;
		IllinoisChunkerPlugin shallowParser = new IllinoisChunkerPlugin();
		ACECorpus aceCorpus = new ACECorpus();
		aceCorpus.initCorpus(inDirPath);
//		ACEDocument doc = aceCorpus.getDocFromID("AFP_ENG_20030304.0250");
		ACEDocument doc = aceCorpus.getDocFromID("CNN_ENG_20030630_085848.18");
		List<Pair<String, Paragraph>> paragraphs = doc.paragraphs;
		List<Paragraph> contentParas = new ArrayList<>();
		for(Pair<String, Paragraph> pair: paragraphs) {
			if(pair.getFirst().equals("text"))
				contentParas.add(pair.getSecond());
		}
		
		int i=0;
		for(AnnotatedText ta: doc.taList) {
			shallowParser.labelText(ta.getTa());
			List<Constituent> annots = ta.getTa().getView(shallowParser.getName()).getConstituents();
			for(Constituent annot: annots) {
				if(annot.getLabel().equals("NP"))
					System.out.println(annot.toString() + "-->" + annot.getLabel() + "-->" + (annot.getStartCharOffset() + contentParas.get(i).offsetFilterTags) 
							+ "-->" + (annot.getEndCharOffset() + contentParas.get(i).offsetFilterTags));
			}
			i++;
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

	@Override
	public String getName() {
		return NAME;
	}

}
