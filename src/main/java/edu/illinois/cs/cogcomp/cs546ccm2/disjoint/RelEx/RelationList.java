package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;

public class RelationList<T> extends ArrayList<T> {

	public T mention;
	
	private static final long serialVersionUID = 3835443629631147614L;

	public RelationList(T m) {
		super();
		this.mention = m;
	}
	
	public RelationList(T m, int n){
		super(n);
		this.mention = m;
	}
	
	public static List<Pair<Constituent, Constituent>> getAllConjunctions(List<RelationList<Constituent>> relList) {
		List<Pair<Constituent, Constituent>> allPairs = new ArrayList<Pair<Constituent, Constituent>>();
		for(int i=0; i<relList.size()-1; i++) {
			Constituent mention1 = relList.get(i).mention;
			for (int j=i+1; j<relList.size(); j++) {
				Constituent mention2 = relList.get(j).mention;
				if(!relList.get(i).contains(mention2))
					allPairs.add(new Pair<Constituent, Constituent>(mention1, mention2));
			}
		}
		
		return allPairs;
	}
	
	public static List<RelationList<Constituent>> getRelationListFromRelExView(TextAnnotation ta, String viewName) {
		List<RelationList<Constituent>> relList = new ArrayList<>();
		List<Constituent> docAnnots = ((PredicateArgumentView)ta.getView(viewName)).getPredicates();
		
		for(Constituent cons: docAnnots) {
			if (cons.getIncomingRelations().size() != 0)
				continue;
			
			RelationList<Constituent> newRelList = new RelationList<>(cons);
			
			if (cons.getOutgoingRelations().size() > 0) {
				for (Relation rel : cons.getOutgoingRelations()) {
					newRelList.add(rel.getTarget());
				}
			}
			
			relList.add(newRelList);
		}
		
		return relList;
	}

}
