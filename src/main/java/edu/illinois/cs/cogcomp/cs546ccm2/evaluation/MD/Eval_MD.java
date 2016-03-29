package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.MD;

import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.ACE2004DatasetWrapper;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.ACE2005DatasetWrapper;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria.MatchRelation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria.StrongNoOverlapMentionMatch;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria.StrongOverlapMentionMatch;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria.WeakNoOverlapMentionMatch;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Metrics.BasicMetrics;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Metrics.BasicMetricsRecord;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.MD.SystemPlugins.IllinoisChunkerWrapper;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.MD.SystemPlugins.IllinoisNERWrapper;

public class Eval_MD {
	
	public static void main(String[] args) throws Exception {
		boolean printMacro = true;
		boolean printMicro = true;
		boolean printTpFpFn = true;
		boolean printMax = false;
		boolean printMin = false;
		boolean printSD = false;
		
		System.out.println("Loading the datasets...");
		
		/******** Datasets *********/
//		String ace04InputDir = "data/ACE2004_processed/";
		String ace05InputDir = "data/ACE2005_processed/";

//		ACE2004Dataset ace04 = new ACE2004Dataset(ace04InputDir);
		ACE2005DatasetWrapper ace05 = new ACE2005DatasetWrapper(ace05InputDir);
		ace05.loadAllDocs();
//		ace05.loadNWDocs();
//		ace05.loadNERTags();
		ace05.loadNERTagsFromView();

		/******** Match Relations *********/
		MatchRelation<Annotation> mam = new StrongNoOverlapMentionMatch();
//		MatchRelation<Annotation> mam = new StrongOverlapMentionMatch();
//		MatchRelation<Annotation> mam = new WeakNoOverlapMentionMatch();
		
		/******** Annotators *********/
		IllinoisChunkerWrapper chunker = new IllinoisChunkerWrapper();
//		IllinoisNERWrapper nerCoNLL = new IllinoisNERWrapper();
//		IllinoisNERWrapper nerOntonotes = new IllinoisNERWrapper(true);
	
		BasicMetrics<Annotation> metrics = new BasicMetrics<Annotation>();
		List<HashSet<Annotation>> computedAnnotations = chunker.getEntityMentionTagList(ace05);
//		List<HashSet<Annotation>> computedAnnotations = nerCoNLL.getEntityMentionTagList(ace05);
//		List<HashSet<Annotation>> computedAnnotations = nerOntonotes.getEntityMentionTagList(ace05);
		BasicMetricsRecord rs = metrics.getResult(computedAnnotations, ace05.getEntityMentionTagsList(), mam);
		
		System.out.println(computedAnnotations.size() + " " + ace05.getEntityMentionTagsList().size());
	
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