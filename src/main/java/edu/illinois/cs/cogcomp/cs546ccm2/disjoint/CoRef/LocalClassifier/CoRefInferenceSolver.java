package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier;

import java.io.Serializable;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.AbstractInferenceSolver;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;

public class CoRefInferenceSolver extends AbstractInferenceSolver implements Serializable {

	private static final long serialVersionUID = 5253748728743334706L;
	private AbstractFeatureGenerator featGen;
	
	public CoRefInferenceSolver(AbstractFeatureGenerator featGen) {
		this.featGen = featGen;
	}
	
	@Override
	public IStructure getBestStructure(WeightVector weight, IInstance ins) {
		return getLossAugmentedBestStructure(weight, ins, null);
	}

	@Override
	public IStructure getLossAugmentedBestStructure(WeightVector weight, IInstance ins, IStructure goldStructure) {
		List<String> labels = CCM2Constants.CoRefLabels;
		double bestScore = Double.NEGATIVE_INFINITY;
		CoRefLabel best = null;
		for(String label : labels) {
			double score = weight.dotProduct(featGen.getFeatureVector(ins, new CoRefLabel(label)));
			if(bestScore < score) {
				best = new CoRefLabel(label);
				bestScore = score;
			}
		}
		return best;
	}

	@Override
	public float getLoss(IInstance ins, IStructure gold, IStructure pred) {
		return CoRefLabel.getLoss((CoRefLabel)gold, (CoRefLabel)pred);
	}
	
	@Override
	public Object clone() {
		return new CoRefInferenceSolver(featGen);
	}
}