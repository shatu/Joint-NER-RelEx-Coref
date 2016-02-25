package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Metrics;

/**
 * Adopted by Shashank from the BAT-Framework
 */

import java.io.Serializable;

public class BasicMetricsRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	private float microF1, microRecall, microPrecision, macroF1, macroRecall,
			macroPrecision;
	private float maxF1, maxRecall, maxPrecision;
	private float minF1, minRecall, minPrecision;
	private float sdF1, sdRecall, sdPrecision;
	private int tp, fn, fp;
	private float[] precisions, recalls, f1s;
	private int[] tps, fns, fps;

	public BasicMetricsRecord(float microF1, float microRecall,
			float microPrecision, float macroF1, float macroRecall,
			float macroPrecision, int tp, int fn, int fp, float[] precisions,
			float[] recalls, float[] f1s, int[] tps, int[] fps, int[] fns,
			float maxF1, float minF1, float sdF1,
			float maxPrecision, float minPrecision, float sdPrecision,
			float maxRecall, float minRecall, float sdRecall) {
		this.microF1 = microF1;
		this.microRecall = microRecall;
		this.microPrecision = microPrecision;
		this.macroF1 = macroF1;
		this.macroRecall = macroRecall;
		this.macroPrecision = macroPrecision;
		this.tp = tp;
		this.fn = fn;
		this.fp = fp;
		this.precisions = precisions;
		this.recalls = recalls;
		this.f1s = f1s;
		this.tps = tps;
		this.fns = fns;
		this.fps = fps;
		this.maxF1 = maxF1;
		this.minF1 = minF1;
		this.sdF1 = sdF1;
		this.maxPrecision = maxPrecision;
		this.minPrecision = minPrecision;
		this.sdPrecision = sdPrecision;
		this.maxRecall = maxRecall;
		this.minRecall = minRecall;
		this.sdRecall = sdRecall;
	}

	public int testedInstances() {
		return precisions.length;
	}

	public float getMicroRecall() {
		return microRecall;
	}

	public float getMicroPrecision() {
		return microPrecision;
	}

	public float getMicroF1() {
		return microF1;
	}

	public float getMacroRecall() {
		return macroRecall;
	}

	public float getMacroPrecision() {
		return macroPrecision;
	}

	public float getMacroF1() {
		return macroF1;
	}
	
	public float getMaxRecall() {
		return maxRecall;
	}

	public float getMaxPrecision() {
		return maxPrecision;
	}

	public float getMaxF1() {
		return maxF1;
	}

	public float getMinRecall() {
		return minRecall;
	}

	public float getMinPrecision() {
		return minPrecision;
	}

	public float getMinF1() {
		return minF1;
	}
	
	public float getSDRecall() {
		return sdRecall;
	}

	public float getSDPrecision() {
		return sdPrecision;
	}

	public float getSDF1() {
		return sdF1;
	}
	
	public int getGlobalTp() {
		return tp;
	}

	public int getGlobalFp() {
		return fp;
	}

	public int getGlobalFn() {
		return fn;
	}

	public float getPrecisions(int i) {
		return precisions[i];
	}

	public float getRecalls(int i) {
		return recalls[i];
	}

	public float getF1s(int i) {
		return f1s[i];
	}

	public String toString() {
		return String
				.format("Micro P/R/F1: %.3f/%.3f/%.3f%nMacro P/R/F1: %.3f/%.3f/%.3f%nMax P/R/F1: %.3f/%.3f/%.3f%nMin P/R/F1: %.3f/%.3f/%.3f%nS.D. P/R/F1: %.3f/%.3f/%.3f%nGlobal TP/FP/FN: %d/%d/%d",
						this.getMicroPrecision(), this.getMicroRecall(),
						this.getMicroF1(), this.getMacroPrecision(),
						this.getMacroRecall(), this.getMacroF1(), 
						this.getMaxPrecision(), this.getMaxRecall(), this.getMaxF1(),
						this.getMinPrecision(), this.getMinRecall(), this.getMinF1(),
						this.getSDPrecision(), this.getSDRecall(), this.getSDF1(),
						this.getGlobalTp(), this.getGlobalFp(),
						this.getGlobalFn());
	}

	public int getTPs(int i) {
		return tps[i];
	}

	public int getFPs(int i) {
		return fps[i];
	}

	public int getFNs(int i) {
		return fns[i];
	}
}
