package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import java.io.Serializable;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.AbstractInferenceSolver;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;

public class NerInferenceSolver extends AbstractInferenceSolver implements Serializable {

	private static final long serialVersionUID = 5253748728743334706L;
	private AbstractFeatureGenerator featGen;
	
	public NerInferenceSolver(AbstractFeatureGenerator featGen) {
		this.featGen = featGen;
	}
	
	@Override
	public IStructure getBestStructure(WeightVector weight, IInstance ins) {
		return getLossAugmentedBestStructure(weight, ins, null);
	}

	@Override
	public IStructure getLossAugmentedBestStructure(WeightVector weight, IInstance ins, IStructure goldStructure) {
		List<String> labels = CCM2Constants.NerLabels;
		double bestScore = Double.MIN_VALUE;
		NerLabel best = null;
		for(String label : labels) {
			double score = weight.dotProduct(featGen.getFeatureVector(ins, new NerLabel(label)));
			if(bestScore < score) {
				best = new NerLabel(label);
				bestScore = score;
			}
		}
		return best;
	}

	@Override
	public float getLoss(IInstance ins, IStructure gold, IStructure pred) {
		return NerLabel.getLoss((NerLabel)gold, (NerLabel)pred);
	}
	
	@Override
	public Object clone() {
		return new NerInferenceSolver(featGen);
	}
}