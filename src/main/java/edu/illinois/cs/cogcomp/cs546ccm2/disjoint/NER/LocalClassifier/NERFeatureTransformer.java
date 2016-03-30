package edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier;

import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;

public class NERFeatureTransformer extends AbstractFeatureGenerator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This function returns a feature vector \Phi(x,y) based on an instance-structure pair.
	 * 
	 * @return Feature Vector \Phi(x,y), where x is the input instance and y is the
	 *         output structure
	 */

	@Override
	public IFeatureVector getFeatureVector(IInstance x, IStructure y) {
		NERInstance mx = (NERInstance) x;
		NERLabel my = (NERLabel)y;
		FeatureVectorBuffer fvb = new FeatureVectorBuffer(mx.baseFv);
		fvb.shift(my.output * mx.baseNfeature);
		return fvb.toFeatureVector();
	}
}
