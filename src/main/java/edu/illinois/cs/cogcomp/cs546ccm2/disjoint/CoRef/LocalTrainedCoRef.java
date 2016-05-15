package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.AnnotatorService;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.CoreferenceView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier.CoRefInstance;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier.CoRefLabel;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.FakeRetrainedChunkerPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.GoldMD;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.IllinoisChunkerPlugin;
import edu.illinois.cs.cogcomp.cs546ccm2.disjoint.MD.IllinoisNER_MDPlugin;
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
			CoreferenceView corefView = (CoreferenceView)ta.getView(coref.viewName);
			Set<Constituent> canonicalAnnots = corefView.getCanonicalEntitiesViaRelations();
			for (Constituent cons: canonicalAnnots) {
				System.out.print(cons + "-->" + cons.getAttribute("EntityType") + "-->");
				
				for (Constituent chained : corefView.getCoreferentMentionsViaRelations(cons)) {
					if (cons != chained)
						System.out.print(chained + "-->" + chained.getAttribute("EntityType") + "-->");
				}
				
				System.out.println("\n");
			}
		}
	}
	
	public LocalTrainedCoRef(String viewName, String[] requiredViews) throws ClassNotFoundException, IOException {
		super(viewName, requiredViews);
		String modelPath = CCM2Constants.ACE05CoRefModelPath + "/" + requiredViews[1] + ".model"; 
		model = SLModel.loadModel(modelPath);
		
	}

//	@Override
//	public void addView(TextAnnotation ta) throws AnnotatorException {
//		try {
//			addRequiredViews(ta, requiredViews[1]);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			System.exit(-1);
//		}
//		
//        SpanLabelView mentionView = (SpanLabelView) ta.getView(requiredViews[1]);
//        SpanLabelView sentenceView = (SpanLabelView) ta.getView(ViewNames.SENTENCE);
//
//        IQueryable<Constituent> allMentions = new QueryableList<>(mentionView.getConstituents());
//
//        ArrayList<Pair<Constituent, Constituent>> predictionProbs = new ArrayList<>();
//        
//        for (Constituent sentence : sentenceView.getConstituents()) {
//            IQueryable<Constituent> mentionsInSentence = allMentions.where(Queries.containedInConstituent(sentence));
//
//            for (Constituent firstEntity : mentionsInSentence) {
//                for (Constituent secondEntity : mentionsInSentence) {
//                    if (firstEntity != secondEntity) {
//                        predictionProbs.add(new Pair<>(firstEntity, secondEntity));
//                    }
//                }
//            }
//        }
//
//        CoreferenceView corefView = new CoreferenceView(this.viewName, ta);
//        
//        for (Pair<Constituent, Constituent> instance : predictionProbs) {
//        	CoRefInstance x = new CoRefInstance(instance.getFirst(), instance.getSecond());
//        	CoRefLabel y;
//			try {
//				y = (CoRefLabel) model.infSolver.getBestStructure(model.wv, x);
//				double score = 1.0 * model.wv.dotProduct(model.featureGenerator.getFeatureVector(x, y));
//				
//				if (y.type.equalsIgnoreCase("FALSE") == false) {
//					Constituent predicate = instance.getFirst().cloneForNewViewWithDestinationLabel(corefView.getViewName(), y.type);
//					Constituent argument = instance.getSecond().cloneForNewViewWithDestinationLabel(corefView.getViewName(), y.type);
//					corefView.addPredicateArguments(predicate, Collections.singletonList(argument), new String[] {y.type}, new double[] {score});
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				System.exit(-1);
//			}
//        }
//
//        ta.addView(this.viewName, corefView);
//	}
	
    @Override
    public void addView (TextAnnotation textAnnotation) throws AnnotatorException {
		try {
			addRequiredViews(textAnnotation, requiredViews[1]);
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(-1);
		}

        CoreferenceView finalView = new CoreferenceView(this.viewName, textAnnotation);

        List<Constituent> mentions = textAnnotation.getView(requiredViews[1]).getConstituents();
        mentions = sortIncreasing(mentions);
        
        int nummentions = mentions.size();

        String clusterPrefix = "clusterID-";
        int clusterId = 0;

        List<Constituent> coref_mentions = new ArrayList<Constituent>();
        Map<String, List<Constituent>> clusters = new HashMap<>();
        
        for (int i=0; i<nummentions; i++) {
            Constituent currC = mentions.get(i);
            int past = i-1;
            int best = -1; 
            double bestScore = Double.MIN_VALUE;
            
            while (past >= 0) {
                Constituent prevC = coref_mentions.get(past);
                CoRefInstance CE = new CoRefInstance(currC, prevC);
                double t_score = getPOSScore(CE);
                double f_score = getNEGScore(CE);
                System.out.println(t_score + "\t" + f_score);
                if ((t_score > f_score) && (t_score > bestScore)){
                    bestScore = t_score;
                    best = past;
                }
                
                past--;
            }

            if (best != -1 && bestScore > 0.0) {
                String clusterLabel = coref_mentions.get(best).getLabel();
                Constituent newCorefC = currC.cloneForNewViewWithDestinationLabel(this.viewName, clusterLabel);
                clusters.get(clusterLabel).add(newCorefC);
                coref_mentions.add(newCorefC);
            } 
            else {
                String newClusterID = clusterPrefix + clusterId;
                Constituent newCorefC = currC.cloneForNewViewWithDestinationLabel(this.viewName, newClusterID);
                coref_mentions.add(newCorefC);
                clusters.put(newClusterID, new ArrayList<Constituent>());
                clusters.get(newClusterID).add(newCorefC);
                clusterId++;
            }
        }

        for (String clusterID : clusters.keySet()){
            List<Constituent> corefmentions = clusters.get(clusterID);
            Constituent canonical = null; 
            int length = -1;
            
            for (Constituent c : corefmentions) {
                if (c.getSurfaceForm().length() > length)
                    canonical = c;
            }
            
            finalView.addCorefEdges(canonical, corefmentions);
        }

        textAnnotation.addView(this.viewName, finalView);
    }

    public double getNEGScore(CoRefInstance problem){
    	double score = 1.0 * model.wv.dotProduct(model.featureGenerator.getFeatureVector(problem, new CoRefLabel("FALSE")));
        return score;
    }

    public double getPOSScore(CoRefInstance problem){
    	double score = 1.0 * model.wv.dotProduct(model.featureGenerator.getFeatureVector(problem, new CoRefLabel("TRUE")));
        return score;
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
	
    public List<Constituent> sortIncreasing(List<Constituent> constituents){
    	Collections.sort(constituents, new Comparator<Constituent>(){
            @Override
            public int compare(Constituent o1, Constituent o2) {
                if(o1.getStartSpan() >= o2.getStartSpan() )
                    return 1;
                else
                    return -1;
            }
        });
        
        return constituents;
    }
}
