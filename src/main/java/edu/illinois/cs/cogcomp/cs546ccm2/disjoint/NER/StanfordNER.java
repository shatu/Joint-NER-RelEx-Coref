package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER;

import java.io.IOException;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.ACECorpus;


/*
 * author Shashank
 */

public class StanfordNER implements ANER {
	
	private String NAME = CCM2Constants.StanfordNER;
	
	public static void main(String[] args) throws AnnotatorException, IOException {
		String inDirPath = "data/ACE2005_processed";
		StanfordNER ner = new StanfordNER();
		ACECorpus aceCorpus = new ACECorpus();
		aceCorpus.initCorpus(inDirPath);
		//ACEDocument doc = aceCorpus.getDocFromID("AFP_ENG_20030304.0250");
		ACEDocument doc = aceCorpus.getDocFromID("CNNHL_ENG_20030526_221156.39");
		for(AnnotatedText ta: doc.taList) {
			ner.labelText(ta.getTa());
			List<Constituent> annots = ta.getTa().getView(ner.getName()).getConstituents();
			for(Constituent annot: annots) {
					System.out.println(annot.toString() + "-->" + annot.getLabel());
			}
		}
	}
	
	public void labelText(TextAnnotation ta) throws AnnotatorException {
		SpanLabelView view = new SpanLabelView(getName(), this.getClass().getName(), ta, 1d, true);
        
		String textContent = ta.text;
		String label;
		int startCharOffset;
		int endCharOffset;
		int startTokenOffset;
		int endTokenOffset;
		double score;
		String mentionText;
		
		
		//TODO Code here to get annotations from Stanford NER using the textContent
		
		
		/*TODO 
		  loop over those annotations and get their character offsets in the text
		  	label = 
		  	startCharOffset =   
		  	endCharOffset = 
		  	startTokenOffset = ta.getTokenIdFromCharacterOffset(startCharOffset);
		  	endTokenOffset = ta.getTokenIdFromCharacterOffset(endCharOffset);
		  	mentionText = ta.getTokensInSpan(startTokenOffset, endTokenOffset);
		  	Constituent cont = new Constituent(label, getName(), mentionText, startTokenOffset, endTokenOffset);
		  	view.addSpanLabel(cont.getSpan().getFirst(), cont.getSpan().getSecond(), label, score);
		*/ 
		
        ta.addView(getName(), view);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
