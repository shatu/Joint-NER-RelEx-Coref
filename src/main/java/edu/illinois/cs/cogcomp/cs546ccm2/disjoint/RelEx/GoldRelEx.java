package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx;

import java.io.IOException;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.ACECorpus;

public class GoldRelEx implements ARelEx {
	
	private String NAME = CCM2Constants.RelExGold;
	
	public static void main(String[] args) throws AnnotatorException, IOException {
		String inDirPath = CCM2Constants.ACE05ProcessedPath;
		GoldRelEx relEx = new GoldRelEx();
		ACECorpus aceCorpus = new ACECorpus();
		aceCorpus.initCorpus(inDirPath);
		ACEDocument doc = aceCorpus.getDocFromID("AFP_ENG_20030304.0250");
//		ACEDocument doc = aceCorpus.getDocFromID("CNNHL_ENG_20030526_221156.39");
		for(AnnotatedText at: doc.taList) {
			TextAnnotation ta = at.getTa(); 
			relEx.labelText(ta);
			List<Constituent> annots = ta.getView(CCM2Constants.RelExGold).getConstituents();
			for(Constituent annot: annots) {
				if(annot.getOutgoingRelations().size() > 0) {
					System.out.println(annot.getOutgoingRelations().size());
					for(Relation rel : annot.getOutgoingRelations()) {
						System.out.println(rel.getRelationName() + "-->" + rel.getSource() + "-->" + rel.getSource().getLabel() + "-->" + 
								rel.getTarget() + "-->" + rel.getTarget().getLabel());
					}
				}
			}
		}
	}
	
	public void labelText(TextAnnotation ta) throws AnnotatorException {
		/*
		 * Dummy function -- RelEx view is already a part of the text annotation
		 */
	}

	@Override
	public String getName() {
		return NAME;
	}

}
