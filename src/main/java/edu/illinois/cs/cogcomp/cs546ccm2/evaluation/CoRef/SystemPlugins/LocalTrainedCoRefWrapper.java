//package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.CoRef.SystemPlugins;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//
//import org.apache.commons.lang.NotImplementedException;
//
//import edu.illinois.cs.cogcomp.core.datastructures.Pair;
//import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
//import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
//import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
//import edu.illinois.cs.cogcomp.cs546ccm2.corpus.Paragraph;
//import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalTrainedCoRef;
//import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.A2WDataset;
//import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.ACEDatasetWrapper;
//import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
//import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
//import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;
//import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers.A2WSystem;
//
//public class LocalTrainedCoRefWrapper implements A2WSystem {
//	
//	private String NAME;
//	private LocalTrainedCoRef coref;
//	
//	public LocalTrainedCoRefWrapper(String md, String modelPath) throws IOException, ClassNotFoundException {
//		coref = new LocalTrainedCoRef(md, modelPath);
//		NAME = coref.getName();
//	}
//	
//	@Override
//	public HashSet<Annotation> solveA2W(String text) {
//		throw new NotImplementedException();
//	}
//
//	@Override
//	public HashSet<Tag> solveC2W(String text) {
//		throw new NotImplementedException();
//	}
//	
//	@Override
//	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions) {
//		throw new NotImplementedException();
//	}
//	
//	// This function doesn't allow overlapping mentions to be returned. Mentions need to match exactly.
//	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions, boolean allow_overlap) {
//		throw new NotImplementedException();
//	}
//	
//	public List<HashSet<Annotation>> getA2WOutputAnnotationList(A2WDataset ds) {
//		throw new NotImplementedException();
//	}
//
//	@Override
//	public String getName() {
//		return this.NAME;
//	}
//	
//	public List<HashSet<Annotation>> getCoRefTagList(ACEDatasetWrapper ds) throws Exception {
//		List<HashSet<Annotation>> res = new ArrayList<>();
//		for(ACEDocument doc: ds.getDocs()) {
//			HashSet<Annotation> outAnnots = new HashSet<>();
//			List<Pair<String, Paragraph>> paragraphs = doc.paragraphs;
//			List<Paragraph> contentParas = new ArrayList<>();
//			for(Pair<String, Paragraph> pair: paragraphs) {
//				if(pair.getFirst().equals("text"))
//					contentParas.add(pair.getSecond());
//			}
//			int i=0;
//			for(AnnotatedText ta: doc.taList) {
//				coref.labelText(doc, contentParas.get(i), ta.getTa());
//				List<Constituent> docAnnots;
//				docAnnots = ta.getTa().getView(coref.getName()).getConstituents();
//				
//				for(Constituent cons: docAnnots) {
//					if(cons.getLabel().equalsIgnoreCase("FALSE"))
//						continue;
//					Annotation annot = new Annotation(cons.getStartCharOffset() + contentParas.get(i).offsetFilterTags,
//							cons.getEndCharOffset() - cons.getStartCharOffset(), cons.getLabel());
//					outAnnots.add(annot);
//				}
//				i++;
//			}
//			res.add(outAnnots);
//		}
//		
//		return res;
//	}
//}
