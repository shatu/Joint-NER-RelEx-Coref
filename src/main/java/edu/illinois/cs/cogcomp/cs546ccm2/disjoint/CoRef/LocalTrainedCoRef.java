package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.AnnotatorService;
import edu.illinois.cs.cogcomp.core.datastructures.IQueryable;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.QueryableList;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.CoreferenceView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Queries;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.SpanLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier.CoRefInstance;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier.CoRefLabel;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.FakeRetrainedChunkerPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.GoldMD;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.IllinoisChunkerPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.IllinoisNER_MDPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx.LocalClassifier.RelInstance;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.RelEx.LocalClassifier.RelLabel;
import edu.illinois.cs.cogcomp.curator.CuratorFactory;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.core.SLModel;

/**
 * @author shashank
 *
 */
public class LocalTrainedCoRef extends Annotator {
	private SLModel model;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String inDirPath = CCM2Constants.ACE05TrainCorpusPath;
		LocalTrainedCoRef coref = new LocalTrainedCoRef(CCM2Constants.LocalTrainedCoref_GoldMDView, new String[]{ViewNames.POS, CCM2Constants.MDGoldExtent});
		ACEReader aceReader = new ACEReader(inDirPath, false);
		String docID = "AFP_ENG_20030413.0098.apf.xml";
		
		for (TextAnnotation ta: aceReader) {
			if (ta.getId().contains(docID) == false)
				continue;
			coref.addView(ta);
			CoreferenceView corefView = (CoreferenceView) ta.getView(coref.viewName);
			Set<Constituent> canonicalAnnots = corefView.getCanonicalEntitiesViaRelations();
			for (Constituent cons: canonicalAnnots) {
				for (Constituent chained : corefView.getCoreferentMentionsViaRelations(cons))
						System.out.println(cons + "-->" + cons.getLabel() + "-->" + 
								chained + "-->" + chained.getLabel());
			}
		}
	}
	
	public LocalTrainedCoRef(String viewName, String[] requiredViews) throws ClassNotFoundException, IOException {
		super(viewName, requiredViews);
		String modelPath = CCM2Constants.ACE05RelExModelPath + "/" + requiredViews[1] + ".model"; 
		model = SLModel.loadModel(modelPath);
		
	}

	@Override
	public void addView(TextAnnotation ta) throws AnnotatorException {
		try {
			addRequiredViews(ta, requiredViews[1]);
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
        SpanLabelView mentionView = (SpanLabelView) ta.getView(requiredViews[1]);
        SpanLabelView sentenceView = (SpanLabelView) ta.getView(ViewNames.SENTENCE);

        IQueryable<Constituent> allMentions = new QueryableList<>(mentionView.getConstituents());

        ArrayList<Pair<Constituent, Constituent>> predictionProbs = new ArrayList<>();
        
        for (Constituent sentence : sentenceView.getConstituents()) {
            IQueryable<Constituent> mentionsInSentence = allMentions.where(Queries.containedInConstituent(sentence));

            for (Constituent firstEntity : mentionsInSentence) {
                for (Constituent secondEntity : mentionsInSentence) {
                    if (firstEntity != secondEntity) {
                        predictionProbs.add(new Pair<>(firstEntity, secondEntity));
                    }
                }
            }
        }

        CoreferenceView corefView = new CoreferenceView(this.viewName, ta);
        
        for (Pair<Constituent, Constituent> instance : predictionProbs) {
        	CoRefInstance x = new CoRefInstance(instance.getFirst(), instance.getSecond());
        	CoRefLabel y;
			try {
				y = (CoRefLabel) model.infSolver.getBestStructure(model.wv, x);
				double score = 1.0 * model.wv.dotProduct(model.featureGenerator.getFeatureVector(x, y));
				
				if (y.type.equalsIgnoreCase("FALSE") == false) {
					Constituent predicate = instance.getFirst().cloneForNewViewWithDestinationLabel(corefView.getViewName(), y.type);
					Constituent argument = instance.getSecond().cloneForNewViewWithDestinationLabel(corefView.getViewName(), y.type);
					corefView.addPredicateArguments(predicate, Collections.singletonList(argument), new String[] {y.type}, new double[] {score});
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
        }

        ta.addView(this.viewName, corefView);
	}
	
	public static void addRequiredViews(TextAnnotation ta, String mdView) throws Exception {
		if (ta.hasView(ViewNames.POS) == false) {
			AnnotatorService annotator = CuratorFactory.buildCuratorClient();
			annotator.addView(ta, ViewNames.POS);
		}
		
		if (mdView == null)
			return;
		
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
