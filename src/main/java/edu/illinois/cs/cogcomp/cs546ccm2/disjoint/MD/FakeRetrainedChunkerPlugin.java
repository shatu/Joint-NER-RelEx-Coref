package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD;

import java.util.List;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.AnnotatorService;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.curator.CuratorFactory;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;

public class FakeRetrainedChunkerPlugin extends Annotator {
	
	private AnnotatorService annotator;
	
	public FakeRetrainedChunkerPlugin () throws Exception {
		this(CCM2Constants.IllinoisChunkerMD, new String[]{});
	}
	
	private FakeRetrainedChunkerPlugin(String viewName, String[] requiredViews) throws Exception {
		super(viewName, requiredViews);
		this.annotator = CuratorFactory.buildCuratorClient();
	}

	public static void main(String[] args) throws Exception {
		String inDirPath = CCM2Constants.ACE05TrainCorpusPath;
		FakeRetrainedChunkerPlugin shallowParser = new FakeRetrainedChunkerPlugin();
		
		ACEReader aceReader = new ACEReader(inDirPath, false);
		String docID = "AFP_ENG_20030413.0098.apf.xml";
		
		for (TextAnnotation ta: aceReader) {
			if (ta.getId().contains(docID) == false)
				continue;
			
			shallowParser.addView(ta);
			List<Constituent> annots = ta.getView(shallowParser.viewName).getConstituents();
			
			for (Constituent annot: annots) {
				if (annot.getLabel().equals("NP"))
					System.out.println(annot.toString() + "-->" + annot.getLabel() + "-->" + annot.getStartCharOffset() + "-->" + 
							annot.getEndCharOffset());
			}
		}
	}

	@Override
	public void addView(TextAnnotation ta) throws AnnotatorException {
		annotator.addView(ta, CCM2Constants.IllinoisChunkerMD);
	}
}
