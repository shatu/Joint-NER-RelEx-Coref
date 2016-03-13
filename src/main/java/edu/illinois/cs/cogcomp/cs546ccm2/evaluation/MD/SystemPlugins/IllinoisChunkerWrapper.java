package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.MD.SystemPlugins;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.A2WDataset;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.ACEDatasetWrapper;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.Wrappers.A2WSystem;

public class IllinoisChunkerWrapper implements A2WSystem {
	
	private String NAME = "Illinois-Chunker";
	
	//TODO: Wrap IllinoisChunker from the disjoint MD package here
	public IllinoisChunkerWrapper() {
		
	}
	
	@Override
	public HashSet<Annotation> solveA2W(String text) {
		throw new NotImplementedException();
	}

	@Override
	public HashSet<Tag> solveC2W(String text) {
		throw new NotImplementedException();
	}
	
	@Override
	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions) {
		throw new NotImplementedException();
	}
	
	// This function doesn't allow overlapping mentions to be returned. Mentions need to match exactly.
	public HashSet<Annotation> solveD2W(String text, HashSet<Mention> mentions, boolean allow_overlap) {
		throw new NotImplementedException();
	}
	
	public List<HashSet<Annotation>> getA2WOutputAnnotationList(A2WDataset ds) {
		throw new NotImplementedException();
	}

	@Override
	public String getName() {
		return this.NAME;
	}
	
	public List<HashSet<Annotation>> getEntityMentionTagList(ACEDatasetWrapper ds) {
		if(!ds.isCorpusReady()) {
			System.out.println("Corpus not loaded in memory.. first initialize the corpus .. exiting ....");
			System.exit(0);
		}
		
		//TODO: Do actual annotation here ... addview etc thing .. i.e. call appropriate function from the actual Chunker object
		List<HashSet<Annotation>> res = new ArrayList<>();
		for(ACEDocument doc: ds.aceCorpus.getAllDocs()) {
			
		}
		
		return res;
	}
	
//	private void loadNERTags() {
//		for(ACEDocument doc: aceCorpus.getAllDocs()) {
//			docEntities.add(wrapNERTags(doc.getAllEntities()));
//		}
//	}
	
	private HashSet<Annotation> wrapAnnotations(List<ACEEntity> mentionlist) {
		HashSet<Annotation> mentionSet = new HashSet<>();
		for(ACEEntity mention: mentionlist) {
			Annotation e = new Annotation(mention.extentStart, mention.extentEnd - mention.extentStart + 1, "");
			mentionSet.add(e);
		}
		
		return mentionSet;
	}
	
}
