package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.RelEx;

import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.ACE2004DatasetWrapper;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.ACE2005DatasetWrapper;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.RelationAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria.MatchRelation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria.StrongNoOverlapRelationMatch;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Metrics.BasicMetrics;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Metrics.BasicMetricsRecord;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.RelEx.SystemPlugins.GoldRelExWrapper;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.RelEx.SystemPlugins.LocalTrainedRelExWrapper;

public class Eval_RelEx {
	
	public static void main(String[] args) throws Exception {
		boolean printMacro = true;
		boolean printMicro = true;
		boolean printTpFpFn = true;
		boolean printMax = false;
		boolean printMin = false;
		boolean printSD = false;
		
		System.out.println("Loading the datasets...");
		
		/******** Datasets *********/
//		String aceInputDir = CCM2Constants.ACE04ProcessedPath;
		String aceInputDir = CCM2Constants.ACE05TestCorpusPath;

//		ACE2004Dataset ace = new ACE2004Dataset(aceInputDir);
		ACE2005DatasetWrapper ace = new ACE2005DatasetWrapper(aceInputDir);
		ace.loadAllDocs();
		ace.loadRelationTags(CCM2Constants.RelExGoldExtent);

		/******** Match Relations *********/
//		MatchRelation<Annotation> wam = new WeakNoOverlapAnnotationMatch();
		MatchRelation<RelationAnnotation> sam = new StrongNoOverlapRelationMatch();
		
		/******** Annotators *********/
//		GoldRelExWrapper relEx = new GoldRelExWrapper(CCM2Constants.RelExGoldExtent);
		LocalTrainedRelExWrapper relEx = new LocalTrainedRelExWrapper(CCM2Constants.LocalTrainedRelEx_GoldMDView, CCM2Constants.MDGoldExtent);
//		LocalTrainedNERWrapper ner = new LocalTrainedNERWrapper(CCM2Constants.LocalTrainedNER_GoldMDView, CCM2Constants.IllinoisChunkerMD);
		
		BasicMetrics<RelationAnnotation> metrics = new BasicMetrics<RelationAnnotation>();
//		List<HashSet<Annotation>> computedAnnotations = nerCoNLL.getNERTagList(ace05);
//		List<HashSet<Annotation>> computedAnnotations = nerOntonotes.getNERTagList(ace05);
		List<HashSet<RelationAnnotation>> computedAnnotations = relEx.getRelationTagsList(ace);
		BasicMetricsRecord rs = metrics.getResult(computedAnnotations, ace.getRelationTagsList(), sam);
		
		System.out.println(computedAnnotations.size() + " " + ace.getNERTagsList().size());
	
		/** Print the results about correctness (F1, precision, recall) to the screen */
			
		System.out.println("Correctness performance [F1/prec/rec]");
		
		if (printMicro) {
			String out = String.format("[mic: %.3f\t%.3f\t%.3f] ",rs.getMicroF1(), rs.getMicroPrecision(), rs.getMicroRecall());
			System.out.println(out);
		}
		if (printMacro) {
			String out = String.format("[mac: %.3f\t%.3f\t%.3f] ", rs.getMacroF1(), rs.getMacroPrecision(), rs.getMacroRecall());
			System.out.println(out);
		}
		if (printMax) {
			String out = String.format("[max: %.3f\t%.3f\t%.3f] ", rs.getMaxF1(), rs.getMaxPrecision(), rs.getMaxRecall());
			System.out.println(out);
		}
		if (printMin) {
			String out = String.format("[min: %.3f\t%.3f\t%.3f] ", rs.getMinF1(), rs.getMinPrecision(), rs.getMinRecall());
			System.out.println(out);
		}
		if (printSD) {
			String out = String.format("[SD: %.3f\t%.3f\t%.3f] ", rs.getSDF1(), rs.getSDPrecision(), rs.getSDRecall());
			System.out.println(out);
		}
		if (printTpFpFn) {
			String out = String.format("TP/FP/FN: %d/%d/%d", rs.getGlobalTp(), rs.getGlobalFp(), rs.getGlobalFn());
			System.out.println(out);
		}
	}
	
}