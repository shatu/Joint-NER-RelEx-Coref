package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD;

import java.util.List;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.AnnotatorService;
//import edu.illinois.cs.cogcomp.annotation.handler.IllinoisNerHandler;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.curator.CuratorFactory;
//import edu.illinois.cs.cogcomp.nlp.common.PipelineConfigurator;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;

public class IllinoisNER_MDPlugin extends Annotator {
	private AnnotatorService annotator;
	
	public static void main(String[] args) throws Exception {
		IllinoisNER_MDPlugin ner = new IllinoisNER_MDPlugin(CCM2Constants.IllinoisNERConllMD);
		
		String inDirPath = CCM2Constants.ACE05TrainCorpusPath;
		ACEReader aceReader = new ACEReader(inDirPath, false);
	
		String docID = "AFP_ENG_20030413.0098.apf.xml";
		
		for (TextAnnotation ta: aceReader) {
			if (ta.getId().contains(docID) == false)
				continue;
			System.out.println(ta.getId());
			
			ner.addView(ta);
			List<Constituent> annots = ta.getView(ner.viewName).getConstituents();
			
			for (Constituent annot: annots) {
					System.out.println(annot.toString() + "-->" + annot.getLabel());
			}
		}
	}
	
	public IllinoisNER_MDPlugin () throws Exception {
		this(CCM2Constants.IllinoisNERConllMD, new String[]{});
	}
	
	public IllinoisNER_MDPlugin (String viewName) throws Exception {
		this(viewName, new String[]{});
	}
	
	private IllinoisNER_MDPlugin (String viewName, String[] requiredViews) throws Exception {
		super(viewName, requiredViews);
		this.annotator = CuratorFactory.buildCuratorClient();
	}

	@Override
	public void addView(TextAnnotation ta) throws AnnotatorException {
		annotator.addView(ta, viewName);
	}

}
