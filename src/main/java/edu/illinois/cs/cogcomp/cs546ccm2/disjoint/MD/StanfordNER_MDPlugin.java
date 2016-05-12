package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD;

import java.util.List;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;


/**
 * author Shashank
 */

public class StanfordNER_MDPlugin extends Annotator {
	
	public StanfordNER_MDPlugin(String viewName) {
		this(viewName, new String[]{});
	}
	
	private StanfordNER_MDPlugin(String viewName, String[] requiredViews) {
		super(viewName, requiredViews);
	}
	
	public static void main(String[] args) throws Exception {
		StanfordNER_MDPlugin ner = new StanfordNER_MDPlugin(CCM2Constants.StanfordMDView);
		
		String inDirPath = CCM2Constants.ACE05TrainCorpusPath;
		ACEReader aceReader = new ACEReader(inDirPath, false);
		
		String docID = "AFP_ENG_20030304.0250";
		for (TextAnnotation ta: aceReader) {
			if (ta.getId().contains(docID) == false)
				continue;
			System.out.println(ta.getId());
			ner.labelText(ta);
			List<Constituent> annots = ta.getView(ner.viewName).getConstituents();
			for (Constituent annot: annots) {
				System.out.println(annot.toString() + "-->" + annot.getLabel());
			}
		}
	}
	
	public void labelText(TextAnnotation ta) throws AnnotatorException {
		SpanLabelView view = new SpanLabelView(viewName, this.getClass().getName(), ta, 1d, true);
        
		String textContent = ta.text;
		String label;
		int startCharOffset;
		int endCharOffset;
		int startTokenOffset;
		int endTokenOffset;
		double score;
		
		
		//TODO Code here to get annotations from Stanford NER using the textContent
		
		
		/*TODO 
		  loop over those annotations and get their character offsets in the text
		  	label = 
		  	startCharOffset =   
		  	endCharOffset = 
		  	startTokenOffset = ta.getTokenIdFromCharacterOffset(startCharOffset);
		  	endTokenOffset = ta.getTokenIdFromCharacterOffset(endCharOffset);
		  	Constituent cont = new Constituent(label, getName(), ta, startTokenOffset, endTokenOffset);
		  	view.addSpanLabel(cont.getSpan().getFirst(), cont.getSpan().getSecond(), label, score);
		*/ 
		
        ta.addView(viewName, view);
	}

	@Override
	public void addView(TextAnnotation ta) throws AnnotatorException {
		
	}

}
