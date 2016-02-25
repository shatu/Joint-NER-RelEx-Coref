package edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.ACECorpus;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Annotation;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Mention;
import edu.illinois.cs.cogcomp.cs546ccm2.evaluation.BAT.DataStructures.Tag;

public class ACE2005Dataset implements A2WDataset {
	private List<String> textList;
	private List<HashSet<Annotation>> annList;
	
	public ACE2005Dataset(String aceCorpusPath) {
		//Initialize ACE Corpus
		ACECorpus aceCorpus = new ACECorpus();
		aceCorpus.readCorpus(aceCorpusPath);
		
		//load the text
		HashMap<String, String> filenameToBody= loadBody(aceCorpusPath);

		//load the annotations
		HashMap<String, HashSet<Annotation>> filenameToAnnotations= loadTags(aceCorpusPath);

		//unify the two mappings and generate the lists.
		unifyMaps(filenameToBody, filenameToAnnotations);
	}
	
	/**
	 * TODO Use ACECorpus object here to load desired Annotations
	 * 
	 * @param aceCorpusPath
	 * @return
	 */
	public HashMap<String, HashSet<Annotation>> loadTags(String aceCorpusPath) {
		return null;
	}

	/**
	 * TODO: Use ACECorpus object here to load text
	 * This can wait for now
	 */
	public HashMap<String, String> loadBody(String aceCorpusPath) {
		return null;
	}

	public void unifyMaps(HashMap<String, String> filenameToBody, HashMap<String, HashSet<Annotation>> filenameToAnnotations) {
		annList = new Vector<HashSet<Annotation>>();
		textList = new Vector<String>();
		for (String filename : filenameToAnnotations.keySet()) {
			textList.add(filenameToBody.get(filename));
			annList.add(filenameToAnnotations.get(filename));
		}
	}

	public int getSize() {
		return this.textList.size();
	}

	public int getTagsCount() {
		int count = 0;
		for (HashSet<Annotation> s : annList) {
			count += s.size();
		}
		return count;
	}

	public List<String> getTextInstanceList() {
		return this.textList;
	}

	public String getName() {
		return "ACE2005";
	}

	public List<HashSet<Annotation>> getA2WGoldStandardList() {
		return annList;
	}

	@Override
	public List<HashSet<Tag>> getC2WGoldStandardList() {
		throw new NotImplementedException();
	}

	@Override
	public List<HashSet<Mention>> getMentionsInstanceList() {
		throw new NotImplementedException();
	}

	@Override
	public List<HashSet<Annotation>> getD2WGoldStandardList() {
		throw new NotImplementedException();
	}

}
