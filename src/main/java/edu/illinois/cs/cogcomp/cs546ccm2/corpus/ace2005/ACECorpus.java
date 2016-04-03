package edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.handler.IllinoisPOSHandler;
import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocumentAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACorpus;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.util.XMLException;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;

/**
 * 
 * Intended to provide a set of functions to deal with the ACE corpus.
 * 
 * @author shashank
 *
 */

public class ACECorpus extends ACorpus {
	
	static String NAME = "ACE2005";
	
	private static IntPair bcSpan;
	private static IntPair bnSpan;
	private static IntPair ctsSpan;
	private static IntPair nwSpan;
	private static IntPair unSpan;
	private static IntPair wlSpan;

	static String[] failureFileList = new String[] {
			"AFP_ENG_20030425.0408.apf.xml",
			"AFP_ENG_20030519.0372.apf.xml",
			"AFP_ENG_20030522.0878.apf.xml",
			"MARKETVIEW_20050222.0729.apf.xml",
			"rec.music.makers.guitar.acoustic_20041228.1628.apf.xml"
	};
	
	public List<ACEDocument> getbcDocs() {
		return docs.subList(bcSpan.getFirst(), bcSpan.getSecond());
	}
	
	public List<ACEDocument> getbnDocs() {
		return docs.subList(bnSpan.getFirst(), bnSpan.getSecond());
	}
	
	public List<ACEDocument> getctsDocs() {
		return docs.subList(ctsSpan.getFirst(), ctsSpan.getSecond());
	}
	
	public List<ACEDocument> getnwDocs() {
		return docs.subList(nwSpan.getFirst(), nwSpan.getSecond());
	}
	
	public List<ACEDocument> getunDocs() {
		return docs.subList(unSpan.getFirst(), unSpan.getSecond());
	}
	
	public List<ACEDocument> getwlDocs() {
		return docs.subList(wlSpan.getFirst(), wlSpan.getSecond());
	}
	
	public static String getName() {
		return NAME;
	}
	
    public static void main (String[] args) throws Exception {

	    String aceCorpusDir = CCM2Constants.ACE05CorpusPath;
		String outputDir = CCM2Constants.ACE05ProcessedPath;
		
		ACECorpus aceCorpus = new ACECorpus();
		aceCorpus.prepareCorpus(aceCorpusDir, outputDir);
		
		String processedCorpusDir = outputDir;
		aceCorpus.initCorpus(processedCorpusDir);
		
		if(!ACECorpus.isCorpusReady()) {
			System.out.println("Some problem in initializing the corpus -- please make sure that you run readCorpus() before trying to read the corpus");
			System.exit(-1);
		}
		
		System.out.println(aceCorpus.getbcDocs().size());
		System.out.println(aceCorpus.getbnDocs().size());
		System.out.println(aceCorpus.getctsDocs().size());
		System.out.println(aceCorpus.getnwDocs().size());
		System.out.println(aceCorpus.getunDocs().size());
		System.out.println(aceCorpus.getwlDocs().size());
		System.out.println(aceCorpus.getAllDocs().size());
		
		aceCorpus.testRelationView();
		aceCorpus.printRelationTypes();
	}
    
    
    public void testRelationView() {
    	ACEDocument doc = getDocFromID("AFP_ENG_20030304.0250");
		for(AnnotatedText ta: doc.taList) {
			List<Constituent> annots = ta.getTa().getView(CCM2Constants.RelExGold).getConstituents();
			for(Constituent annot: annots) {
				if(annot.getOutgoingRelations().size() > 0) {
					System.out.println(annot.getOutgoingRelations().size());
					for(Relation rel : annot.getOutgoingRelations()) {
						System.out.println(rel.getRelationName() + "-->" + rel.getSource() + "-->" + rel.getSource().getLabel() + "-->" + 
								rel.getTarget() + "-->" + rel.getTarget().getLabel());
					}
				}
			}
		}
    }
    
    public void printRelationTypes() {
    	HashSet<String> relationTypes = new HashSet<String>();
    	for(ACEDocument doc : getAllDocs()) {
    		for(AnnotatedText ta: doc.taList) {
    			List<Constituent> annots = ta.getTa().getView(CCM2Constants.RelExGold).getConstituents();
    			for(Constituent annot: annots) {
    				if(annot.getOutgoingRelations().size() > 0) {
    					for(Relation rel : annot.getOutgoingRelations()) {
    						relationTypes.add(rel.getRelationName());
    					}
    				}
    			}
    		}
    	}
    	
    	for(String relType : relationTypes) {
    		System.out.println(relType);
    	}
    	
    }
    
    @SuppressWarnings("unchecked")
	public void initCorpus(String inDirPath) {
    	if(isInit) {
    		System.out.println("Corpus has already been initialized");
    		return;
    	}
    	docs = new ArrayList<ACEDocument>();
    	docIDtoDocMap = new HashMap<>();
    	File inDir = new File(inDirPath);
    	File[] subFolderList = inDir.listFiles();
		for (int i = 0; i < subFolderList.length; ++i) {
	    	File infile = new File(subFolderList[i], getName() + ".obj");
			ObjectInputStream s;
			List<ACEDocument> tempDocs;
			
			String part = subFolderList[i].getName();
			try {
			    s = new ObjectInputStream(new FileInputStream(infile));
			    tempDocs = (List<ACEDocument>) s.readObject();
			    //System.out.println(aceDoc.aceAnnotation.id);
			    switch(part) {
			    	case "bc":  
			    		bcSpan = new IntPair(docCount, docCount + tempDocs.size());
			    	case "bn":
			    		bnSpan = new IntPair(docCount, docCount + tempDocs.size());
			    	case "cts": 
			    		ctsSpan = new IntPair(docCount, docCount + tempDocs.size());
			    	case "nw": 
			    		nwSpan = new IntPair(docCount, docCount + tempDocs.size());
			    	case "un": 
			    		unSpan = new IntPair(docCount, docCount + tempDocs.size());
			    	case "wl": 
			    		wlSpan = new IntPair(docCount, docCount + tempDocs.size());
			    }
			    docs.addAll(tempDocs);
			    docCount += tempDocs.size();
			    s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}    	
    	System.out.println(docCount + " Files Read successfully");
    	
    	for(int i=0; i< docs.size(); i++) {
    		docIDtoDocMap.put(docs.get(i).getDocID(), i);
    	}
    	
    	checkConsistency();
    	isInit = true;
    }
    
    private void checkConsistency() {
    	for(String docID: docIDtoDocMap.keySet()) {
    		if(!docID.equals(docs.get(docIDtoDocMap.get(docID)).getDocID())) {
    			throw new RuntimeException("Problem in creating docID to Doc map");
    		}	
    	}
    	System.out.println("Consistency Test Passed");
    }
       
    public void prepareCorpus(String docDirInput, String docDirOutput) throws AnnotatorException {
		File outDir = new File(docDirOutput);
		if (outDir.exists() == true) {
			return;
		}
		outDir.mkdir();

		CcgTextAnnotationBuilder taBuilder = new CcgTextAnnotationBuilder(new IllinoisTokenizer());
		AceFileProcessor fileProcessor = new AceFileProcessor(taBuilder);
		processAndDumpDocuments(fileProcessor, docDirInput, docDirOutput);
    }

	private void processAndDumpDocuments(AceFileProcessor processor, String inputFolderStr, String outputFolderStr) throws AnnotatorException {
		HashSet<String> failureFileSet = new HashSet<String>();
		
		IllinoisPOSHandler posTagger = new IllinoisPOSHandler();
		
		for (int i = 0; i < failureFileList.length; ++i) {
			failureFileSet.add(failureFileList[i]);
		}
		
		File inputFolder = new File (inputFolderStr);
		File[] subFolderList = inputFolder.listFiles();

		for (int folderIndex = 0; folderIndex < subFolderList.length; ++folderIndex) {

			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File directory, String fileName) {
					return fileName.endsWith(".apf.xml");
				}
			};
            File subFolderEntry = subFolderList[folderIndex];
			File[] fileList = subFolderEntry.listFiles(filter);
			File outputFolder = new File (outputFolderStr, subFolderEntry.getName());
			outputFolder.mkdir();
			docs = new ArrayList<>();
			for (int fileID = 0; fileID < fileList.length; ++fileID) {
				
				if(failureFileSet.contains(fileList[fileID].getName()))
                	continue;
                String annotationFile = fileList[fileID].getAbsolutePath();

                System.err.println( "reading ace annotation from '" + annotationFile + "'..." );
                ACEDocumentAnnotation annotationACE = null;
                try {
                    annotationACE = ReadACEAnnotation.readDocument(annotationFile);
                } catch (XMLException e) {
                    e.printStackTrace();
                    continue;
                }

                ACEDocument aceDoc = processor.processAceEntry(subFolderEntry, annotationACE, annotationFile);
                
                //Add POS Tags
                for(AnnotatedText at : aceDoc.taList) {
                	posTagger.addView(at.getTa());
                }
                
                docs.add(aceDoc);
			}
			
			File outputFile = new File (outputFolder, getName() + ".obj");
            FileOutputStream f;
			try {
				f = new FileOutputStream(outputFile);
			    ObjectOutputStream s = new ObjectOutputStream(f);
			    s.writeObject(docs);
			    s.close();
			    f.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
