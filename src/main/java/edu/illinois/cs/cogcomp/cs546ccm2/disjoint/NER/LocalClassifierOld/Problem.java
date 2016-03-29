package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javatools.parsers.PlingStemmer;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.quant.driver.QuantSpan;
import edu.illinois.cs.cogcomp.quant.standardize.Quantity;

public class Problem {

	public int id;
	public String question;
	public TextAnnotation ta;
	public Double answer;
	public List<QuantSpan> quantities;
	public Node expr;
	public List<String> stems;
	public List<String> lemmas;
	public List<Constituent> posTags;
	public List<Constituent> chunks;
	public List<Constituent> dependency;
	public List<Constituent> ner;
	
	@Override
	public String toString() {
		String str = "";
		str += "\nQuestion : "+question+"\nAnswer : "+answer +
				"\nQuantities : "+Arrays.asList(quantities) + 
				"\nExpression : "+expr;
		return str;
	}
 	
	public Problem(int id, String q, double a) throws AnnotatorException {
		this.id = id;
		question = q;
		ta = Tools.pipeline.createAnnotatedTextAnnotation(q, false);
		answer = a;
		quantities = new ArrayList<QuantSpan>();
	}
	
	public void extractQuantities() throws IOException{
		List<QuantSpan> spanArray = Tools.quantifier.getSpans(question);
		quantities = new ArrayList<QuantSpan>();
		for(QuantSpan span:spanArray){
			if(span.object instanceof Quantity){
				if(Character.isLowerCase(question.charAt(span.end)) ||
						Character.isUpperCase(question.charAt(span.end))) continue;
				if(span.start>0 && (Character.isLowerCase(question.charAt(span.start-1))
						|| Character.isUpperCase(question.charAt(span.start-1)))) continue;
				quantities.add(span);
			}
		}
	}
	
	public void extractAnnotations() throws Exception {
		posTags = ta.getView(ViewNames.POS).getConstituents();
		chunks = ta.getView(ViewNames.SHALLOW_PARSE).getConstituents();
		dependency = ta.getView(ViewNames.DEPENDENCY_STANFORD).getConstituents();
		ner = ta.getView(ViewNames.NER_CONLL).getConstituents();
		stems = new ArrayList<>();
		for(String token : ta.getTokens()) {
			stems.add(PlingStemmer.stem(token));
		}
		lemmas = new ArrayList<>();
		for(Constituent cons : ta.getView(ViewNames.LEMMA).getConstituents()) {
			lemmas.add(cons.getLabel());
		}
		// Fix some chunker issues
		chunks = fixChunkerIssues(chunks);
	}
	
	public List<Constituent> fixChunkerIssues(List<Constituent> chunks) {
		List<Constituent> newChunks = new ArrayList<>();
		for(int i=0; i<chunks.size(); ++i) {
			Constituent chunk = chunks.get(i);
			if(i+1<chunks.size() && chunks.get(i+1).getSurfaceForm().startsWith("'s")) {
				newChunks.add(new Constituent("NP", null, ta, chunk.getStartSpan(), 
						chunks.get(i+1).getEndSpan()));
				i++;
				continue;
			}
			boolean hasQuant = false, doneSplit = false; int quantToken = -1;
			for(QuantSpan qs : quantities) {
				if(ta.getTokenIdFromCharacterOffset(qs.start) >= chunk.getStartSpan() && 
						ta.getTokenIdFromCharacterOffset(qs.start) < chunk.getEndSpan()) {
					hasQuant = true;
					quantToken = ta.getTokenIdFromCharacterOffset(qs.start);
				}
			}
			if(hasQuant) {
				if(ta.getToken(chunk.getStartSpan()).equals("him") || ta.getToken(chunk.getStartSpan()).equals("her")
						|| ta.getToken(chunk.getStartSpan()).equals("his")) {
					newChunks.add(new Constituent("NP", null, ta, chunk.getStartSpan(), 
							quantToken));
					newChunks.add(new Constituent("NP", null, ta, quantToken, 
							chunk.getEndSpan()));
					doneSplit = true;
					continue;
				}
				for(Constituent cons : ner) {
					if(cons.getStartSpan() == chunk.getStartSpan() && quantToken > cons.getStartSpan()) {
						newChunks.add(new Constituent("NP", null, ta, chunk.getStartSpan(), 
								quantToken));
						newChunks.add(new Constituent("NP", null, ta, quantToken, 
								chunk.getEndSpan()));
						doneSplit = true;
						break;
					}
				}
			}
			if(doneSplit) continue;
			newChunks.add(new Constituent(chunk.getLabel(), null, ta, chunk.getStartSpan(), chunk.getEndSpan()));
		}
		return newChunks;
	}

	public void getRelevantQuantityIndicesAndOperationForAI2(){
		for(int i=0;i<quantities.size();i++){
			for(int j=i+1;j<quantities.size();j++){
				double val1 = Tools.getValue(quantities.get(i));
				double val2 = Tools.getValue(quantities.get(j));
				if(Tools.safeEquals(val1+val2, answer)){
					Node node1 = new Node(i, quantities.get(i), "NUM");
					Node node2 = new Node(j, quantities.get(j), "NUM");
					expr = new Node("ADD", Arrays.asList(node1, node2));
					break;
				}
				if(Tools.safeEquals(val1-val2,answer)){
					Node node1 = new Node(i, quantities.get(i), "NUM");
					Node node2 = new Node(j, quantities.get(j), "NUM");
					expr = new Node("SUB", Arrays.asList(node1, node2));
					break;
				}
				if(Tools.safeEquals(val2-val1,answer)){
					Node node1 = new Node(j, quantities.get(j), "NUM");
					Node node2 = new Node(i, quantities.get(i), "NUM");
					expr = new Node("SUB", Arrays.asList(node1, node2));
					break;
				}
			}
			if(expr != null) break;
		}
		// For AI2 problems with 3 quantities
		if(quantities.size() >= 3 && expr == null) {
			for(int i=0; i<quantities.size(); i++){
				for(int j=i+1; j<quantities.size(); j++){
					for(int k=j+1; k<quantities.size(); ++k) {
						double val1 = Tools.getValue(quantities.get(i));
						double val2 = Tools.getValue(quantities.get(j));
						double val3 = Tools.getValue(quantities.get(k));
						if(Tools.safeEquals(val1+val2+val3, answer)) {
							Node node1 = new Node(i, quantities.get(i), "NUM");
							Node node2 = new Node(j, quantities.get(j), "NUM");
							Node node3 = new Node(k, quantities.get(k), "NUM");
							Node node4 = new Node("ADD", Arrays.asList(node1, node2));
							expr = new Node("ADD", Arrays.asList(node4, node3));
							break;
						}
					}
					if(expr != null) break;
				}
				if(expr != null) break;
			}
		}
		// Check
		if(expr == null) {
			System.out.println("Error in finding gold label");
			System.out.println("Problem : "+question);
			System.out.println("Quantities : "+quantities);
			System.out.println("Answer : "+answer);
		}
	}
	
	public void getRelevantQuantityIndicesAndOperationForIllinois(){
		for(int i=0;i<quantities.size();i++){
			for(int j=i+1;j<quantities.size();j++){
				double val1 = Tools.getValue(quantities.get(i));
				double val2 = Tools.getValue(quantities.get(j));
				if(Tools.safeEquals(val1+val2, answer)){
					Node node1 = new Node(i, quantities.get(i), "NUM");
					Node node2 = new Node(j, quantities.get(j), "NUM");
					expr = new Node("ADD", Arrays.asList(node1, node2));
					break;
				}
				if(Tools.safeEquals(val1*val2,answer)){
					Node node1 = new Node(i, quantities.get(i), "NUM");
					Node node2 = new Node(j, quantities.get(j), "NUM");
					expr = new Node("MUL", Arrays.asList(node1, node2));
					break;
				}
				if(Tools.safeEquals(val1-val2,answer)){
					Node node1 = new Node(i, quantities.get(i), "NUM");
					Node node2 = new Node(j, quantities.get(j), "NUM");
					expr = new Node("SUB", Arrays.asList(node1, node2));
					break;
				}
				if(Tools.safeEquals(val2-val1,answer)){
					Node node1 = new Node(j, quantities.get(j), "NUM");
					Node node2 = new Node(i, quantities.get(i), "NUM");
					expr = new Node("SUB", Arrays.asList(node1, node2));
					break;
				}
				if(Tools.safeEquals(val1/val2,answer)){
					Node node1 = new Node(i, quantities.get(i), "NUM");
					Node node2 = new Node(j, quantities.get(j), "NUM");
					expr = new Node("DIV", Arrays.asList(node1, node2));
					break;
				}
				if(Tools.safeEquals(val2/val1,answer)){
					Node node1 = new Node(j, quantities.get(j), "NUM");
					Node node2 = new Node(i, quantities.get(i), "NUM");
					expr = new Node("DIV", Arrays.asList(node1, node2));
					break;
				}
			}
			if(expr != null) break;
		}
		// Check
		if(expr == null) {
			System.out.println("Error in finding gold label");
			System.out.println("Problem : "+question);
			System.out.println("Quantities : "+quantities);
			System.out.println("Answer : "+answer);
		}
	}

	public void getRelevantQuantityIndicesAndOperationForCC(String source) {
		for(int i=0;i<quantities.size();i++) {
			double val1 = Tools.getValue(quantities.get(i));
			for(int j=i+1;j<quantities.size();j++) {
				double val2 = Tools.getValue(quantities.get(j));
				for(int k=j+1;k<quantities.size(); ++k) {
					double val3 = Tools.getValue(quantities.get(k));
					if(source.contains("addsub") && Tools.safeEquals(val1+val2-val3, answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("ADD", Arrays.asList(node1, node2));
						expr = new Node("SUB", Arrays.asList(node4, node3));
						break;
					}
					if(source.contains("addmul") && Tools.safeEquals((val1+val2)*val3, answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("ADD", Arrays.asList(node1, node2));
						expr = new Node("MUL", Arrays.asList(node3, node4));
						break;
					}
					if(source.contains("addmul") && Tools.safeEquals(val1*(val2+val3), answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("ADD", Arrays.asList(node2, node3));
						expr = new Node("MUL", Arrays.asList(node1, node4));
						break;
					}
					if(source.contains("adddiv") && Tools.safeEquals((val1+val2)/val3, answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("ADD", Arrays.asList(node1, node2));
						expr = new Node("DIV", Arrays.asList(node4, node3));
						break;
					}
					if(source.contains("adddiv") && Tools.safeEquals((val2+val3)/val1, answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("ADD", Arrays.asList(node2, node3));
						expr = new Node("DIV", Arrays.asList(node4, node1));
						break;
					}
					if(source.contains("subadd") && Tools.safeEquals(val1-val2+val3, answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("ADD", Arrays.asList(node1, node3));
						expr = new Node("SUB", Arrays.asList(node4, node2));
						break;
					}
					if(source.contains("submul") && Tools.safeEquals((val1-val2)*val3, answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("SUB", Arrays.asList(node1, node2));
						expr = new Node("MUL", Arrays.asList(node3, node4));
						break;
					}
					if(source.contains("submul") && Tools.safeEquals((val1-val3)*val2, answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("SUB", Arrays.asList(node1, node3));
						expr = new Node("MUL", Arrays.asList(node2, node4));
						break;
					}
					if(source.contains("submul") && Tools.safeEquals((val2-val3)*val1, answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("SUB", Arrays.asList(node2, node3));
						expr = new Node("MUL", Arrays.asList(node1, node4));
						break;
					}
					if(source.contains("submul") && Tools.safeEquals((val2-val1)*val3, answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("SUB", Arrays.asList(node2, node1));
						expr = new Node("MUL", Arrays.asList(node3, node4));
						break;
					}
					if(source.contains("subdiv") && Tools.safeEquals((val1-val2)/val3, answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("SUB", Arrays.asList(node1, node2));
						expr = new Node("DIV", Arrays.asList(node4, node3));
						break;
					}
					if(source.contains("subdiv") && Tools.safeEquals((val2-val3)/val1, answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("SUB", Arrays.asList(node2, node3));
						expr = new Node("DIV", Arrays.asList(node4, node1));
						break;
					}
					if(source.contains("subdiv") && Tools.safeEquals((val1-val3)/val2, answer)){
						Node node1 = new Node(i, quantities.get(i), "NUM");
						Node node2 = new Node(j, quantities.get(j), "NUM");
						Node node3 = new Node(k, quantities.get(k), "NUM");
						Node node4 = new Node("SUB", Arrays.asList(node1, node3));
						expr = new Node("DIV", Arrays.asList(node4, node2));
						break;
					}
				}
				if(expr != null) break;
			}
			if(expr != null) break;
		}
		// Check
		if(expr == null) {
			System.out.println("Error in finding gold label");
			System.out.println("Problem : "+question);
			System.out.println("Quantities : "+quantities);
			System.out.println("Answer : "+answer);
		}
	}
	
}
