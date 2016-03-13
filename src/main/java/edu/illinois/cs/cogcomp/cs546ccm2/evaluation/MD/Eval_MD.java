package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.MD;

import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.A2WDataset;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.ACE2005Dataset;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria.MatchRelation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.MatchCriteria.StrongNoOverlapMentionMatch;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Metrics.BasicMetrics;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Metrics.BasicMetricsRecord;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers.A2WSystem;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.MD.SystemPlugins.MockAnnotator;

public class Eval_Stub {
	
	public static void main(String[] args) throws Exception {
		boolean printMacro = true;
		boolean printMicro = true;
		boolean printTpFpFn = true;
		boolean printMax = false;
		boolean printMin = false;
		boolean printSD = false;
		
		System.out.println("Loading the datasets...");
		
		/******** Datasets *********/
		String aceInputDir = "target/test05_1/";
		A2WDataset ace = new ACE2005Dataset(aceInputDir);	

		/******** Match Relations *********/
		MatchRelation<Annotation> mam = new StrongNoOverlapMentionMatch();
			
		/******** Annotators *********/
		A2WSystem mock = new MockAnnotator();
	
		BasicMetrics<Annotation> metrics = new BasicMetrics<Annotation>();
		List<HashSet<Annotation>> computedAnnotations = mock.getA2WOutputAnnotationList(ace);
		BasicMetricsRecord rs = metrics.getResult(computedAnnotations, ace.getA2WGoldStandardList(), mam);
	
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