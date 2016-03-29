package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER;

import java.io.IOException;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.ACECorpus;

public class GoldNER {
	
	public static void main(String[] args) throws AnnotatorException, IOException {
		String inDirPath = "data/ACE2005_processed";
		GoldNER ner = new GoldNER();
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
		/*
		 * Dummy function -- NER view is already a part of the text annotation
		 */
	}

}
