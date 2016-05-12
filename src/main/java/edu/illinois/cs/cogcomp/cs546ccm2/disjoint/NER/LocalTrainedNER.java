/**
 * 
 */
package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER;

import java.io.IOException;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.FakeRetrainedChunkerPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.GoldMD;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.IllinoisChunkerPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.IllinoisNER_MDPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.NerInstance;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.NerLabel;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.core.SLModel;

/**
 * @author shashank
 *
 */
public class LocalTrainedNER extends Annotator {
	private SLModel model;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String inDirPath = CCM2Constants.ACE05TrainCorpusPath;
		LocalTrainedNER ner = new LocalTrainedNER(CCM2Constants.LocalTrainedNER_GoldMDView, new String[]{CCM2Constants.MDGoldExtent});
		ACEReader aceReader = new ACEReader(inDirPath, false);
		String docID = "AFP_ENG_20030413.0098.apf.xml";
		
		for (TextAnnotation ta: aceReader) {
			if (ta.getId().contains(docID) == false)
				continue;
			
			System.out.println(ta.getId());
			ner.addView(ta);
			List<Constituent> annots = ta.getView(ner.viewName).getConstituents();
			
			for (Constituent annot: annots) {
				System.out.println(annot.toString() + "-->" + annot.getLabel() + "-->" + annot.getStartCharOffset() + "-->" + 
						annot.getEndCharOffset());
			}
		}
	}
	
	public LocalTrainedNER(String viewName, String[] requiredViews) throws ClassNotFoundException, IOException {
		super(viewName, requiredViews);
		String modelPath = CCM2Constants.ACE05NerModelPath + "/" + requiredViews[0] + ".model"; 
		model = SLModel.loadModel(modelPath);
		
	}

	@Override
	public void addView(TextAnnotation ta) throws AnnotatorException {
		try {
			addRequiredViews(ta, requiredViews[0]);
			
			List<Constituent> docAnnots = ta.getView(requiredViews[0]).getConstituents();
			SpanLabelView view = new SpanLabelView(viewName, this.getClass().getName(), ta, 1d, true);
			
			for (Constituent cons: docAnnots) {
				NerInstance x = new NerInstance(cons);
				NerLabel y = (NerLabel) model.infSolver.getBestStructure(model.wv, x);
				double score = 1.0 * model.wv.dotProduct(model.featureGenerator.getFeatureVector(x, y));
				view.addSpanLabel(cons.getSpan().getFirst(), cons.getSpan().getSecond(), y.type, score);
			}
			
			ta.addView(viewName, view);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void addRequiredViews(TextAnnotation ta, String mdView) throws Exception {
		if (ta.hasView(mdView) == false) {
			if (mdView.equalsIgnoreCase(CCM2Constants.MDGoldExtent)) {
				GoldMD goldMD = new GoldMD(CCM2Constants.MDGoldExtent);
				goldMD.addView(ta);
			}
			else if (mdView.equalsIgnoreCase(CCM2Constants.MDGoldHead)) {
				GoldMD goldMD = new GoldMD(CCM2Constants.MDGoldHead);
				goldMD.addView(ta);
			}
			else if (mdView.equalsIgnoreCase(CCM2Constants.IllinoisNERConllMD)) {
				IllinoisNER_MDPlugin nerMD = new IllinoisNER_MDPlugin(CCM2Constants.IllinoisNERConllMD);
				nerMD.addView(ta);
			}
			else if (mdView.equalsIgnoreCase(CCM2Constants.IllinoisNEROntonotesMD)) {
				IllinoisNER_MDPlugin nerMD = new IllinoisNER_MDPlugin(CCM2Constants.IllinoisNEROntonotesMD);
				nerMD.addView(ta);
			}
			else if (mdView.equalsIgnoreCase(CCM2Constants.IllinoisChunkerMD)) {
				IllinoisChunkerPlugin chunkerMD = new IllinoisChunkerPlugin();
				chunkerMD.addView(ta);
			}
			else if (mdView.equalsIgnoreCase(CCM2Constants.RetrainedChunkerMDViewName)) {
				FakeRetrainedChunkerPlugin fakeMD = new FakeRetrainedChunkerPlugin();
				fakeMD.addView(ta);
			}
		}
	}
}
