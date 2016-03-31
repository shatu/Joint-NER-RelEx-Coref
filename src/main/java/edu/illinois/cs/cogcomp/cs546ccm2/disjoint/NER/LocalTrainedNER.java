/**
 * 
 */
package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.Paragraph;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.AMentionDetector;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.GoldMD;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.IllinoisChunkerPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.IllinoisNERPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifierOld.NerInstance;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifierOld.NerLabel;
import edu.illinois.cs.cogcomp.sl.core.SLModel;

/**
 * @author shashank
 *
 */
public class LocalTrainedNER implements ANER{

	//TODO: Assign an appropriate name according to the method used
	private String NAME;
	private AMentionDetector md;
	private SLModel model;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * @param mentionView (MDGold/IllinoisChunker/IllinoisNEROntonotes/IllinoisNERConll) 
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public LocalTrainedNER(String mentionView, String modelPath) throws IOException, ClassNotFoundException {
		if(mentionView.equalsIgnoreCase("MDGold")) {
			this.md = new GoldMD();
			this.NAME = "NER_" + md.getName();
		}
		else if(mentionView.equalsIgnoreCase("IllinoisChunker")) {
			this.md = new IllinoisChunkerPlugin();
			this.NAME = "NER_" + md.getName();
		}
		else if(mentionView.equalsIgnoreCase("IllinoisNEROntonotes")) {
			this.md = new IllinoisNERPlugin(true);
			this.NAME = "NER_" + md.getName();
		}
		else if(mentionView.equalsIgnoreCase("IllinoisNERConll")) {
			this.md = new IllinoisNERPlugin();
			this.NAME = "NER_" + md.getName();
		}
		else
			throw new IllegalArgumentException("MentionView " + mentionView + " not supported");
		
		model = SLModel.loadModel(modelPath);
	}
	
	public LocalTrainedNER(AMentionDetector md) {
		this.md = md;
		this.NAME = "NER_" + md.getName();
	}
	
	public void labelText(ACEDocument doc, Paragraph p, TextAnnotation ta) throws Exception {
		md.labelText(ta);
		List<Constituent> docAnnots = ta.getView(md.getName()).getConstituents();
		SpanLabelView view = new SpanLabelView(getName(), this.getClass().getName(), ta, 1d);
		
		for(Constituent cons: docAnnots) {
			NerInstance x = new NerInstance(doc, p, cons);
			NerLabel y = (NerLabel) model.infSolver.getBestStructure(model.wv, x);
			double score = 1.0 * model.wv.dotProduct(model.featureGenerator.getFeatureVector(x, y));
			view.addSpanLabel(cons.getSpan().getFirst(), cons.getSpan().getSecond(), y.type, score);
		}
		
		ta.addView(getName(), view);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void labelText(TextAnnotation ta) throws AnnotatorException {
		throw new NotImplementedException();
	}

}
