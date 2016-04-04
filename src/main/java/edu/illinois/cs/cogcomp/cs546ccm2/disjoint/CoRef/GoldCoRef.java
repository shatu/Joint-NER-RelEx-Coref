package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef;

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

public class GoldCoRef implements ACoRef {
	
	private String NAME = CCM2Constants.CoRefGold;
	
	public static void main(String[] args) throws AnnotatorException, IOException {
		String inDirPath = CCM2Constants.ACE05ProcessedPath;
		GoldCoRef coref = new GoldCoRef();
		ACECorpus aceCorpus = new ACECorpus();
		aceCorpus.initCorpus(inDirPath);
		ACEDocument doc = aceCorpus.getDocFromID("AFP_ENG_20030304.0250");
//		ACEDocument doc = aceCorpus.getDocFromID("CNNHL_ENG_20030526_221156.39");
		for(AnnotatedText at: doc.taList) {
			TextAnnotation ta = at.getTa(); 
			coref.labelText(ta);
			List<Constituent> annots = ta.getView(CCM2Constants.CoRefGold).getConstituents();
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
	
	public void labelText(TextAnnotation ta) throws AnnotatorException {
		/*
		 * Dummy function -- CoRef view is already a part of the text annotation
		 */
	}

	@Override
	public String getName() {
		return NAME;
	}

}
