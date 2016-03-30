package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD;

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
 * TODO: Convert this class to just a dumb wrapper class and instead add the GoldMention View while creating the ACECorpus itself.
 *       provided we need the mention types (nominals, aliases, ..) to do something smart.
 */

public class GoldMD implements AMentionDetector{
	
	public static void main(String[] args) throws AnnotatorException, IOException {
		String inDirPath = "data/ACE2005_processed";
		GoldMD ner = new GoldMD();
		ACECorpus aceCorpus = new ACECorpus();
		aceCorpus.initCorpus(inDirPath);
		//ACEDocument doc = aceCorpus.getDocFromID("AFP_ENG_20030304.0250");
		ACEDocument doc = aceCorpus.getDocFromID("CNNHL_ENG_20030526_221156.39");
		for(AnnotatedText ta: doc.taList) {
			ner.labelText(ta.getTa());
			List<Constituent> annots = ta.getTa().getView(CCM2Constants.NERGold).getConstituents();
			for(Constituent annot: annots) {
					System.out.println(annot.toString() + "-->" + annot.getLabel());
			}
		}
	}
	
	public void labelText(TextAnnotation ta) throws AnnotatorException {
		SpanLabelView view = new SpanLabelView(CCM2Constants.MDGold, CCM2Constants.ACE_Gold, ta, 1d, true);
        
		for(Constituent cont : ta.getView(CCM2Constants.NERGold).getConstituents()) {
			view.addSpanLabel(cont.getSpan().getFirst(), cont.getSpan().getSecond(), "Gold Mention", 1d);
			
		}
        
        ta.addView(CCM2Constants.MDGold, view);
	}

}
