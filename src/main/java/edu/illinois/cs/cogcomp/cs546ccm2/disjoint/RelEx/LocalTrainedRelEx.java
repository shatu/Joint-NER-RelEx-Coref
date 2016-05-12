///**
// * 
// */
//package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.commons.lang.NotImplementedException;
//
//import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
//import edu.illinois.cs.cogcomp.core.datastructures.Pair;
//import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
//import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
//import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
//import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
//import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
//import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
//import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
//import edu.illinois.cs.cogcomp.cs546ccm2.corpus.Paragraph;
//import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.ACECorpus;
//import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.AMentionDetector;
//import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.GoldMD;
//import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.IllinoisChunkerPlugin;
//import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.IllinoisNERPlugin;
//import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx.LocalClassifier.RelInstance;
//import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx.LocalClassifier.RelLabel;
//import edu.illinois.cs.cogcomp.sl.core.SLModel;
//
///**
// * @author shashank
// *
// */
//public class LocalTrainedRelEx implements ARelEx {
//
//	//TODO: Assign an appropriate name according to the method used
//	private String NAME;
//	private AMentionDetector md;
//	private SLModel model;
//	
//	/**
//	 * @param args
//	 * @throws Exception 
//	 */
//	public static void main(String[] args) throws Exception {
//		String inDirPath = CCM2Constants.ACE05ProcessedPath;
//		LocalTrainedRelEx relEx = new LocalTrainedRelEx(CCM2Constants.NERGold, CCM2Constants.ACE05RelExModelPath + "/GoldMentions.save");
//		ACECorpus aceCorpus = new ACECorpus();
//		aceCorpus.initCorpus(inDirPath);
//		//ACEDocument doc = aceCorpus.getDocFromID("AFP_ENG_20030304.0250");
//		ACEDocument doc = aceCorpus.getDocFromID("CNNHL_ENG_20030526_221156.39");
//		List<Pair<String, Paragraph>> paragraphs = doc.paragraphs;
//		List<Paragraph> contentParas = new ArrayList<>();
//		for(Pair<String, Paragraph> pair: paragraphs) {
//			if(pair.getFirst().equals("text"))
//				contentParas.add(pair.getSecond());
//		}
//		
//		int i=0;
//		for(AnnotatedText ta: doc.taList) {
//			relEx.labelText(doc, contentParas.get(i), ta.getTa());
//			List<Constituent> annots = ta.getTa().getView(relEx.getName()).getConstituents();
//			for(Constituent annot: annots) {
//				System.out.println(annot.toString() + "-->" + annot.getLabel() + "-->" + (annot.getStartCharOffset() + contentParas.get(i).offsetFilterTags) 
//						+ "-->" + (annot.getEndCharOffset() + contentParas.get(i).offsetFilterTags));
//			}
//			i++;
//		}
//	}
//	
//	/**
//	 * @param nerView (NERGold/IllinoisNEROntonotes/IllinoisNERConll/NER_GoldMD/NER_IllinoisChunker/NER_IllinoisNEROntonotes/NER_IllinoisNERConll) 
//	 * @throws IOException
//	 * @throws ClassNotFoundException 
//	 */
//	public LocalTrainedRelEx(String nerView, String modelPath) throws IOException, ClassNotFoundException {
//		if(mentionView.equalsIgnoreCase("MDGold")) {
//			this.md = new GoldMD();
//			this.NAME = "RelEx_" + md.getName();
//		}
//		else if(mentionView.equalsIgnoreCase("IllinoisChunker")) {
//			this.md = new IllinoisChunkerPlugin();
//			this.NAME = "RelEx_" + md.getName();
//		}
//		else if(mentionView.equalsIgnoreCase("IllinoisNEROntonotes")) {
//			this.md = new IllinoisNERPlugin(true);
//			this.NAME = "RelEx_" + md.getName();
//		}
//		else if(mentionView.equalsIgnoreCase("IllinoisNERConll")) {
//			this.md = new IllinoisNERPlugin();
//			this.NAME = "RelEx_" + md.getName();
//		}
//		else
//			throw new IllegalArgumentException("MentionView " + mentionView + " not supported");
//		
//		model = SLModel.loadModel(modelPath);
//	}
//	
//	public LocalTrainedRelEx(AMentionDetector md, String modelPath) throws ClassNotFoundException, IOException {
//		this.md = md;
//		this.NAME = "RelEx_" + md.getName();
//		model = SLModel.loadModel(modelPath);
//	}
//	
//	//TODO: Check this thoroughly!!
//	public void labelText(ACEDocument doc, Paragraph p, TextAnnotation ta) throws Exception {
//		md.labelText(ta);
//		List<Constituent> docAnnots = ta.getView(md.getName()).getConstituents();
//		SpanLabelView view = new SpanLabelView(getName(), this.getClass().getName(), ta, 1d, true);
//		
//		for(int i=0; i<docAnnots.size(); i++) {
//			for(int j = i+1; j< docAnnots.size(); j++) {
//				
//				
//				
//				for(Constituent cons: docAnnots) {
//					RelInstance x = new RelInstance(doc, p, cons);
//					RelLabel y = (RelLabel) model.infSolver.getBestStructure(model.wv, x);
//					double score = 1.0 * model.wv.dotProduct(model.featureGenerator.getFeatureVector(x, y));
//					view.addSpanLabel(cons.getSpan().getFirst(), cons.getSpan().getSecond(), y.type, score);
//				}
//			}
//		}
//		
//		ta.addView(getName(), view);
//	}
//
//	@Override
//	public String getName() {
//		return NAME;
//	}
//
//	@Override
//	public void labelText(TextAnnotation ta) throws AnnotatorException {
//		throw new NotImplementedException();
//	}
//
//}
