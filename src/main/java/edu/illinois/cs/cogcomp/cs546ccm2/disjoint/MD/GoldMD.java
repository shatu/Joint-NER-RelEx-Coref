package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD;

import java.util.List;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;

public class GoldMD extends Annotator {
	
	public GoldMD (String viewName) {
		this(viewName, new String[]{});
	}
	
	private GoldMD (String viewName, String[] requiredViews) {
		super(viewName, requiredViews);
	}

	public static void main(String[] args) throws Exception {
		String inDirPath = CCM2Constants.ACE05TrainCorpusPath;
		GoldMD md = new GoldMD(CCM2Constants.MDGoldExtent);
		ACEReader aceReader = new ACEReader(inDirPath, false);
		String docID = "AFP_ENG_20030413.0098.apf.xml";
		
		for (TextAnnotation ta: aceReader) {
			if (ta.getId().contains(docID) == false)
				continue;
			
			md.addView(ta);
			List<Constituent> annots = ta.getView(md.viewName).getConstituents();
			for (Constituent annot: annots) {
				System.out.println(annot.toString());
			}
		}
	}

	@Override
	public void addView(TextAnnotation ta) throws AnnotatorException {
		/**
		 * Dummy function -- Gold NER view is already a part of the TextAnnotation
		 */
	}

}
