package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef;

import java.util.Set;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.CoreferenceView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;

public class GoldCoRef extends Annotator {
	
	public GoldCoRef(String viewName) {
		super(viewName, new String[]{});
	}
	
	private GoldCoRef(String viewName, String[] requiredViews) {
		super(viewName, requiredViews);
	}

	public static void main(String[] args) throws Exception {
		String inDirPath = CCM2Constants.ACE05TrainCorpusPath;
		GoldCoRef coref = new GoldCoRef(CCM2Constants.CoRefGoldExtent);
		ACEReader aceReader = new ACEReader(inDirPath, false);
		String docID = "AFP_ENG_20030413.0098.apf.xml";
		
		for (TextAnnotation ta: aceReader) {
			if (ta.getId().contains(docID) == false)
				continue;
			coref.addView(ta);
			CoreferenceView corefView = (CoreferenceView)ta.getView(coref.viewName);
			Set<Constituent> canonicalAnnots = corefView.getCanonicalEntitiesViaRelations();
			for (Constituent cons: canonicalAnnots) {
				for (Constituent chained : corefView.getCoreferentMentionsViaRelations(cons))
						System.out.println(cons + "-->" + cons.getAttribute("EntityType") + "-->" + 
								chained + "-->" + chained.getAttribute("EntityType"));
			}
		}
	}

	@Override
	public void addView(TextAnnotation ta) throws AnnotatorException {
		/*
		 * Dummy function -- CoRef view is already a part of the text annotation
		 */
	}
}
