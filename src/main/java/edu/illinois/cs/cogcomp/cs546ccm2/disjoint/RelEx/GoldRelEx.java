package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx;

import java.util.List;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;

public class GoldRelEx extends Annotator {
	
	public GoldRelEx(String viewName) {
		super(viewName, new String[]{});
	}
	
	private GoldRelEx(String viewName, String[] requiredViews) {
		super(viewName, requiredViews);
	}

	public static void main(String[] args) throws Exception {
		String inDirPath = CCM2Constants.ACE05TrainCorpusPath;
		GoldRelEx relEx = new GoldRelEx(CCM2Constants.RelExGoldExtent);
		ACEReader aceReader = new ACEReader(inDirPath, false);
		String docID = "AFP_ENG_20030413.0098.apf.xml";
		
		for (TextAnnotation ta: aceReader) {
			if (ta.getId().contains(docID) == false)
				continue;
			relEx.addView(ta);
			List<Constituent> annots = ta.getView(relEx.viewName).getConstituents();
			for (Constituent annot: annots) {
				if(annot.getOutgoingRelations().size() > 0) {
					System.out.println(annot.getOutgoingRelations().size());
					for (Relation rel : annot.getOutgoingRelations()) {
						System.out.println(rel.getRelationName() + "-->" + rel.getSource() + "-->" + rel.getSource().getLabel() + "-->" + 
								rel.getTarget() + "-->" + rel.getTarget().getLabel());
					}
				}
			}
		}
	}

	@Override
	public void addView(TextAnnotation ta) throws AnnotatorException {
		/*
		 * Dummy function -- RelEx view is already a part of the text annotation
		 */
	}
}
