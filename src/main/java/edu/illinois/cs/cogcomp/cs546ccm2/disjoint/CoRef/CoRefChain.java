package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;

public class CoRefChain<T> extends ArrayList<T> {

	private static final long serialVersionUID = 3967913863145113656L;

	public CoRefChain() {
		super();
	}
	
	public CoRefChain(int n){
		super(n);
	}
	
	public List<Pair<T, T>> getAllPairs() {
		List<Pair<T, T>> allPairs = new ArrayList<Pair<T, T>>();
		
		for(int i=0; i<size()-1; i++) {
			for (int j=i+1; j<size(); j++) {
				allPairs.add(new Pair<T, T>(get(i), get(j)));
			}
		}
		
		return allPairs;
	}
	
	public List<Pair<T, T>> getSequentialPairs() {
		List<Pair<T, T>> seqPairs = new ArrayList<Pair<T, T>>();
		
		for(int i=0; i<size()-1; i++) {
			int j = i+1;
			seqPairs.add(new Pair<T, T>(get(i), get(j)));
		}
		
		return seqPairs;
	}
	
	public List<Pair<T, T>> getAllConjunctions(List<T> that) {
		List<Pair<T, T>> allPairs = new ArrayList<Pair<T, T>>();
		
		for(int i=0; i<this.size(); i++) {
			for (int j=0; j<that.size(); j++) {
				allPairs.add(new Pair<T, T>(this.get(i), that.get(j)));
			}
		}
		
		return allPairs;
	}
	
	public List<Pair<T, T>> getSequentialConjunctions(List<T> that) {
		List<Pair<T, T>> seqPairs = new ArrayList<Pair<T, T>>();
		
		for(int i=0; i<this.size(); i++) {
			for (int j=0; (j<that.size()) && (j<=i); j++) {
				seqPairs.add(new Pair<T, T>(this.get(i), that.get(j)));
			}
		}
		
		return seqPairs;
	}
	
	public static List<CoRefChain<Constituent>> getCoRefChainsFromCoRefView(TextAnnotation ta, String viewName) {
		List<CoRefChain<Constituent>> chains = new ArrayList<>();
		List<Constituent> docAnnots = ta.getView(viewName).getConstituents();
		
		for(Constituent cons: docAnnots) {
			if(cons.getIncomingRelations().size() != 0)
				continue;
			
			CoRefChain<Constituent> newChain = new CoRefChain<>();
			newChain.add(cons);
			
			if(cons.getOutgoingRelations().size() > 0) {
				Constituent chained = cons;
				while(chained.getOutgoingRelations().size() > 0) {
					Relation rel = chained.getOutgoingRelations().get(0);
					newChain.add(rel.getTarget());
					chained = rel.getTarget();
				}
			}
			
			chains.add(newChain);
		}
		return chains;
	}

}
