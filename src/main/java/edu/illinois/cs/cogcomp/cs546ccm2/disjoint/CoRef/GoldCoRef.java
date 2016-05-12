package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef;

import java.util.List;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
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
			List<Constituent> annots = ta.getView(coref.viewName).getConstituents();
			
			for(Constituent annot: annots) {
				if(annot.getIncomingRelations().size() != 0)
					continue;
				if(annot.getOutgoingRelations().size() > 0) {
					System.out.println(annot.getOutgoingRelations().size());
					Constituent chain = annot;
					while(chain.getOutgoingRelations().size() > 0) {
						Relation rel = chain.getOutgoingRelations().get(0);
						System.out.println(rel.getRelationName() + "-->" + rel.getSource() + "-->" + rel.getSource().getLabel() + "-->" + 
								rel.getTarget() + "-->" + rel.getTarget().getLabel());
						
						chain = rel.getTarget();
						System.out.println("*****");
					}
				}
				else {
					System.out.println(annot.getOutgoingRelations().size());
					System.out.println(annot + "-->" + annot.getLabel() + "-->");
					System.out.println("^^^^^^^^^^^^^^^^^");
				}
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
