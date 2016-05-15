package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.RelEx.SystemPlugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx.GoldRelEx;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.A2WDataset;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.ACEDatasetWrapper;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.RelationAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers.A2WSystem;

public class GoldRelExWrapper implements A2WSystem {
	
	private String NAME;
	private GoldRelEx relEx;
	
	public GoldRelExWrapper(String relViewName) throws IOException, ClassNotFoundException {
		relEx = new GoldRelEx(relViewName);
		NAME = relViewName;
	}
	
	@Override
	public HashSet<Annotation> solveA2W(String text) {
		throw new NotImplementedException();
	}

	@Override
	public HashSet<Tag> solveC2W(String text) {
		throw new NotImplementedException();
	}
	
	@Override
	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions) {
		throw new NotImplementedException();
	}
	
	// This function doesn't allow overlapping mentions to be returned. Mentions need to match exactly.
	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions, boolean allow_overlap) {
		throw new NotImplementedException();
	}
	
	public List<HashSet<Annotation>> getA2WOutputAnnotationList(A2WDataset ds) {
		throw new NotImplementedException();
	}

	@Override
	public String getName() {
		return this.NAME;
	}
	
	public List<HashSet<RelationAnnotation>> getRelationTagsList(ACEDatasetWrapper ds) throws Exception {
		
		List<HashSet<RelationAnnotation>> relList = new ArrayList<>();
		
		for (TextAnnotation ta: ds.getDocs()) {
			relEx.addView(ta);
			HashSet<RelationAnnotation> relSet = new HashSet<>();
			List<Constituent> annots = ((PredicateArgumentView)ta.getView(relEx.getViewName())).getPredicates();
			
			for (Constituent annot: annots) {
				if (annot.getOutgoingRelations().size() > 0) {
					
					Constituent source = annot;
					for (Relation rel : annot.getOutgoingRelations()) {
						Constituent target = rel.getTarget();
						String relName = rel.getRelationName();
						
						RelationAnnotation relation = new RelationAnnotation(source.getStartSpan(), source.length(),
								target.getStartSpan(), target.length(),
								relName);
						
						relSet.add(relation);
					}
				}
			}
			
			relList.add(relSet);
		}
		
		return relList;
	}
}
