package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER;

import java.util.List;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;

public class GoldNER extends Annotator {
	
	public GoldNER(String viewName) {
		this(viewName, new String[]{});
	}
	
	private GoldNER(String viewName, String[] requiredViews) {
		super(viewName, requiredViews);
	}
	
	public static void main(String[] args) throws Exception {
		String inDirPath = CCM2Constants.ACE05TrainCorpusPath;
		GoldNER ner = new GoldNER(CCM2Constants.NERGoldExtent);
		ACEReader aceReader = new ACEReader(inDirPath, false);
		
		String docID = "AFP_ENG_20030413.0098.apf.xml";
		for (TextAnnotation ta: aceReader) {
			if (ta.getId().contains(docID) == false)
				continue;
			System.out.println(ta.getId());
			ner.addView(ta);
			List<Constituent> annots = ta.getView(ner.viewName).getConstituents();
			for (Constituent annot: annots) {
				System.out.println(annot.toString() + "-->" + annot.getLabel());
			}
		}
	}

	@Override
	public void addView(TextAnnotation ta) throws AnnotatorException {
		/*
		 * Gold NER view has already been added
		 */
	}

}
